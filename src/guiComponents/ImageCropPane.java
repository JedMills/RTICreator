package guiComponents;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * Created by Jed on 12-Jul-17.
 */
public class ImageCropPane extends BorderPane{

    private Group imageLayer;
    private ImageView imageView;
    private Image image;
    private RubberBandSelection bandSelection;
    private boolean cropActive = false;

    private TextField widthField;
    private TextField heightField;
    private boolean hasTextFields;


    public enum Colour{ WHITE   ("White"),
                        GREY    ("Grey"),
                        BLACK   ("Black"),
                        RED     ("Red"),
                        GREEN   ("Green"),
                        BLUE    ("Blue");

        private String asString;

        private Colour(String name){
            asString = name;
        }

        @Override
        public String toString(){
            return asString;
        }
    }

    public ImageCropPane() {
        setUp();
        hasTextFields = false;
    }




    public ImageCropPane(TextField widthField, TextField heightField){
        this.widthField = widthField;
        this.heightField = heightField;
        hasTextFields = true;
        setUp();
    }




    private void setUp(){
        imageLayer = new Group();
        imageView = new ImageView();
        imageLayer.getChildren().add(imageView);
        bandSelection = new RubberBandSelection(this, imageLayer);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        setCenter(imageLayer);
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        imageView.setImage(image);
    }

    public void clearImage(){
        this.image = null;
        imageView.setImage(null);
    }

    public void setCropActive(boolean cropActive) {
        this.cropActive = cropActive;

        if(cropActive){
            addCropSelection();
        }else{
            removeCropSelection();
        }
    }


    private void addCropSelection(){
        bandSelection.group.getChildren().addAll(bandSelection.rect,
                                                bandSelection.topLeftHandle,
                                                bandSelection.bottomRightHandle);

        bandSelection.topLeftHandle.setX(imageView.getBoundsInParent().getWidth() / 4);
        bandSelection.topLeftHandle.setY(imageView.getBoundsInParent().getHeight() / 4);

        bandSelection.bottomRightHandle.setX(3 * imageView.getBoundsInParent().getWidth() / 4);
        bandSelection.bottomRightHandle.setY(3 * imageView.getBoundsInParent().getHeight() / 4);

        updateTextFields(bandSelection.rect.getWidth(), bandSelection.rect.getHeight());
    }


    private void removeCropSelection(){
        bandSelection.group.getChildren().removeAll(bandSelection.rect,
                                                    bandSelection.topLeftHandle,
                                                    bandSelection.bottomRightHandle);

        if(hasTextFields){
            widthField.setText("");
            heightField.setText("");
        }
    }



    private void updateTextFields(double width, double height){
        if(!hasTextFields){return;}

        double viewWidth = imageView.getBoundsInParent().getWidth();
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        widthField.setText(String.valueOf(Math.round(imageWidth * width / viewWidth)));
        heightField.setText(String.valueOf(Math.round(imageHeight * height / viewHeight)));
    }


    public void updateSize(){
        imageLayer.maxWidth(imageView.getBoundsInParent().getWidth());
        imageLayer.maxHeight(imageView.getBoundsInParent().getHeight());
    }


    public int getCropXInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        return (int) Math.round(imageWidth * bandSelection.rect.xProperty().get() / viewWidth);
    }

    public int getCropYInImage(){
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageHeight = image.getHeight();

        return (int) Math.round(imageHeight * bandSelection.rect.yProperty().get() / viewHeight);
    }

    public int getCropWidthInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        return (int) Math.round(imageWidth * bandSelection.rect.getWidth() / viewWidth);
    }

    public int getCropHeightInImage(){
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageHeight = image.getHeight();

        return (int) Math.round(imageHeight * bandSelection.rect.getHeight() / viewHeight);
    }



    public void changeColour(Colour colour){
        double r = 0, g = 0, b = 0;

        if(colour.equals(Colour.BLACK)){        r = 0.0;    g = 0.0;    b = 0.0; }
        else if(colour.equals(Colour.WHITE)){   r = 1.0;    g = 1.0;    b = 1.0;}
        else if(colour.equals(Colour.GREY)){    r = 0.75;   g = 0.75;   b = 0.75;}
        else if(colour.equals(Colour.RED)){     r = 0.93;   g = 0.14;   b = 0.0;}
        else if(colour.equals(Colour.GREEN)){   r = 0.33;   g = 0.85;   b = 0.15;}
        else if(colour.equals(Colour.BLUE)){    r = 0.01;   g = 0.62;   b = 0.83;}

        Color borderColour = new Color(r, g, b,1.0);
        Color fillColour = new Color(r, g, b, 0.175);

        bandSelection.topLeftHandle.setStroke(borderColour);
        bandSelection.topLeftHandle.setFill(fillColour);

        bandSelection.bottomRightHandle.setStroke(borderColour);
        bandSelection.bottomRightHandle.setFill(fillColour);

        bandSelection.rect.setStroke(borderColour);
        bandSelection.rect.setFill(fillColour);
    }


    public static class RubberBandSelection {

        private Rectangle rect = new Rectangle();
        private ImageCropPane parent;
        private Rectangle topLeftHandle;
        private Rectangle bottomRightHandle;

        private Group group;

        public Bounds getBounds() {
            return rect.getBoundsInParent();
        }

        public RubberBandSelection(ImageCropPane parent,  Group group) {
            this.parent = parent;
            this.group = group;


            topLeftHandle = new Rectangle(0, 0, 10, 10);
            topLeftHandle.setStrokeWidth(1);

            bottomRightHandle = new Rectangle(0, 0, 10, 10);
            bottomRightHandle.setStrokeWidth(1);

            setTopLeftHandle();
            setBottomRightHandle();

            rect = new Rectangle();

            rect.xProperty().bind(topLeftHandle.xProperty());
            rect.yProperty().bind(topLeftHandle.yProperty());

            rect.widthProperty().bind(bottomRightHandle.xProperty().subtract(
                            topLeftHandle.xProperty()).add(bottomRightHandle.widthProperty()));
            rect.heightProperty().bind(bottomRightHandle.yProperty().subtract(
                            topLeftHandle.yProperty()).add(bottomRightHandle.heightProperty()));


            ChangeListener<Number> updateFields = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    parent.updateTextFields(rect.getWidth(), rect.getHeight());
                }
            };

            rect.widthProperty().addListener(updateFields);
            rect.heightProperty().addListener(updateFields);


            rect.setStrokeWidth(1);
            rect.setStrokeLineCap(StrokeLineCap.ROUND);

        }


        private void setTopLeftHandle(){
            topLeftHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(!parent.cropActive){return;}

                    topLeftHandle.setX(event.getX() - topLeftHandle.getWidth() / 2);
                    topLeftHandle.setY(event.getY() - topLeftHandle.getHeight() / 2);

                    constrain(topLeftHandle);
                }
            });
        }



        private void setBottomRightHandle(){
            bottomRightHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(!parent.cropActive){return;}

                    bottomRightHandle.setX(event.getX() - bottomRightHandle.getWidth() / 2);
                    bottomRightHandle.setY(event.getY() - bottomRightHandle.getHeight() / 2);

                    constrain(bottomRightHandle);
                }
            });
        }

        private void constrain(Rectangle handle){
            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            if(handle.getX() + handle.getWidth() > imageViewWidth){
                handle.setX(imageViewWidth - handle.getWidth() - 1);
            }

            if(handle.getX() < 0){
                handle.setX(handle.getStrokeWidth());
            }

            if(handle.getY() + handle.getHeight() > imageViewHeight){
                handle.setY(imageViewHeight - handle.getHeight() - handle.getStrokeWidth());
            }

            if(handle.getY() < 0){
                handle.setY(handle.getStrokeWidth());
            }

            if(handle == bottomRightHandle){
                if(bottomRightHandle.getX() < topLeftHandle.getX()){
                    bottomRightHandle.setX(topLeftHandle.getX());
                }
                if(bottomRightHandle.getY() < topLeftHandle.getY()){
                    bottomRightHandle.setY(topLeftHandle.getY());
                }
            }else if(handle == topLeftHandle){
                if(topLeftHandle.getX() > bottomRightHandle.getX()){
                    topLeftHandle.setX(bottomRightHandle.getX());
                }
                if(topLeftHandle.getY() > bottomRightHandle.getY()){
                    topLeftHandle.setY(bottomRightHandle.getY());
                }
            }
        }


    }






}
