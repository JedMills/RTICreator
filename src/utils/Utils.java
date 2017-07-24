package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Various utility functions used throughout the app.
 *
 * @author Jed
 */
public class Utils {


    /**
     * Returns true if value is in the array, false if not.
     *
     * @param string    value to find
     * @param strings   array to find it in
     * @return          whether value is in the array
     */
    public static boolean checkIn(String string, String[] strings){
        for(String s : strings) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if value is in the array, false if not.
     *
     * @param string    value to find
     * @param strings   array to find it in
     * @return          whether value is in the array
     */
    public static boolean checkIn(String string, ArrayList<String> strings){
        for(String s : strings) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns true if value is in the set, false if not.
     *
     * @param string    value to find
     * @param strings   array to find it in
     * @return          whether value is in the array
     */
    public static boolean checkIn(String string, Set<String> strings){
        for(String s : strings){
            if(s.equals(string)){
                return true;
            }
        }
        return false;
    }


    /**
     * Crops a JavaFX image with top left corner at [x, y] and the given width a height.
     *
     * @param image     image to crop
     * @param x         top left x pos of the crop rectangle
     * @param y         top left y pos of the crop rectangle
     * @param width     width of the crop rectangle
     * @param height    height of the crop rectangle
     * @return          the cropped image
     */
    public static Image cropImage(Image image, int x, int y, int width, int height){
        PixelReader reader = image.getPixelReader();
        return new WritableImage(reader, x, y, width, height);
    }


    /**
     * Thrown when three's an error parsing an .lp file.
     */
    public static class LPException extends Exception{

        /**
         * Creates a new LPException.
         *
         * @param message reason why this exception was thrown
         */
        public LPException(String message) {
            super(message);
        }
    }


    /**
     * Reads an LP file and converts all the information into a mpa of image names and the light vector for that image.
     *
     * @param lpFile            file to read
     * @return                  map of images and their light vector
     * @throws IOException      if there's an error accessing the lp file.
     * @throws LPException
     */
    public static HashMap<String, Vector3f> readLPFile(File lpFile) throws IOException, LPException{
        //check the file actually exists and is an  .lp file
        if(lpFile.isDirectory()){throw new RuntimeException("LP file is directory.");}
        if(!lpFile.getName().endsWith(".lp")){throw new RuntimeException("LP file does not end with '.lp'.");}

        BufferedReader reader = new BufferedReader(new FileReader(lpFile));
        String line = reader.readLine();
        int currentLine = 1;

        //check the first line contains a number for the number of images
        int numImages;
        try{
            numImages = Integer.parseInt(line);
        }catch(NumberFormatException e){
            throw new LPException("File did not contain number of images on line 1.");
        }

        //will store all the image names an light positions
        HashMap<String, Vector3f> lpData = new HashMap<>();
        int numLPData = 0;
        line = reader.readLine();
        currentLine ++;

        //read through the rest of the lines and get the file name, and light vector from them
        String[] lineItems;
        String fileName;
        float lightX, lightY, lightZ;
        while(numLPData < numImages){
            while(line.equals("")){
                currentLine ++;
                line = reader.readLine();
            }

            lineItems = line.split("\\s+");

            //if there's a space in the directory path, this will mean more than 3 items on the line, and
            //we don't want spaces anywhere in the program as the fitters can't deal with them
            if(lineItems.length > 4){
                throw new LPException("Error parsing lp data on line: " + currentLine + ", check there are no " +
                                        "spaces in the specified file paths or names.");
            }

            try{
                //get the info
                fileName = lineItems[0];
                lightX = Float.parseFloat(lineItems[1]);
                lightY = Float.parseFloat(lineItems[2]);
                lightZ = Float.parseFloat(lineItems[3]);

            }catch(NumberFormatException|IndexOutOfBoundsException e){
                e.printStackTrace();
                throw new LPException("Error parsing lp data on line: " + currentLine);
            }

            //put the info in the map
            lpData.put(fileName, new Vector3f(lightX, lightY, lightZ));
            numLPData ++;

            line = reader.readLine();
            currentLine ++;

            if(line == null){break;}
        }

        reader.close();
        return lpData;
    }




    /**
     * Returns true if there is a space in any of the strings given.
     *
     * @param strings   strings to check
     * @return          wether any of them have a space
     */
    public static boolean containsSpaces(String... strings){
        for(String s : strings){
            if(s.contains(" ")){return false;}
        }
        return false;
    }




    /**
     * Returns true if there is a space in any of the text fields given.
     *
     * @param textFields fields to check
     * @return           whether any of them have a space
     */
    public static boolean containsSpaces(TextField... textFields){
        for(TextField field : textFields){
            if(field.getText().contains(" ")){
                return true;
            }
        }
        return false;
    }




    /**
     * Returns true if nay of the text fields given are blank.
     *
     * @param textFields    the fields to check
     * @return              if any of them are blank
     */
    public static boolean haveEmptyField(TextField... textFields){
        for(TextField field : textFields){
            if(field.getText().equals("")){
                return true;
            }
        }
        return false;
    }




    /**
     * Disables all the nodes given, so they can't be clicked or typed in.
     *
     * @param nodes     nodes to disable
     */
    public static void disableNodes(Node... nodes){
        for(Node node : nodes){node.setDisable(true);}
    }




    /**
     * Enables all the nodes given, so they can be clicked or typed in.
     *
     * @param nodes     nodes to enable
     */
    public static void enableNodes(Node... nodes){
        for(Node node : nodes){node.setDisable(false);}
    }




    /**
     * Holds a boolean value, useful for when you want to pass a boolean to an anonymous class that must
     * take a final variable.
     */
    public static class BooleanHolder{

        /** The boolean value this holds*/
        private boolean value;

        /**
         * Creates a new BooleanHolder with the given boolean to...hold.
         *
         * @param value boolean this holds
         */
        public BooleanHolder(boolean value) {
            this.value = value;
        }

        /**
         * @return {@link BooleanHolder#value}
         */
        public boolean isTrue() {
            return value;
        }

        /**
         * @param value to set the {@link BooleanHolder#value}
         */
        public void setTrue(boolean value) {
            this.value = value;
        }
    }




    /**
     * Creates a spacer pane that always grows to the maximum possible width.
     *
     * @return  a brand new spacer
     */
    public static Pane createSpacer(){
        Pane spacer = new Pane();
        //make it grow to the max possible width always
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(1, 1);
        return spacer;
    }




    /**
     * Turns a JavaFX image int oa buffered image. Useful for te highlight detection, which was all originally
     * written for BufferedImages.
     *
     * @param image     image to convert
     * @return          new buferred image
     */
    public static BufferedImage fxImageToBufferedJPEG(Image image){
        BufferedImage bufImg = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        //basically just copy all the pixels over
        for(int x = 0; x < bufImg.getWidth(); x++){
            for(int y = 0; y < bufImg.getHeight(); y++){
                newImg.setRGB(x, y, bufImg.getRGB(x, y));
            }
        }

        return newImg;
    }




    /**
     * Gets the text after the last dor of a file name
     *
     * @param fileName      name of the file
     * @return              extension at the end of the file
     */
    public static String getFileExtension(String fileName){
        String[] parts = fileName.split("[.]");
        return parts[parts.length - 1];
    }



    /**
     * Check if  a file exists in the given parent folder. This method *is* case sensitive, unlike the java.io
     * method for files, which is important when writing lp files that need to be case sensitive.
     *
     * @param dir           parent dir to find the file in
     * @param filename      name of the fle to find
     * @return              whether there's a file with the given name in the parent dir
     */
    public static boolean fileExists(File dir, String filename){
        String[] files = dir.list();
        for(String file : files)
            if(file.equals(filename))
                return true;
        return false;
    }


    /**
     * Uses the Java Advanced Imaging (JAI) library to read unusual image files such as .tif and .bmps.
     *
     * @param imagePath     path of the unusual image to read
     * @return              the unusual image file as a JavaFx image
     */
    public static Image readUnusualImage(String imagePath){
        Image image = null;
        try {
            //the extra JAI libraries mean we can read these unusual images
            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            image = SwingFXUtils.toFXImage(bufferedImage, null);
        }catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }




    /**
     * Holds an integer value, useful for when you want to pass an integer to an anonymous class that must
     * take a final variable.
     */
    public static class IntHolder{

        /** The int value */
        private int value;

        /**
         * Creates a new IntHolder with the given int.
         *
         * @param value int to hold
         */
        public IntHolder(int value) {
            this.value = value;
        }

        /**
         * @return {@link IntHolder#value}
         */
        public int getValue() {
            return value;
        }

        /**
         * @param value value to set the {@link IntHolder#value}
         */
        public void setValue(int value) {
            this.value = value;
        }

        /**
         * Increment the {@link IntHolder#value}
         */
        public void pp(){
            value ++;
        }

        /**
         * Decrement the {@link IntHolder#value}
         */
        public void mm(){
            value --;
        }
    }


    /**
     * Links a given button to a text field so that when the button is pressed, a file or directory chooser is opened,
     * and the path to the file or directory that the user chooses is put in the given text field.
     *
     * @param title         title to set the file/directory chooser to
     * @param button        button to open the directory chooser
     * @param textField     text field to put the path into
     * @param stage         main window of the app
     * @param folder        false for a file chooser, true for a directory chooser
     */
    public static void linkDirButtonToTextField(String title, Button button, TextField textField,
                                                Stage stage, boolean folder){
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = null;
                if(folder) {
                    Main.directoryChooser.setTitle(title);
                    file = Main.directoryChooser.showDialog(stage);
                }else{
                    Main.fileChooser.setTitle(title);
                    file = Main.fileChooser.showOpenDialog(stage);
                }

                if(file == null){return;}

                if(Utils.containsSpaces(file.getAbsolutePath())){
                    Main.showInputAlert("Please ensure there are no spaces in directory or file names.");
                    return;
                }

                textField.setText(file.getAbsolutePath());

            }
        });
    }


    /**
     * Links a given button to a text field so that when the button is pressed, a file or directory chooser is opened,
     * and the path to the file or directory that the user chooses is put in the given text field. Also sets a file
     * extension filter on the file chooser to look for certain kinds of files. The extension filter for looking for
     * jpegs, for example, would be "*.jpg".
     *
     * @param title         title to set the file/directory chooser to
     * @param button        button to open the directory chooser
     * @param textField     text field to put the path into
     * @param stage         main window of the app
     * @param extDesc       description of the file extension
     * @param fileExt       extension to filter with the chooser
     */
    public static void linkDirButtonToTextField(String title, Button button, TextField textField, Stage stage,
                                                            String extDesc, String fileExt){
        Main.fileChooser.getExtensionFilters().clear();
        Main.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extDesc, fileExt));
        linkDirButtonToTextField(title, button, textField, stage, false);
    }





    /**
     * All the 3D vectors needed in this program are from here!
     *
     * Created by jed on 16/05/17.
     */
    public static class Vector3f{
        /**The x component*/
        public float x;
        /**The y component*/
        public float y;
        /**The z component*/
        public float z;

        /**
         *
         * @param x     for the x component of the vector
         * @param y     for the y component of the vector
         * @param z     for the z component of the vector
         */
        public Vector3f(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * @return x component of the vector
         */
        public float getX() {
            return x;
        }

        /**
         * @param x set the x component of the vector
         */
        public void setX(float x) {
            this.x = x;
        }

        /**
         * @return y component of the vector
         */
        public float getY() {
            return y;
        }

        /**
         * @param y set the y component of the vector
         */
        public void setY(float y) {
            this.y = y;
        }

        /**
         * @return z component of the vector
         */
        public float getZ() {
            return z;
        }

        /**
         * @param z set the z component of the vector
         */
        public void setZ(float z) {
            this.z = z;
        }

        /**
         * @return  a new vector with normalised lengths
         */
        public Vector3f normalise(){
            Vector3f v = new Vector3f(0f, 0f, 0f);
            float length = (float) Math.sqrt(x*x + y*y + z*z);
            if(length != 0){
                v.x = x / length;
                v.y = y / length;
                v.z = z / length;
            }
            return v;
        }

        public float get(int i){
            if(i == 0){return x;}
            else if(i == 1){return y;}
            else if(i == 2){return z;}

            return 0;
        }

        public float dot(Vector3f v){
            return (x * v.x) + (y * v.y) + (z * v.z);
        }

        public Vector3f multiply(float a){
            return new Vector3f(this.x * a, this.y * a, this.z * a);
        }

        public float length(){return (float) Math.pow(x*x + y*y + z*z, 0.5);}

        public Vector3f add(Vector3f vec){
            return new Vector3f(x + vec.x, y + vec.y, z + vec.z);
        }

        public Vector3f minus(Vector3f vec){
            return new Vector3f(x - vec.x, y - vec.y, z - vec.z);
        }
    }

}
