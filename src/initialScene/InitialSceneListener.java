package initialScene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import main.Main;
import main.ProjectType;

import java.io.File;

/**
 * Created by Jed on 06-Jul-17.
 */
public class InitialSceneListener implements EventHandler<ActionEvent> {

    private static InitialSceneListener ourInstance = new InitialSceneListener();

    private InitialLayout initialLayout;

    public static InitialSceneListener getInstance() {
        return ourInstance;
    }

    private InitialSceneListener() {
    }

    public void init(InitialLayout initialLayout){
        this.initialLayout = initialLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("startNewProject")){
                if(checkInputs()){
                    Main.changeToNewProjLayout();
                }
            }else if(source.getId().equals("openExistingProject")){
                Main.directoryChooser.setTitle("Open existing project");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        File file = Main.directoryChooser.showDialog(Main.primaryStage);
                    }
                });
            }
        }
    }


    private boolean checkInputs(){
        String projectName = initialLayout.getProjectNameField().getText();
        ProjectType projectType = initialLayout.getProjectOptions().getSelectionModel().getSelectedItem();
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

        }else if(projectType == null){
            Main.inputAlert.setContentText("Please select a project type.");
            validInput = false;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.inputAlert.showAndWait();
                    initialLayout.getProjectOptions().requestFocus();
                }
            });
        }

        return validInput;
    }
}
