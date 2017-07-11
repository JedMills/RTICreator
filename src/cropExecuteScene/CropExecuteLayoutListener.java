package cropExecuteScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

/**
 * Created by Jed on 11-Jul-17.
 */
public class CropExecuteLayoutListener implements EventHandler<ActionEvent> {

    private CropExecuteLayout cropExecuteLayout;

    private static CropExecuteLayoutListener ourInstance = new CropExecuteLayoutListener();

    public static CropExecuteLayoutListener getInstance() {
        return ourInstance;
    }

    private CropExecuteLayoutListener() {
    }

    public void init(CropExecuteLayout cropExecuteLayout){
        this.cropExecuteLayout = cropExecuteLayout;
    }


    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() instanceof Button){
            Button source = (Button) event.getSource();

        }else if(event.getSource() instanceof CheckBox){
            CheckBox source = (CheckBox) event.getSource();
            if(source.isSelected()){
                cropExecuteLayout.enableCrop();
            }else{
                cropExecuteLayout.disableCrop();
            }

        }
    }
}
