package highlightDetectionScene;

import guiComponents.ImageCropPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;


/**
 * Created by Jed on 14-Jul-17.
 */
public class HighlightDetectionLayoutListener implements EventHandler<ActionEvent> {

    private HighlightDetectionLayout highlightLayout;

    private static HighlightDetectionLayoutListener ourInstance = new HighlightDetectionLayoutListener();

    public static HighlightDetectionLayoutListener getInstance() {
        return ourInstance;
    }

    private HighlightDetectionLayoutListener() {}

    public void init(HighlightDetectionLayout highlightDetectionLayout){
        highlightLayout = highlightDetectionLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

            if(source.getId().equals("setSphereButton")){

            }else if(source.getId().equals("backButton")){

            }else if(source.getId().equals("nextButton")){

            }else if(source.getId().equals("sphereXMinus")){
                highlightLayout.translateCircleSelect(-1, 0);

            }else if(source.getId().equals("sphereXPlus")){
                highlightLayout.translateCircleSelect(1, 0);

            }else if(source.getId().equals("sphereYMinus")){
                highlightLayout.translateCircleSelect(0, -1);

            }else if(source.getId().equals("sphereYPlus")){
                highlightLayout.translateCircleSelect(0, 1);

            }else if(source.getId().equals("sphereRMinus")){
                highlightLayout.changeCircleR(-1);

            }else if(source.getId().equals("sphereRPlus")){
                highlightLayout.changeCircleR(1);

            }


        }else if(event.getSource() instanceof ComboBox){
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();

            highlightLayout.setCircleSelectionColour(source.getSelectionModel().getSelectedItem());
        }
    }




}
