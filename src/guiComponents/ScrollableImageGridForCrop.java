package guiComponents;

import javafx.scene.image.ImageView;

/**
 * Created by Jed on 11-Jul-17.
 */
public class ScrollableImageGridForCrop extends ScrollableImageGrid {

    private ImageView imageView;

    public ScrollableImageGridForCrop(String title, boolean tickBox, boolean clickable, boolean preview,
                                      ImageView imageView) {
        super(title, tickBox, clickable, preview);
        this.imageView = imageView;
    }


    @Override
    public void setSelectedTile(ImageGridTile tile) {
        super.setSelectedTile(tile);

        imageView.setImage(tile.getImage());
    }


    public void setImageView(ImageView imageView){
        this.imageView = imageView;
    }
}
