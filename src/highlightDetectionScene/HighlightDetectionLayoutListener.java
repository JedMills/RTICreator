package highlightDetectionScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Created by Jed on 14-Jul-17.
 */
public class HighlightDetectionLayoutListener implements EventHandler<ActionEvent> {
    private static HighlightDetectionLayoutListener ourInstance = new HighlightDetectionLayoutListener();

    public static HighlightDetectionLayoutListener getInstance() {
        return ourInstance;
    }

    private HighlightDetectionLayoutListener() {
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("detectSphereButton")){

            }else if(source.getId().equals("setSphereButton")){

            }else if(source.getId().equals("detectHighlightsButton")){

            }else if(source.getId().equals("redoProcessButton")){

            }else if(source.getId().equals("backButton")){

            }else if(source.getId().equals("nextButton")){

            }

        }
    }
}
