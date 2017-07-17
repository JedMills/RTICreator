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
 * Created by Jed on 06-Jul-17.
 */
public class Main extends Application {

    public static Stage primaryStage;
    private static CreatorScene currentScene;
    public static Alert inputAlert;
    public static FileChooser fileChooser;
    public static DirectoryChooser directoryChooser;
    public static Alert fileReadingAlert;
    private static LoadingDialog loadingDialog;
    public static Alert successAlert;

    public static RTIProject currentRTIProject;
    public static File currentImagesFolder;
    public static File currentAssemblyFolder;
    public static File currentLPFile;

    private static Scene initialScene;
    private static Scene newProjScene;
    private static Scene cropExecuteScene;
    private static Scene highlightDetectionScene;

    public static final Image thumbnail = new Image("images/rtiThumbnail.png");


    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("RTI Creator");
        primaryStage.getIcons().add(thumbnail);

        setupDialogs();
        createScenes();

        setCreatorStage(initialScene, InitialLayout.getInstance());
        //setCreatorStage(cropExecuteScene, CropExecuteLayout.getInstance());
        //setCreatorStage(highlightDetectionScene, HighlightDetectionLayout.getInstance());

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScene.updateSize(primaryStage.getWidth(), primaryStage.getHeight());
            }
        });

        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                currentScene.updateSize(primaryStage.getWidth(), primaryStage.getHeight());
            }
        });


        primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                currentScene.updateSize(primaryStage.getWidth(), primaryStage.getHeight());
            }
        });


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void createScenes(){
        initialScene = new Scene(InitialLayout.getInstance());
        newProjScene = new Scene(NewProjectLayout.getInstance());
        cropExecuteScene = new Scene(CropExecuteLayout.getInstance());
        highlightDetectionScene = new Scene(HighlightDetectionLayout.getInstance());
    }



    private static void setupDialogs(){
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



    private static void setCreatorStage(Scene scene, Parent layout){
        primaryStage.setScene(scene);

        CreatorScene creatorScene = (CreatorScene) layout;
        currentScene = creatorScene;

        primaryStage.setMinWidth(creatorScene.getSceneMinWidth());
        primaryStage.setMaxWidth(creatorScene.getSceneMaxWidth());
        primaryStage.setMinHeight(creatorScene.getSceneMinHeight());
        primaryStage.setMaxHeight(creatorScene.getSceneMaxHeight());
    }


    public static void changeToNewProjLayout(RTIProject rtiProject){
        currentRTIProject = rtiProject;
        NewProjectLayout.getInstance().setProject(rtiProject);
        setCreatorStage(newProjScene, NewProjectLayout.getInstance());
    }



    public static void changeToCropExecuteScene(ArrayList<ImageGridTile> approvedTiles){
        ArrayList<ImageGridTile> clones = (ArrayList<ImageGridTile>) approvedTiles.clone();
        CropExecuteLayout.getInstance().setLPTiles(clones);
        setCreatorStage(cropExecuteScene, CropExecuteLayout.getInstance());
        CropExecuteLayout.getInstance().setFirstGridTileSelected();
    }



    public static void showFileReadingAlert(String contextText){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fileReadingAlert.setContentText(contextText);
                fileReadingAlert.showAndWait();
            }
        });

    }


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

    public static void hideLoadingDialog(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadingDialog.hide();
            }
        });
    }

    public static void showInputAlert(String text){
        inputAlert.setContentText(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                inputAlert.show();
            }
        });
    }

    public static void showSuccessAlert(String text){
        successAlert.setContentText(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                successAlert.show();
            }
        });
    }


    public static void backButtonPressed(CreatorScene currentScene){
        if(currentScene == NewProjectLayout.getInstance()){
            setCreatorStage(initialScene, InitialLayout.getInstance());
            NewProjectLayout.getInstance().resetScene();

        }else if(currentScene == CropExecuteLayout.getInstance()){
            if(currentRTIProject.getProjectType().equals(ProjectType.DOME_LP)){
                setCreatorStage(newProjScene, NewProjectLayout.getInstance());
                CropExecuteLayout.getInstance().resetScene();

            }else if(currentRTIProject.getProjectType().equals(ProjectType.HIGHLIGHT)){


            }


        }
    }


    public static void changeToHighlightDetectionScene(ArrayList<ImageGridTile> tilesToCopy, File selectedImagesFile){
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



    private static class LoadingDialog{

        private ProgressIndicator progIndicator;
        private Stage stage;
        private Scene scene;
        private VBox vBox;
        private Label label;

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

        public void show(){
            stage.show();
        }

        public void hide(){
            stage.hide();
        }

        public void setText(String text) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    LoadingDialog.this.label.setText(text);
                }
            });
        }

        public void bringToFront(){
            stage.toFront();
        }
    }
}
