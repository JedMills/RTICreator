package cropExecuteScene;

import guiComponents.ImageCropPane;
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
 * Listens to events from the various widgets in the {@link CropExecuteLayout}.
 *
 * @author Jed Mills
 */
public class CropExecuteLayoutListener implements EventHandler<ActionEvent> {

    /** The CropExecuteLayout that this listener listens to */
    private CropExecuteLayout cropExecuteLayout;

    /** The singleton instance of this class */
    private static CropExecuteLayoutListener ourInstance = new CropExecuteLayoutListener();

    /**
     * @return {@link CropExecuteLayoutListener#ourInstance}
     */
    public static CropExecuteLayoutListener getInstance() {
        return ourInstance;
    }



    /**
     * Creates a new CropExecuteLayoutListener
     */
    private CropExecuteLayoutListener() {}




    /**
     * Sets the CropExecuteLayout that this CropExecuteLayoutListener should listen to
     *
     * @param cropExecuteLayout layout to listen to events from
     */
    public void init(CropExecuteLayout cropExecuteLayout){
        this.cropExecuteLayout = cropExecuteLayout;
    }




    /**
     * Handles events from the CropExecuteLayout that this instance is currently listening to.
     *
     * @param event the event that this CropExecuteLayoutListener will respond to
     */
    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("runFitterButton")){
                //check the user has chosen a fitter location and output file name
                if(!checkFitterInputs()){ return; }

                //if they have, run the fitter!
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runFitter();
                    }
                }).start();

            }else if(source.getId().equals("browseFitterLocation")){
                //open the file chooser for finding the fitter to use
                browseFitterLocation();

            }else if(source.getId().equals("browseOutputLocation")){
                //open the file chooser for finding the output file destination
                browseOutputLocation();

            }else if(source.getId().equals("backButton")){
                //move back to the relevant scene for this project type
                Main.backButtonPressed(cropExecuteLayout, cropExecuteLayout.getLpImagesGrid().getGridTiles());
            }

        }else if(event.getSource() instanceof CheckBox){
            //the oly check box is the crop box
            CheckBox source = (CheckBox) event.getSource();
            if(source.isSelected()){
                cropExecuteLayout.enableCrop();
            }else{
                cropExecuteLayout.disableCrop();
            }

        }else if(event.getSource() instanceof ComboBox){
            //the only combo box id the crop colour box
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();
            ImageCropPane.Colour colour = source.getSelectionModel().getSelectedItem();
            cropExecuteLayout.setCropColour(colour);

        }else if(event.getSource() instanceof RadioButton){
            //radio buttons are for fitter options in the fitter options pane
            RadioButton source = (RadioButton) event.getSource();

            if(source.getId().equals("ptmButton")){
                cropExecuteLayout.setPTMOptions();

            }else if(source.getId().equals("hshButton")){
                cropExecuteLayout.setHSHOptions();
            }

        }
    }




    /**
     * Checks that the user has selected a file for the fitter location and output file for the fitter.
     * If they have not,the method shows a dialog telling them to do and returns false.
     *
     * @return  whether the user has selected a fitter and output file location
     */
    private boolean checkFitterInputs(){
        TextField fitterField = cropExecuteLayout.getFitterLocationField();
        TextField outputField = cropExecuteLayout.getOutputLocationField();

        if(Utils.haveEmptyField(fitterField, outputField)) {
            //the user hasn't put both in
            Main.showInputAlert("Please select locations of the fitter program and output destination");
            return false;

        } else if(Utils.containsSpaces(fitterField, outputField)){
            //the fitters give errors if they detect any spaces anywhere in paths, so we'll deal with it here
            Main.showInputAlert("Please ensure there are no spaces in the paths to the fitter and output file.");
            return false;
        }

        return true;
    }




    /**
     * Runs the selected fitter program on the images in the {@link CropExecuteLayout#lpImagesGrid} by creating a new
     * LP file for them, cropping them if the crop is selected, and running the fitter using the new LP file. Prints
     * the output of th e fitter to the {@link CropExecuteLayout#fitterOutputArea}.Deals with all errors that
     * could occur in this method through the use of an error dialog.
     */
    private void runFitter(){
        File lpFile = null;

        if(cropExecuteLayout.isUseCrop()){
            //if using crop, crop the files and create the lp file for them
            lpFile = cropAndCreateLPFile();

        }else if(!cropExecuteLayout.getImagesFormat().equals("jpg")){
            //if not cropping, bu the images aren't jpg, we need to convert them to jpegs for the fitters
            //so we'll convert them to jpg and create a new lp file for them
            lpFile = convertImagesAndCreateLPFile();

        }else{
            //otherwise, we can create an lp file straight from the images
            lpFile = createLPFileJPEGNoCrop();
        }

        Main.showLoadingDialog("Running fitter...");

        //location of the fitter and the  lp file to use
        String fitterLocation = cropExecuteLayout.getFitterLocation();
        String lpFileLocation = lpFile.getAbsolutePath();
        String destinationFileName = cropExecuteLayout.getOutputLocation();

        String fitterArgs = fitterLocation + " ";

        if(cropExecuteLayout.ptmSelected()){
            /* the ptm fitter has command-line args of :
                    <fitter location> -i <lp file location> -o <destination filepath> -f <rgb / lrgb>
            */
            fitterArgs += "-i " + lpFileLocation;
            fitterArgs += " -o " + destinationFileName;

            if(cropExecuteLayout.ptmRGBSelected()){
                fitterArgs += " -f 0 ";
            }
            else if(cropExecuteLayout.ptmLRGBSelected()){
                fitterArgs += " -f 1 ";
            }

        }else if(cropExecuteLayout.hshSelected()){
            /* the hsh has command-line args of :
                    <fitter location> <lp file location> <HSH order> <destination filepath>
            */
            fitterArgs += lpFileLocation + " ";
            fitterArgs += String.valueOf(cropExecuteLayout.getHSHOrder()) + " ";
            fitterArgs += destinationFileName;
        }

        try {
            //run the executable with the command-line arguments
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(fitterArgs);

            //reads to feed the output from the fitters to the text area in the crop execute layout
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




    /**
     * Create an LP file using the image locations of the image sin the {@link CropExecuteLayout#lpImagesGrid}.
     *
     * @return the new.lp file
     */
    private File createLPFileJPEGNoCrop(){
        return createNewLPFile(Main.currentAssemblyFolder.getAbsolutePath(),
                                "_default.lp",
                                true, null, true);
    }




    /**
     * Converts the images in the {@link CropExecuteLayout#lpImagesGrid} to jpgs, saves them in a new folder, and
     * creates an lp file for them. The folder with them in will be in the {@link Main#currentAssemblyFolder}.
     *
     * @return  the lp file for the newly converted jpegs
     */
    private File convertImagesAndCreateLPFile(){
        Main.showLoadingDialog("Converting images...");

        //the location of the folder for the converted jpegs to go in
        final String convertedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_convertedJPEGS";
        File convertedFolder = new File(convertedFolderLocation);

        if(!convertedFolder.exists()){ convertedFolder.mkdir(); }

        //this has set is so we can easily parallelise the converting of these images
        HashSet<ImageGridTile> gridTileSet = lpImagesToHashSet();

        final Utils.BooleanHolder success = new Utils.BooleanHolder(true);

        //convert the images in parallel
        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                //convert it to a jpeg, and replace its extension as .jpg
                BufferedImage jpgImg = Utils.fxImageToBufferedJPEG(tile.getImage());
                String tileNameNoExt = tile.getName().split("[.]")[0];
                File destination = new File(convertedFolder.getAbsolutePath() + "/" + tileNameNoExt + ".jpg");

                //write the file to the disk
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

        //create the new lp file for these converted images
        return createNewLPFile(convertedFolderLocation, "_converted.lp",
                false, ".jpg", false);

    }




    /**
     * Crops the images in the {@link CropExecuteLayout#lpImagesGrid} to jpgs, saves them in a new folder, and
     * creates an lp file for them. The folder with them in will be in the {@link Main#currentAssemblyFolder}.
     *
     * @return  the lp file for the newly cropped jpegs
     */
    private File cropAndCreateLPFile(){
        Main.showLoadingDialog("Cropping images...");

        //the location of the folder for the cropped jpegs to go in
        final String croppedFolderLocation = Main.currentAssemblyFolder.getAbsolutePath() +
                "/" + Main.currentRTIProject.getName() + "_croppedFiles";
        File croppedFolder = new File(croppedFolderLocation);

        if(!croppedFolder.exists()){ croppedFolder.mkdir(); }


        int[] cropParams = cropExecuteLayout.getCropParams();

        //this has set is so we can easily parallelise the converting of these images
        HashSet<ImageGridTile> gridTileSet = lpImagesToHashSet();

        //if they're already jpegs, we don't need to change the file extension as well as cropping
        final Utils.BooleanHolder areJPEGS = new Utils.BooleanHolder(false);

        ImageGridTile firstTile = cropExecuteLayout.getLpImagesGrid().getGridTiles()[0];

        if(firstTile.getName().endsWith(".jpg")){areJPEGS.setB(true);}

        //crop all the image sin parallel
        final Utils.BooleanHolder success = new Utils.BooleanHolder(true);
        gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
            @Override
            public void accept(ImageGridTile tile) {
                Image croppedImage = Utils.cropImage(tile.getImage(), cropParams[0],
                        cropParams[1], cropParams[2], cropParams[3]);
                BufferedImage newImg = Utils.fxImageToBufferedJPEG(croppedImage);

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

        //make a new lp file for the images, if the old images were jpegs, we don'tneed to give these
        //ones a new file extension
        if(areJPEGS.isB()) {
            return createNewLPFile(croppedFolderLocation, "_cropped.lp",
                    true, null, false);
        }else{
            return createNewLPFile(croppedFolderLocation, "_cropped.lp",
                    false, ".jpg", false);
        }
    }




    /**
     * Creates a new .lp file in the location given by parentDirLoc, with the name given by lpFileName. Uses the
     * {@link Main#currentLPFile} as the source for the  lp data.
     *
     * @param parentDirLoc          location to write the .lp file to
     * @param lpFileName            name of the .lp file to write
     * @param useOriginalImgExt     whether to use the file extension of the image sin the original .lp file
     * @param newExt                file extension of the new images
     * @param uncroppedJPEGs        if the images ar uncropped jpegs, this process is easier
     * @return                      the newly created lp file
     */
    private File createNewLPFile(String parentDirLoc, String lpFileName, boolean useOriginalImgExt,
                                 String newExt, boolean uncroppedJPEGs){
        //the location for the new lp file
        File newLPFile = new File(parentDirLoc + "/" +
                Main.currentRTIProject.getName() + lpFileName);

        Main.showLoadingDialog("Creating new LP file...");

        BufferedWriter writer = null;
        try {
            //get the lp data from the original lp file
            HashMap<String, Utils.Vector3f> originalLPData = Utils.readLPFile(Main.currentLPFile);
            if (!newLPFile.exists()) { newLPFile.createNewFile(); }

            //write the number of images on the first ine fothe lp file
            writer = new BufferedWriter(new FileWriter(newLPFile));
            writer.write(String.valueOf(originalLPData.size()) + System.lineSeparator());

            //go through the images in the original lp file and write them with their new extension and
            //location in the new lp file
            File originalImageFile;
            String name, x, y, z;
            for(String key : originalLPData.keySet()){
                originalImageFile = new File(key);

                //if the jpegs are uncropped, the name and location of the images will be the same as
                //in the original lp file, otherwise,the location and/or extension needs tobe changed
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
                //write the line tothe new lp file
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




    /**
     * Creates a hash set from the ImageGridTiles in the {@link CropExecuteLayout#lpImagesGrid}, which
     * canbe iterated over and allows for easy parallelisation.
     *
     * @return  the images as a hashset
     */
    private HashSet<ImageGridTile> lpImagesToHashSet(){
        //create a new hash set, add the images, and return it, bish bash bosh
        HashSet<ImageGridTile> gridTileSet = new HashSet<>();
        ImageGridTile[] gridTiles = cropExecuteLayout.getLpImagesGrid().getGridTiles();
        for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

        return gridTileSet;
    }




    /**
     * Opens the file chooser dialog, with the relevant title for the type of RTI fitter selected. Sets the
     * fitter location in the {@link CropExecuteLayout} to the file selected.
     */
    private void browseFitterLocation(){
        Main.fileChooser.getExtensionFilters().clear();
        if(cropExecuteLayout.ptmSelected()){
            Main.fileChooser.setTitle("Select PTM Fitter");
        }else{
            Main.fileChooser.setTitle("Select HSH Fitter");
        }
        File fitter = Main.fileChooser.showOpenDialog(Main.primaryStage);

        //if  the user didn't choose a file, it will be null
        if(fitter != null){
            cropExecuteLayout.setFitterLocation(fitter.getAbsolutePath());
        }
    }




    /**
     * Opens he file chooser dialog so the user can choose where to save the .rti/.ptm file. Will add the relevant
     * file extension to the choose depending on whether the {@link CropExecuteLayout#ptmButton} or the
     * {@link CropExecuteLayout#hshButton} is selected.
     */
    private void browseOutputLocation(){
        Main.fileChooser.getExtensionFilters().clear();

        //set the right extension filter for /ptm or .rti files
        if(cropExecuteLayout.ptmSelected()){
            Main.fileChooser.setTitle("Select Destination For PTM");
            Main.fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Polynomial Texture Map File (.ptm)",
                                                                ".ptm"));
        }else{
            Main.fileChooser.setTitle("Select Destination For HSH");
            Main.fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Reflectance Transformation Imaging File (.rti)",
                                                            ".rti"));
        }

        File dest = Main.fileChooser.showSaveDialog(Main.primaryStage);

        //the file will be null if the user didn't end up choosing anything
        if(dest != null){
            cropExecuteLayout.setOutputLocation(dest.getAbsolutePath());
        }
    }
}
