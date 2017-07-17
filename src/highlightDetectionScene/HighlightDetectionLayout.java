package highlightDetectionScene;

import guiComponents.ImageCropPane;
import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGridForCrop;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.CreatorScene;
import utils.Utils;

import java.util.ArrayList;

/**
 * Created by Jed on 14-Jul-17.
 */
public class HighlightDetectionLayout extends VBox implements CreatorScene{

    private Button nextButton;
    private Button backButton;

    private ScrollableImageGridForCrop imageGrid;
    private ImageCropPane imageCropPane;


    private Label circleCropColourLabel;
    private ComboBox<ImageCropPane.Colour> circleCropColourBox;

    private Label sphereXLabel;
    private Button sphereXPlus;
    private Button sphereXMinus;
    private TextField sphereXField;

    private Label sphereYLabel;
    private Button sphereYPlus;
    private Button sphereYMinus;
    private TextField sphereYField;

    private Label sphereRadiusLabel;
    private Button sphereRPlus;
    private Button sphereRMinus;
    private TextField sphereRField;

    private Label setSphereLabel;
    private Button setSphereButton;

    private Image plusImage;
    private Image minusImage;

    private Label finalCircleXLabel;
    private TextField finalCircleX;

    private Label finalCircleYLabel;
    private TextField finalCircleY;

    private Label finalCircleRLabel;
    private TextField finalCircleR;

    private Label highlightLevelLabel;
    private Slider highlightLevelSlider;


    private static HighlightDetectionLayout ourInstance = new HighlightDetectionLayout();

    public static HighlightDetectionLayout getInstance() {
        return ourInstance;
    }

    private HighlightDetectionLayout() {
        plusImage = new Image("images/plus.png");
        minusImage = new Image("images/minus.png");

        HBox mainLayout = createMainLayout();
        HBox bottomBar = createBottomBar();

        getChildren().addAll(mainLayout, bottomBar);
        getStylesheets().add("stylesheets/default.css");
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(5);

        HighlightDetectionLayoutListener.getInstance().init(this);
    }



    private HBox createMainLayout(){
        HBox hBox = new HBox();

            VBox imagesAndTools= new VBox();
            imageGrid = new ScrollableImageGridForCrop("Selected Images", false,
                                                    true, true, imageCropPane);
            imagesAndTools.getChildren().add(imageGrid);
            imagesAndTools.setSpacing(5);

            imageGrid.setMinWidth(380);
            imageGrid.setMaxWidth(380);

            VBox cropAndToolsBox = new VBox();
                VBox toolsBox = createToolsBox();
                imagesAndTools.getChildren().add(toolsBox);
                imageCropPane = createImageCropPane(cropAndToolsBox);
                imageGrid.setImageView(imageCropPane);

            cropAndToolsBox.getChildren().addAll(imageCropPane);
            cropAndToolsBox.setSpacing(5);
            cropAndToolsBox.setAlignment(Pos.CENTER);


        hBox.getChildren().addAll(imagesAndTools, cropAndToolsBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);

        HBox.setHgrow(cropAndToolsBox, Priority.SOMETIMES);
        HBox.setHgrow(imageGrid, Priority.ALWAYS);

        return hBox;
    }




    private VBox createToolsBox(){
        VBox toolsBox = new VBox();

        Label toolsBoxLabel = new Label("Highlight Processing");
        toolsBoxLabel.setFont(Font.font(null, FontWeight.BOLD, 12));

        HBox tools = new HBox();

        GridPane spherePositionBox = createSpherePositionBox();


        tools.getChildren().addAll(spherePositionBox);
        tools.setAlignment(Pos.CENTER);

        toolsBox.getChildren().addAll(toolsBoxLabel, tools);
        toolsBox.setSpacing(10);
        toolsBox.setPadding(new Insets(5, 5, 5, 5));
        toolsBox.setAlignment(Pos.TOP_CENTER);
        toolsBox.getStyleClass().add("defaultBorder");
        toolsBox.setFillWidth(true);

        return toolsBox;
    }






    private GridPane createSpherePositionBox(){
        GridPane gridPane = new GridPane();

            sphereXLabel = new Label("Center X:");
            GridPane.setConstraints(sphereXLabel, 0, 1);

            sphereXField = createTextField(1, 1);
            sphereXMinus = createButton("-", "sphereXMinus", 2, 1);
            sphereXPlus = createButton("+", "sphereXPlus", 3, 1);


            sphereYLabel = new Label("Center Y:");
            GridPane.setConstraints(sphereYLabel, 0, 2);

            sphereYField = createTextField(1, 2);
            sphereYMinus = createButton("-", "sphereYMinus", 2, 2);
            sphereYPlus = createButton("+", "sphereYPlus", 3, 2);

            sphereRadiusLabel = new Label("Radius:");
            GridPane.setConstraints(sphereRadiusLabel, 0, 3);

            sphereRField = createTextField(1, 3);
            sphereRMinus = createButton("-", "sphereRMinus", 2, 3);
            sphereRPlus = createButton("+", "sphereRPlus", 3, 3);

            setSphereLabel = new Label("Set sphere:");
            GridPane.setConstraints(setSphereLabel, 0, 4);

            setSphereButton = new Button("Set");
            setSphereButton.setId("setSphereButton");
            setSphereButton.setOnAction(HighlightDetectionLayoutListener.getInstance());
            GridPane.setConstraints(setSphereButton, 1, 4, 3, 1);
            setSphereButton.setMaxWidth(Double.MAX_VALUE);


            circleCropColourLabel = new Label("Selector colour:");
            GridPane.setConstraints(circleCropColourLabel, 0, 0);

            circleCropColourBox = new ComboBox<>(FXCollections.observableArrayList(ImageCropPane.Colour.BLACK,
                                                                                    ImageCropPane.Colour.GREY,
                                                                                    ImageCropPane.Colour.WHITE,
                                                                                    ImageCropPane.Colour.RED,
                                                                                    ImageCropPane.Colour.GREEN,
                                                                                    ImageCropPane.Colour.BLUE));
            circleCropColourBox.setOnAction(HighlightDetectionLayoutListener.getInstance());
            circleCropColourBox.getSelectionModel().select(ImageCropPane.Colour.BLUE);
            circleCropColourBox.setMaxWidth(Double.MAX_VALUE);
            GridPane.setConstraints(circleCropColourBox, 1, 0, 3, 1);


        gridPane.getChildren().addAll(  sphereXLabel,           sphereXField,       sphereXMinus,   sphereXPlus,
                                        sphereYLabel,           sphereYField,       sphereYMinus,   sphereYPlus,
                                        sphereRadiusLabel,      sphereRField,       sphereRMinus,   sphereRPlus,
                                        setSphereLabel,         setSphereButton,
                                        circleCropColourLabel,  circleCropColourBox);


        gridPane.getStyleClass().add("noBorderClass");
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);


        return gridPane;
    }

    public void translateCircleSelect(int dx, int dy){
        imageCropPane.translateCircleFromGUI(dx, dy);
    }

    public void changeCircleR(int dr){
        imageCropPane.changeRFromGUI(dr);
    }

    private TextField createTextField(int col, int row){
        TextField textField = new TextField();
        GridPane.setConstraints(textField, col, row);
        textField.setEditable(false);
        textField.setPrefWidth(60);
        textField.setMinWidth(0);

        return textField;
    }


    private Button createButton(String label, String id, int col, int row){
        Button button = new Button();
        button.setId(id);
        button.setOnAction(HighlightDetectionLayoutListener.getInstance());
        GridPane.setConstraints(button, col, row);
        button.setShape(new Circle(12.5));

        ImageView imageView = new ImageView();
        if(label.equals("-")){
            imageView.setImage(minusImage);
        }else if(label.equals("+")){
            imageView.setImage(plusImage);
        }
        imageView.setFitWidth(8);
        imageView.setFitHeight(8);

        button.setGraphic(imageView);

        return button;
    }





    private ImageCropPane createImageCropPane(VBox vBox){
        ImageCropPane cropPane = new ImageCropPane(sphereXField, sphereYField,sphereRField);
        cropPane.prefHeightProperty().bind(vBox.heightProperty());
        cropPane.prefWidthProperty().bind(vBox.widthProperty());
        cropPane.setMinHeight(0);
        cropPane.setMinWidth(0);
        cropPane.setStyle("-fx-background-color: #000000;");

        return cropPane;
    }



    private HBox createBottomBar(){
        HBox hBox = new HBox();
        hBox.getStyleClass().add("bottomBar");
        backButton = new Button("< Back");
        backButton.setId("backButton");
        backButton.setOnAction(HighlightDetectionLayoutListener.getInstance());

        finalCircleXLabel = new Label("Final X:");
        finalCircleX = createBottomField("finalXField");

        finalCircleYLabel = new Label("Final Y:");
        finalCircleY = createBottomField("finalCircleY");

        finalCircleRLabel = new Label("Final R:");
        finalCircleR = createBottomField("finalCircleR");

        highlightLevelLabel = new Label("Highlight Threshold:");
        highlightLevelSlider = new Slider(0, 255, 200);
        highlightLevelSlider.setShowTickMarks(true);
        highlightLevelSlider.setShowTickLabels(true);
        highlightLevelSlider.setMajorTickUnit(50);
        highlightLevelSlider.setMinorTickCount(2);
        highlightLevelSlider.setBlockIncrement(10);

        nextButton = new Button("Detect Highlights >");
        nextButton.setId("nextButton");
        nextButton.setOnAction(HighlightDetectionLayoutListener.getInstance());

        Pane spacer = Utils.createSpacer();

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backButton, spacer, finalCircleXLabel, finalCircleX,
                                    finalCircleYLabel, finalCircleY, finalCircleRLabel, finalCircleR,
                                            highlightLevelLabel, highlightLevelSlider, nextButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }

    public void resetScene(){
        Utils.disableNodes(nextButton, finalCircleXLabel, finalCircleX,
                finalCircleYLabel, finalCircleY, finalCircleRLabel, highlightLevelLabel,
                    highlightLevelSlider, finalCircleR);
    }


    public void enableFinalParamsNodes(){
        Utils.enableNodes(nextButton, finalCircleXLabel, finalCircleX,
                finalCircleYLabel, finalCircleY, finalCircleRLabel, highlightLevelLabel,
                highlightLevelSlider, finalCircleR);
    }


    public void setFinalParamsFields(int x, int y, int r){
        finalCircleX.setText(String.valueOf(x));
        finalCircleY.setText(String.valueOf(y));
        finalCircleR.setText(String.valueOf(r));
    }

    public int[] getFinalParamsFields(){
        int x = Integer.parseInt(finalCircleX.getText());
        int y = Integer.parseInt(finalCircleY.getText());
        int r = Integer.parseInt(finalCircleR.getText());
        int thresh = (int) highlightLevelSlider.getValue();

        return new int[]{x, y, r, thresh};
    }

    public ImageGridTile[] getGridTiles(){
        return imageGrid.getGridTiles();
    }

    private TextField createBottomField(String id){
        TextField textField = new TextField();
        textField.setPrefWidth(60);
        textField.setEditable(false);
        textField.setId(id);

        return textField;
    }



    public void setTiles(ArrayList<ImageGridTile> imageGridTiles){
        for(ImageGridTile tile : imageGridTiles){
            imageGrid.addImageTile(tile);
            tile.setParent(imageGrid);
        }
    }

    public int[] getSphereVals(){
        try {
            int x = Integer.parseInt(sphereXField.getText());
            int y = Integer.parseInt(sphereYField.getText());
            int r = Integer.parseInt(sphereRField.getText());

            return new int[]{x, y, r};
        }catch(NumberFormatException e){
            return null;
        }
    }

    public Bounds getImageBounds(){
        return new BoundingBox(0, 0,
                imageCropPane.getImage().getWidth(), imageCropPane.getImage().getHeight());
    }


    public void setCircleSelectionActive(boolean active){
        imageCropPane.setCircleActive(active);
        imageCropPane.setCircleSelection(imageCropPane.getImageView().getBoundsInParent().getWidth() / 2,
                                            imageCropPane.getImageView().getBoundsInParent().getHeight() / 2,
                                            30);
        imageCropPane.changeColour(ImageCropPane.Colour.BLUE);
    }

    public void setCircleSelectionColour(ImageCropPane.Colour colour){
        imageCropPane.changeColour(colour);
    }


    public void setFirstTileSelected(){
        imageGrid.setSelectedTile(imageGrid.getGridTiles()[0]);
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
        imageGrid.setTheHeight(height);
        imageCropPane.updateSize();
    }

}
