package initialScene;

import guiComponents.ImageGridTile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.RTIProject;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.Utils.linkDirButtonToTextField;

/**
 * Created by Jed on 19-Jul-17.
 */
public class LoadExistingLPDialog {

    private Stage stage;
    private TextField lpField;
    private Button browseLPLoc;
    private TextField assemblyField;
    private Button browseAssemblyLoc;
    private Button okButton;
    private Button cancelButton;

    public LoadExistingLPDialog(){
        stage = new Stage(StageStyle.UNIFIED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.setTitle("Load LP From Existing Project");

        stage.getIcons().add(Main.thumbnail);

        VBox layout = createLayout();
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("stylesheets/default.css");


        stage.setMinHeight(160);
        stage.setMaxHeight(160);
        stage.setMinWidth(400);
        stage.setMaxWidth(700);

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue instanceof Double){
                    lpField.setPrefWidth((Double)newValue);
                    assemblyField.setPrefWidth((Double)newValue);
                }
            }
        });

        stage.setScene(scene);
    }

    private VBox createLayout(){
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));

            GridPane gridPane = new GridPane();

                Label lpLabel = new Label("LP file:");
                lpLabel.setMinWidth(Label.USE_PREF_SIZE);
                GridPane.setConstraints(lpLabel, 0, 0);

                lpField = createField(1, 0);

                browseLPLoc = new Button("Browse");
                browseLPLoc.setMinWidth(Button.USE_PREF_SIZE);
                linkDirButtonToTextField("Select LP File", browseLPLoc, lpField, stage,
                        "LP Files (.lp)", "*.lp");
                GridPane.setConstraints(browseLPLoc, 2, 0);

                Label assemblyLabel = new Label("Folder for assembly files:");
                assemblyLabel.setMinWidth(Label.USE_PREF_SIZE);
                GridPane.setConstraints(assemblyLabel, 0, 1);

                assemblyField = createField(1, 1);

                browseAssemblyLoc = new Button("Browse");
                browseAssemblyLoc.setMinWidth(Button.USE_PREF_SIZE);
                linkDirButtonToTextField("Select Folder For Assembly Files",
                                                    browseAssemblyLoc, assemblyField, stage, true);
                GridPane.setConstraints(browseAssemblyLoc, 2, 1);
                gridPane.getChildren().addAll(lpLabel, lpField, browseLPLoc,
                        assemblyLabel, assemblyField, browseAssemblyLoc);

            gridPane.setVgap(5);
            gridPane.setHgap(5);
            gridPane.setPadding(new Insets(5, 5, 5, 5));
            gridPane.getStyleClass().add("defaultBorder");

            HBox hBox = new HBox();

                okButton = new Button("OK");
                setOkButtonAction();
                cancelButton = new Button("Cancel");
                setCancelButtonAction();

            hBox.getChildren().addAll(okButton, cancelButton);
            hBox.setSpacing(10);
            hBox.setPadding(new Insets(5, 5, 5, 5));
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.getStyleClass().add("defaultBorder");

        vBox.getChildren().addAll(gridPane, hBox);
        vBox.setSpacing(5);

        return vBox;
    }



    private void setOkButtonAction() {
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadLPData();
                    }
                }).start();
            }
        });
    }

    private void loadLPData(){
        if(Utils.haveEmptyField(lpField, assemblyField)){
            Main.showInputAlert("Please select locations for the LP file and assembly folder.");
            return;
        }

        File lpFile = new File(lpField.getText());
        File assemblyFile = new File(assemblyField.getText());

        if(!lpFile.exists() || !assemblyFile.exists()){
            Main.showInputAlert("Couldn't find the specified resources. Please check that they " +
                    "still exist and have the correct format.");
            return;
        }

        Main.showLoadingDialog("Loading LP file...");

        HashMap<String, Utils.Vector3f> lpData;
        try {
            lpData = Utils.readLPFile(lpFile);
        }catch(IOException e){
            Main.showFileReadingAlert("Error accessing LP file. Check that it still exists.");
            Main.hideLoadingDialog();
            return;
        }catch(Utils.LPException e){
            Main.showFileReadingAlert("Error reading LP file: " + e.getMessage());
            Main.hideLoadingDialog();
            return;
        }

        ArrayList<ImageGridTile> gridTiles = new ArrayList<>();
        for(String imagePath : lpData.keySet()){
            File imageFile = new File(imagePath);

            if(!imageFile.exists()){
                Main.showFileReadingAlert("Couldn't image file specified in lp file: " + imagePath);
                Main.hideLoadingDialog();
                return;
            }

            String imageExt = Utils.getFileExtension(imageFile.getName());
            Image image;
            if(Utils.checkIn(imageExt.toLowerCase(), new String[]{"jpg", "png"})){
                image = new Image("file:" + imagePath);
            }else{
                image = Utils.readUnusualImage(imagePath);
            }

            if(image == null){
                Main.showFileReadingAlert("Couldn't read image: " + imagePath + ", check it is " +
                        "one of the accepted formats");
                Main.hideLoadingDialog();
                return;
            }

            ImageGridTile gridTile = new ImageGridTile(null, imageFile.getName(), image, 150,
                    150, false, true, true);
            gridTiles.add(gridTile);
        }


        Main.currentAssemblyFolder = assemblyFile;
        Main.currentLPFile = lpFile;

        Main.hideLoadingDialog();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
        Main.changeToCropExecuteScene(gridTiles);

    }

    private void setCancelButtonAction(){
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lpField.setText("");
                assemblyField.setText("");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stage.close();
                    }
                });
            }
        });
    }



    public void show(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.show();
            }
        });
    }




    private TextField createField(int col, int row){
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setPrefWidth(Double.MAX_VALUE);
        GridPane.setConstraints(textField, col, row);
        return textField;
    }

}
