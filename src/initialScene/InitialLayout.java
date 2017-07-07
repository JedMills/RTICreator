package initialScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private ComboBox<ProjectType> projectOptions;
    private Button startNewProjectBtn;
    private Button openExistingProjectBtn;
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

        GridPane gridPane = new GridPane();

        Label projectNameLabel = new Label("Project name:");
        GridPane.setConstraints(projectNameLabel, 0, 0, 1, 1);

        projectNameField = new TextField();
        GridPane.setConstraints(projectNameField, 1, 0, 1, 1);
        projectNameField.setMinWidth(0);

        Label projectOptionsLabel = new Label("Project type:");
        GridPane.setConstraints(projectOptionsLabel, 0, 1, 1, 1);

        projectOptions = new ComboBox<>();
        projectOptions.getItems().setAll(ProjectType.values());
        GridPane.setConstraints(projectOptions, 1, 1, 1, 1);
        projectOptions.setMinWidth(0);


        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setId("initialLayoutGridPane");
        gridPane.setPadding(new Insets(10, 0, 10, 0));

        setMargin(gridPane, new Insets(10, 10, 10, 10));

        gridPane.getChildren().addAll(projectNameLabel, projectNameField, projectOptionsLabel, projectOptions);

        HBox hBox = new HBox();
        hBox.setId("initialLayoutHBox");

        startNewProjectBtn = new Button("Start new project");
        startNewProjectBtn.setId("startNewProject");
        startNewProjectBtn.setOnAction(InitialSceneListener.getInstance());

        openExistingProjectBtn = new Button("Open existing project");
        openExistingProjectBtn.setId("openExistingProject");
        openExistingProjectBtn.setOnAction(InitialSceneListener.getInstance());

        hBox.getChildren().addAll(startNewProjectBtn, openExistingProjectBtn);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 10, 0));
        setMargin(hBox, new Insets(10, 10, 10, 10));

        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(borderPane, gridPane, hBox);
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
        projectNameField.setPrefWidth(width / 1.5);
        projectOptions.setPrefWidth(width / 1.5);
    }


    public ComboBox<ProjectType> getProjectOptions() {
        return projectOptions;
    }

    public Button getStartNewProjectBtn() {
        return startNewProjectBtn;
    }

    public Button getOpenExistingProjectBtn() {
        return openExistingProjectBtn;
    }

    public TextField getProjectNameField() {
        return projectNameField;
    }
}
