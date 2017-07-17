package guiComponents;

/**
 * Created by Jed on 11-Jul-17.
 */
public class ScrollableImageGridForCrop extends ScrollableImageGrid {

    private ImageCropPane imageCropPane;
    private boolean active;

    public ScrollableImageGridForCrop(String title, boolean tickBox, boolean clickable, boolean preview,
                                      ImageCropPane imageCropPane) {
        super(title, tickBox, clickable, preview);
        this.imageCropPane = imageCropPane;
        active = true;
    }


    @Override
    public void setSelectedTile(ImageGridTile tile) {
        super.setSelectedTile(tile);

        if(active) {
            imageCropPane.setImage(tile.getImage());
        }
    }


    public void setImageView(ImageCropPane imageCropPane){
        this.imageCropPane = imageCropPane;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
