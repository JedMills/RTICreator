package main;

import initialScene.InitialLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import newProjectScene.NewProjectLayout;

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

    private static RTIProject currentRTIProjct;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("RTI Creator");

        setupDialogs();

        setCreatorStage(InitialLayout.getInstance());

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

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
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
    }



    private static void setCreatorStage(Parent layout){
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);

        CreatorScene creatorScene = (CreatorScene) layout;
        currentScene = creatorScene;

        primaryStage.setMinWidth(creatorScene.getSceneMinWidth());
        primaryStage.setMaxWidth(creatorScene.getSceneMaxWidth());
        primaryStage.setMinHeight(creatorScene.getSceneMinHeight());
        primaryStage.setMaxHeight(creatorScene.getSceneMaxHeight());
    }


    public static void changeToNewProjLayout(RTIProject rtiProject){
        currentRTIProjct = rtiProject;
        NewProjectLayout.getInstance().setProject(rtiProject);
        setCreatorStage(NewProjectLayout.getInstance());
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
            scene = new Scene(vBox, 200, 150);
            stage.setScene(scene);
        }

        public void show(){
            stage.show();
        }

        public void hide(){
            stage.hide();
        }

        public void setText(String text) {
            this.label.setText(text);
        }
    }
}
