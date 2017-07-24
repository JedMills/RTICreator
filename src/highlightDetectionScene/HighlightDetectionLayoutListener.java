package highlightDetectionScene;

import guiComponents.ImageCropPane;
import guiComponents.ImageGridTile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import main.Main;
import utils.ImageProcessing;
import utils.Utils;
import static utils.Utils.Vector3f;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;


/**
 * Listens to events from the {@link HighlightDetectionLayout}.
 *
 * @see HighlightDetectionLayout
 *
 * @author Jed Mills
 */
public class HighlightDetectionLayoutListener implements EventHandler<ActionEvent> {

    /** The layout that this listener listens to*/
    private HighlightDetectionLayout highlightLayout;

    /** The singleton instance of this class */
    private static HighlightDetectionLayoutListener ourInstance = new HighlightDetectionLayoutListener();

    /**
     * @return {@link HighlightDetectionLayoutListener#ourInstance}
     */
    public static HighlightDetectionLayoutListener getInstance() {
        return ourInstance;
    }

    /**
     * Creates a new HighlightDetectionLayoutListener
     */
    private HighlightDetectionLayoutListener() {}

    /**
     * Sets the HighlightDetectionLayout that this class listens to events from.
     *
     * @param highlightDetectionLayout
     */
    public void init(HighlightDetectionLayout highlightDetectionLayout){
        highlightLayout = highlightDetectionLayout;
    }


    /**
     * Handles events fom the HighlightDetectionLayout that this instance is listening to.
     *
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("setSphereButton")){
                //setting the final positions of the sphere
                setSphere();

            }else if(source.getId().equals("backButton")){
                //move back to the last layout
                Main.backButtonPressed(highlightLayout);

            }else if(source.getId().equals("nextButton")){
                //detect the highlights and move to the CropExecuteLayout
                highlightDetectAndMoveScene();

            }else if(source.getId().equals("sphereXMinus")){
                //move the sphere left
                highlightLayout.translateCircleSelect(-1, 0);

            }else if(source.getId().equals("sphereXPlus")){
                //move the sphere right
                highlightLayout.translateCircleSelect(1, 0);

            }else if(source.getId().equals("sphereYMinus")){
                //move the sphere up
                highlightLayout.translateCircleSelect(0, -1);

            }else if(source.getId().equals("sphereYPlus")){
                //move the sphere down
                highlightLayout.translateCircleSelect(0, 1);

            }else if(source.getId().equals("sphereRMinus")){
                //decrease sphere radius
                highlightLayout.changeCircleR(-1);

            }else if(source.getId().equals("sphereRPlus")){
                //increase sphere radius
                highlightLayout.changeCircleR(1);

            }


        }else if(event.getSource() instanceof ComboBox){
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();
            //change the colour of the selection sphere
            highlightLayout.setCircleSelectionColour(source.getSelectionModel().getSelectedItem());
        }
    }


    /**
     * Sets the values in the final sphere x/y/r boxes at the bottom of the layout, enabling them and the
     * button to allow the user to do the highlight detection. Called when the user clicks the set sphere button.
     */
    private void setSphere(){
        int[] xyr = highlightLayout.getSphereVals();

        Bounds imageBounds = highlightLayout.getImageBounds();
        //check something hasn't gone horribly wrong and the circle selections's been set
        if(xyr == null){
            Main.showInputAlert("Sphere values have not been set. Please try moving and resizing the " +
                                        "circular selector.");
            return;
        }

        int x = xyr[0];
        int y = xyr[1];
        int r = xyr[2];
        //check something hasn't gone horribly wrong and the circle selections's inside the image
        if(!imageBounds.contains(x - r, y - r, 2 * r, 2 * r)){
            Main.showInputAlert("It appears the circle has been set so that it resides outside of the " +
                                        "selected image. Please try resizing or moving the circular selector.");
            return;
        }
        //enable the buttons to detect highlights
        highlightLayout.enableFinalParamsNodes();
        highlightLayout.setFinalParamsFields(x, y, r);
    }


    /**
     * Detects the highlights for the given sphere, creates an .lp file for them, and moves the scene onto the
     * CropExecuteLayout if that was all successful.
     */
    private void highlightDetectAndMoveScene(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Processing highlights...");

                //get the final location of the sphere
                int[] xyrt = highlightLayout.getFinalParamsFields();

                ImageGridTile[] gridTiles = highlightLayout.getGridTiles();

                //this is for easy parallelisation  of the highlight detection
                HashSet<ImageGridTile> gridTileSet = new HashSet<>();

                //the data generated by the highlight detection will be put in here
                HashMap<String, Vector3f> lpData = new HashMap<>();

                for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

                //run the highlight detection for the images
                Utils.IntHolder numRejected = new Utils.IntHolder(0);
                gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
                    @Override
                    public void accept(ImageGridTile tile) {
                        BufferedImage image = Utils.fxImageToBufferedJPEG(tile.getImage());

                        Vector3f highlight = getHighlightVec(xyrt[0], xyrt[1], xyrt[2], xyrt[3], image);

                        //if the highlight detection can't get a highlight, it often gives NaN or sometimes
                        //infinity, so check for this
                        if(vectorOK(highlight)){
                            lpData.put(tile.getName(), highlight);
                        }else {
                            //if the highlight detection couldn't get the highlight, reject this one
                            numRejected.pp();
                        }
                    }
                });

                Main.hideLoadingDialog();

                if(!(numRejected.getValue() == 0)){
                    if(numRejected.getValue() == gridTiles.length){
                        //if we rejected all the images, there's no point continuing
                        Main.showInputAlert("No highlights could be detected in any of the images. Please " +
                                            "retry with different images or settings.");
                        return;
                    }else{
                        //otherwise just tell the user that at least some were rejected
                        Main.showInputAlert(numRejected.getValue() + " images were removed from image selection " +
                                "as highlights in these could not be calculated.");
                    }
                }

                

                Main.showLoadingDialog("Creating new LP file...");

                //create the lp file from the highlight positions
                File lpFile = writeLPDataToFile(lpData);
                if(lpFile == null){return;}

                Main.currentLPFile = lpFile;

                //the images that highlights could be detectd for need to be passed to the CropExecuteLayout
                //so find these
                ArrayList<ImageGridTile> gridTilesArray = new ArrayList<>();
                for(ImageGridTile gridTile : gridTiles){
                    if(Utils.checkIn(gridTile.getName(), lpData.keySet())) {
                        gridTilesArray.add(gridTile);
                    }
                }

                Main.hideLoadingDialog();


                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Main.changeToCropExecuteScene(gridTilesArray);

                    }
                });

            }
        }).start();
    }


    /**
     * The highlight detection gives NaNs whe nit can't detect highlights so this is used to check or them.
     *
     * @param vector3f  vector to check
     * @return          if it is a proper position
     */
    private boolean vectorOK(Vector3f vector3f){
        if(     Float.isNaN(vector3f.getX())        ||
                Float.isNaN(vector3f.getY())        ||
                Float.isNaN(vector3f.getZ())        ||
                Float.isInfinite(vector3f.getX())   ||
                Float.isInfinite(vector3f.getY())   ||
                Float.isInfinite(vector3f.getZ()) ){
            return false;
        }

        return true;
    }


    /**
     * Saves the .lp data gievn to a file on the disk at the location of {@link Main#currentAssemblyFolder}, with
     * the project name + "_highlightGenerated.lp" as the name.
     *
     * @param lpData    data to write to the .lp file
     * @return          the file that was created
     */
    private File writeLPDataToFile(HashMap<String, Vector3f> lpData){
        //the path to the new file
        String lpFilePath = Main.currentAssemblyFolder.getAbsolutePath() + "\\" + Main.currentRTIProject.getName() +
                            "_highlightGenerated.lp";

        File newLPFile = new File(lpFilePath);

        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(newLPFile));
            //write the number of images on the first line
            writer.write(String.valueOf(lpData.size()) + System.lineSeparator());

            //write all the data o the file
            String name, x, y, z;
            for(String key : lpData.keySet()){
                name = Main.currentImagesFolder.getAbsolutePath() + "/" + key;

                x = String.valueOf(lpData.get(key).getX());
                y = String.valueOf(lpData.get(key).getY());
                z = String.valueOf(lpData.get(key).getZ());
                //write the image line
                writer.write(name + " " + x + " " + y + " " + z + System.lineSeparator());
            }
        }catch(IOException e){
            e.printStackTrace();
            Main.showFileReadingAlert("Error creating new .lp file from highlights.");
            return null;

        }finally {
            if(writer != null){
                try{writer.close();}
                catch(IOException e){e.printStackTrace();}
            }
        }

        return newLPFile;
    }


    /**
     * Called for each image in the {@link HighlightDetectionLayout#imageGrid} to detect the highlight of the
     * sphere at (sphereX, sphereY) with radius sphereR, and the threshold value chosen by the user on the slider.
     *
     * @param sphereX       x pos of the sphere in the image
     * @param sphereY       y pos of the sphere in the image
     * @param sphereR       radius of the sphere in the image
     * @param threshold     threshold set by the user
     * @param image         image to detect the highlight or
     * @return              the highlight vector for the given image
     */
    private Vector3f getHighlightVec(int sphereX, int sphereY, int sphereR, int threshold, BufferedImage image){
        //the box around the sphere
        int x = sphereX - sphereR;
        int y = sphereY - sphereR;
        int size = 2 * sphereR;

        //crop the image to this box
        image = image.getSubimage(x, y, size, size);

        //convert to greyscale helps with highlight detection
        BufferedImage grey = ImageProcessing.convertToGrayscale(image);
        BufferedImage blend = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ImageProcessing.blendFilterGray(blend, grey);

        //clear all the pixels not inside the radius to help make highlight detection easier
        float[] ballInfo = {sphereR, sphereR, sphereR};
        BufferedImage clear = ImageProcessing.clearOutside(grey, ballInfo);

        //find the highlight
        float[] highlights = ImageProcessing.findTh(clear, threshold);

        //convert that to a light vector
        float[] lightVec = ImageProcessing.calculateLightPosition(ballInfo, highlights);

        return new Vector3f(lightVec[0], lightVec[1], lightVec[2]);
    }
}
