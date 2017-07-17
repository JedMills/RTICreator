package initialScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import main.CreatorScene;
import main.Main;
import main.ProjectType;

/**
 * Created by Jed on 06-Jul-17.
 */
public class InitialLayout extends VBox implements CreatorScene {

    private Image rtiLogo;
    private RadioButton highlightProjBtn;
    private RadioButton lpFileProjBtn;
    private Button startNewProjectBtn;
    private TextField projectNameField;

    private static InitialLayout ourInstance = new InitialLayout();

    public static InitialLayout getInstance() {
        return ourInstance;
    }

    private InitialLayout() {
        rtiLogo = new Image("images/rtiLogo.png");
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        InitialSceneListener.getInstance().init(this);
    }


    private void createLayout(){
        BorderPane borderPane = new BorderPane();
        ImageView imageView = new ImageView(rtiLogo);
        borderPane.setCenter(imageView);
        borderPane.setStyle("-fx-background-color: #005dab;");
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageView.fitWidthProperty().bind(Main.primaryStage.widthProperty());
        imageView.setFitHeight(100);


        VBox projectOptionsContainer = new VBox();
        projectOptionsContainer.getStyleClass().add("defaultBorder");
        projectOptionsContainer.setPadding(new Insets(10 , 10 ,10, 10));

        HBox nameHBox = new HBox();
        Label projectNameLabel = new Label("Project name:");
        projectNameLabel.setMinWidth(Label.USE_PREF_SIZE);
        projectNameField = new TextField();
        projectNameField.setMinWidth(0);
        projectNameField.prefWidthProperty().bind(widthProperty());
        nameHBox.getChildren().addAll(projectNameLabel, projectNameField);
        nameHBox.setSpacing(10);
        nameHBox.setAlignment(Pos.CENTER_LEFT);

        HBox projTypeBox = new HBox();
        Label projectOptionsLabel = new Label("Project type:");


        ToggleGroup toggleGroup = new ToggleGroup();
        highlightProjBtn = new RadioButton("Highlight - detection");
        highlightProjBtn.setToggleGroup(toggleGroup);
        lpFileProjBtn = new RadioButton("Existing LP File");
        lpFileProjBtn.setToggleGroup(toggleGroup);

        projTypeBox.getChildren().addAll(projectOptionsLabel, createSpacer(), highlightProjBtn, createSpacer(), lpFileProjBtn, createSpacer());
        projTypeBox.setSpacing(10);
        projTypeBox.setAlignment(Pos.CENTER_LEFT);

        projectOptionsContainer.setSpacing(10);
        projectOptionsContainer.getChildren().addAll(nameHBox, projTypeBox);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("defaultBorder");


        startNewProjectBtn = new Button("Start new project");
        startNewProjectBtn.setId("startNewProject");
        startNewProjectBtn.setOnAction(InitialSceneListener.getInstance());


        hBox.getChildren().addAll(startNewProjectBtn);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 10, 0));
        setMargin(hBox, new Insets(10, 10, 10, 10));
        setMargin(projectOptionsContainer, new Insets(20, 10, 10, 10));

        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(borderPane, projectOptionsContainer, hBox);
    }

    private Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(1, 1);
        return spacer;
    }

    @Override
    public int getSceneMinWidth() {
        return 550;
    }

    @Override
    public int getSceneMaxWidth() {
        return 850;
    }

    @Override
    public int getSceneMinHeight() {
        return 320;
    }

    @Override
    public int getSceneMaxHeight() {
        return 320;
    }


    @Override
    public void updateSize(double width, double height) {

    }

    public RadioButton getHighlightProjBtn() {
        return highlightProjBtn;
    }

    public RadioButton getLpFileProjBtn() {
        return lpFileProjBtn;
    }

    public Button getStartNewProjectBtn() {
        return startNewProjectBtn;
    }

    public TextField getProjectNameField() {
        return projectNameField;
    }
}
