package newProjectScene;

import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGrid;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import main.Main;
import main.ProjectType;
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
 * Created by Jed on 07-Jul-17.
 */
public class NewProjectLayoutListener implements EventHandler<ActionEvent> {

    private NewProjectLayout newProjectLayout;

    private static NewProjectLayoutListener ourInstance = new NewProjectLayoutListener();

    public static NewProjectLayoutListener getInstance() {return ourInstance;}

    private NewProjectLayoutListener() {}

    public void init(NewProjectLayout newProjectLayout){
        this.newProjectLayout = newProjectLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("openFolder")){
                if(newProjectLayout.getProjectType().equals(ProjectType.HIGHLIGHT)){
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.HIGHLIGHT);
                }else{
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.LP);
                }

            }else if(source.getId().equals("removePicButton")){
                String comment = newProjectLayout.getRemoveRsnTxtField().getText();
                ImageGridTile tile = newProjectLayout.removeGridTileSelected();
                if(tile != null) {
                    newProjectLayout.getRemoveRsnTxtField().setText("");
                    tile.setRejectComment(comment);
                    tile.setSelected(false);
                    newProjectLayout.addTileToRejected(tile);
                }

            }else if(source.getId().equals("replacePicBtn")){
                ImageGridTile tile = newProjectLayout.removeGridTileRejected();
                if(tile != null){
                    tile.removeRejectComment();
                    tile.setSelected(false);
                    newProjectLayout.addTileToSelected(tile);
                }

            }else if(source.getId().equals("backBtn")){
                Main.backButtonPressed(newProjectLayout);

            }else if(source.getId().equals("nextBtn")){
                if(newProjectLayout.getProjectType().equals(ProjectType.LP)){
                    moveSceneDomeLP();

                }else if(newProjectLayout.getProjectType().equals(ProjectType.HIGHLIGHT)){
                    moveSceneHighlight();

                }
            }
        }
    }



    private void moveSceneHighlight(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Checking project resources...");

                if(!newProjectLayout.isResourcesSet()){
                    Main.hideLoadingDialog();
                    Main.showInputAlert("Please open the resources for this project " +
                            "using the 'Open Project Resources' button.");
                    return;
                }

                File selectedImagesFolder;
                selectedImagesFolder = saveImagesAsJPEGs();

                if(selectedImagesFolder == null){
                    Main.hideLoadingDialog();
                    return;
                }

                Main.hideLoadingDialog();

                ArrayList<ImageGridTile> handoverTiles = new ArrayList<>();

                if(newProjectLayout.getImagesExtension().equals("jpg")){
                    for(ImageGridTile gridTile : newProjectLayout.getSelectedImages().getGridTiles()){
                        handoverTiles.add(new ImageGridTile(null, gridTile.getName(), gridTile.getImage(),
                                                                gridTile.getTileWidth(), gridTile.getTileHeight(),
                                                                true, true, true));
                    }

                }else{
                    handoverTiles = createGridTilesFromDir(selectedImagesFolder);
                }

                Main.changeToHighlightDetectionScene(handoverTiles, true);
            }
        }).start();
    }



    private ArrayList<ImageGridTile> createGridTilesFromDir(File directory){

        try{
            File[] images = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.toLowerCase().endsWith(".jpg")){
                        return true;
                    }
                    return false;
                }
            });


            if(images.length == 0){
                Main.hideLoadingDialog();
                Main.showFileReadingAlert("No images were found in the generated images directory.");
                return null;
            }

            Image[] imagesInDir = new Image[images.length];
            String[] imageLocations = new String[images.length];
            String[] imageNames = new String[images.length];


            for(int i = 0; i < imagesInDir.length; i++){
                Image currentImg = new Image("file:" + images[i].getAbsolutePath());

                imagesInDir[i] = currentImg;
                imageLocations[i] = images[i].getAbsolutePath();
                imageNames[i] = images[i].getName();
            }


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




    private File saveImagesAsJPEGs(){
        final String convertedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_jpegsForHLDetection";

        File convertedFolder = new File(convertedFolderLocation);

        if(!convertedFolder.exists()){ convertedFolder.mkdir(); }

        ImageGridTile[] gridTiles = newProjectLayout.getSelectedImages().getGridTiles();

        if(gridTiles[0].getName().toLowerCase().endsWith(".jpg")){
            return Main.currentImagesFolder;
        }

        HashSet<ImageGridTile> gridTileSet = new HashSet<>();
        for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

        Utils.BooleanHolder success = new Utils.BooleanHolder(true);
        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                BufferedImage jpgImg = fxImageToBufferedJPEG(tile.getImage());
                String tileNameNoExt = tile.getName().split("[.]")[0];
                File destination = new File(convertedFolder.getAbsolutePath()
                                                            + "/" + tileNameNoExt + ".jpg");

                try{
                    ImageIO.write(jpgImg, "jpg", destination);
                }catch (IOException e){
                    e.printStackTrace();
                    success.setB(false);
                }
            }
        });

        if(!success.isB()){
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("Attempted to convert images to JPEGs, " +
                                            "but an error occurred.");
            return null;
        }

        return convertedFolder;
    }




    private void moveSceneDomeLP(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Checking project resources...");

                if(!newProjectLayout.isResourcesSet()){
                    Main.hideLoadingDialog();
                    Main.showInputAlert("Please open the resources for this project " +
                                                    "using the 'Open Project Resources' button.");
                    return;
                }


                String imgFolderPath = newProjectLayout.getImgsFolder().getAbsolutePath();
                File imageParentDir = new File(imgFolderPath);

                if(!imageParentDir.exists() || !imageParentDir.isDirectory()){
                    Main.hideLoadingDialog();
                    Main.showFileReadingAlert("Couldn't access the image file folder. Check that it still  " +
                                            "exists and that its name hasn't changed.");
                    return;
                }

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
