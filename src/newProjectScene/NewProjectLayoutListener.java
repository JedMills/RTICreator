package newProjectScene;

import guiComponents.ImageGridTile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import main.Main;
import main.ProjectType;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Jed on 07-Jul-17.
 */
public class NewProjectLayoutListener implements EventHandler<ActionEvent> {

    private NewProjectLayout newProjectLayout;

    private static NewProjectLayoutListener ourInstance = new NewProjectLayoutListener();

    public static NewProjectLayoutListener getInstance() {return ourInstance;}

    private NewProjectLayoutListener() {
    }


    public void init(NewProjectLayout newProjectLayout){
        this.newProjectLayout = newProjectLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("openFolder")){
                if(newProjectLayout.getProjectType().equals(ProjectType.HIGHLIGHT)){
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.HIGHLIGHT);
                }else{
                    LoadProjRsrcsDialog.getInstance().show(LoadProjRsrcsDialog.DialogType.LP);
                }

            }else if(source.getId().equals("addPropertyButton")){
                AddPropertyDialog.getInstance().show();

            }else if(source.getId().equals("delPropertyButton")){
                newProjectLayout.deleteSelectedProperty();

            }else if(source.getId().equals("removePicButton")){
                String comment = newProjectLayout.getRemoveRsnTxtField().getText();
                ImageGridTile tile = newProjectLayout.removeGridTileSelected();
                if(tile != null) {
                    newProjectLayout.getRemoveRsnTxtField().setText("");
                    tile.setRejectComment(comment);
                    tile.setSelected(false);
                    newProjectLayout.addTileToRejected(tile);
                }

            }else if(source.getId().equals("replacePicBtn")){
                ImageGridTile tile = newProjectLayout.removeGridTileRejected();
                if(tile != null){
                    tile.removeRejectComment();
                    tile.setSelected(false);
                    newProjectLayout.addTileToSelected(tile);
                }

            }else if(source.getId().equals("backBtn")){
                Main.changeToInitialLayout();
                newProjectLayout.resetScene();

            }else if(source.getId().equals("nextBtn")){
                if(newProjectLayout.getProjectType().equals(ProjectType.DOME_LP)){
                    moveSceneDomePTM();

                }else if(newProjectLayout.getProjectType().equals(ProjectType.HIGHLIGHT)){
                    System.out.println("Not yet implemented: " + newProjectLayout.getProjectType().toString());

                }else if(newProjectLayout.getProjectType().equals(ProjectType.LP)){
                    System.out.println("Not yet implemented: " + newProjectLayout.getProjectType().toString());

                }
            }
        }
    }




    private void moveSceneDomePTM(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Main.showLoadingDialog("Checking project resources...");

                if(!newProjectLayout.isResourcesSet()){
                    Main.hideLoadingDialog();
                    Main.showInputAlert("Please open the resources for this project using the 'Open Project Resources' button.");
                    return;
                }


                String imgFolderPath = newProjectLayout.getImgsFolder().getAbsolutePath();

                String fileName;
                ArrayList<String> fileNames = new ArrayList<>();
                File currentFile;
                for(String s : newProjectLayout.getLpData().keySet()){
                    fileName = new File(s).getName();
                    currentFile = new File(imgFolderPath + "/" + fileName);

                    if(!currentFile.exists() || currentFile.isDirectory()){
                        Main.hideLoadingDialog();
                        Main.showFileReadingAlert("Not all of the images specified in the LP file were found in the " +
                                "image resources file.");
                        return;
                    }

                    fileNames.add(fileName);
                }

                ArrayList<ImageGridTile> selectedImages = newProjectLayout.getSelectedImages(fileNames);

                Main.hideLoadingDialog();
                Main.changeToCropExecuteScene(selectedImages);
            }
        }).run();

    }

}
