package guiComponents;

/**
 * This class is another ScrollableImageGrid, bu this one is linked to a {@link ImageCropPane} so that when a tile
 * in the ScrollableImageGrid is selected, the image in the CropPane changes to the selected tile's image.
 *
 * @see ImageGridTile
 * @see ImageCropPane
 *
 * @author Jed Mills
 */
public class ScrollableImageGridForCrop extends ScrollableImageGrid {

    /** The pane to update the images of */
    private ImageCropPane imageCropPane;

    /** If the ImageCropPane actually wants the images to be updated from this ScrollableImageGrid*/
    private boolean active;


    /**
     * Creates a enw ScrollableImageGridForCrop.
     *
     * @param title             title of the ScrollableImageGridForCrop
     * @param tickBox           whether the tiles made by this grid have tick boxes
     * @param clickable         whether the tiles made by this grid are selectable
     * @param preview           whether the tiles made by this grid have a preview on double-click
     * @param imageCropPane     the pane this grid is linked to to update the image of
     */
    public ScrollableImageGridForCrop(String title, boolean tickBox, boolean clickable, boolean preview,
                                      ImageCropPane imageCropPane) {
        super(title, tickBox, clickable, preview);
        this.imageCropPane = imageCropPane;
        active = true;
    }




    /**
     * Sets the ImageCropPane's image as the image in the selected tile.
     *
     * @param tile  tile to change the image to
     */
    @Override
    public void setSelectedTile(ImageGridTile tile) {
        super.setSelectedTile(tile);

        if(active) {
            imageCropPane.setImage(tile.getImage());
        }
    }




    /**
     * Set the image view of the crop pane that this ScrollableImageGridForCrop is linked to.
     *
     * @param imageCropPane image view to link to
     */
    public void setImageView(ImageCropPane imageCropPane){
        this.imageCropPane = imageCropPane;
    }

}
