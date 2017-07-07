package main;

import initialScene.InitialLayout;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
        fileChooser = new FileChooser();
        directoryChooser = new DirectoryChooser();
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


    public static void changeToNewProjLayout(){
        setCreatorStage(NewProjectLayout.getInstance());
    }
}
