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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.ProjectProperty;
import utils.Utils;

/**
 * Created by Jed on 09-Jul-17.
 */
public class AddPropertyDialog {

    private NewProjectLayout newProjectLayout;

    private TextField propertyName;
    private TextField propertyValue;
    private Button okButton;
    private Button cancelButton;
    private Stage stage;

    private static AddPropertyDialog ourInstance = new AddPropertyDialog();

    public static AddPropertyDialog getInstance() {
        return ourInstance;
    }


    public void init(NewProjectLayout newProjectLayout){
        this.newProjectLayout = newProjectLayout;
    }



    private AddPropertyDialog() {
        stage = new Stage(StageStyle.UNIFIED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.setTitle("Add Project Property");

        VBox layout = createLayout();
        Scene scene = new Scene(layout);
        stage.setScene(scene);

        stage.setMinWidth(300);
        stage.setMaxWidth(600);
        stage.setMinHeight(163);
        stage.setMaxHeight(163);

        scene.getStylesheets().add("stylesheets/default.css");

        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue instanceof Double){
                    propertyName.setPrefWidth((Double)newValue);
                    propertyValue.setPrefWidth((Double)newValue);
                }

            }
        });
    }


    private VBox createLayout(){
        VBox vBox = new VBox();

        GridPane gridPane = new GridPane();
            Label nameLabel = new Label("Name:");
            GridPane.setConstraints(nameLabel, 0, 0);
            nameLabel.setMinWidth(Label.USE_PREF_SIZE);

            propertyName = new TextField();
            GridPane.setConstraints(propertyName, 1, 0);

            Label valueLabel = new Label("Value:");
            GridPane.setConstraints(valueLabel, 0, 1);
            valueLabel.setMinWidth(Label.USE_PREF_SIZE);

            propertyValue = new TextField();
            GridPane.setConstraints(propertyValue, 1, 1);
        gridPane.getChildren().addAll(nameLabel, propertyName, valueLabel, propertyValue);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));


        HBox hBox = new HBox();
            okButton = new Button("OK");
            cancelButton = new Button("Cancel");
            setupButtonActions();
        hBox.getChildren().addAll(okButton, cancelButton);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setSpacing(10);


        vBox.getChildren().addAll(gridPane, hBox);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        return vBox;
    }



    private void setupButtonActions(){
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                propertyName.setText("");
                propertyValue.setText("");
                stage.close();
            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(propertyName.getText().equals("") || propertyValue.getText().equals("")){
                    Main.showInputAlert("Please enter a name and value for the new property.");

                }else if(Utils.checkIn(propertyName.getText().toLowerCase(), newProjectLayout.getPropertyNamesLower())){
                    Main.showInputAlert("A property with that name already exists. Please enter a new name.");

                }else{
                    ProjectProperty property = new ProjectProperty(propertyName.getText(), propertyValue.getText());
                    newProjectLayout.addProjectProperty(property);
                    propertyName.setText("");
                    propertyValue.setText("");
                    stage.close();
                }
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
}
