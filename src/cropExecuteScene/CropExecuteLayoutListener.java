package cropExecuteScene;

import guiComponents.ImageGridTile;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import main.Main;
import utils.Utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

            if(source.getId().equals("runFitterButton")){
                runFitter();
            }

        }else if(event.getSource() instanceof CheckBox){
            CheckBox source = (CheckBox) event.getSource();
            if(source.isSelected()){
                cropExecuteLayout.enableCrop();
            }else{
                cropExecuteLayout.disableCrop();
            }

        }else if(event.getSource() instanceof ComboBox){
            ComboBox<ImageCropPane.Colour> source = (ComboBox<ImageCropPane.Colour>) event.getSource();
            ImageCropPane.Colour colour = source.getSelectionModel().getSelectedItem();
            cropExecuteLayout.setCropColour(colour);

        }else if(event.getSource() instanceof RadioButton){
            RadioButton source = (RadioButton) event.getSource();

            if(source.getId().equals("ptmButton")){
                cropExecuteLayout.setPTMOptions();

            }else if(source.getId().equals("hshButton")){
                cropExecuteLayout.setHSHOptions();
            }

        }
    }


    private void runFitter(){
        if(cropExecuteLayout.isUseCrop()){
            String croppedFolderName = Main.currentAssemblyFolder.getName() +
                                        "\\" + Main.currentRTIProjct.getName() + "_croppedFiles";
            File croppedFolder = new File(croppedFolderName);

            if(!croppedFolder.exists()){
                croppedFolder.mkdir();
            }


            System.out.println(croppedFolder.getName());
            int[] cropParams = cropExecuteLayout.getCropParams();

            for(int i : cropParams){
                System.out.println(i);
            }
            System.out.println("Cores: " + Main.NUM_THREADS);
            /*
            HashSet<ImageGridTile> gridTileSet = new HashSet<>();
            ImageGridTile[] gridTiles = cropExecuteLayout.getLpImagesGrid().getGridTiles();
            for(ImageGridTile tile : gridTiles){gridTileSet.add(tile);}

            int cores = Main.NUM_THREADS;
            */

            /*
            gridTileSet.parallelStream().forEach(new Consumer<ImageGridTile>() {
                @Override
                public void accept(ImageGridTile tile) {
                    Image cropppedImage = Utils.cropImage(tile.getImage(), )
                }
            });
            */
        }
    }




}
