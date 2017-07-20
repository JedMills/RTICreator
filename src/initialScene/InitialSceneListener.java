package initialScene;

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
import main.ProjectType;
import main.RTIProject;
import newProjectScene.LoadProjRsrcsDialog;

import java.io.File;

/**
 * Created by Jed on 06-Jul-17.
 */
public class InitialSceneListener implements EventHandler<ActionEvent> {

    private static InitialSceneListener ourInstance = new InitialSceneListener();

    private InitialLayout initialLayout;
    private LoadExistingLPDialog lpDialog;

    public static InitialSceneListener getInstance() {
        return ourInstance;
    }

    private InitialSceneListener() {}

    public void init(InitialLayout initialLayout){
        this.initialLayout = initialLayout;
        lpDialog = new LoadExistingLPDialog();
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("startNewProject")){
                if(checkInputs()){
                    ProjectType projectType = null;

                    if(initialLayout.getHighlightProjBtn().isSelected()){
                        projectType = ProjectType.HIGHLIGHT;
                    }
                    else if (initialLayout.getLpFileProjBtn().isSelected()){
                        projectType = ProjectType.LP;
                    }
                    else if(initialLayout.getLpFileExistingProject().isSelected()){
                        projectType = ProjectType.EXISTING_LP;
                    }

                    RTIProject createdProject = new RTIProject(initialLayout.getProjectNameField().getText(), projectType);

                    if(projectType.equals(ProjectType.HIGHLIGHT) || projectType.equals(ProjectType.LP)) {
                        Main.changeToNewProjLayout(createdProject);
                    }else if(projectType.equals(ProjectType.EXISTING_LP)){
                        Main.currentRTIProject = createdProject;
                        lpDialog.show();
                    }
                }
            }
        }
    }


    private boolean checkInputs(){
        String projectName = initialLayout.getProjectNameField().getText();

        boolean projectSelected = false;

        if(initialLayout.getHighlightProjBtn().isSelected()         ||
                initialLayout.getLpFileProjBtn().isSelected()       ||
                initialLayout.getLpFileExistingProject().isSelected()){
            projectSelected = true;
        }


        boolean validInput = true;

        if((!projectName.replaceAll("\\w|[_]", "").equals("")) || projectName.equals("")){
            Main.inputAlert.setContentText("Please enter a valid project name. Name can only contain letters" +
                                            "(A-Z / a-z), numbers (0-9), and underscores.");
            validInput = false;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.inputAlert.showAndWait();
                    initialLayout.getProjectNameField().requestFocus();
                }
            });

        }else if(!projectSelected){
            validInput = false;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.showInputAlert("Please select a project type.");
                }
            });
        }

        return validInput;
    }


}
