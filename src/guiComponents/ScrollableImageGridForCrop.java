package guiComponents;

import cropExecuteScene.ImageCropPane;
import javafx.scene.image.ImageView;

/**
 * Created by Jed on 11-Jul-17.
 */
public class ScrollableImageGridForCrop extends ScrollableImageGrid {

    private ImageCropPane imageCropPane;

    public ScrollableImageGridForCrop(String title, boolean tickBox, boolean clickable, boolean preview,
                                      ImageCropPane imageCropPane) {
        super(title, tickBox, clickable, preview);
        this.imageCropPane = imageCropPane;
    }


    @Override
    public void setSelectedTile(ImageGridTile tile) {
        super.setSelectedTile(tile);

        imageCropPane.setImage(tile.getImage());
    }


    public void setImageView(ImageCropPane imageCropPane){
        this.imageCropPane = imageCropPane;
    }
}
