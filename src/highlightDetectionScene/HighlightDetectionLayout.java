package highlightDetectionScene;

import guiComponents.ImageCropPane;
import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGrid;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

    private ScrollableImageGrid imageGrid;
    private ImageCropPane imageCropPane;

    private Label sphereDetectTitle;

    private Label sphereColorBoxLabel;
    private ComboBox<String> sphereColourComboBox;

    private Label detectSphereLabel;
    private Button detectSphereButton;

    private Label setSphereTitle;

    private Label sphereXLabel;
    private Spinner<Integer> sphereXSpinner;

    private Label sphereYLabel;
    private Spinner<Integer> sphereYSpinner;

    private Label sphereRadiusLabel;
    private Spinner<Integer> sphereRadiusSpinner;

    private Label setSphereLabel;
    private Button setSphereButton;

    private Label lastToolboxTitle;

    private Button highlightDetectButton;
    private Button redoProcessButton;


    private enum HighlightProcessState{DETECT_SPHERE, POSITION_SPHERE, HIGHLIGHT_DETECT;}

    private HighlightProcessState currentState;

    private static HighlightDetectionLayout ourInstance = new HighlightDetectionLayout();

    public static HighlightDetectionLayout getInstance() {
        return ourInstance;
    }

    private HighlightDetectionLayout() {
        HBox mainLayout = createMainLayout();
        HBox bottomBar = createBottomBar();

        getChildren().addAll(mainLayout, bottomBar);
        getStylesheets().add("stylesheets/default.css");
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(5);

        imageCropPane.setImage(new Image("images/fish_fossil_01.jpg"));

        setCurrentState(HighlightProcessState.DETECT_SPHERE);
    }



    private HBox createMainLayout(){
        HBox hBox = new HBox();

            imageGrid = new ScrollableImageGrid("Selected Images", true, true, true);
            imageGrid.setMinWidth(300);
            imageGrid.setMaxWidth(500);

            VBox cropAndToolsBox = new VBox();
                imageCropPane = createImageCropPane(cropAndToolsBox);

                VBox toolsBox = createToolsBox();

            cropAndToolsBox.getChildren().addAll(imageCropPane, toolsBox);
            cropAndToolsBox.setSpacing(5);
            cropAndToolsBox.setAlignment(Pos.CENTER);


        hBox.getChildren().addAll(imageGrid, cropAndToolsBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);

        HBox.setHgrow(cropAndToolsBox, Priority.SOMETIMES);
        HBox.setHgrow(imageGrid, Priority.SOMETIMES);

        return hBox;
    }


    private ImageView createArrowImageView(Image image, int width, int height){
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);

        return imageView;
    }



    private VBox createToolsBox(){
        VBox toolsBox = new VBox();

        Image arrow = new Image("images/arrow-right-6x.png");

        Label toolsBoxLabel = new Label("Highlight Processing");
        toolsBoxLabel.setFont(Font.font(null, FontWeight.BOLD, 12));

        HBox tools = new HBox();

        VBox sphereDetectionBox = createSphereDetectionBox();

        VBox firstArrow = new VBox(createArrowImageView(arrow, 25, 25));
        firstArrow.setAlignment(Pos.CENTER);

        VBox spherePositionBox = createSpherePositionBox();


        VBox secondArrow = new VBox(createArrowImageView(arrow, 25, 25));
        secondArrow.setAlignment(Pos.CENTER);

        VBox endBox = createLastToolbox();


        tools.getChildren().addAll(sphereDetectionBox, firstArrow, spherePositionBox, secondArrow, endBox);
        tools.setAlignment(Pos.CENTER);
        HBox.setHgrow(sphereDetectionBox, Priority.SOMETIMES);
        HBox.setHgrow(firstArrow, Priority.SOMETIMES);
        HBox.setHgrow(spherePositionBox, Priority.SOMETIMES);
        HBox.setHgrow(secondArrow, Priority.SOMETIMES);
        HBox.setHgrow(endBox, Priority.SOMETIMES);

        toolsBox.getChildren().addAll(toolsBoxLabel, tools);
        toolsBox.setSpacing(10);
        toolsBox.setPadding(new Insets(5, 5, 5, 5));
        toolsBox.setAlignment(Pos.TOP_CENTER);
        toolsBox.getStyleClass().add("defaultBorder");
        toolsBox.setFillWidth(true);

        return toolsBox;
    }




    private VBox createLastToolbox(){
        VBox vBox = new VBox();

        lastToolboxTitle = new Label("Highlight Detection");

        highlightDetectButton = new Button("Detect highlights");
        highlightDetectButton.setId("detectHighlightsButton");
        highlightDetectButton.setOnAction(HighlightDetectionLayoutListener.getInstance());
        highlightDetectButton.setMaxWidth(Double.MAX_VALUE);

        redoProcessButton = new Button("Redo process");
        redoProcessButton.setId("redoProcessButton");
        redoProcessButton.setOnAction(HighlightDetectionLayoutListener.getInstance());
        redoProcessButton.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().addAll(lastToolboxTitle, highlightDetectButton, redoProcessButton);
        vBox.setSpacing(10);
        vBox.getStyleClass().add("defaultBorder");
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));

        return vBox;
    }




    private VBox createSpherePositionBox(){
        VBox vBox = new VBox();

            setSphereTitle = new Label("Sphere Positioning");

            GridPane gridPane = new GridPane();

                sphereXLabel = new Label("Center X:");
                GridPane.setConstraints(sphereXLabel, 0, 0);

                sphereXSpinner = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
                sphereXSpinner.setPrefWidth(100);
                GridPane.setConstraints(sphereXSpinner, 1, 0);

                sphereYLabel = new Label("Center Y:");
                GridPane.setConstraints(sphereYLabel, 0, 1);

                sphereYSpinner = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
                sphereYSpinner.setPrefWidth(100);
                GridPane.setConstraints(sphereYSpinner, 1, 1);

                sphereRadiusLabel = new Label("Radius:");
                GridPane.setConstraints(sphereRadiusLabel, 0, 2);

                sphereRadiusSpinner = new Spinner<>(0, Integer.MAX_VALUE, 0, 1);
                sphereRadiusSpinner.setPrefWidth(100);
                GridPane.setConstraints(sphereRadiusSpinner, 1, 2);

                setSphereLabel = new Label("Set sphere:");
                GridPane.setConstraints(setSphereLabel, 0, 3);

                setSphereButton = new Button("Set");
                setSphereButton.setId("setSphereButton");
                setSphereButton.setOnAction(HighlightDetectionLayoutListener.getInstance());
                GridPane.setConstraints(setSphereButton, 1, 3);

            gridPane.getChildren().addAll(  sphereXLabel,       sphereXSpinner,
                                            sphereYLabel,       sphereYSpinner,
                                            sphereRadiusLabel,  sphereRadiusSpinner,
                                            setSphereLabel,     setSphereButton);
            gridPane.getStyleClass().add("noBorderClass");
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(10);


        vBox.getChildren().addAll(setSphereTitle, gridPane);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);
        vBox.getStyleClass().add("defaultBorder");
        vBox.setPadding(new Insets(5, 5, 5, 5));

        return vBox;
    }






    private VBox createSphereDetectionBox(){
        VBox vBox = new VBox();

        sphereDetectTitle = new Label("Sphere Detection");

        GridPane gridPane = new GridPane();
        sphereColorBoxLabel = new Label("Sphere colour:");
        GridPane.setConstraints(sphereColorBoxLabel, 0, 0);
        sphereColourComboBox = new ComboBox<>();
        sphereColourComboBox.setItems(FXCollections.observableArrayList("Black", "Red"));
        sphereColourComboBox.getSelectionModel().select(0);
        GridPane.setConstraints(sphereColourComboBox, 1, 0);

        detectSphereLabel = new Label("Detect sphere:");
        GridPane.setConstraints(detectSphereLabel, 0, 1);

        detectSphereButton = new Button("Detect");
        detectSphereButton.setId("detectSphereButton");
        detectSphereButton.setOnAction(HighlightDetectionLayoutListener.getInstance());
        detectSphereButton.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(detectSphereButton, 1, 1);

        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.getStyleClass().add("noBorderClass");
        gridPane.getChildren().addAll(  sphereColorBoxLabel,    sphereColourComboBox,
                                        detectSphereLabel,      detectSphereButton);
        gridPane.setAlignment(Pos.CENTER);


        vBox.getChildren().addAll(sphereDetectTitle, gridPane);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(10);
        vBox.getStyleClass().add("defaultBorder");
        vBox.setPadding(new Insets(5, 5, 5,5));

        return vBox;
    }





    private ImageCropPane createImageCropPane(VBox vBox){
        ImageCropPane cropPane = new ImageCropPane();
        cropPane.prefHeightProperty().bind(vBox.heightProperty());
        cropPane.prefWidthProperty().bind(vBox.widthProperty());
        cropPane.setMinHeight(0);
        cropPane.setMinWidth(0);
        cropPane.setStyle("-fx-background-color: #000000;");

        //cropPane.setCropActive(true);
        //cropPane.changeColour(ImageCropPane.Colour.BLUE);

        return cropPane;
    }



    private HBox createBottomBar(){
        HBox hBox = new HBox();
        hBox.getStyleClass().add("bottomBar");
        backButton = new Button("< Back");
        backButton.setId("backButton");
        backButton.setOnAction(HighlightDetectionLayoutListener.getInstance());

        nextButton = new Button("Next >");
        nextButton.setId("nextButton");
        nextButton.setOnAction(HighlightDetectionLayoutListener.getInstance());

        Pane spacer = Utils.createSpacer();

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backButton, spacer, nextButton);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }


    public void setCurrentState(HighlightProcessState state){
        currentState = state;
        disableAllToolboxNodes();

        if(state.equals(HighlightProcessState.DETECT_SPHERE)){
            enableNodes(sphereDetectTitle, sphereColorBoxLabel, sphereColourComboBox,
                                detectSphereLabel, detectSphereButton);

        }else if(state.equals(HighlightProcessState.POSITION_SPHERE)){
            enableNodes(setSphereTitle, sphereXLabel, sphereXSpinner, sphereYLabel, sphereYSpinner,
                            sphereRadiusLabel, sphereRadiusSpinner, setSphereLabel, setSphereButton);

        }else if(state.equals(HighlightProcessState.HIGHLIGHT_DETECT)){
            enableNodes(lastToolboxTitle, highlightDetectButton, redoProcessButton);

        }
    }


    private void disableAllToolboxNodes(){
        disableNodes(sphereDetectTitle, setSphereTitle, lastToolboxTitle,
                        sphereColorBoxLabel, sphereColourComboBox, detectSphereLabel, detectSphereButton,
                                sphereXLabel, sphereXSpinner, sphereYLabel, sphereYSpinner,
                                    sphereRadiusLabel, sphereRadiusSpinner, setSphereLabel,
                                        setSphereButton, highlightDetectButton, redoProcessButton);
    }

    private void disableNodes(Node... nodes){
        for (Node node : nodes){node.setDisable(true);}
    }

    private void enableNodes(Node... nodes){
        for(Node node : nodes){node.setDisable(false);}
    }


    public void setTiles(ArrayList<ImageGridTile> imageGridTiles){
        for(ImageGridTile tile : imageGridTiles){
            imageGrid.addImageTile(tile);
            tile.setParent(imageGrid);
        }
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
