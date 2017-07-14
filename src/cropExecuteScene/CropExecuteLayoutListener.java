package cropExecuteScene;

import guiComponents.ImageGridTile;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import main.Main;
import utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Created by Jed on 11-Jul-17.
 */
public class CropExecuteLayoutListener implements EventHandler<ActionEvent> {

    private CropExecuteLayout cropExecuteLayout;

    private static CropExecuteLayoutListener ourInstance = new CropExecuteLayoutListener();

    public static CropExecuteLayoutListener getInstance() {
        return ourInstance;
    }

    private CropExecuteLayoutListener() {}

    public void init(CropExecuteLayout cropExecuteLayout){
        this.cropExecuteLayout = cropExecuteLayout;
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("runFitterButton")){

                if(!checkFitterInputs()){ return; }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runFitter();
                    }
                }).start();

            }else if(source.getId().equals("browseFitterLocation")){
                browseFitterLocation();

            }else if(source.getId().equals("browseOutputLocation")){
                browseOutputLocation();

            }else if(source.getId().equals("backButton")){
                Main.backButtonPressed(cropExecuteLayout);

            }

        }else if(event.getSource() instanceof CheckBox){
            CheckBox source = (CheckBox) event.getSource();
            if(source.isSelected()){
                cropExecuteLayout.enableCrop();
            }else{
                cropExecuteLayout.disableCrop();
            }

        }else if(event.getSource() instanceof ComboBox){
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();
            ImageCropPane.Colour colour = source.getSelectionModel().getSelectedItem();
            cropExecuteLayout.setCropColour(colour);

        }else if(event.getSource() instanceof RadioButton){
            RadioButton source = (RadioButton) event.getSource();

            if(source.getId().equals("ptmButton")){
                cropExecuteLayout.setPTMOptions();

            }else if(source.getId().equals("hshButton")){
                cropExecuteLayout.setHSHOptions();
            }

        }
    }


    private boolean checkFitterInputs(){
        TextField fitterField = cropExecuteLayout.getFitterLocationField();
        TextField outputField = cropExecuteLayout.getOutputLocationField();

        if(Utils.haveEmptyField(fitterField, outputField)){
            Main.showInputAlert("Please select locations of the fitter program and output destination");
            return false;

        }else if(Utils.containsSpaces(fitterField, outputField)){
            Main.showInputAlert("Please ensure there are no spaces in the paths to the fitter and output file.");
            return false;
        }

        return true;
    }



    private void runFitter(){
        File lpFile = null;
        if(cropExecuteLayout.isUseCrop()){
            lpFile = cropAndCreateLPFile();
        }else if(!cropExecuteLayout.getImagesFormat().equals("jpg")){
            lpFile = convertImagesAndCreateLPFile();
        }else{
            lpFile = createLPFileJPEGNoCrop();
        }

        Main.showLoadingDialog("Running fitter...");

        String fitterLocation = cropExecuteLayout.getFitterLocation();
        String lpFileLocation = lpFile.getAbsolutePath();
        String destinationFileName = cropExecuteLayout.getOutputLocation();

        String fitterArgs = fitterLocation + " ";


        if(cropExecuteLayout.ptmSelected()){
            fitterArgs += "-i " + lpFileLocation;
            fitterArgs += " -o " + destinationFileName;

            if(cropExecuteLayout.ptmRGBSelected()){
                fitterArgs += " -f 0 ";
            }
            else if(cropExecuteLayout.ptmLRGBSelected()){
                fitterArgs += " -f 1 ";
            }

        }else if(cropExecuteLayout.hshSelected()){
            fitterArgs += lpFileLocation + " ";
            fitterArgs += String.valueOf(cropExecuteLayout.getHSHOrder()) + " ";
            fitterArgs += destinationFileName;
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(fitterArgs);


            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            // read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if(cropExecuteLayout.ptmSelected() && (!s.startsWith("Processing row"))) {
                    cropExecuteLayout.printToFitterOutput(s);
                }
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                if(!s.matches("\\d+")) {
                    cropExecuteLayout.printToFitterOutput(s);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("Error running fitter executable. See fitter output pane.");
        }

        Main.hideLoadingDialog();
    }



    private File createLPFileJPEGNoCrop(){
        return createNewLPFile(Main.currentAssemblyFolder.getAbsolutePath(),
                                "_default" + ".lp",
                                true, null, true);
    }





    private File convertImagesAndCreateLPFile(){
        Main.showLoadingDialog("Converting images...");
        final String convertedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_convertedJPEGS";
        File convertedFolder = new File(convertedFolderLocation);

        if(!convertedFolder.exists()){ convertedFolder.mkdir(); }

        HashSet<ImageGridTile> gridTileSet = lpImagesToHashSet();

        final Utils.BooleanHolder success = new Utils.BooleanHolder(true);

        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                BufferedImage jpgImg = fxImageToBufferedJPEG(tile.getImage());
                String tileNameNoExt = tile.getName().split("[.]")[0];
                File destination = new File(convertedFolder.getAbsolutePath() + "/" + tileNameNoExt + ".jpg");

                try{
                    ImageIO.write(jpgImg, "jpg", destination);
                }catch (IOException e){
                    e.printStackTrace();
                    success.setB(false);
                }
            }
        });
        Main.hideLoadingDialog();
        if(!success.isB()){
            Main.showFileReadingAlert("Error in writing converted jpegs to disk.");
            return null;
        }

        return createNewLPFile(convertedFolderLocation, "_converted.lp", false, ".jpg", false);

    }



    private File cropAndCreateLPFile(){
        Main.showLoadingDialog("Cropping images...");
        final String croppedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_croppedFiles";
        File croppedFolder = new File(croppedFolderLocation);

        if(!croppedFolder.exists()){ croppedFolder.mkdir(); }


        int[] cropParams = cropExecuteLayout.getCropParams();


        HashSet<ImageGridTile> gridTileSet = lpImagesToHashSet();

        final Utils.BooleanHolder areJPEGS = new Utils.BooleanHolder(false);
        ImageGridTile firstTile = cropExecuteLayout.getLpImagesGrid().getGridTiles()[0];
        if(firstTile.getName().endsWith(".jpg")){areJPEGS.setB(true);}

        final Utils.BooleanHolder success = new Utils.BooleanHolder(true);
        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                Image croppedImage = Utils.cropImage(tile.getImage(), cropParams[0],
                        cropParams[1], cropParams[2], cropParams[3]);
                BufferedImage newImg = fxImageToBufferedJPEG(croppedImage);

                File destination;
                if(areJPEGS.isB()) {
                    destination = new File(croppedFolder.getAbsolutePath() + "/" + tile.getName());
                }else{
                    destination = new File(croppedFolder.getAbsolutePath() + "/" +
                                                        tile.getName().split("[.]")[0] + ".jpg");
                }
                try {
                    ImageIO.write(newImg, "jpg", destination);
                }catch(IOException e){
                    e.printStackTrace();
                    success.setB(false);
                    return;
                }
            }
        });
        Main.hideLoadingDialog();
        if(!success.isB()){
            Main.showFileReadingAlert("Error in writing cropped files to disk.");
            return null;
        }

        if(areJPEGS.isB()) {
            return createNewLPFile(croppedFolderLocation, "_cropped.lp", true, null, false);
        }else{
            return createNewLPFile(croppedFolderLocation, "_cropped.lp", false, ".jpg", false);
        }
    }


    private File createNewLPFile(String parentDirLoc, String lpFileName, boolean useOriginalImgExt, String newExt, boolean uncroppedJPEGs){
        File newLPFile = new File(parentDirLoc + "/" +
                Main.currentRTIProject.getName() + lpFileName);

        Main.showLoadingDialog("Creating new LP file...");

        BufferedWriter writer = null;
        try {
            HashMap<String, Utils.Vector3f> originalLPData = Utils.readLPFile(Main.currentLPFile);
            if (!newLPFile.exists()) { newLPFile.createNewFile(); }

            writer = new BufferedWriter(new FileWriter(newLPFile));
            writer.write(String.valueOf(originalLPData.size()) + System.lineSeparator());

            File originalImageFile;
            String name, x, y, z;
            for(String key : originalLPData.keySet()){
                originalImageFile = new File(key);

                if(uncroppedJPEGs){
                  name = Main.currentImagesFolder.getAbsolutePath() + "/" + originalImageFile.getName();

                } else if(useOriginalImgExt){
                    name = parentDirLoc + "/" + originalImageFile.getName();

                } else{
                    name = parentDirLoc + "/" + originalImageFile.getName().split("[.]")[0] + newExt;
                }

                x = String.valueOf(originalLPData.get(key).getX());
                y = String.valueOf(originalLPData.get(key).getY());
                z = String.valueOf(originalLPData.get(key).getZ());

                writer.write(name + " " + x + " " + y + " " + z + System.lineSeparator());
            }

        }catch(IOException e){
            e.printStackTrace();
            Main.showFileReadingAlert("Error creating new .lp file.");
            return null;
        }catch(Utils.LPException e){
            e.printStackTrace();
            Main.showFileReadingAlert("Error reading original LP file: " + e.getMessage());
        }finally{
            Main.hideLoadingDialog();
            if(writer != null){
                try {
                    writer.close();
                }catch(IOException e){e.printStackTrace();}
            }
        }

        return newLPFile;
    }





    private HashSet<ImageGridTile> lpImagesToHashSet(){
        HashSet<ImageGridTile> gridTileSet = new HashSet<>();
        ImageGridTile[] gridTiles = cropExecuteLayout.getLpImagesGrid().getGridTiles();
        for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

        return gridTileSet;
    }




    private BufferedImage fxImageToBufferedJPEG(Image image){
        BufferedImage bufImg = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < bufImg.getWidth(); x++){
            for(int y = 0; y < bufImg.getHeight(); y++){
                newImg.setRGB(x, y, bufImg.getRGB(x, y));
            }
        }

        return newImg;
    }




    private void browseFitterLocation(){
        Main.fileChooser.getExtensionFilters().clear();
        if(cropExecuteLayout.ptmSelected()){
            Main.fileChooser.setTitle("Select PTM Fitter");
        }else{
            Main.fileChooser.setTitle("Select HSH Fitter");
        }
        File fitter = Main.fileChooser.showOpenDialog(Main.primaryStage);

        if(fitter != null){
            cropExecuteLayout.setFitterLocation(fitter.getAbsolutePath());
        }
    }


    private void browseOutputLocation(){
        Main.fileChooser.getExtensionFilters().clear();
        if(cropExecuteLayout.ptmSelected()){
            Main.fileChooser.setTitle("Select Destination For PTM");
            Main.fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Poly Texture Map File (.ptm)",
                                                                ".ptm"));
        }else{
            Main.fileChooser.setTitle("Select Destination For HSH");
            Main.fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Reflectance Transformation Imaging File (.rti)",
                                                            ".rti"));
        }

        File dest = Main.fileChooser.showSaveDialog(Main.primaryStage);

        if(dest != null){
            cropExecuteLayout.setOutputLocation(dest.getAbsolutePath());
        }
    }
}
