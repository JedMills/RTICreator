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
 * Created by Jed on 12-Jul-17.
 */
public class ImageCropPane extends BorderPane{

    private Group imageLayer;
    private ImageView imageView;
    private Image image;
    private RubberBandSelection bandSelection;
    private CircleSelection circleSelection;

    private boolean cropActive = false;
    private boolean circleActive = false;


    private TextField widthField;
    private TextField heightField;
    private boolean hasTextFields;

    private TextField circleXField;
    private TextField circleYField;
    private TextField circleRField;

    private boolean hasSpinners;

    private double oldImageWidth = 0;
    private double oldImageHeight = 0;


    public enum Colour{ WHITE   ("White"),
                        GREY    ("Grey"),
                        BLACK   ("Black"),
                        RED     ("Red"),
                        GREEN   ("Green"),
                        BLUE    ("Blue");

        private String asString;

        Colour(String name){
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
        hasSpinners = false;
    }




    public ImageCropPane(TextField widthField, TextField heightField){
        this.widthField = widthField;
        this.heightField = heightField;
        hasTextFields = true;
        hasSpinners = false;
        setUp();
    }


    public ImageCropPane(TextField circleXField, TextField circleYField, TextField circleRField){
        this.circleXField = circleXField;
        this.circleYField = circleYField;
        this.circleRField = circleRField;

        hasTextFields = false;
        hasSpinners = true;
        setUp();
    }



    private void setUp(){
        imageLayer = new Group();
        imageView = new ImageView();
        imageLayer.getChildren().add(imageView);

        bandSelection = new RubberBandSelection(this, imageLayer);
        circleSelection = new CircleSelection(this, imageLayer);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        setCenter(imageLayer);


        ChangeListener<Number> sizeChanged = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updatedCirclesAndRect();
            }
        };
        widthProperty().addListener(sizeChanged);
        heightProperty().addListener(sizeChanged);
    }


    private void scaleCirclePos(double deltaW, double deltaH){
        if(Double.isInfinite(deltaW) || Double.isInfinite(deltaH)){return;}

        circleSelection.centerCircle.setCenterX(circleSelection.centerCircle.getCenterX() * deltaW);
        circleSelection.centerCircle.setCenterY(circleSelection.centerCircle.getCenterY() * deltaH);

        circleSelection.edgeCircle.setCenterX(circleSelection.edgeCircle.getCenterX() * deltaW);
        circleSelection.edgeCircle.setCenterY(circleSelection.edgeCircle.getCenterY() * deltaH);

        circleSelection.setCircleRadius();
    }


    private void scaleRectPos(double deltaW, double deltaH){

        if(Double.isInfinite(deltaW) || Double.isInfinite(deltaH)){return;}
        bandSelection.topLeftHandle.setX(bandSelection.topLeftHandle.getX() * deltaW);
        bandSelection.topLeftHandle.setY(bandSelection.topLeftHandle.getY() * deltaH);

        bandSelection.bottomRightHandle.setX(bandSelection.bottomRightHandle.getX() * deltaW);
        bandSelection.bottomRightHandle.setY(bandSelection.bottomRightHandle.getY() * deltaH);
    }


    private void updatedCirclesAndRect(){
        double newImageWidth = imageView.getBoundsInParent().getWidth();
        double newImageHeight = imageView.getBoundsInParent().getHeight();

        double deltaH  = newImageHeight / oldImageHeight;
        double deltaW = newImageWidth / oldImageWidth;

        scaleCirclePos(deltaW, deltaH);
        scaleRectPos(deltaW, deltaH);

        oldImageWidth = newImageWidth;
        oldImageHeight = newImageHeight;
    }



    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImage(Image image) {
        this.image = image;
        imageView.setImage(image);

        oldImageWidth = imageView.getBoundsInParent().getWidth();
        oldImageHeight = imageView.getBoundsInParent().getHeight();
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


    public void setCircleActive(boolean active){
        this.circleActive = active;

        if(active){
            addCircleSelection();
        }else{
            removeCircleSelection();
        }
    }



    private void addCircleSelection(){
        circleSelection.group.getChildren().addAll( circleSelection.hCenterLine,
                                                    circleSelection.vCenterLine,
                                                    circleSelection.circle,
                                                    circleSelection.centerCircle,
                                                    circleSelection.edgeCircle);

        circleSelection.centerCircle.setCenterX(imageView.getBoundsInParent().getWidth() / 4);
        circleSelection.centerCircle.setCenterY(imageView.getBoundsInParent().getHeight() / 4);
    }


    private void removeCircleSelection(){
        circleSelection.group.getChildren().removeAll(  circleSelection.vCenterLine,
                                                        circleSelection.hCenterLine,
                                                        circleSelection.circle,
                                                        circleSelection.centerCircle,
                                                        circleSelection.edgeCircle);
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

    public void setCircleSelection(double x, double y, double r){
        circleSelection.centerCircle.setCenterX(x);
        circleSelection.centerCircle.setCenterY(y);
        circleSelection.edgeCircle.setCenterX(x + r);
        circleSelection.edgeCircle.setCenterY(y);
        circleSelection.setCircleRadius();

        updateSpinners();
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

        setColour(borderColour, fillColour, bandSelection.topLeftHandle, bandSelection.bottomRightHandle,
                                            bandSelection.rect, circleSelection.centerCircle, circleSelection.circle,
                                            circleSelection.edgeCircle, circleSelection.hCenterLine,
                                            circleSelection.vCenterLine);

    }


    private void setColour(Color stroke, Color fill, Shape... shapes){
        for(Shape shape : shapes){
            shape.setStroke(stroke);
            shape.setFill(fill);
        }
    }



    private int[] getCirclePosInImage(){
        double viewWidth = imageView.getBoundsInParent().getWidth();

        double imageWidth = image.getWidth();

        double onePixelDist = imageWidth / viewWidth;

        int x = (int) Math.round(circleSelection.centerCircle.getCenterX() * onePixelDist);
        int y = (int) Math.round(circleSelection.centerCircle.getCenterY() * onePixelDist);

        int r = (int) (circleSelection.circle.getRadius() * onePixelDist);

        return new int[]{x, y, r};
    }

    public void translateCircleFromGUI(int dX, int dY){
        circleSelection.centerCircleDraggedFunction(circleSelection.centerCircle.getCenterX() + dX,
                                                    circleSelection.centerCircle.getCenterY() + dY);
    }

    public void changeRFromGUI(int dr){
        double dX = circleSelection.edgeCircle.getCenterX() - circleSelection.centerCircle.getCenterX();
        double dY = circleSelection.edgeCircle.getCenterY() - circleSelection.centerCircle.getCenterY();

        double theta = Math.atan2(dY,dX);
        double newR = circleSelection.circle.getRadius() + dr;

        double newX = circleSelection.centerCircle.getCenterX() + (newR * Math.cos(theta));
        double newY = circleSelection.centerCircle.getCenterY() + (newR * Math.sin(theta));

        circleSelection.edgeCircleDraggedFunction(newX, newY);
    }


    private void updateSpinners(){
        if(hasSpinners){
            int[] xyr = getCirclePosInImage();
            circleXField.setText(String.valueOf(xyr[0]));
            circleYField.setText(String.valueOf(xyr[1]));
            circleRField.setText(String.valueOf(xyr[2]));
        }
    }


    public static class CircleSelection{

        private Circle circle;
        private ImageCropPane parent;
        private Circle centerCircle;
        private Line vCenterLine;
        private Line hCenterLine;

        private Circle edgeCircle;
        private Group group;


        public CircleSelection(ImageCropPane parent, Group group){

            this.parent = parent;
            this.group = group;

            circle = new Circle(0 ,0 ,0);
            circle.setStrokeWidth(1);

            centerCircle = new Circle(0, 0, 10);
            circle.setStrokeWidth(1);


            createCenterCircleCross();

            circle.centerXProperty().bind(centerCircle.centerXProperty());
            circle.centerYProperty().bind(centerCircle.centerYProperty());

            edgeCircle = new Circle(0, 0, 10);

            setCenterCircleHandle();
            setEdgeCircleHandle();
            setCircleRadius();
        }


        private void createCenterCircleCross(){
            vCenterLine = new Line();
            hCenterLine = new Line();

            vCenterLine.startXProperty().bind(centerCircle.centerXProperty());
            vCenterLine.startYProperty().bind(centerCircle.centerYProperty().
                    subtract(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            vCenterLine.endXProperty().bind(centerCircle.centerXProperty());
            vCenterLine.endYProperty().bind(centerCircle.centerYProperty().
                    add(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));


            hCenterLine.startXProperty().bind(centerCircle.centerXProperty().
                    subtract(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            hCenterLine.startYProperty().bind(centerCircle.centerYProperty());
            hCenterLine.endXProperty().bind(centerCircle.centerXProperty().
                    add(centerCircle.radiusProperty().
                            subtract(centerCircle.strokeWidthProperty())));
            hCenterLine.endYProperty().bind(centerCircle.centerYProperty());
        }


        private void centerCircleDraggedFunction(double x, double y){
            if(!parent.circleActive){return;}

            double oldX = centerCircle.getCenterX();
            double oldY = centerCircle.getCenterY();

            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            centerCircle.setCenterX(x);
            centerCircle.setCenterY(y);


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

            constrainCircle(centerCircle, imageViewWidth, imageViewHeight);
            constrainCircle(edgeCircle, imageViewWidth, imageViewHeight);



            double dx = oldX - centerCircle.getCenterX();
            double dy = oldY - centerCircle.getCenterY();

            edgeCircle.setCenterX(edgeCircle.getCenterX() - dx);
            edgeCircle.setCenterY(edgeCircle.getCenterY() - dy);

            setCircleRadius();

            parent.updateSpinners();
        }




        private void setCenterCircleHandle(){
            centerCircle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    centerCircleDraggedFunction(event.getX(), event.getY());
                }
            });
        }



        private void edgeCircleDraggedFunction(double x, double y){
            if(!parent.circleActive){return;}

            double imageViewWidth = parent.imageView.getBoundsInParent().getWidth();
            double imageViewHeight = parent.imageView.getBoundsInParent().getHeight();

            double newX = x;
            double newY = y;

            double newDX = newX - centerCircle.getCenterX();
            double newDY = newY - centerCircle.getCenterY();
            double newR = Math.sqrt(newDX * newDX + newDY * newDY) + circle.getStrokeWidth();


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

            constrainCircle(edgeCircle, imageViewWidth, imageViewHeight);

            setCircleRadius();
            parent.updateSpinners();
        }




        private void setEdgeCircleHandle(){
            edgeCircle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    edgeCircleDraggedFunction(event.getX(), event.getY());
                }
            });

        }


        private void constrainCircle(Circle c, double imageViewWidth, double imageViewHeight){
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


        private void setCircleRadius(){
            double dx = centerCircle.getCenterX() - edgeCircle.getCenterX();
            double dy = centerCircle.getCenterY() - edgeCircle.getCenterY();

            circle.setRadius(Math.sqrt(dx * dx + dy * dy));
        }




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
