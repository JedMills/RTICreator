package highlightDetectionScene;

import guiComponents.ImageCropPane;
import guiComponents.ImageGridTile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
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
import java.util.TreeMap;
import java.util.function.Consumer;


/**
 * Created by Jed on 14-Jul-17.
 */
public class HighlightDetectionLayoutListener implements EventHandler<ActionEvent> {

    private HighlightDetectionLayout highlightLayout;

    private static HighlightDetectionLayoutListener ourInstance = new HighlightDetectionLayoutListener();

    public static HighlightDetectionLayoutListener getInstance() {
        return ourInstance;
    }

    private HighlightDetectionLayoutListener() {}

    public void init(HighlightDetectionLayout highlightDetectionLayout){
        highlightLayout = highlightDetectionLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("setSphereButton")){
                setSphere();

            }else if(source.getId().equals("backButton")){

            }else if(source.getId().equals("nextButton")){
                highlightDetectAndMoveScene();

            }else if(source.getId().equals("sphereXMinus")){
                highlightLayout.translateCircleSelect(-1, 0);

            }else if(source.getId().equals("sphereXPlus")){
                highlightLayout.translateCircleSelect(1, 0);

            }else if(source.getId().equals("sphereYMinus")){
                highlightLayout.translateCircleSelect(0, -1);

            }else if(source.getId().equals("sphereYPlus")){
                highlightLayout.translateCircleSelect(0, 1);

            }else if(source.getId().equals("sphereRMinus")){
                highlightLayout.changeCircleR(-1);

            }else if(source.getId().equals("sphereRPlus")){
                highlightLayout.changeCircleR(1);

            }


        }else if(event.getSource() instanceof ComboBox){
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();

            highlightLayout.setCircleSelectionColour(source.getSelectionModel().getSelectedItem());
        }
    }


    private void setSphere(){
        int[] xyr = highlightLayout.getSphereVals();

        Bounds imageBounds = highlightLayout.getImageBounds();

        if(xyr == null){
            Main.showInputAlert("Sphere values have not been set. Please try moving and resizing the " +
                                        "circular selector.");
            return;
        }

        int x = xyr[0];
        int y = xyr[1];
        int r = xyr[2];

        if(!imageBounds.contains(x - r, y - r, 2 * r, 2 * r)){
            Main.showInputAlert("It appears the circle has been set so that it resides outside of the " +
                                        "selected image. Please try resizing or moving the circular selector.");
            return;
        }

        highlightLayout.enableFinalParamsNodes();
        highlightLayout.setFinalParamsFields(x, y, r);
    }


    private void highlightDetectAndMoveScene(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Processing highlights...");

                int[] xyrt = highlightLayout.getFinalParamsFields();

                ImageGridTile[] gridTiles = highlightLayout.getGridTiles();

                HashSet<ImageGridTile> gridTileSet = new HashSet<>();

                HashMap<String, Vector3f> lpData = new HashMap<>();

                for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

                gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
                    @Override
                    public void accept(ImageGridTile tile) {
                        BufferedImage image = Utils.fxImageToBufferedJPEG(tile.getImage());

                        Vector3f highlight = getHighlightVec(xyrt[0], xyrt[1], xyrt[2], xyrt[3], image);

                        lpData.put(tile.getName(), highlight);
                    }
                });

                Main.hideLoadingDialog();

                Main.showLoadingDialog("Creating new LP file...");
                File lpFile = writeLPDataToFile(lpData);

                if(lpFile == null){return;}

                Main.currentLPFile = lpFile;

                ArrayList<ImageGridTile> gridTilesArray = new ArrayList<>();
                for(ImageGridTile gridTile : gridTiles){gridTilesArray.add(gridTile);}

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


    private File writeLPDataToFile(HashMap<String, Vector3f> lpData){
        String lpFilePath = Main.currentAssemblyFolder.getAbsolutePath() + "\\" + Main.currentRTIProject.getName() +
                            "_highlightGenerated.lp";

        File newLPFile = new File(lpFilePath);

        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(newLPFile));
            writer.write(String.valueOf(lpData.size()) + System.lineSeparator());

            String name, x, y, z;
            for(String key : lpData.keySet()){
                name = Main.currentImagesFolder.getAbsolutePath() + "/" + key;

                x = String.valueOf(lpData.get(key).getX());
                y = String.valueOf(lpData.get(key).getY());
                z = String.valueOf(lpData.get(key).getZ());

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




    private Vector3f getHighlightVec(int sphereX, int sphereY, int sphereR, int threshold, BufferedImage image){
        int x = sphereX - sphereR;
        int y = sphereY - sphereR;
        int size = 2 * sphereR;

        image = image.getSubimage(x, y, size, size);

        BufferedImage grey = ImageProcessing.convertToGrayscale(image);

        BufferedImage blend = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        ImageProcessing.blendFilterGray(blend, grey);

        float[] ballInfo = {sphereR, sphereR, sphereR};

        BufferedImage clear = ImageProcessing.clearOutside(grey, ballInfo);

        float[] highlights = ImageProcessing.findTh(clear, threshold);

        float[] lightVec = ImageProcessing.calculateLightPosition(ballInfo, highlights);

        return new Vector3f(lightVec[0], lightVec[1], lightVec[2]);
    }
}
