package initialScene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import main.Main;
import main.RTIProject;

/**
 * Listens to events from the {@link InitialLayout} and handles them.
 *
 * @see InitialLayout
 *
 * @author Jed Mills
 */
public class InitialSceneListener implements EventHandler<ActionEvent> {

    /** The single instance oft his class */
    private static InitialSceneListener ourInstance = new InitialSceneListener();

    /** The initialLayout that this instance that this instance is listening to */
    private InitialLayout initialLayout;

    /** Dialog that is called if the user is running a new project using an lp file with full paths */
    private LoadExistingLPDialog lpDialog;

    /**
     * @return {@link InitialSceneListener#ourInstance}
     */
    public static InitialSceneListener getInstance() {
        return ourInstance;
    }


    /**
     * Creates a new InitialSceneListener.
     */
    private InitialSceneListener() {}

    /**
     * Sets the InitialScene that this listener is listening to.
     *
     * @param initialLayout th eInitialScene to listen to
     */
    public void init(InitialLayout initialLayout){
        this.initialLayout = initialLayout;
        lpDialog = new LoadExistingLPDialog();
    }


    /**
     * Handles the events from the InitialScene.
     *
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("startNewProject")){
                //if the user has typed in a valid name and selected a project type
                if(checkInputs()){
                    RTIProject.ProjectType projectType = null;

                    if(initialLayout.getHighlightProjBtn().isSelected()){
                        //to start a new highlight based project and move to the
                        //highlight version of the new project layout
                        projectType = RTIProject.ProjectType.HIGHLIGHT;
                    }
                    else if (initialLayout.getLpFileProjBtn().isSelected()){
                        //tp start a new lp project and move to the lp version of thw new project layout
                        projectType = RTIProject.ProjectType.LP;
                    }
                    else if(initialLayout.getLpFileExistingProject().isSelected()){
                        //to open the dialog to open the lp file
                        projectType = RTIProject.ProjectType.EXISTING_LP;
                    }

                    //create a new RTIProject that we'll use to know what layouts to show
                    RTIProject createdProject = new RTIProject(
                                    initialLayout.getProjectNameField().getText(), projectType);

                    if(projectType.equals(RTIProject.ProjectType.HIGHLIGHT) || projectType.equals(RTIProject.ProjectType.LP)) {
                        Main.changeToNewProjLayout(createdProject);

                    }else if(projectType.equals(RTIProject.ProjectType.EXISTING_LP)){
                        Main.currentRTIProject = createdProject;
                        lpDialog.show();
                    }
                }
            }
        }
    }




    /**
     * Checks whether the use has typed in an acceptable project name and chosen a project type.
     *
     * @return  whether the use has correctly chosen a project name and type
     */
    private boolean checkInputs(){
        String projectName = initialLayout.getProjectNameField().getText();

        //user has to choose a project type
        boolean projectSelected = false;
        if(initialLayout.getHighlightProjBtn().isSelected()         ||
                initialLayout.getLpFileProjBtn().isSelected()       ||
                initialLayout.getLpFileExistingProject().isSelected()){
            projectSelected = true;
        }


        //user has to type in an acceptable project name, which can only be letters, number, and underscores
        //as the ptm and hsh fitters at the end of the program ca't cpe with file paths with other chars in
        boolean validInput = true;
        if((!projectName.replaceAll("\\w|[_]", "").equals("")) || projectName.equals("")){
            Main.inputAlert.setContentText("Please enter a valid project name. Name can only contain letters" +
                                            "(A-Z / a-z), numbers (0-9), and underscores.");
            validInput = false;
            //show the alert on the JavaFx thread
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
