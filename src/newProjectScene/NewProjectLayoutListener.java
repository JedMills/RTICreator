package newProjectScene;

import guiComponents.ImageGridTile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import main.Main;
import main.RTIProject;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

import static utils.Utils.fxImageToBufferedJPEG;


/**
 * Listens to events from the {@link NewProjectLayout}
 *
 * @author Jed Mills
 */
public class NewProjectLayoutListener implements EventHandler<ActionEvent> {

    /** The layout that this instance is listening to*/
    private NewProjectLayout newProjectLayout;

    /** The singleton instance of this class */
    private static NewProjectLayoutListener ourInstance = new NewProjectLayoutListener();

    /**
     * @return {@link NewProjectLayoutListener#ourInstance}
     */
    public static NewProjectLayoutListener getInstance() {return ourInstance;}

    /**
     * Creates a new NewProjectLayoutListener
     */
    private NewProjectLayoutListener() {}

    /**
     * Sets the NewProjectLayout that is listener listens to.
     *
     * @param newProjectLayout      the layout that this class should listen to
     */
    public void init(NewProjectLayout newProjectLayout){
        this.newProjectLayout = newProjectLayout;
    }


    /**
     * Handles events created by the varioud widgetsin the {@link NewProjectLayoutListener#newProjectLayout}.
     *
     * @param event     the event that was created in the NewProjectLayout
     */
    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("openFolder")){
                if(newProjectLayout.getProjectType().equals(RTIProject.ProjectType.HIGHLIGHT)){
                    //open wht LoadProjRsrscsDialog with the highlight layout
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.HIGHLIGHT);

                }else{
                    //open wht LoadProjRsrscsDialog with the lp layout
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.LP);
                }

            }else if(source.getId().equals("removePicButton")){
                //move a picture from the selected images grid and add it to the rejected images grid,
                //making the tooltip with the rejection reason
                String comment = newProjectLayout.getRemoveRsnTxtField().getText();
                ImageGridTile tile = newProjectLayout.removeGridTileSelected();

                if(tile != null) {
                    newProjectLayout.getRemoveRsnTxtField().setText("");
                    tile.setRejectComment(comment);
                    tile.setSelected(false);
                    newProjectLayout.addTileToRejected(tile);
                }

            }else if(source.getId().equals("replacePicBtn")){
                //move an image back from the rejectd images grid to the selected images grid
                ImageGridTile tile = newProjectLayout.removeGridTileRejected();
                if(tile != null){
                    tile.removeRejectComment();
                    tile.setSelected(false);
                    newProjectLayout.addTileToSelected(tile);
                }

            }else if(source.getId().equals("backBtn")){
                //move back to the initial layout
                Main.backButtonPressed(newProjectLayout);

            }else if(source.getId().equals("nextBtn")){
                //go to the next layout that;s appropriate for the project type
                if(newProjectLayout.getProjectType().equals(RTIProject.ProjectType.LP)){
                    moveSceneDomeLP();

                }else if(newProjectLayout.getProjectType().equals(RTIProject.ProjectType.HIGHLIGHT)){
                    moveSceneHighlight();

                }
            }
        }
    }


    /**
     * Does the required work to allow the app to move to the HighlightDetectionLayout.
     */
    private void moveSceneHighlight(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Checking project resources...");

                //make sure the user has even chosen resources for this project
                if(!newProjectLayout.isResourcesSet()){
                    Main.hideLoadingDialog();
                    Main.showInputAlert("Please open the resources for this project " +
                            "using the 'Open Project Resources' button.");
                    return;
                }

                //the images have to be jpegs to move on, so this method will make sure they are
                File selectedImagesFolder;
                selectedImagesFolder = saveImagesAsJPEGs();

                Main.hideLoadingDialog();

                if(selectedImagesFolder == null){return;}

                //this is going to te the array of tiles that are given to the highlight detection layout
                ArrayList<ImageGridTile> handoverTiles = new ArrayList<>();

                if(newProjectLayout.getImagesExtension().equals("jpg")){
                    //add all the tiles in the selected images grid
                    for(ImageGridTile gridTile : newProjectLayout.getSelectedImages().getGridTiles()){
                        handoverTiles.add(new ImageGridTile(null, gridTile.getName(), gridTile.getImage(),
                                                                gridTile.getTileWidth(), gridTile.getTileHeight(),
                                                                true, true, true));
                    }

                }else{
                    handoverTiles = createGridTilesFromDir(selectedImagesFolder);
                }
                //finally change the scene
                Main.changeToHighlightDetectionScene(handoverTiles, true);
            }
        }).start();
    }


    /**
     * Used to create new image grid tiles containing jpegs, as jpegs are needed to feed into the PTM and HSH fitters.
     *
     * @param directory     directory of images
     * @return              all the nice new jpeg image grid tiles
     */
    private ArrayList<ImageGridTile> createGridTilesFromDir(File directory){

        try{
            File[] images = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    //only accept jpegs
                    if(name.toLowerCase().endsWith(".jpg")){
                        return true;
                    }
                    return false;
                }
            });

            //there are no jpegs there
            if(images.length == 0){
                Main.hideLoadingDialog();
                Main.showFileReadingAlert("No images were found in the generated images directory.");
                return null;
            }

            Image[] imagesInDir = new Image[images.length];
            String[] imageLocations = new String[images.length];
            String[] imageNames = new String[images.length];


            //load all the images
            for(int i = 0; i < imagesInDir.length; i++){
                Image currentImg = new Image("file:" + images[i].getAbsolutePath());

                imagesInDir[i] = currentImg;
                imageLocations[i] = images[i].getAbsolutePath();
                imageNames[i] = images[i].getName();
            }

            //create grid tiles from 'em
            ArrayList<ImageGridTile> imageGridTiles = new ArrayList<>();
            for(int i = 0; i < imagesInDir.length; i++){
                imageGridTiles.add(new ImageGridTile(null, imageNames[i], imagesInDir[i], 150,
                                                150, true, true, true));
            }

            return imageGridTiles;

        }catch(Exception e){
            e.printStackTrace();
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("There was an error reading files in the chosen directory.");
            return null;
        }
    }


    /**
     * Saves all the image sin the {@link NewProjectLayout#selectedImages} grid tiles as jpegs in  a new folder in
     * the assembly folder location. Used for when the user has loaded a direcotry of non-jpegs.
     *
     * @return  the folder with the new jpegs
     */
    private File saveImagesAsJPEGs(){
        //the destination folder in the assembly folder
        final String convertedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_jpegsForHLDetection";

        File convertedFolder = new File(convertedFolderLocation);
        if(!convertedFolder.exists()){ convertedFolder.mkdir(); }

        ImageGridTile[] gridTiles = newProjectLayout.getSelectedImages().getGridTiles();

        //we don't need to do any converting if they're already jpegs
        if(gridTiles[0].getName().toLowerCase().endsWith(".jpg")){
            return Main.currentImagesFolder;
        }

        //use the hash set for easy prallelisation
        HashSet<ImageGridTile> gridTileSet = new HashSet<>();
        for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

        Utils.BooleanHolder success = new Utils.BooleanHolder(true);
        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                //convert the non-jpegs to jpegs
                BufferedImage jpgImg = fxImageToBufferedJPEG(tile.getImage());
                String tileNameNoExt = tile.getName().split("[.]")[0];
                File destination = new File(convertedFolder.getAbsolutePath()
                                                            + "/" + tileNameNoExt + ".jpg");

                //write it in the new folder
                try{
                    ImageIO.write(jpgImg, "jpg", destination);
                }catch (IOException e){
                    e.printStackTrace();
                    success.setTrue(false);
                }
            }
        });

        if(!success.isTrue()){
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("Attempted to convert images to JPEGs, " +
                                            "but an error occurred.");
            return null;
        }

        return convertedFolder;
    }


    /**
     * Does the required work to allow the app to move to the CropExecuteLayout.
     */
    private void moveSceneDomeLP(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Checking project resources...");

                //check the user has actually loaded some project resources
                if(!newProjectLayout.isResourcesSet()){
                    Main.hideLoadingDialog();
                    Main.showInputAlert("Please open the resources for this project " +
                                                    "using the 'Open Project Resources' button.");
                    return;
                }

                //the folder with the images in it
                String imgFolderPath = newProjectLayout.getImgsFolder().getAbsolutePath();
                File imageParentDir = new File(imgFolderPath);

                //check the resources the use has set actually exist
                if(!imageParentDir.exists() || !imageParentDir.isDirectory()){
                    Main.hideLoadingDialog();
                    Main.showFileReadingAlert("Couldn't access the image file folder. Check that it still  " +
                                            "exists and that its name hasn't changed.");
                    return;
                }

                //check all the images specified in the lp file can be found in the given images folder
                String imageName;
                ArrayList<String> fileNames = new ArrayList<>();
                for(String imagePath : newProjectLayout.getLpData().keySet()){
                    imageName = new File(imagePath).getName();

                    if(!Utils.fileExists(imageParentDir, imageName)){
                        Main.hideLoadingDialog();
                        Main.showFileReadingAlert("Not all of the images specified in the " +
                                "LP file were found in the image resources file. Check that all files are in " +
                                "the directory (case sensitive).");
                        return;
                    }

                    fileNames.add(imageName);
                }

                ArrayList<ImageGridTile> selectedImages = newProjectLayout.getSelectedImages(fileNames);

                Main.hideLoadingDialog();
                Main.changeToCropExecuteScene(selectedImages);
            }
        }).run();

    }

}
