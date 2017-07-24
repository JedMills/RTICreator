package newProjectScene;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import utils.Utils;

import java.io.File;

import static utils.Utils.linkDirButtonToTextField;

/**
 * The dialog that is shown when the 'Open Project Resources' button is clicked. This class is responsible for checking
 * the resources passed.
 *
 * @author Jed Mills
 */
public class LoadProjRsrcsDialog {

    /** The NewProjectLayout that this dialog was created from */
    private NewProjectLayout newProjectLayout;

    /** The window for this dialog */
    private Stage stage;

    /** The layout that will be shown if the current project is highlight based*/
    private Scene highlightLayout;

    /** The layout that will be shown if the current project is lp file based*/
    private Scene lpLayout;

    /** 'Browse' button for finding the image folder location for highlight based projects*/
    private Button browseImgLocationHL;

    /** Field that displays the selected folder for image file in a highlight based project */
    private TextField imgLocationFieldHL;

    /** 'Browse' button for finding the location of the folder for assembly files for a highlight based project */
    private Button browseAssemblyLocationHL;

    /** Field that displays the selected folder for assembly files in a highlight based project */
    private TextField assemblyLocationFieldHL;

    /** 'Browse' button for finding the image folder location for lp file projects*/
    private Button browseImgLocationLP;

    /** Field that displays the selected folder for image file in a lp file project */
    private TextField imgLocationFieldLP;

    /** 'Browse' button for finding the location of the folder for assembly files for a lp file project */
    private Button browseAssemblyLocationLP;

    /** Field that displays the selected folder for assembly files in a lp file  project */
    private TextField assemblyLocationFieldLP;

    /** 'Browse' button for finding the location of the lp file for a lp file project */
    private Button browseLPLocation;

    /** Field that displays the selected lp file in a lp file  project */
    private TextField lpLocationField;

    /** Ok button for the highlight project layout */
    private Button okButtonHL;

    /** Cancel button for the highlight project layout */
    private Button cancelButtonHL;

    /** Ok button for the lp project layout */
    private Button okButtonLP;

    /** Cancel button for the lp project layout */
    private Button cancelButtonLP;

    /** All the text field for convenietnyl checking etc. */
    private TextField[] textFields;

    /** The two different layout ypes*/
    public enum DialogType{HIGHLIGHT, LP;}

    /** The singleton instance of this class*/
    private static LoadProjRsrcsDialog ourInstance = new LoadProjRsrcsDialog();



    /**
     * @return {@link LoadProjRsrcsDialog#ourInstance}
     */
    public static LoadProjRsrcsDialog getInstance() {
        return ourInstance;
    }




    /**
     * Creates a new LoadProjRsrcsDialog.
     */
    private LoadProjRsrcsDialog() {
        //create the window
        stage = new Stage(StageStyle.UNIFIED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.setTitle("Find Project Resources");
        stage.setMinWidth(390);
        stage.setMaxWidth(840);

        stage.getIcons().add(Main.thumbnail);

        //make the components for the two layouts
        initComponents();
        highlightLayout = new Scene(createHighlightLayout());
        highlightLayout.getStylesheets().add("stylesheets/default.css");
        lpLayout = new Scene(createLPLayout());
        lpLayout.getStylesheets().add("stylesheets/default.css");

        //for the components to change size upon resize
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setTextBoxWidths(newValue);
            }
        });

    }




    /**
     * @param newProjectLayout the layout that thisDialog belongs to
     */
    public void init(NewProjectLayout newProjectLayout){
        this.newProjectLayout = newProjectLayout;
    }




    /**
     * Sets the text field pref widths to the width so they fill up all available width of the dialog.
     *
     * @param width     width of the dialog window
     */
    private void setTextBoxWidths(Number width){
        if(width instanceof Double) {
            for (TextField textField : textFields) {
                textField.setPrefWidth((Double) width);
            }
        }
    }




    /**
     * Creates all the components for the two layout types.
     */
    private void initComponents(){
        //just create all the components nothing fancy here
        imgLocationFieldHL = new TextField();
        browseImgLocationHL = new Button("Browse");
        assemblyLocationFieldHL = new TextField();
        browseAssemblyLocationHL = new Button("Browse");

        browseImgLocationLP = new Button("Browse");
        imgLocationFieldLP = new TextField();
        browseAssemblyLocationLP = new Button("Browse");
        assemblyLocationFieldLP = new TextField();
        browseLPLocation = new Button("Browse");
        lpLocationField = new TextField();

        okButtonHL = new Button("OK");
        cancelButtonHL = new Button("Cancel");

        okButtonLP = new Button("OK");
        cancelButtonLP = new Button("Cancel");

        textFields =  new TextField[]{imgLocationFieldLP, imgLocationFieldHL, assemblyLocationFieldLP,
                assemblyLocationFieldHL, lpLocationField};

        for(TextField textField : textFields){
            textField.setEditable(false);
        }


        for(Button button : new Button[]{browseImgLocationHL, browseImgLocationLP, browseAssemblyLocationHL,
                browseAssemblyLocationLP, browseLPLocation}){
            button.setMinWidth(button.USE_PREF_SIZE);
        }

        setButtonActions();
    }


    /**
     * Crete the layout that is shown when the project is highlight based, so has a selector for the images folder
     * and the assembly folder.
     *
     * @return  the highlight project layout
     */
    private VBox createHighlightLayout(){
        VBox vBox = new VBox();
        GridPane gridPane = new GridPane();
        setGridPaneLayout(gridPane);
        addImagesLocationComponents(DialogType.HIGHLIGHT, gridPane, 0);
        addOutFolderLocationComponents(DialogType.HIGHLIGHT, gridPane, 1);

        HBox buttonBar = createButtonBar(DialogType.HIGHLIGHT);

        vBox.getChildren().addAll(gridPane, buttonBar);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.setSpacing(5);
        return vBox;
    }


    /**
     * Crete the layout that is shown when the project is lp-file based, so has a selector for the images folder
     * and the assembly folder, and a selector for the lp file.
     *
     * @return  the lp file project layout
     */
    private VBox createLPLayout(){
        VBox vBox = new VBox();
        GridPane gridPane = new GridPane();
        setGridPaneLayout(gridPane);
        addImagesLocationComponents(DialogType.LP, gridPane, 0);
        addLPFileLocationComponents(gridPane, 1);
        addOutFolderLocationComponents(DialogType.LP, gridPane, 2);

        HBox buttonBar = createButtonBar(DialogType.LP);

        vBox.getChildren().addAll(gridPane, buttonBar);
        vBox.setPadding(new Insets(5, 5, 5,5));
        vBox.setSpacing(5);
        return vBox;
    }


    private void setGridPaneLayout(GridPane gridPane){
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
    }


    private void addImagesLocationComponents(DialogType type, GridPane gridPane, int rowNum){
        Label imagesLabel = new Label("Image folder:");
        imagesLabel.setMinWidth(imagesLabel.USE_PREF_SIZE);
        GridPane.setConstraints(imagesLabel, 0, rowNum, 1, 1);

        if(type.equals(DialogType.HIGHLIGHT)) {
            GridPane.setConstraints(imgLocationFieldHL, 1, rowNum, 1, 1);
            GridPane.setConstraints(browseImgLocationHL, 2, rowNum, 1, 1);
            gridPane.getChildren().addAll(imagesLabel, imgLocationFieldHL, browseImgLocationHL);
        }else if(type.equals(DialogType.LP)){
            GridPane.setConstraints(imgLocationFieldLP, 1, rowNum, 1, 1);
            GridPane.setConstraints(browseImgLocationLP, 2, rowNum, 1, 1);
            gridPane.getChildren().addAll(imagesLabel, imgLocationFieldLP, browseImgLocationLP);
        }
    }


    private void addOutFolderLocationComponents(DialogType type, GridPane gridPane, int rowNum){
        Label outLabel = new Label("Folder for assembly files:");
        outLabel.setMinWidth(outLabel.USE_PREF_SIZE);
        GridPane.setConstraints(outLabel, 0, rowNum, 1, 1);

        if(type.equals(DialogType.HIGHLIGHT)) {
            GridPane.setConstraints(assemblyLocationFieldHL, 1, rowNum, 1, 1);
            GridPane.setConstraints(browseAssemblyLocationHL, 2, rowNum, 1, 1);
            gridPane.getChildren().addAll(outLabel, assemblyLocationFieldHL, browseAssemblyLocationHL);
        }else if(type.equals(DialogType.LP)){
            GridPane.setConstraints(assemblyLocationFieldLP, 1, rowNum, 1, 1);
            GridPane.setConstraints(browseAssemblyLocationLP, 2, rowNum, 1, 1);
            gridPane.getChildren().addAll(outLabel, assemblyLocationFieldLP, browseAssemblyLocationLP);
        }
    }


    private void addLPFileLocationComponents(GridPane gridPane, int rowNum){
        Label lpLabel = new Label("LP file:");
        lpLabel.setMinWidth(lpLabel.USE_PREF_SIZE);
        GridPane.setConstraints(lpLabel, 0, rowNum, 1, 1);

        GridPane.setConstraints(lpLocationField, 1, rowNum, 1, 1);
        GridPane.setConstraints(browseLPLocation, 2, rowNum, 1, 1);

        gridPane.getChildren().addAll(lpLabel, lpLocationField, browseLPLocation);
    }


    private HBox createButtonBar(DialogType type){
        HBox hBox = new HBox();

        if(type.equals(DialogType.LP)){
            hBox.getChildren().addAll(okButtonLP, cancelButtonLP);
        }else if(type.equals(DialogType.HIGHLIGHT)){
            hBox.getChildren().addAll(okButtonHL, cancelButtonHL);
        }

        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setSpacing(10);

        return hBox;
    }



    public void show(DialogType dialogType){
        int height = 0;
        if(dialogType.equals(DialogType.HIGHLIGHT)){
            height = 163;
            stage.setScene(highlightLayout);
        }else if(dialogType.equals(DialogType.LP)){
            height = 198;
            stage.setScene(lpLayout);
        }
        stage.setMaxHeight(height);
        stage.setMinHeight(height);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.show();
            }
        });
    }


    private void setButtonActions(){
        linkDirButtonToTextField("Select project images folder",
                browseImgLocationHL, imgLocationFieldHL, stage,true);

        linkDirButtonToTextField("Select project images folder",
                browseImgLocationLP, imgLocationFieldLP, stage, true);

        linkDirButtonToTextField("Select project output folder",
                browseAssemblyLocationHL, assemblyLocationFieldHL, stage, true);

        linkDirButtonToTextField("Select project output folder",
                browseAssemblyLocationLP, assemblyLocationFieldLP, stage, true);

        linkFileButtonToTextField("Select LP file", browseLPLocation, lpLocationField);


        EventHandler<ActionEvent> close = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for(TextField textField : textFields){textField.setText("");}
                stage.close();
            }
        };

        cancelButtonLP.setOnAction(close);
        cancelButtonHL.setOnAction(close);

        okButtonHL.setOnAction(createOKButtonHandler(imgLocationFieldHL, assemblyLocationFieldHL));
        okButtonLP.setOnAction(createOKButtonHandler(imgLocationFieldLP, lpLocationField, assemblyLocationFieldLP));
    }


    private EventHandler<ActionEvent> createOKButtonHandler(TextField... fields){
        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(Utils.haveEmptyField(fields)){
                    Main.showInputAlert("Please select all fields.");

                }else if(Utils.containsSpaces(fields)){
                    Main.showInputAlert("Please ensure there are no spaces in the file paths provided.");

                }else{
                    if(fields.length == 2) {
                        newProjectLayout.setResources(imgLocationFieldHL.getText(), assemblyLocationFieldHL.getText());
                    }else if(fields.length == 3){
                        newProjectLayout.setResources(imgLocationFieldLP.getText(),
                                lpLocationField.getText(), assemblyLocationFieldLP.getText());
                    }
                    stage.close();
                }
            }
        };

        return handler;
    }




    private void linkFileButtonToTextField(String title, Button button, TextField textField){
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.fileChooser.setTitle(title);
                Main.fileChooser.getExtensionFilters().add(new
                        FileChooser.ExtensionFilter("LP Files (.lp)", "*.lp"));
                File file = Main.fileChooser.showOpenDialog(stage);

                if(file != null){
                    textField.setText(file.getAbsolutePath());
                }
            }
        });
    }
}
