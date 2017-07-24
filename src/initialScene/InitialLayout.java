package initialScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import main.CreatorScene;
import main.Main;

/**
 * The initial scene that the user is greeted with. Has the box to type the project name in, the choice for the kind of
 * project, and the button to start the project. Has the gorgeous Exeter RTI logo too.
 *
 * @author Jed Mills
 */
public class InitialLayout extends VBox implements CreatorScene {

    /** RTI logo banner at the top */
    private Image rtiLogo;

    /** Button for a highlight - detection project */
    private RadioButton highlightProjBtn;

    /** Button for an lp file project without full image paths */
    private RadioButton lpFileProjBtn;

    /** Button for an lp file project with full image paths */
    private RadioButton lpFileExistingProject;

    /** Button to start the project */
    private Button startNewProjectBtn;

    /** field to write the project name in */
    private TextField projectNameField;

    /** Singleton instance of this layout */
    private static InitialLayout ourInstance = new InitialLayout();

    /**
     * @return {@link InitialLayout#ourInstance}
     */
    public static InitialLayout getInstance() {
        return ourInstance;
    }


    /**
     * Creates a new InitialLayout. Adds the {@link InitialSceneListener} to listen to clicks etc.
     */
    private InitialLayout() {
        rtiLogo = new Image("images/rtiLogo.png");
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        InitialSceneListener.getInstance().init(this);
    }


    /**
     * Create all the widgets and layout for this scene.
     */
    private void createLayout(){
        //the RTI logo banner at the top of this layout
        BorderPane borderPane = new BorderPane();
        ImageView imageView = new ImageView(rtiLogo);
        borderPane.setCenter(imageView);
        //make the banner the same colour as the logo background so it looks all part of it
        borderPane.setStyle("-fx-background-color: #005dab;");
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        //make the logo banner always pan the layout
        imageView.fitWidthProperty().bind(Main.primaryStage.widthProperty());
        imageView.setFitHeight(100);

        //vbox containing all the buttons and fields etc.
        VBox projectOptionsContainer = new VBox();
            projectOptionsContainer.getStyleClass().add("defaultBorder");
            projectOptionsContainer.setPadding(new Insets(10 , 10 ,10, 10));

            //hbox for the name field
            HBox nameHBox = new HBox();
                Label projectNameLabel = new Label("Project name:");
                projectNameLabel.setMinWidth(Label.USE_PREF_SIZE);
                projectNameField = new TextField();
                projectNameField.setMinWidth(0);
                projectNameField.prefWidthProperty().bind(widthProperty());

            nameHBox.getChildren().addAll(projectNameLabel, projectNameField);
            nameHBox.setSpacing(10);
            nameHBox.setAlignment(Pos.CENTER_LEFT);

            //hbox for theproject type radio buttons
            HBox projTypeBox = new HBox();
                Label projectOptionsLabel = new Label("Project type:");
                ToggleGroup toggleGroup = new ToggleGroup();
                highlightProjBtn = new RadioButton("Highlight detection");
                highlightProjBtn.setToggleGroup(toggleGroup);
                lpFileProjBtn = new RadioButton("LP file - image folder");
                lpFileProjBtn.setToggleGroup(toggleGroup);
                lpFileExistingProject = new RadioButton("LP file - full paths");
                lpFileExistingProject.setToggleGroup(toggleGroup);

            projTypeBox.getChildren().addAll(projectOptionsLabel, createSpacer(), highlightProjBtn,
                                            createSpacer(), lpFileProjBtn, createSpacer(),
                                                lpFileExistingProject, createSpacer());
            projTypeBox.setSpacing(10);
            projTypeBox.setAlignment(Pos.CENTER_LEFT);

        projectOptionsContainer.setSpacing(10);
        projectOptionsContainer.getChildren().addAll(nameHBox, projTypeBox);

        //hbox for the start project button
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


    /**
     * @return  a new spacer that expands to fill all available width
     */
    private Pane createSpacer(){
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(1, 1);
        return spacer;
    }


    /**
     * @return min width of the scene
     */
    @Override
    public int getSceneMinWidth() {
        return 550;
    }

    /**
     * @return max width of the scene
     */
    @Override
    public int getSceneMaxWidth() {
        return 850;
    }


    /**
     * @return min height of the scene
     */
    @Override
    public int getSceneMinHeight() {
        return 320;
    }


    /**
     * @return max height of the scene
     */
    @Override
    public int getSceneMaxHeight() {
        return 320;
    }


    /**
     * All the widgets in this layout are bound to the window properties, so this method is empty.
     *
     * @param width     width of the window
     * @param height    height of the window
     */
    @Override
    public void updateSize(double width, double height) {}


    /**
     * @return  {@link InitialLayout#highlightProjBtn}
     */
    public RadioButton getHighlightProjBtn() {
        return highlightProjBtn;
    }


    /**
     * @return {@link InitialLayout#lpFileProjBtn}
     */
    public RadioButton getLpFileProjBtn() {
        return lpFileProjBtn;
    }


    /**
     * @return {@link InitialLayout#projectNameField}
     */
    public TextField getProjectNameField() {
        return projectNameField;
    }


    /**
     * @return {@link InitialLayout#lpFileExistingProject}
     */
    public RadioButton getLpFileExistingProject() {
        return lpFileExistingProject;
    }
}
