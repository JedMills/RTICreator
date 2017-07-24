package guiComponents;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import static java.lang.Math.PI;
import static java.lang.Math.ceil;

/**
 * <p>
 *     This is a component that can be used to display images, and bring up rectangular or circular selectors. This
 *     class is used in the {@link cropExecuteScene.CropExecuteLayout} and the
 *     {@link highlightDetectionScene.HighlightDetectionLayout). The ImageCropPane can have two TextFields bound to
 *     is that will show the postion and size of the crop rectangle that is shows.
 * </p>
 *
 * @author Jed Mills
 */
public class ImageCropPane extends BorderPane{

    /** The layer which holds the image and the selectors on top of it*/
    private Group imageLayer;

    /** Displays the image in the pane */
    private ImageView imageView;

    /** The current image being displayed by the pane */
    private Image image;

    /** The rectangular selector */
    private RectangleSelection bandSelection;

    /** The circular selector*/
    private CircleSelection circleSelection;

    /** Whether the rectangular selector is visible */
    private boolean cropActive = false;

    /** Whether the circular selector is active */
    private boolean circleActive = false;

    /** Field that can be bound to this ImageCropPane that will be updated with the rectangle selector's width */
    private TextField widthField;

    /** Field that can be bound to this ImageCropPane that will be updated with the rectangle selector's height */
    private TextField heightField;

    /** Whether this ImageCropPane has text fields bound for the rectangle selector*/
    private boolean hasTextFields;

    /** Field that can be bound to this ImageCropPane that will be updated with the circular selector's x pos */
    private TextField circleXField;

    /** Field that can be bound to this ImageCropPane that will be updated with the circular selector's y pos */
    private TextField circleYField;

    /** Field that can be bound to this ImageCropPane that will be updated with the circular selector's radius */
    private TextField circleRField;

    /** Whether this ImageCropPane has text fields bound for the circular selector*/
    private boolean hasCircleFields;

    /** Used to detect changes in width when resizing the pane*/
    private double oldImageWidth = 0;

    /** Used to detect changes in height when resizing the pane*/
    private double oldImageHeight = 0;


    /**
     * This class represents the colours that the rectangle/circle selectors can be changed to.
     */
    public enum Colour{ WHITE   ("White"),
                        GREY    ("Grey"),
                        BLACK   ("Black"),
                        RED     ("Red"),
                        GREEN   ("Green"),
                        BLUE    ("Blue");

        /** The name of the enum value as a string*/
        private String asString;

        /**
         * Makes a new Colour with the given string name
         *
         * @param name  String name of the colour
         */
        Colour(String name){
            asString = name;
        }

        /**
         * @return {@link Colour#asString}
         */
        @Override
        public String toString(){
            return asString;
        }
    }


    /**
     * Creates a new ImageCropPane without spinners or fields bound for the rectangle or circular selectors.
     */
    public ImageCropPane() {
        setUp();
        hasTextFields = false;
        hasCircleFields = false;
    }


    /**
     * Creates a new ImageCropPane that has width and height fields bound that will be updated with the size
     * of the rectangle selection.
     *
     * @param widthField        field that will be updated with the rectangle selector's width
     * @param heightField       field that will be updated with the rectangle selector's height
     */
    public ImageCropPane(TextField widthField, TextField heightField){
        this.widthField = widthField;
        this.heightField = heightField;
        hasTextFields = true;
        hasCircleFields = false;
        setUp();
    }


    /**
     * Creates a new ImageCropPane that has fields bound that will be updated with the position and radius of the
     * circular selector.
     *
     * @param circleXField field to be updated with the circle selector's x pos
     * @param circleYField field to be updated with the circle selector's y pos
     * @param circleRField field to be updated with the circle selector's radius
     */
    public ImageCropPane(TextField circleXField, TextField circleYField, TextField circleRField){
        this.circleXField = circleXField;
        this.circleYField = circleYField;
        this.circleRField = circleRField;

        hasTextFields = false;
        hasCircleFields = true;
        setUp();
    }


    /**
     * Creates the Pane. Creates a new circular and rectangle selector, add them to the pane, and binds listeners to the 
     * width and the height of the pane so it updates its size nicely. 
     */
    private void setUp(){
        //the image and the rectangle/circle selectors go on this
        imageLayer = new Group();
        imageView = new ImageView();
        imageLayer.getChildren().add(imageView);

        bandSelection = new RectangleSelection(this, imageLayer);
        circleSelection = new CircleSelection(this, imageLayer);

        //make the image fill the size of the pane, and keep the aspect ratio so it doesn't get stretched
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        setCenter(imageLayer);

        //listener to the width and height so the position and size of the selectors scales when the
        //size fo the pane is changed
        ChangeListener<Number> sizeChanged = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updatedCirclesAndRect();
            }
        };
        widthProperty().addListener(sizeChanged);
        heightProperty().addListener(sizeChanged);
    }


    /**
     * Scales the size and position of the circular selector when the size of the ImageCropPane is changed.
     *
     * @param deltaW        change in the width of the crop pane
     * @param deltaH        change in the height of the crop pane
     */
    private void scaleCirclePos(double deltaW, double deltaH){
        //it can sometimes cause an error when the image crop pane is initialised and has no image in it
        //so deal with that here
        if(Double.isInfinite(deltaW) || Double.isInfinite(deltaH)){return;}

        //scale the center circle and edge circle by change in size
        circleSelection.centerCircle.setCenterX(circleSelection.centerCircle.getCenterX() * deltaW);
        circleSelection.centerCircle.setCenterY(circleSelection.centerCircle.getCenterY() * deltaH);

        circleSelection.edgeCircle.setCenterX(circleSelection.edgeCircle.getCenterX() * deltaW);
        circleSelection.edgeCircle.setCenterY(circleSelection.edgeCircle.getCenterY() * deltaH);

        //make sure the radius of the main circle keeps up with the changing edge circle
        circleSelection.setCircleRadius();
    }


    /**
     * Scales the size and position of the rectangle selector when the size of the ImageCropPane is changed.
     *
     * @param deltaW        change in the width of the crop pane
     * @param deltaH        change in the height of the crop pane
     */
    private void scaleRectPos(double deltaW, double deltaH){
        //it can sometimes cause an error when the image crop pane is initialised and has no image in it
        //so deal with that here
        if(Double.isInfinite(deltaW) || Double.isInfinite(deltaH)){return;}

        //scale the handles by the change in size, the main rectangle will update by itself
        bandSelection.topLeftHandle.setX(bandSelection.topLeftHandle.getX() * deltaW);
        bandSelection.topLeftHandle.setY(bandSelection.topLeftHandle.getY() * deltaH);

        bandSelection.bottomRightHandle.setX(bandSelection.bottomRightHandle.getX() * deltaW);
        bandSelection.bottomRightHandle.setY(bandSelection.bottomRightHandle.getY() * deltaH);
    }


    /**
     * Updates the size of the  rectangle and circular selectors when the size of the ImageCropPane is changed.
     */
    private void updatedCirclesAndRect(){
        if(image == null){return;}

        //fins the difference in size so we can scale the positions of the selectors by this change
        double newImageWidth = imageView.getBoundsInParent().getWidth();
        double newImageHeight = imageView.getBoundsInParent().getHeight();

        double deltaH  = newImageHeight / oldImageHeight;
        double deltaW = newImageWidth / oldImageWidth;

        scaleCirclePos(deltaW, deltaH);
        scaleRectPos(deltaW, deltaH);

        //update the old sizes for the next change that is made
        oldImageWidth = newImageWidth;
        oldImageHeight = newImageHeight;
    }


    /**
     * @return {@link ImageCropPane#image}
     */
    public Image getImage() {
        return image;
    }


    /**
     * @return {@link ImageCropPane#imageView}
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Sets the image in the {@link ImageCropPane#imageView} of this pane.
     *
     * @param image     image to set in the pane
     */
    public void setImage(Image image) {
        this.image = image;
        imageView.setImage(image);

        //update the pane with the size of the image
        oldImageWidth = imageView.getBoundsInParent().getWidth();
        oldImageHeight = imageView.getBoundsInParent().getHeight();
    }


    /**
     * Removes the picture from this crop pane.
     */
    public void clearImage(){
        this.image = null;
        imageView.setImage(null);
    }


    /**
     * Makes the rectangle crop selector active and visible.
     *
     * @param cropActive    whether to make the rectangle visible and movable
     */
    public void setCropActive(boolean cropActive) {
        this.cropActive = cropActive;

        if(cropActive){
            addCropSelection();
        }else{
            removeCropSelection();
        }
    }

    /**
     * Makes the circle selector active and visible.
     *
     * @param active    whether to make the circle selector visible and movable
     */
    public void setCircleActive(boolean active){
        this.circleActive = active;

        if(active){
            addCircleSelection();
        }else{
            removeCircleSelection();
        }
    }


    /**
     * Adds the circle selection widget to the crop pane.
     */
    private void addCircleSelection(){
        //make sure it doesn't already have the widgets n the pane to prevent a duplicate child error
        if(!circleSelection.group.getChildren().contains(circleSelection.hCenterLine)) {
            circleSelection.group.getChildren().addAll(circleSelection.hCenterLine,
                    circleSelection.vCenterLine,
                    circleSelection.circle,
                    circleSelection.centerCircle,
                    circleSelection.edgeCircle);
        }
        //make the circle in the center of the image
        circleSelection.centerCircle.setCenterX(imageView.getBoundsInParent().getWidth() / 4);
        circleSelection.centerCircle.setCenterY(imageView.getBoundsInParent().getHeight() / 4);
    }


    /**
     * Removes the circular selection from the crop pane.
     */
    private void removeCircleSelection(){
        circleSelection.group.getChildren().removeAll(  circleSelection.vCenterLine,
                                                        circleSelection.hCenterLine,
                                                        circleSelection.circle,
                                                        circleSelection.centerCircle,
                                                        circleSelection.edgeCircle);
    }


    /**
     * Adds the rectangular selection widget to the crop pane.
     */
    private void addCropSelection(){
        //add the components to the pane
        bandSelection.group.getChildren().addAll(bandSelection.rect,
                                                bandSelection.topLeftHandle,
                                                bandSelection.bottomRightHandle);

        //this basically means that the rectangular selection is in the center of the image,
        //width half the width and half the height of the image
        bandSelection.topLeftHandle.setX(imageView.getBoundsInParent().getWidth() / 4);
        bandSelection.topLeftHandle.setY(imageView.getBoundsInParent().getHeight() / 4);

        bandSelection.bottomRightHandle.setX(3 * imageView.getBoundsInParent().getWidth() / 4);
        bandSelection.bottomRightHandle.setY(3 * imageView.getBoundsInParent().getHeight() / 4);

        //update the text fields with the size of the selection
        updateTextFields(bandSelection.rect.getWidth(), bandSelection.rect.getHeight());
    }


    /**
     * Removes the circular selection from the crop pane.
     */
    private void removeCropSelection(){
        bandSelection.group.getChildren().removeAll(bandSelection.rect,
                                                    bandSelection.topLeftHandle,
                                                    bandSelection.bottomRightHandle);
        //make text fields blank if this crop pane has them, as the selector's gone
        if(hasTextFields){
            widthField.setText("");
            heightField.setText("");
        }
    }


    /**
     * Updates the text fields for the crop pane, if they are bound, so that they display the width and the height
     * of the rectangular selector.
     *
     * @param width         width of the rectangle
     * @param height        height of the rectangle
     */
    private void updateTextFields(double width, double height){
        if(!hasTextFields){return;}

        //map the size of the rectangle to the size it would be in the image in the pane, at full size
        double viewWidth = imageView.getBoundsInParent().getWidth();
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        widthField.setText(String.valueOf(Math.round(imageWidth * width / viewWidth)));
        heightField.setText(String.valueOf(Math.round(imageHeight * height / viewHeight)));
    }


    /**
     * Updates the size of the image layer, sued for when the pane resizes.
     */
    public void updateSize(){
        imageLayer.maxWidth(imageView.getBoundsInParent().getWidth());
        imageLayer.maxHeight(imageView.getBoundsInParent().getHeight());
    }


    /**
     * Returns the x position of the the top left corner of the crop rectangle, mapped to the actual size of the image
     * displayed in the crop pane.
     *
     * @return  the top left x position of the crop rectangle
     */
    public int getCropXInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        return (int) Math.round(imageWidth * bandSelection.rect.xProperty().get() / viewWidth);
    }


    /**
     * Returns the y position of the the top left corner of the crop rectangle, mapped to the actual size of the image
     * displayed in the crop pane.
     *
     * @return  the top left y position of the crop rectangle
     */
    public int getCropYInImage(){
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageHeight = image.getHeight();

        return (int) Math.round(imageHeight * bandSelection.rect.yProperty().get() / viewHeight);
    }


    /**
     * Returns the width of the rectangle selector, mapped to actual size of the image being displayed.
     *
     * @return  width of the selection
     */
    public int getCropWidthInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        return (int) Math.round(imageWidth * bandSelection.rect.getWidth() / viewWidth);
    }




    /**
     * Returns the height of the rectangle selector, mapped to actual size of the image being displayed.
     *
     * @return  height of the selection
     */
    public int getCropHeightInImage(){
        double viewHeight = imageView.getBoundsInParent().getHeight();

        double imageHeight = image.getHeight();

        return (int) Math.round(imageHeight * bandSelection.rect.getHeight() / viewHeight);
    }


    /**
     * Sets the circular selector to the position (x, y) in the image, with radius r. These values are
     * not mapped to the actual size of the image: they are relative to the size of the dispalyed image.
     *
     * @param x     center x pos to place the selector
     * @param y     center y pos to place the selector
     * @param r     radius to give the selector
     */
    public void setCircleSelection(double x, double y, double r){
        circleSelection.centerCircle.setCenterX(x);
        circleSelection.centerCircle.setCenterY(y);
        circleSelection.edgeCircle.setCenterX(x + r);
        circleSelection.edgeCircle.setCenterY(y);
        circleSelection.setCircleRadius();

        //update the spinners so they show the position of the selector
        updateCircleFields();
    }


    /**
     * Changes the colour of both the rectangle and circle selectors to the given value.
     *
     * @param colour    colour tochange the selectors to
     */
    public void changeColour(Colour colour){
        double r = 0, g = 0, b = 0;

        if(colour.equals(Colour.BLACK)){        r = 0.0;    g = 0.0;    b = 0.0; }
        else if(colour.equals(Colour.WHITE)){   r = 1.0;    g = 1.0;    b = 1.0;}
        else if(colour.equals(Colour.GREY)){    r = 0.75;   g = 0.75;   b = 0.75;}
        else if(colour.equals(Colour.RED)){     r = 0.93;   g = 0.14;   b = 0.0;}
        else if(colour.equals(Colour.GREEN)){   r = 0.33;   g = 0.85;   b = 0.15;}
        else if(colour.equals(Colour.BLUE)){    r = 0.01;   g = 0.62;   b = 0.83;}

        //the fill colour is translucent, while the border colour is opaque
        Color borderColour = new Color(r, g, b,1.0);
        Color fillColour = new Color(r, g, b, 0.175);

        //set the colour of all the selector componenets
        setColour(borderColour, fillColour, bandSelection.topLeftHandle, bandSelection.bottomRightHandle,
                                            bandSelection.rect, circleSelection.centerCircle, circleSelection.circle,
                                            circleSelection.edgeCircle, circleSelection.hCenterLine,
                                            circleSelection.vCenterLine);

    }


    /**
     * Convenience method to set the fill and border colour of JavaFX shapes
     *
     * @param stroke    border colour to set
     * @param fill      fill colour to set
     * @param shapes    shape to set the colour of
     */
    private void setColour(Color stroke, Color fill, Shape... shapes){
        for(Shape shape : shapes){
            shape.setStroke(stroke);
            shape.setFill(fill);
        }
    }


    /**
     * Gets the circle x, y and r in the pane, mapped to the actual size of the image being displayed.
     *
     * @return  [x, y, r] of the circle selector mapped to the image size
     */
    private int[] getCirclePosInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        double onePixelDist = imageWidth / viewWidth;

        //map the actual position to the position in the full size image being displayed
        int x = (int) Math.round(circleSelection.centerCircle.getCenterX() * onePixelDist);
        int y = (int) Math.round(circleSelection.centerCircle.getCenterY() * onePixelDist);
        int r = (int) (circleSelection.circle.getRadius() * onePixelDist);

        return new int[]{x, y, r};
    }


    /**
     * Moves the circle selector by (dx, dy). These arguments are the actual distances to move the circle,
     * not mapped to the actual size of the image.
     *
     * @param dX        x distance to move the circle
     * @param dY        y distance to move the circle
     */
    public void translateCircleFromGUI(int dX, int dY){
        circleSelection.centerCircleDraggedFunction(circleSelection.centerCircle.getCenterX() + dX,
                                                    circleSelection.centerCircle.getCenterY() + dY);
    }


    /**
     * Changes the radius of the circle by dr.  This is the actual change in radius, not mapped to the actual size of
     * the image being displayed. 
     *
     * @param dr    change in radius 
     */
    public void changeRFromGUI(int dr) {
        //we need to make the edge circle change increase in distance at the same angle that is currently 
        //is to the center circle, so we need to find the difference in x and y between them and use polar coords
        //to change the r
        double dX = circleSelection.edgeCircle.getCenterX() - circleSelection.centerCircle.getCenterX();
        double dY = circleSelection.edgeCircle.getCenterY() - circleSelection.centerCircle.getCenterY();

        double theta = Math.atan2(dY,dX);
        double newR = circleSelection.circle.getRadius() + dr;

        double newX = circleSelection.centerCircle.getCenterX() + (newR * Math.cos(theta));
        double newY = circleSelection.centerCircle.getCenterY() + (newR * Math.sin(theta));

        //finally converted the polar coords to cartesian, so move the edge circle
        circleSelection.edgeCircleDraggedFunction(newX, newY);
    }


    /**
     * Updates the fields showing the position and radius of the circular selector.
     */
    private void updateCircleFields(){
        if(hasCircleFields){
            //find the position and radius ofthe circle mapped to the actual image size
            int[] xyr = getCirclePosInImage();
            circleXField.setText(String.valueOf(xyr[0]));
            circleYField.setText(String.valueOf(xyr[1]));
            circleRField.setText(String.valueOf(xyr[2]));
        }
    }


    /**
     * This class is the circular selector that can be used to select the specular balls for RTI. It has a center
     * circle with a cross in it, and an edge circle that can be used to change the radius of the circle selection.
     * The circular selector's radius is bound to the position of the edge circle. Yes, it comes in a variety of
     * colours.
     */
    public class CircleSelection{

        /** The circle that selects the area */
        private Circle circle;

        /** The crop pane that this selector belongs to */
        private ImageCropPane parent;

        /** The circle at the center of the selector */
        private Circle centerCircle;

        /** The vertical line in the center circle*/
        private Line vCenterLine;

        /** The horizontal line in the center circle */
        private Line hCenterLine;

        /** The circle that the radius is bound to */
        private Circle edgeCircle;

        /** The parent's group that contains the imageview and the circle selector */
        private Group group;

        /**
         * Creates a new CircleSelection for the parent ImageCropPane.
         *
         * @param parent    pane that this  selector belongs to
         * @param group     group that this selector belongs to
         */
        public CircleSelection(ImageCropPane parent, Group group){
            this.parent = parent;
            this.group = group;

            //initialise the components
            circle = new Circle(0 ,0 ,0);
            circle.setStrokeWidth(1);

            centerCircle = new Circle(0, 0, 10);
            circle.setStrokeWidth(1);

            createCenterCircleCross();

            //make the circle selection always have the same ceneter as the center circle
            circle.centerXProperty().bind(centerCircle.centerXProperty());
            circle.centerYProperty().bind(centerCircle.centerYProperty());

            edgeCircle = new Circle(0, 0, 10);

            //makes the center and edge circles draggable
            setCenterCircleHandle();
            setEdgeCircleHandle();

            //update the radius
            setCircleRadius();
        }


        /**
         * Creates the cross in center circle, and binds their start and end so they move about with the center circle.
         */
        private void createCenterCircleCross(){
            vCenterLine = new Line();
            hCenterLine = new Line();

            //binds the ends inside the center circle, subtracting the center circle stroke width
            //so they don't stick out ever so slightly from the center circle
            vCenterLine.startXProperty().bind(centerCircle.centerXProperty());
            vCenterLine.startYProperty().bind(centerCircle.centerYProperty().
                    subtract(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            vCenterLine.endXProperty().bind(centerCircle.centerXProperty());
            vCenterLine.endYProperty().bind(centerCircle.centerYProperty().
                    add(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));

            //same with the horizontal line
            hCenterLine.startXProperty().bind(centerCircle.centerXProperty().
                    subtract(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            hCenterLine.startYProperty().bind(centerCircle.centerYProperty());
            hCenterLine.endXProperty().bind(centerCircle.centerXProperty().
                    add(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            hCenterLine.endYProperty().bind(centerCircle.centerYProperty());
        }


        /**
         * This function is called when the center circle is dragged. It is used so that the edge circle is moved with
         * the center circle.
         *
         * @param x         new x pos to move the center circle to, not mapped to the actual image size
         * @param y         new y pos to move the center circle to, not mapped to the actual image size
         */
        private void centerCircleDraggedFunction(double x, double y){
            if(!parent.circleActive){return;}

            double oldX = centerCircle.getCenterX();
            double oldY = centerCircle.getCenterY();

            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            centerCircle.setCenterX(x);
            centerCircle.setCenterY(y);

            //all this makes sure the circle selector is never dragged outside the bounds of the image,
            //taking into account the edge circle and the radius of the circle selector
            if(centerCircle.getCenterX() + circle.getRadius() + circle.getStrokeWidth() > imageViewWidth){
                centerCircle.setCenterX(imageViewWidth - circle.getRadius() - circle.getStrokeWidth());
            }

            if(centerCircle.getCenterY() + circle.getRadius() +  circle.getStrokeWidth() > imageViewHeight){
                centerCircle.setCenterY(imageViewHeight - circle.getRadius() - circle.getStrokeWidth());
            }

            if(centerCircle.getCenterX() - circle.getRadius() - circle.getStrokeWidth() < 0){
                centerCircle.setCenterX(circle.getRadius() + circle.getStrokeWidth());
            }

            if(centerCircle.getCenterY() - circle.getRadius() - circle.getStrokeWidth() < 0){
                centerCircle.setCenterY(circle.getRadius() + circle.getStrokeWidth());
            }
            //this constrains the main circle, considering its radius and the edge circle
            constrainCircle(centerCircle, imageViewWidth, imageViewHeight);
            constrainCircle(edgeCircle, imageViewWidth, imageViewHeight);

            double dx = oldX - centerCircle.getCenterX();
            double dy = oldY - centerCircle.getCenterY();

            edgeCircle.setCenterX(edgeCircle.getCenterX() - dx);
            edgeCircle.setCenterY(edgeCircle.getCenterY() - dy);

            //update the main circle radius
            setCircleRadius();

            //update the fields that show the circle x,y and r
            parent.updateCircleFields();
        }


        /**
         * Sets the function for when the center circle is dragged.
         */
        private void setCenterCircleHandle(){
            centerCircle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    centerCircleDraggedFunction(event.getX(), event.getY());
                }
            });
        }


        /**
         * The function for when the edge circle is dragged. The x and y args are ot mapped to the actual image size.
         * The function results in the radius of the circle selection changing.
         *
         * @param x         new x pos to move the edge circle to, not mapped to the actual image size
         * @param y         new y pos to move the edge circle to, not mapped to the actual image size
         */
        private void edgeCircleDraggedFunction(double x, double y){
            if(!parent.circleActive){return;}

            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            double newX = x;
            double newY = y;

            //find out the new radius of the circle selector
            double newDX = newX - centerCircle.getCenterX();
            double newDY = newY - centerCircle.getCenterY();
            double newR = Math.sqrt(newDX * newDX + newDY * newDY) + circle.getStrokeWidth();


            //make sure that the edge circle never goes outside the image, and that the radius of the
            //main circle component can never be increased outside of the image
            if(     centerCircle.getCenterX() + newR  > imageViewWidth  ||
                    centerCircle.getCenterX() - newR < 0                ||
                    centerCircle.getCenterY() + newR > imageViewHeight  ||
                    centerCircle.getCenterY() - newR < 0){

                double theta = Math.atan2(newDY, newDX);

                x = centerCircle.getCenterX() + (circle.getRadius() * Math.cos(theta));
                y = centerCircle.getCenterY() + (circle.getRadius() * Math.sin(theta));

                edgeCircle.setCenterX(x);
                edgeCircle.setCenterY(y);

            }else{
                edgeCircle.setCenterX(x);
                edgeCircle.setCenterY(y);
            }

            //make sure the main circle is never outside the image
            constrainCircle(edgeCircle, imageViewWidth, imageViewHeight);

            //update the circle radius
            setCircleRadius();
            parent.updateCircleFields();
        }


        /**
         * Sets the function for when the edge circle is dragged.
         */
        private void setEdgeCircleHandle(){
            edgeCircle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    edgeCircleDraggedFunction(event.getX(), event.getY());
                }
            });

        }


        /**
         * Constrains the given circle of the selector so that is can never be dragged outside the image, and that the
         * radius can never be increased outside of the image.
         *
         * @param c                 the circle to constrain
         * @param imageViewWidth    the height of the image view in the crop pane
         * @param imageViewHeight   the width of the image view in the crop pane
         */
        private void constrainCircle(Circle c, double imageViewWidth, double imageViewHeight){
            // the stroke width is used here as you can sometimes see the 1 or 2 pixel difference due to the
            //stroke width
            if(c.getCenterX() + c.getRadius() + c.getStrokeWidth() > imageViewWidth){
                c.setCenterX(imageViewWidth - c.getRadius() - c.getStrokeWidth());
            }

            if(c.getCenterX() - c.getRadius() - c.getStrokeWidth() < 0){
                c.setCenterX(0 + c.getRadius() + c.getStrokeWidth());
            }

            if(c.getCenterY() + c.getRadius() + c.getStrokeWidth() + 1> imageViewHeight){
                c.setCenterY(imageViewHeight - c.getRadius() - c.getStrokeWidth() - 1);
            }

            if(c.getCenterY() - c.getRadius() - c.getStrokeWidth() < 0){
                c.setCenterY(0 + c.getRadius() + c.getStrokeWidth());
            }
        }


        /**
         * Sest the main circle radius so that it is the distance between the center circle and the edge circle.
         */
        private void setCircleRadius(){
            double dx = centerCircle.getCenterX() - edgeCircle.getCenterX();
            double dy = centerCircle.getCenterY() - edgeCircle.getCenterY();

            circle.setRadius(Math.sqrt(dx * dx + dy * dy));
        }
    }





    /**
     * This class is the rectangular selector that has a main rectangle, with the two handles in the top left and
     * bottom right corner that allows the user to resize it.
     */
    public class RectangleSelection {

        /** The main rectangle selection */
        private Rectangle rect = new Rectangle();

        /** The ImageCropPane that this selector belongs to*/
        private ImageCropPane parent;

        /** The draggable top left handle*/
        private Rectangle topLeftHandle;

        /** The draggable bottom right handle */
        private Rectangle bottomRightHandle;

        /** the imageview/shape grou pthat this selector belongs to */
        private Group group;


        /**
         * Creates a new rectangle selector. Binds the changing of the corner handles to resizing the main rectangle.
         *
         * @param parent    pane that this  selector belongs to
         * @param group     group that this selector belongs to
         */
        public RectangleSelection(ImageCropPane parent,  Group group) {
            this.parent = parent;
            this.group = group;

            //crate the handles
            topLeftHandle = new Rectangle(0, 0, 10, 10);
            topLeftHandle.setStrokeWidth(1);

            bottomRightHandle = new Rectangle(0, 0, 10, 10);
            bottomRightHandle.setStrokeWidth(1);

            //set the draggable functionality of the handles
            setTopLeftHandle();
            setBottomRightHandle();

            rect = new Rectangle();

            //bind the top left of the main rect to the top left pfthe top ;eft handle
            rect.xProperty().bind(topLeftHandle.xProperty());
            rect.yProperty().bind(topLeftHandle.yProperty());

            //and the width and height of the main rect to the difference between the handles' positions
            rect.widthProperty().bind(bottomRightHandle.xProperty().subtract(
                            topLeftHandle.xProperty()).add(bottomRightHandle.widthProperty()));
            rect.heightProperty().bind(bottomRightHandle.yProperty().subtract(
                            topLeftHandle.yProperty()).add(bottomRightHandle.heightProperty()));

            //update the text fields with the width and height of the rect
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


        /**
         * Set the draggable functionality of the top left handle.
         */
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


        /**
         * Set the draggable functionality of the bottom right handle.
         */
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


        /**
         * Constrains the rectangle handles to the bounds of the parent's image vie, and akes sure that
         * the bottom right handle never goes above or to the left of the top left handle, so you don't get
         * a rectangle with negative width or height
         *
         * @param handle        the handle to constrain
         */
        private void constrain(Rectangle handle){
            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            //constrain the handle inside the image view
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

            //make sure the handles don't go past each other or you'd get a rectangle with negative width ot height
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
