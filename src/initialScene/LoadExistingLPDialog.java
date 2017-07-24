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
 * Dialog that is shown if the user selects the LP project using full file paths in the {@link InitialLayout}. Used
 * for selecting the lp file path and assembly folder.
 *
 * @see InitialLayout
 *
 * @author Jed Mills
 */
public class LoadExistingLPDialog {

    /** Window that this dialog exist in*/
    private Stage stage;

    /** Field where the path for the chosen lp file goes */
    private TextField lpField;

    /** Field where the path for the assembly folder goes */
    private TextField assemblyField;

    /** 'OK' button */
    private Button okButton;

    /** *@Cancel' button */
    private Button cancelButton;

    /**
     * Creates a new LoadExistingLPDialog, adds listeners for the window's width property so the text fields
     * expand to fill the width.
     */
    public LoadExistingLPDialog(){
        //create a new window
        stage = new Stage(StageStyle.UNIFIED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.setTitle("Load LP From Existing Project");

        //give it the RTI logo in the corner
        stage.getIcons().add(Main.thumbnail);

        //create the fields and labels and buttons
        VBox layout = createLayout();
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("stylesheets/default.css");


        stage.setMinHeight(160);
        stage.setMaxHeight(160);
        stage.setMinWidth(400);
        stage.setMaxWidth(700);

        //make the text fields expand to fit the width of the stage
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


    /**
     * Creates all the content and widgets in this dialog.
     *
     * @return  the VBox containing all the fields and buttons and labels
     */
    private VBox createLayout(){
        //contains all the stuff
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));

            //contains the labels, fields and browse buttons for the lp and assembly folder location
            GridPane gridPane = new GridPane();

                Label lpLabel = new Label("LP file:");
                lpLabel.setMinWidth(Label.USE_PREF_SIZE);
                GridPane.setConstraints(lpLabel, 0, 0);

                lpField = createField(1, 0);

                Button browseLPLoc = new Button("Browse");
                browseLPLoc.setMinWidth(Button.USE_PREF_SIZE);
                linkDirButtonToTextField("Select LP File", browseLPLoc, lpField, stage,
                        "LP Files (.lp)", "*.lp");
                GridPane.setConstraints(browseLPLoc, 2, 0);

                Label assemblyLabel = new Label("Folder for assembly files:");
                assemblyLabel.setMinWidth(Label.USE_PREF_SIZE);
                GridPane.setConstraints(assemblyLabel, 0, 1);

                assemblyField = createField(1, 1);

                Button browseAssemblyLoc = new Button("Browse");
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

            //contains the ok and cancel buttons
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


    /**
     * Sets the action for th e 'OK' button,. which checks the lp file is a rel file, and attempts to load the
     * LP data before moving to the {@link cropExecuteScene.CropExecuteLayout}.
     */
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


    /**
     * Reads the lp data in the file, loading the images in the locations given, and handing these to the
     * {@link cropExecuteScene.CropExecuteLayout}, whihc the program will switch to if all this is
     * successful.
     *
     * @see cropExecuteScene.CropExecuteLayout
     */
    private void loadLPData(){
        //checj the user has actually chosen things for the fields
        if(Utils.haveEmptyField(lpField, assemblyField)){
            Main.showInputAlert("Please select locations for the LP file and assembly folder.");
            return;
        }

        File lpFile = new File(lpField.getText());
        File assemblyFile = new File(assemblyField.getText());

        //check the specified file/folder actually exist
        if(!lpFile.exists() || !assemblyFile.exists()){
            Main.showInputAlert("Couldn't find the specified resources. Please check that they " +
                    "still exist and have the correct format.");
            return;
        }

        Main.showLoadingDialog("Loading LP file...");

        //read the lpdata from the file
        HashMap<String, Utils.Vector3f> lpData;
        try {
            lpData = Utils.readLPFile(lpFile);

        }catch(IOException e){
            //error actually accessing the file
            Main.showFileReadingAlert("Error accessing LP file. Check that it still exists.");
            Main.hideLoadingDialog();
            return;

        }catch(Utils.LPException e){
            //error when parsing the lp data
            Main.showFileReadingAlert("Error reading LP file: " + e.getMessage());
            Main.hideLoadingDialog();
            return;
        }

        //will now create an array of image grid tiles which will be passed to the CropExecuteLayout
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
                //jpegs and pngs are easy to load
                image = new Image("file:" + imagePath);
            }else{
                //otherwise we need to use the JAI API to load them
                image = Utils.readUnusualImage(imagePath);
            }

            if(image == null){
                Main.showFileReadingAlert("Couldn't read image: " + imagePath + ", check it is " +
                        "one of the accepted formats");
                Main.hideLoadingDialog();
                return;
            }

            //if we've succesfully loaded the image, create a grid tile from it and the name in the lp file
            ImageGridTile gridTile = new ImageGridTile(null, imageFile.getName(), image, 150,
                    150, false, true, true);
            gridTiles.add(gridTile);
        }

        //the folders that the CropExecuteLayout need to know about
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


    /**
     * Sets the action when the cancel button is clicked: close the dialog without doing anything.
     */
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


    /**
     * Show this dialog on the JavaFX thread.
     */
    public void show(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.show();
            }
        });
    }


    /**
     * Convenience method for creating a TextField in a GridPane.
     *
     * @param col   column in the grid
     * @param row   row in the grid
     * @return      the new TextField
     */
    private TextField createField(int col, int row){
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setPrefWidth(Double.MAX_VALUE);
        GridPane.setConstraints(textField, col, row);
        return textField;
    }

}
