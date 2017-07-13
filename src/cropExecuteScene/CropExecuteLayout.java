package cropExecuteScene;

import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGridForCrop;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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

    private ImageCropPane imageCropPane;
    private TextField cropWidthField;
    private TextField cropHeightField;
    private CheckBox useCropCheckBox;
    private ComboBox<ImageCropPane.Colour> cropColourSelector;


    private TextField fitterLocationField;
    private Button fitterLocationButton;
    private TextField outputLocationField;
    private Button outputLocationButton;

    private RadioButton ptmButton;
    private RadioButton hshButton;
    private RadioButton ptmRGBButton;
    private RadioButton ptmLRGBButton;
    private RadioButton hshTerms1;
    private RadioButton hshTerms2;
    private RadioButton hshTerms3;
    private HBox fitterOptionsBox;
    private Label ptmTypeLabel;
    private Label hshTermsLabel;

    private TextArea fitterOutputArea;

    private boolean useCrop;


    private static CropExecuteLayout ourInstance = new CropExecuteLayout();

    public static CropExecuteLayout getInstance() {
        return ourInstance;
    }

    private CropExecuteLayout() {
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        CropExecuteLayoutListener.getInstance().init(this);

        cropColourSelector.getSelectionModel().select(ImageCropPane.Colour.BLUE);
        imageCropPane.changeColour(ImageCropPane.Colour.BLUE);
        hshButton.setSelected(true);

        //imageCropPane.setImage(new Image("images/fish_fossil_01.jpg"));
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
                                                            true, true, imageCropPane);
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
            fitterLocationField.setEditable(false);
            GridPane.setConstraints(fitterLocationField, 1, 0);
            fitterLocationButton = new Button("Browse");
            fitterLocationButton.setId("browseFitterLocation");
            fitterLocationButton.setOnAction(CropExecuteLayoutListener.getInstance());
            GridPane.setConstraints(fitterLocationButton, 2, 0);

            Label outLocLabel = new Label("Output file:");
            GridPane.setConstraints(outLocLabel, 0, 1);
            outputLocationField = new TextField();
            outputLocationField.setEditable(false);
            GridPane.setConstraints(outputLocationField, 1, 1);
            outputLocationButton = new Button("Browse");
            outputLocationButton.setId("browseOutputLocation");
            outputLocationButton.setOnAction(CropExecuteLayoutListener.getInstance());
            GridPane.setConstraints(outputLocationButton, 2, 1);
        fitterOutLocPane.setHgap(10);
        fitterOutLocPane.setVgap(10);
        fitterOutLocPane.setPadding(new Insets(5, 5, 5, 5));
        fitterOutLocPane.getChildren().addAll(fitterLocLabel, fitterLocationField, fitterLocationButton,
                                                outLocLabel, outputLocationField, outputLocationButton);


        HBox fitterTypeBox = new HBox();
        fitterTypeBox.setId("fitterTypeBox");
        Label fitterTypeLabel = new Label("Fitter type:");
        ToggleGroup fitterTypeToggle = new ToggleGroup();
        ptmButton = new RadioButton("PTM");
        ptmButton.setId("ptmButton");
        ptmButton.setOnAction(CropExecuteLayoutListener.getInstance());
        ptmButton.setToggleGroup(fitterTypeToggle);
        hshButton = new RadioButton("HSH");
        hshButton.setId("hshButton");
        hshButton.setOnAction(CropExecuteLayoutListener.getInstance());
        hshButton.setToggleGroup(fitterTypeToggle);
        fitterTypeBox.getChildren().addAll(fitterTypeLabel, createSpacer(), ptmButton, createSpacer(), hshButton, createSpacer());
        fitterTypeBox.setPadding(new Insets(5, 5, 5, 5));

        fitterOptionsBox = new HBox();
        fitterOptionsBox.setId("fitterOptionsBox");
        ptmTypeLabel = new Label("PTM type:");
        ptmTypeLabel.setMinWidth(fitterTypeLabel.getWidth());
        ToggleGroup ptmTypeToggle = new ToggleGroup();
        ptmRGBButton = new RadioButton("RGB");
        ptmRGBButton.setId("rgbPTMButton");
        ptmRGBButton.setOnAction(CropExecuteLayoutListener.getInstance());
        ptmRGBButton.setToggleGroup(ptmTypeToggle);
        ptmLRGBButton = new RadioButton("LRGB");
        ptmLRGBButton.setId("lrgbPTMButton");
        ptmLRGBButton.setSelected(true);
        ptmLRGBButton.setOnAction(CropExecuteLayoutListener.getInstance());
        ptmLRGBButton.setToggleGroup(ptmTypeToggle);
        fitterOptionsBox.setAlignment(Pos.CENTER);

        hshTermsLabel = new Label("HSH Terms:");
        ToggleGroup hshToggleGroup = new ToggleGroup();
        hshTerms1 = new RadioButton("1");
        hshTerms1.setToggleGroup(hshToggleGroup);
        hshTerms2 = new RadioButton("2");
        hshTerms2.setToggleGroup(hshToggleGroup);
        hshTerms3 = new RadioButton("3");
        hshTerms3.setToggleGroup(hshToggleGroup);

        fitterOptionsBox.setPadding(new Insets(5, 5, 5, 5));
        fitterOptionsBox.getChildren().addAll(hshTermsLabel, createSpacer(), hshTerms1, createSpacer(),
                                                hshTerms2, createSpacer(), hshTerms3, createSpacer());
        hshTerms2.setSelected(true);



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
        vBox.getChildren().addAll(fitterPaneTitle, fitterTypeBox, fitterOptionsBox, fitterOutLocPane, fitterOutputBox);
        return vBox;
    }





    private VBox createCropPane(){
        VBox cropPane = new VBox();
            imageCropPane = new ImageCropPane(cropWidthField, cropHeightField);
            imageCropPane.setId("cropExecutePanePreview");
            imageCropPane.setStyle("-fx-background-color: #000000;");
            imageCropPane.prefHeightProperty().bind(cropPane.heightProperty());
            imageCropPane.setMinWidth(0);
            imageCropPane.setMinHeight(0);
            imageCropPane.prefWidthProperty().bind(cropPane.widthProperty());
            imageCropPane.prefHeightProperty().bind(cropPane.heightProperty());

        cropPane.setId("cropExecLayoutCropPane");
        cropPane.setAlignment(Pos.CENTER);
        cropPane.setSpacing(5);
        cropPane.setPadding(new Insets(0, 5, 0, 5));
        cropPane.getChildren().addAll(imageCropPane);
        return cropPane;
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
        Label cropColorLabel = new Label("Crop colour:");
        cropColorLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());
        cropColourSelector = createColourSelector();

        cropColourSelector.disableProperty().bind(useCropCheckBox.selectedProperty().not());
        cropButtonsBox.getChildren().addAll(cropColorLabel, cropColourSelector);
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

    private ComboBox<ImageCropPane.Colour> createColourSelector(){
        ComboBox<ImageCropPane.Colour> box = new ComboBox<>();

        box.getItems().addAll(ImageCropPane.Colour.BLACK, ImageCropPane.Colour.GREY, ImageCropPane.Colour.WHITE,
                                ImageCropPane.Colour.RED, ImageCropPane.Colour.GREEN, ImageCropPane.Colour.BLUE);

        box.setId("colourSelectorBox");
        box.setOnAction(CropExecuteLayoutListener.getInstance());
        return box;
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
        hBox.getStyleClass().add("bottomBar");
        backButton = new Button("< Back");
        backButton.setId("backBtn");
        backButton.setOnAction(CropExecuteLayoutListener.getInstance());

        runFitterButton = new Button("Run Fitter");
        runFitterButton.setId("runFitterButton");
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
        imageCropPane.updateSize();
    }


    public void setLPTiles(ArrayList<ImageGridTile> tiles){
        for(ImageGridTile tile : tiles){
            lpImagesGrid.addImageTile(tile);
            tile.setParent(lpImagesGrid);
        }
        lpImagesGrid.setImageView(imageCropPane);
    }


    public void enableCrop(){
        if(imageCropPane.getImage() == null){
            imageCropPane.setImage(lpImagesGrid.getAllImages()[0]);
        }

        useCrop = true;
        imageCropPane.setCropActive(true);
    }

    public void disableCrop(){
        useCrop = false;
        imageCropPane.setCropActive(false);
    }

    public void setCropColour(ImageCropPane.Colour colour){
        imageCropPane.changeColour(colour);
    }

    public void setPTMOptions(){
        fitterOptionsBox.getChildren().clear();
        fitterOptionsBox.getChildren().addAll(ptmTypeLabel, createSpacer(), ptmRGBButton,
                                            createSpacer(), ptmLRGBButton, createSpacer());
    }

    public void setHSHOptions(){
        fitterOptionsBox.getChildren().clear();
        fitterOptionsBox.getChildren().addAll(hshTermsLabel, createSpacer(), hshTerms1, createSpacer(),
                hshTerms2, createSpacer(), hshTerms3, createSpacer());
    }

    public boolean isUseCrop() {
        return useCrop;
    }

    public ScrollableImageGridForCrop getLpImagesGrid() {
        return lpImagesGrid;
    }

    public int[] getCropParams(){
        int x = imageCropPane.getCropXInImage();
        int y = imageCropPane.getCropYInImage();
        int width = imageCropPane.getCropWidthInImage();
        int height = imageCropPane.getCropHeightInImage();

        return new int[]{x, y, width, height};
    }


    public String getImagesFormat(){
        String imageName = lpImagesGrid.getGridTiles()[0].getName().toLowerCase();

        return imageName.substring(imageName.length() - 3);
    }


    public String getFitterLocation(){
        return fitterLocationField.getText();
    }

    public void setFitterLocation(String location){
        fitterLocationField.setText(location);
    }

    public String getOutputLocation(){
        return outputLocationField.getText();
    }

    public void setOutputLocation(String location){
        outputLocationField.setText(location);
    }

    public boolean ptmSelected(){
        return ptmButton.isSelected();
    }

    public boolean hshSelected(){
        return hshButton.isSelected();
    }

}
