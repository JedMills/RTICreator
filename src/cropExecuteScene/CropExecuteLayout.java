package cropExecuteScene;

import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGrid;
import guiComponents.ScrollableImageGridForCrop;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.CreatorScene;

import java.util.ArrayList;

/**
 * Created by Jed on 11-Jul-17.
 */
public class CropExecuteLayout extends VBox implements CreatorScene {


    private Button backButton;
    private Button runFitterButton;

    private VBox leftPane;
    private ScrollableImageGridForCrop lpImagesGrid;
    private VBox cropPane;
    private VBox fitterInterfacePane;

    private BorderPane imageBorderPane;
    private ImageView imagePreview;
    private TextField cropWidthField;
    private TextField cropHeightField;
    private CheckBox useCropCheckBox;
    private Button clearCropButton;
    private Button cropColourButton;



    private TextField fitterLocationField;
    private Button fitterLocationButton;
    private TextField outputLocationField;
    private Button outputLocationButton;

    private RadioButton ptmRGBButton;
    private RadioButton ptmLRGBButton;

    private TextArea fitterOutputArea;

    private boolean useCrop;

    private StackPane imageContainer;
    private Rectangle cropRectangle;

    private static CropExecuteLayout ourInstance = new CropExecuteLayout();

    public static CropExecuteLayout getInstance() {
        return ourInstance;
    }

    private CropExecuteLayout() {
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        CropExecuteLayoutListener.getInstance().init(this);

        imagePreview.setImage(new Image("images/fish_fossil_01.jpg"));
    }



    private void createLayout(){
        HBox mainLayout = createMainLayout();
        HBox bottomBar = createBottomBar();

        getChildren().addAll(mainLayout, bottomBar);
        setSpacing(10);
        setPadding(new Insets(5, 5, 5, 5));
        setFillWidth(true);
    }





    private HBox createMainLayout(){
        HBox hBox = new HBox();

        leftPane = createLeftPane();
        cropPane = createCropPane();
        fitterInterfacePane = createFitterPane();


        HBox.setHgrow(leftPane, Priority.SOMETIMES);
        HBox.setHgrow(cropPane, Priority.ALWAYS);
        HBox.setHgrow(fitterInterfacePane, Priority.SOMETIMES);


        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.getChildren().addAll(leftPane, cropPane, fitterInterfacePane);
        hBox.setSpacing(10);
        hBox.setFillHeight(true);
        return hBox;
    }



    private VBox createLeftPane(){
        VBox vBox = new VBox();
        lpImagesGrid = new ScrollableImageGridForCrop("Images in LP File", false,
                                                            true, true, imagePreview);
        vBox.setMinWidth(200);
        vBox.setMaxWidth(400);

        VBox cropPane = createCropOptionsPane();

        vBox.setSpacing(5);
        vBox.getChildren().addAll(lpImagesGrid, cropPane);
        return vBox;
    }



    private VBox createFitterPane(){
        VBox vBox = new VBox();
        vBox.setId("fitterPane");
        Label fitterPaneTitle = new Label("RTI Fitter Options");
        fitterPaneTitle.setFont(Font.font(null, FontWeight.BOLD, 12));

        GridPane fitterOutLocPane = new GridPane();
            Label fitterLocLabel = new Label("Fitter location:");
            GridPane.setConstraints(fitterLocLabel, 0, 0);
            fitterLocationField = new TextField();
            GridPane.setConstraints(fitterLocationField, 1, 0);
            fitterLocationButton = new Button("Browse");
            GridPane.setConstraints(fitterLocationButton, 2, 0);

            Label outLocLabel = new Label("Output file:");
            GridPane.setConstraints(outLocLabel, 0, 1);
            outputLocationField = new TextField();
            GridPane.setConstraints(outputLocationField, 1, 1);
            outputLocationButton = new Button("Browse");
            GridPane.setConstraints(outputLocationButton, 2, 1);
        fitterOutLocPane.setHgap(10);
        fitterOutLocPane.setVgap(10);
        fitterOutLocPane.setPadding(new Insets(5, 5, 5, 5));
        fitterOutLocPane.getChildren().addAll(fitterLocLabel, fitterLocationField, fitterLocationButton,
                                                outLocLabel, outputLocationField, outputLocationButton);


        HBox ptmOptions = new HBox();
            Label ptmTypeLabel = new Label("PTM type:");
            ptmRGBButton = new RadioButton("RGB");
            ptmLRGBButton = new RadioButton("LRGB");
        ptmOptions.setPadding(new Insets(5, 5, 5,5));
        ptmOptions.getChildren().addAll(ptmTypeLabel, createSpacer(), ptmRGBButton, createSpacer(), ptmLRGBButton);


        VBox fitterOutputBox = new VBox();
            Label fitterOutLabel = new Label("Fitter Output:");
            fitterOutLabel.setPadding(new Insets(0, 5, 5,5));
            fitterOutputArea = new TextArea();
            fitterOutputArea.setMinHeight(10);
            fitterOutputArea.setMinWidth(10);
            fitterOutputArea.setMaxWidth(320);
        fitterOutputBox.setAlignment(Pos.CENTER);
        fitterOutputBox.getChildren().addAll(fitterOutLabel, fitterOutputArea);

        vBox.setMaxWidth(350);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.getChildren().addAll(fitterPaneTitle, fitterOutLocPane, ptmOptions, fitterOutputBox);
        return vBox;
    }





    private VBox createCropPane(){
        VBox cropPane = new VBox();
            imageBorderPane = new BorderPane();
            imageBorderPane.setId("cropExecutePanePreview");
            imageBorderPane.setStyle("-fx-background-color: #000000;");
            imageBorderPane.prefHeightProperty().bind(cropPane.heightProperty());
            imageBorderPane.setMinWidth(0);
            imageBorderPane.setMinHeight(0);
            imageBorderPane.prefWidthProperty().bind(cropPane.widthProperty());
            imageBorderPane.prefHeightProperty().bind(cropPane.heightProperty());

            cropRectangle = new Rectangle(0, 0, 10, 10);
            cropRectangle.setVisible(false);
            cropRectangle.setStrokeWidth(2);
            cropRectangle.setFill(Color.TRANSPARENT);
            cropRectangle.setStroke(Paint.valueOf("#ff0000"));

            imagePreview = new ImageView();
            imagePreview.setPreserveRatio(true);
            imagePreview.setSmooth(true);
            imagePreview.fitWidthProperty().bind(imageBorderPane.widthProperty());
            imagePreview.fitHeightProperty().bind(imageBorderPane.heightProperty());

            imageContainer = new StackPane(imagePreview, cropRectangle);
            imageContainer.setMinWidth(0);
            imageContainer.setMinHeight(0);
            setupImageContainer();
            imageBorderPane.setCenter(imageContainer);

        cropPane.setId("cropExecLayoutCropPane");
        cropPane.setAlignment(Pos.CENTER);
        cropPane.setSpacing(5);
        cropPane.setPadding(new Insets(0, 5, 0, 5));
        cropPane.getChildren().addAll(imageBorderPane);
        return cropPane;
    }



    private void setupImageContainer(){
        imageContainer.setMinHeight(0);
        imageContainer.prefWidthProperty().bind(imagePreview.fitWidthProperty());
        imageContainer.prefHeightProperty().bind(imagePreview.fitHeightProperty());
        imageContainer.setMinWidth(0);

        imageContainer.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /*
                if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                    cropRectangle.setVisible(true);
                    cropRectangle.setTranslateX(event.getX());
                    cropRectangle.setTranslateY(event.getY());
                }

                if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                    cropRectangle.setWidth(event.getX() - cropRectangle.getTranslateX());
                    cropRectangle.setHeight(event.getY() - cropRectangle.getTranslateY());
                }
                */
                double imageWidth = imagePreview.getBoundsInParent().getWidth();
                double imageHeight = imagePreview.getBoundsInParent().getHeight();

                double adjustedX = event.getX() - (imageContainer.getWidth() / 2);
                double adjustedY = event.getY() - (imageContainer.getHeight() / 2);

                if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
                    if (event.getY() < (0.5 * (imageContainer.getHeight() + imageHeight))
                            && event.getY() > (0.5 * (imageContainer.getHeight() - imageHeight))
                            && event.getX() < (0.5 * (imageContainer.getWidth() + imageWidth))
                            && event.getX() > (0.5 * (imageContainer.getWidth() - imageWidth))) {
                        System.out.println(event.getX() + ", " + event.getY());
                        cropRectangle.setTranslateX(adjustedX);
                        cropRectangle.setTranslateY(adjustedY);
                    }
                }

                if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    if (event.getY() < (0.5 * (imageContainer.getHeight() + imageHeight))
                            && event.getY() > (0.5 * (imageContainer.getHeight() - imageHeight))
                            && event.getX() < (0.5 * (imageContainer.getWidth() + imageWidth))
                            && event.getX() > (0.5 * (imageContainer.getWidth() - imageWidth))) {
                        cropRectangle.setWidth((adjustedX - cropRectangle.getTranslateX()) * 2);
                        cropRectangle.setHeight((adjustedY - cropRectangle.getTranslateY()) * 2);
                    }
                }
            }
        });
    }


    private VBox createCropOptionsPane(){
        VBox cropOptions = new VBox();
        Label optionsLabel = new Label("Crop Options");
        optionsLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
        useCropCheckBox = new CheckBox("Use crop");
        useCropCheckBox.setId("useCropCheckBox");
        useCropCheckBox.setOnAction(CropExecuteLayoutListener.getInstance());

        GridPane widthHeightGrid = new GridPane();
            Label cropWidthLabel = new Label("Crop width:");
            cropWidthLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());
            GridPane.setConstraints(cropWidthLabel, 0, 0);
            cropWidthField = new TextField();
            cropWidthField.disableProperty().bind(useCropCheckBox.selectedProperty().not());
            cropWidthField.setEditable(false);
            GridPane.setConstraints(cropWidthField, 1, 0);

            Label cropHeightLabel = new Label("Crop height:");
            cropHeightLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());
            GridPane.setConstraints(cropHeightLabel, 0, 1);
            cropHeightField = new TextField();
            cropHeightField.disableProperty().bind(useCropCheckBox.selectedProperty().not());
            cropHeightField.setEditable(false);
            GridPane.setConstraints(cropHeightField, 1, 1);
        widthHeightGrid.setVgap(5);
        widthHeightGrid.setHgap(5);
        widthHeightGrid.setAlignment(Pos.CENTER);
        widthHeightGrid.getChildren().addAll(cropWidthLabel, cropWidthField,
                                                cropHeightLabel, cropHeightField);

        HBox cropButtonsBox = new HBox();
        clearCropButton = new Button("Clear crop");
        clearCropButton.disableProperty().bind(useCropCheckBox.selectedProperty().not());
        cropColourButton = new Button("Crop colour");
        cropColourButton.disableProperty().bind(useCropCheckBox.selectedProperty().not());
        cropButtonsBox.getChildren().addAll(clearCropButton, cropColourButton);
        cropButtonsBox.setAlignment(Pos.CENTER);
        cropButtonsBox.setSpacing(10);

        cropOptions.getChildren().addAll(optionsLabel, useCropCheckBox, widthHeightGrid, cropButtonsBox);
        cropOptions.setAlignment(Pos.CENTER);
        cropOptions.setPadding(new Insets(5, 5, 5, 5));
        cropOptions.setSpacing(5);
        cropOptions.setId("cropExecLayoutCropOptions");
        HBox.setHgrow(cropOptions, Priority.NEVER);

        return cropOptions;
    }



    private Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(1, 1);
        return spacer;
    }

    private Pane createSpacer(int maxWidth){
        Pane spacer = createSpacer();
        spacer.setMaxWidth(maxWidth);
        return spacer;
    }


    private HBox createBottomBar(){
        HBox hBox = new HBox();
        backButton = new Button("< Back");
        backButton.setId("backBtn");
        backButton.setOnAction(CropExecuteLayoutListener.getInstance());

        runFitterButton = new Button("Run Fitter");
        runFitterButton.setId("runFitterBtn");
        runFitterButton.setOnAction(CropExecuteLayoutListener.getInstance());

        Pane spacer = createSpacer();

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backButton, spacer, runFitterButton);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }



    @Override
    public int getSceneMinWidth() {
        return 1000;
    }

    @Override
    public int getSceneMaxWidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSceneMinHeight() {
        return 500;
    }

    @Override
    public int getSceneMaxHeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void updateSize(double width, double height) {
        leftPane.setPrefHeight(height);
        lpImagesGrid.setTheHeight(height);
        cropPane.setPrefHeight(height);
        fitterOutputArea.setPrefHeight(height);
        fitterInterfacePane.setPrefHeight(height);
    }


    public void setLPTiles(ArrayList<ImageGridTile> tiles){
        for(ImageGridTile tile : tiles){
            lpImagesGrid.addImageTile(tile);
            tile.setParent(lpImagesGrid);
        }
        lpImagesGrid.setImageView(imagePreview);
    }


    public void enableCrop(){
        useCrop = true;
        cropRectangle.setVisible(true);
    }

    public void disableCrop(){
        useCrop = false;
        cropRectangle.setVisible(false);
    }
}
