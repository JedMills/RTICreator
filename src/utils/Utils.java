package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import main.ProjectType;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jed on 09-Jul-17.
 */
public class Utils {


    public static boolean checkIn(ProjectType projectType, ProjectType[] projectTypes){
        for(ProjectType type : projectTypes) {
            if (type.equals(projectType)) {
                return true;
            }
        }
        return false;
    }


    public static boolean checkIn(String string, String[] strings){
        for(String s : strings) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIn(String string, ArrayList<String> strings){
        for(String s : strings) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }



    public static Image cropImage(Image image, int x, int y, int width, int height){
        PixelReader reader = image.getPixelReader();
        WritableImage croppedImage = new WritableImage(reader, x, y, width, height);
        return croppedImage;
    }




    public static class LPException extends Exception{
        public LPException(String message) {
            super(message);
        }
    }


    public static HashMap<String, Vector3f> readLPFile(File lpFile) throws IOException, LPException{
        if(lpFile.isDirectory()){throw new RuntimeException("LP file is directory.");}
        if(!lpFile.getName().endsWith(".lp")){throw new RuntimeException("LP file does not end with '.lp'.");}

        BufferedReader reader = new BufferedReader(new FileReader(lpFile));
        String line = reader.readLine();
        int currentLine = 1;


        int numImages;
        try{
            numImages = Integer.parseInt(line);
        }catch(NumberFormatException e){
            throw new LPException("File did not contain number of images on line 1.");
        }

        HashMap<String, Vector3f> lpData = new HashMap<>();
        int numLPData = 0;
        line = reader.readLine();
        currentLine ++;

        String[] lineItems;
        String fileName;
        float lightX, lightY, lightZ;
        while(numLPData < numImages){
            while(line.equals("")){
                currentLine ++;
                line = reader.readLine();
            }


            lineItems = line.split("\\s+");

            try{
                fileName = lineItems[0];
                lightX = Float.parseFloat(lineItems[1]);
                lightY = Float.parseFloat(lineItems[2]);
                lightZ = Float.parseFloat(lineItems[3]);
            }catch(NumberFormatException|IndexOutOfBoundsException e){
                e.printStackTrace();
                throw new LPException("Error parsing lp data on line: " + currentLine);
            }

            lpData.put(fileName, new Vector3f(lightX, lightY, lightZ));
            numLPData ++;

            line = reader.readLine();
            currentLine ++;

            if(line == null){break;}
        }

        reader.close();
        return lpData;
    }



    public static boolean containsSpaces(String... strings){
        for(String s : strings){
            if(s.contains(" ")){return false;}
        }
        return false;
    }


    public static boolean containsSpaces(TextField... textFields){
        for(TextField field : textFields){
            if(field.getText().contains(" ")){
                return true;
            }
        }
        return false;
    }


    public static boolean haveEmptyField(TextField... textFields){
        for(TextField field : textFields){
            if(field.getText().equals("")){
                return true;
            }
        }
        return false;
    }


    public static void disableNodes(Node... nodes){
        for(Node node : nodes){node.setDisable(true);}
    }

    public static void enableNodes(Node... nodes){
        for(Node node : nodes){node.setDisable(false);}
    }


    public static class BooleanHolder{
        private boolean b;

        public BooleanHolder(boolean b) {
            this.b = b;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }


    public static Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(1, 1);
        return spacer;
    }

    public static Pane createSpacer(int maxWidth){
        Pane spacer = createSpacer();
        spacer.setMaxWidth(maxWidth);
        return spacer;
    }


    public static BufferedImage fxImageToBufferedJPEG(Image image){
        BufferedImage bufImg = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < bufImg.getWidth(); x++){
            for(int y = 0; y < bufImg.getHeight(); y++){
                newImg.setRGB(x, y, bufImg.getRGB(x, y));
            }
        }

        return newImg;
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
