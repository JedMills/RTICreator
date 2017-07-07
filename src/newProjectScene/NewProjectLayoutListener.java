package newProjectScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import main.Main;

import java.io.File;


/**
 * Created by Jed on 07-Jul-17.
 */
public class NewProjectLayoutListener implements EventHandler<ActionEvent> {


    private static NewProjectLayoutListener ourInstance = new NewProjectLayoutListener();

    public static NewProjectLayoutListener getInstance() {
        return ourInstance;
    }

    private NewProjectLayoutListener() {
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("openFolder")){
                Main.directoryChooser.setTitle("Choose project directory");

                File directory = Main.directoryChooser.showDialog(Main.primaryStage);
            }
        }
    }
}
