package guiComponents;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


/**
 * This is a layout component that is used to display a grid of ImageGridTiles. The tile grid is scrollable, surprise
 * surprise. The ScrollableImageGrid can create new ImageGridTiles, and updates the number of grid tiles in a row when
 * it is resized.
 *
 * @see ImageGridTile
 *
 * @author Jed Mills
 */
public class ScrollableImageGrid extends BorderPane {

    /** Contains the tile pane, gives scrollability */
    private ScrollPane scrollPane;

    /** Contains all of the ImageGridTiles */
    private TilePane tilePane;

    /** Whether the imageGridTiles created by this instance have tick boxes */
    private boolean tickBox;

    /** Whether the imageGridTiles created by this instance are selectable */
    private boolean clickable;

    /** The currently selected ImageGridTile of this instance */
    private ImageGridTile selectedTile;

    /** Whether the imageGridTiles created by this instance have previews on double click */
    private boolean preview;


    /**
     * Creates a enw ScrollableImageGrid.
     *
     * @param title             title of the ScrollableImageGridForCrop
     * @param tickBox           whether the tiles made by this grid have tick boxes
     * @param clickable         whether the tiles made by this grid are selectable
     * @param preview           whether the tiles made by this grid have a preview on double-click
     */
    public ScrollableImageGrid(String title, boolean tickBox, boolean clickable, boolean preview){
        this.tickBox = tickBox;
        this.clickable = clickable;
        this.preview = preview;

        createLayout(title);
        getStylesheets().add("stylesheets/default.css");
        getStyleClass().add("scrollableImageGrid");
    }


    /**
     * Creates the layout of this component. The layout is basically a ScrollPane with a TilePane inside it, so
     * the TilePane can be scrolled. The whole thing is a BorderPane so there can be a title at the top.
     *
     * @param title     title to make foe the ScrollableImageGrid
     */
    private void createLayout(String title){
        //has the title in it
        HBox titleBox = new HBox();

        Label gridTitle = new Label(title);
        gridTitle.setFont(Font.font(null, FontWeight.BOLD, 12));
        gridTitle.setPadding(new Insets(5, 5, 5, 5));
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(gridTitle);

        //has the tile pane in it
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        //never have a horizontal bar, only have as many tiles as you can fit in a row
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //has the tiles in it
        tilePane = new TilePane();
        scrollPane.setContent(tilePane);
        scrollPane.getStyleClass().add("imageGridScrollPane");
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPadding(new Insets(10, 10, 10, 10));
        tilePane.setTileAlignment(Pos.CENTER);
        scrollPane.setFitToWidth(true);

        setTop(titleBox);
        setCenter(scrollPane);
    }


    /**
     * Adds a ImageGridTile to the ScrollableImageGrid on the JavaFX thread.
     *
     * @param tile  tileto add
     */
    public void addImageTile(ImageGridTile tile){
        //has to be on the javafx thread otherwise javafx complains
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().add(tile);
            }
        });
    }


    /**
     * Creates anew ImageGridTilewith the given parameters and adds it to this ScrollableImageGrid on the
     * JavaFx thread.
     *
     * @param name          name of the tile to make
     * @param image         image in the tile to make
     * @param width         width of the tile to make
     * @param height        height of the tile to make
     */
    public void addImageTile(String name, Image image, int width, int height){
        ImageGridTile imageGridTile = new ImageGridTile(this, name, image, width, height,
                                                                tickBox, clickable, preview);

        addImageTile(imageGridTile);
    }


    /**
     * Set the height of this whole component.
     *
     * @param height    preffered height of this whole component
     */
    public void setTheHeight(double height) {
        setPrefHeight(height);
        tilePane.setPrefHeight(height);
        scrollPane.setPrefHeight(height);
    }


    /**
     * Removees all the ImageGridTiles from this ScrollableImageGrid. on the JavaFX thread.
     *
     */
    public void clearTiles(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().clear();
                selectedTile = null;
            }
        });
    }


    /**
     * Sets the currently selected tile of this ScrollableImageGrid to the given tile.
     *
     * @param tile  tile to be selected
     */
    public void setSelectedTile(ImageGridTile tile){
        //deselct all the tiles so there's only ever one selected
        ImageGridTile currentTile;
        for(Node node : tilePane.getChildren()){
            if(node instanceof ImageGridTile){
                currentTile = (ImageGridTile) node;
                currentTile.setSelected(false);
            }
        }
        //select the passed tile
        selectedTile = tile;
        selectedTile.setSelected(true);
    }


    /**
     * @return {@link ScrollableImageGrid#selectedTile}
     */
    public ImageGridTile getSelectedTile(){
        return selectedTile;
    }


    /**
     * Removes the currently selected tile from this ScrollableImageGrid.
     */
    public void removeSelectedTile(){
        tilePane.getChildren().remove(selectedTile);
        selectedTile = null;
    }


    /**
     * Returns all the images in all of the ImageGridTilesin this ScrollableImageGrid. If there are no
     * ImageGridTiles, returns an empty array.
     *
     * @return  all the images of all the tiles
     */
    public Image[] getAllImages(){
        Image[] images = new Image[tilePane.getChildren().size()];

        //get all the images
        int i = 0;
        for(Node node : tilePane.getChildren()){
            if(node instanceof ImageGridTile){
                images[i] = ((ImageGridTile) node).getImage();
                i ++;
            }
        }
        return images;
    }


    /**
     * Returns an array of all the ImageGridTiles in this ScrollableImageGrid. If there are no tiles, returns
     * an empty array.
     *
     * @return
     */
    public ImageGridTile[] getGridTiles(){
        ImageGridTile[] tiles = new ImageGridTile[tilePane.getChildren().size()];

        //get all the tiles
        int i = 0;
        for(Node node : tilePane.getChildren()){
            if(node instanceof ImageGridTile){
                tiles[i] = ((ImageGridTile) node);
                i ++;
            }
        }
        return tiles;
    }

}
