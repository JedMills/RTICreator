package main;

import cropExecuteScene.CropExecuteLayout;
import guiComponents.ImageGridTile;
import highlightDetectionScene.HighlightDetectionLayout;
import initialScene.InitialLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import newProjectScene.NewProjectLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * The Main class. This is the JavaFX application that gets run by the user.. The application starts off at the
 * {@link InitialLayout}, and moves through different layouts depending on the type of project the user has chosen.
 * This class has global fields that get updated as the user moves through the process of creating RTI files that hold
 * the location of files and directories on the disk that are required to build the RTI files, such as the location
 * of the currently used light positions file. This class is also responsible for switching the layouts as the
 * user progresses / moves back in the program.
 *
 * @author Jed Mills
 */
public class Main extends Application {

    /** The window that all the main layouts in the app exist in */
    public static Stage primaryStage;

    /** The current scene that the user is on*/
    private static CreatorScene currentScene;

    /** Alert shown when the user types in or selects something bad */
    public static Alert inputAlert;

    /** File chooser for choosing files such as the lp file */
    public static FileChooser fileChooser;

    /** Directory chooser for choosing dirs such as the images folder / assembly folder */
    public static DirectoryChooser directoryChooser;

    /** Alert shown when the app cannot read or parse a file the user is loading */
    public static Alert fileReadingAlert;

    /** Dialog shown when the app is working on something big */
    private static LoadingDialog loadingDialog;

    /** Alert shown when something is successful */
    public static Alert successAlert;

    /** The current project that the user is working on */
    public static RTIProject currentRTIProject;

    /** The current folder the user has chosen containing the images for the highlight / no path lp projects */
    public static File currentImagesFolder;

    /** Folder where assembly files are put */
    public static File currentAssemblyFolder;

    /** File containing lp data */
    public static File currentLPFile;

    /** Scene made from the {@link InitialLayout}*/
    private static Scene initialScene;

    /** Scene made from the {@link NewProjectLayout}*/
    private static Scene newProjScene;

    /** Scene made from the {@link CropExecuteLayout}*/
    private static Scene cropExecuteScene;

    /** Scene made from the {@link HighlightDetectionLayout}*/
    private static Scene highlightDetectionScene;

    /** Location of the RTI logo for the thumbnail in the corner of the windows */
    public static final Image thumbnail = new Image("images/rtiThumbnail.png");

    /** Image formats accepted by the app */
    public static final String[] acceptedFormats = {"jpg", "png", "tif", "bmp"};




    /**
     * Starts the JavaFx application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }




    /**
     * Runs the app. Creates the scenes from the layouts, bins the width and height of the window to the update
     * size methods of the scenes. Shows the main window with the {@link InitialLayout}
     *
     * @param primaryStage  the window fo the app
     * @throws Exception    if the window can't be created
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("RTI Creator");
        primaryStage.getIcons().add(thumbnail);

        //make all the app dialogs
        setupDialogs();

        //make all the scenes from the layouts
        createScenes();

        //set the initial scene as the first oe
        setCreatorStage(initialScene, InitialLayout.getInstance());

        ChangeListener<Number> sizeChanged = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScene.updateSize(primaryStage.getWidth(), primaryStage.getHeight());
            }
        };

        primaryStage.widthProperty().addListener(sizeChanged);
        primaryStage.heightProperty().addListener(sizeChanged);

        primaryStage.show();
    }


    /**
     * Creates the scenes that the app can change to during the program from the layouts.
     */
    private void createScenes(){
        initialScene = new Scene(InitialLayout.getInstance());
        newProjScene = new Scene(NewProjectLayout.getInstance());
        cropExecuteScene = new Scene(CropExecuteLayout.getInstance());
        highlightDetectionScene = new Scene(HighlightDetectionLayout.getInstance());
    }




    /**
     * Creates the dialogs tht are shown by the various scenes.
     */
    private static void setupDialogs(){
        //remove the header text as the dialogs look better without it
        inputAlert = new Alert(Alert.AlertType.INFORMATION);
        inputAlert.setTitle("Invalid Input");
        inputAlert.setHeaderText("");

        fileChooser = new FileChooser();
        directoryChooser = new DirectoryChooser();

        fileReadingAlert = new Alert(Alert.AlertType.ERROR);
        fileReadingAlert.setTitle("Error reading files");
        fileReadingAlert.setHeaderText("");

        loadingDialog = new LoadingDialog();

        successAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText("");
    }




    /**
     * Sets the current scene that the app is displaying to the given one. The scne and its layout must match
     * or bad things will happen.
     *
     * @param scene     scene to switch to
     * @param layout    its layout to draw onto the window
     */
    private static void setCreatorStage(Scene scene, Parent layout){
        //set the scene and layout
        primaryStage.setScene(scene);

        CreatorScene creatorScene = (CreatorScene) layout;
        currentScene = creatorScene;

        //and the size boundaries
        primaryStage.setMinWidth(creatorScene.getSceneMinWidth());
        primaryStage.setMaxWidth(creatorScene.getSceneMaxWidth());
        primaryStage.setMinHeight(creatorScene.getSceneMinHeight());
        primaryStage.setMaxHeight(creatorScene.getSceneMaxHeight());
    }


    /**
     * Changes the app to the {@link NewProjectLayout}.
     *
     * @param rtiProject the current rti project that the user is making
     */
    public static void changeToNewProjLayout(RTIProject rtiProject){
        currentRTIProject = rtiProject;
        NewProjectLayout.getInstance().setProject(rtiProject);
        setCreatorStage(newProjScene, NewProjectLayout.getInstance());
    }


    /**
     * Change the app to the {@link CropExecuteLayout}, which will show the grid tiles passed.
     *
     * @param approvedTiles     the grid tiles to display in the CropExecuteLayout
     */
    public static void changeToCropExecuteScene(ArrayList<ImageGridTile> approvedTiles){
        //clone the tiles s they don't disappear from the image grid of the last layout, so they'll
        //still be there if the user clicks the back button
        ArrayList<ImageGridTile> clones = (ArrayList<ImageGridTile>) approvedTiles.clone();
        CropExecuteLayout.getInstance().setLPTiles(clones);

        //switch the the new layout on the JavaFX thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                CropExecuteLayout.getInstance().setFirstGridTileSelected();
                setCreatorStage(cropExecuteScene, CropExecuteLayout.getInstance());
            }
        });
    }


    /**
     * Show the {@link Main#fileReadingAlert} on the JavaFx thread with the passed text.
     *
     * @param contextText   text toshow in the dialog
     */
    public static void showFileReadingAlert(String contextText){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fileReadingAlert.setContentText(contextText);
                fileReadingAlert.showAndWait();
            }
        });

    }

    /**
     * Show the {@link Main#loadingDialog} on the JavaFx thread with the passed text.
     *
     * @param text   text to show in the dialog
     */
    public static void showLoadingDialog(String text){
        loadingDialog.setText(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
                loadingDialog.bringToFront();
            }
        });
    }

    /**
     * Hide the {@link Main#loadingDialog} on the JavaFx thread.
     */
    public static void hideLoadingDialog(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hide();
            }
        });
    }


    /**
     * Show the {@link Main#inputAlert} on the JavaFx thread with the passed text.
     *
     * @param text   text toshow in the dialog
     */
    public static void showInputAlert(String text){
        inputAlert.setContentText(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                inputAlert.show();
            }
        });
    }


    /**
     * Called when the use clicks the 'Back' button in a scene. Will move the scene back to the last one depending
     * on what type of project the user is making.
     *
     * @param currentScene      the scene that the user is currently on
     */
    public static void backButtonPressed(CreatorScene currentScene){
        if(currentScene == NewProjectLayout.getInstance()){
            //all projects will go back to the initial layout of they're on the NewProjectLayout
            setCreatorStage(initialScene, InitialLayout.getInstance());
            NewProjectLayout.getInstance().resetScene();

        }else if(currentScene == CropExecuteLayout.getInstance()){
            //go back to the appropriate scene if we're in the CropExecuteLayout
            if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.LP)){
                setCreatorStage(newProjScene, NewProjectLayout.getInstance());
                CropExecuteLayout.getInstance().resetScene();

            }else if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.HIGHLIGHT)){
                setCreatorStage(highlightDetectionScene, HighlightDetectionLayout.getInstance());
                CropExecuteLayout.getInstance().resetScene();

            }else if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.EXISTING_LP)){
                CropExecuteLayout.getInstance().resetScene();
                setCreatorStage(initialScene, InitialLayout.getInstance());
            }


        }else if(currentScene == HighlightDetectionLayout.getInstance()){
            //highlight detection layout has to be after the NewProjectLayout
            changeToNewProjLayout(Main.currentRTIProject);
        }
    }


    /**
     * Called when the use clicks the 'Back' button in a scene. Will move the scene back to the last one depending
     * on what type of project the user is making, and pass the array of image grid tiles to the scene to show
     * so the user doesn't have to reload the images.
     *
     * @param creatorScene      the scene that the user is currently on
     * @param tilesToPass       tiles to pass back to the last scene
     */
    public static void backButtonPressed(CreatorScene creatorScene, ImageGridTile[] tilesToPass){
        if(creatorScene == CropExecuteLayout.getInstance()){

            ArrayList<ImageGridTile> tilesArray = new ArrayList<>();
            for(ImageGridTile tile : tilesToPass){tilesArray.add(tile);}

            if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.HIGHLIGHT)){
                changeToHighlightDetectionScene(tilesArray, true);

            }else if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.LP)){
                changeToNewProjLayout(Main.currentRTIProject);
                NewProjectLayout.getInstance().addTilesToSelected(tilesArray);

            }else if(currentRTIProject.getProjectType().equals(RTIProject.ProjectType.EXISTING_LP)){
                backButtonPressed(creatorScene);

            }
        }else{
            backButtonPressed(creatorScene);
        }
    }


    /**
     * Changes the current scene to the {@link HighlightDetectionLayout}, giving it the passed ImageGridTiles
     * so that these can be loaded into the grid pane of the scene for the user to looks at.
     *
     * @param tilesToCopy               tiles to show in the highlight layout
     * @param removeExistingTiles       whether to clear the tiles already in the scene
     */
    public static void changeToHighlightDetectionScene(ArrayList<ImageGridTile> tilesToCopy,
                                                                        boolean removeExistingTiles){
        //reset the scne if we're loading new images into it
        if(removeExistingTiles){
            HighlightDetectionLayout.getInstance().resetImageGrid();
        }

        //now load in all the images and set up the scene and switch to it
        HighlightDetectionLayout.getInstance().setTiles(tilesToCopy);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setCreatorStage(highlightDetectionScene, HighlightDetectionLayout.getInstance());
                HighlightDetectionLayout.getInstance().setFirstTileSelected();
                HighlightDetectionLayout.getInstance().setCircleSelectionActive(true);
                HighlightDetectionLayout.getInstance().resetScene();
            }
        });
    }



    /**
     * This is the dialog box that appears when the app is loading or reading images/lp files etc. It has a blue
     * spinning progress indicator and text to display what is happening.
     */
    private static class LoadingDialog{

        /** The circle of blue dots that indicate something is actually happening */
        private ProgressIndicator progIndicator;

        /** The window that this dialog exists oin*/
        private Stage stage;

        /** The stuff that is shown in the stage*/
        private Scene scene;

        /** The VBox tha actually contains the loading label and progress indicator */
        private VBox vBox;

        /** The label that is updated with a message for what is happening */
        private Label label;


        /**
         * Create a new LoadingDialog with the layout described in the class declaration.
         */
        public LoadingDialog(){
            stage = new Stage(StageStyle.UNDECORATED);
            progIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
            progIndicator.setId("loadingDialogProgressIndicator");

            vBox = new VBox();
            vBox.setSpacing(20);
            vBox.setId("loadingDialogMainBox");

            vBox.setAlignment(Pos.CENTER);
            label = new Label();
            label.setFont(Font.font(20));
            label.setId("loadingDialogLabel");

            vBox.getChildren().addAll(label, progIndicator);
            vBox.setPadding(new Insets(20, 20,20, 20));
            vBox.setStyle("-fx-border-width: 2; -fx-border-color: #dddddd;");
            scene = new Scene(vBox);
            stage.setScene(scene);
        }

        /**
         * Shows the dialog box.
         */
        public void show(){
            stage.show();
        }

        /**
         * Hides the dialog box.
         */
        public void hide(){
            stage.hide();
        }

        /**
         * @param text the text of the dialog to set above the progress indicator
         */
        public void setText(String text) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.this.label.setText(text);
                }
            });
        }

        /**
         * Brings the loading dialog above all the other windows.
         */
        public void bringToFront(){
            stage.toFront();
        }
    }
}
