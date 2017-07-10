package newProjectScene;

import guiComponents.ImageGridTile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import main.Main;
import main.ProjectType;
import utils.Utils;


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
                if(Utils.checkIn(newProjectLayout.getProjectType(),
                        new ProjectType[]{ProjectType.HIGHLIGHT_HSH, ProjectType.HIGHLIGHT_PTM})){
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

            }
        }
    }


}
