package guiComponents;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.Main;

/**
 * The grid tiles that are part of the {@link ScrollableImageGrid} or {@link ScrollableImageGridForCrop}. the Tiles have
 * an image in them, a name, and can have a tooltip when hovered over. When clicked the tiles become
 * highlighted like other focused widgets, and double clicking will create a new window with  a preview of the image.
 * These grid tiles are passed about in the app so that the images that the user is using do not have to be reloaded
 * everytime the scene is changed or whatever.
 *
 * @see ScrollableImageGridForCrop
 * @see ScrollableImageGrid
 *
 * @author Jed Mills
 */
public class ImageGridTile extends VBox {

    /** Name of the image in the grid tile*/
    private String name;

    /** Width of the whole tile */
    private int width;

    /** Height of the whole tile */
    private int height;

    /** The border pane that contains the imageview */
    private BorderPane borderPane;

    /** Contains the image */
    private ImageView imageView;

    /** The ScrollableImageGrid that this tile belongs to */
    private ScrollableImageGrid parent;

    /** The tooltip that is displayed on mouse hover */
    private Tooltip tooltip;

    /** New CSS psuedo class that allows this tile to be highlighted blue when clicked, like other JavaFX widgets */
    private static PseudoClass TILE_SELECTED_CLASS = PseudoClass.getPseudoClass("selected");


    /**
     * Used for whether this tile is selected, to know if the clsss need to be styled with the blue highlight or not.
     * This system works well with the CSS selectors.
     */
    BooleanProperty selected = new BooleanPropertyBase(false) {

        @Override
        public void invalidated(){
            pseudoClassStateChanged(TILE_SELECTED_CLASS, get());
        }

        @Override
        public Object getBean() {
            return ImageGridTile.this;
        }

        @Override
        public String getName() {
            return "selected";
        }
    };


    /**
     * Creates a new ImageGridTile.
     *
     * @param parent        the ScrollableImageGrid that this tle belonsg tp
     * @param name          name for this ti;e
     * @param image         image to display in this tile
     * @param width         width of the whole tile
     * @param height        height of the whole tile
     * @param hasTickBox    whether the tile has a tick box
     * @param clickable     whether the tile can be clicked to highlight it
     * @param preview       whether a preview pane will open on double click
     */
    public ImageGridTile(ScrollableImageGrid parent, String name, Image image, int width,
                                            int height, boolean hasTickBox, boolean clickable, boolean preview){
        this.parent = parent;
        this.name = name;
        this.width = width;
        this.height = height;

        tooltip = new Tooltip();
        tooltip.setTextAlignment(TextAlignment.JUSTIFY);

        createLayout(image);
        getStyleClass().add("imageGridTile");
        getStylesheets().add("stylesheets/default.css");

        //if it ahs previews, add a listener for the double click
        if(preview){addPreviewListener(image);}
        //if it can be highlighted, add a listener for teh single click
        if(clickable){addSelectableListener();}
    }


    /**
     * Creates the layout for this grid tile.
     *
     * @param image the image to display in this tile.
     */
    private void createLayout(Image image){
        setPrefHeight(height);
        setPrefWidth(width);

        //contains the imageview so that the black bars can shown on either side/above/below
        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #000000;");
        borderPane.setPrefWidth(getPrefWidth());
        borderPane.setPrefHeight(getPrefHeight() - 20);
            imageView = new ImageView(image);
            imageView.setFitWidth(borderPane.getPrefWidth());
            imageView.setFitHeight(borderPane.getPrefHeight());
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);
        borderPane.setCenter(imageView);

        //the labels and stuff
        Label label = new Label(name);
        label.setPadding(new Insets(2, 2, 2, 2));
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);

        labelBox.getChildren().add(label);

        getChildren().addAll(borderPane, labelBox);
        setAlignment(Pos.TOP_CENTER);

        setPadding(new Insets(5, 5, 5, 5));
    }


    /**
     * Adds the listener for the double click to open a new window with the image preview.
     *
     * @param image     the image of thisgrid tile, that will be shown in the preview window
     */
    private void addPreviewListener(Image image){
        borderPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        //if it'sa double click, show the preview
                        BorderPane borderPane = new BorderPane();
                        ImageView imageView = new ImageView();

                        //show the image in the voew
                        imageView.setImage(image);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setCache(true);
                        borderPane.setCenter(imageView);
                        borderPane.setStyle("-fx-background-color: BLACK");
                        Stage newStage = new Stage();
                        newStage.setWidth(300);
                        newStage.setHeight(300);

                        //make the image fit the size of the window
                        borderPane.prefHeightProperty().bind(newStage.heightProperty());
                        borderPane.prefWidthProperty().bind(newStage.widthProperty());
                        imageView.fitHeightProperty().bind(borderPane.heightProperty());
                        imageView.fitWidthProperty().bind(borderPane.widthProperty());

                        //title of the window is the name ofthe tile
                        newStage.setTitle("Preview: " + name);
                        newStage.getIcons().add(Main.thumbnail);
                        Scene scene = new Scene(borderPane, Color.BLACK);
                        newStage.setScene(scene);
                        newStage.show();

                    }
                }
            }
        });
    }


    /**
     * Adds the listener so the tile becomes highlighted when it is clicked.
     */
    private void addSelectableListener(){
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    //only a single click from the left mouse button
                    if(event.getClickCount() == 1){
                        parent.setSelectedTile(ImageGridTile.this);
                    }
                }
            }
        });
    }



    /**
     * @param selected  sets {@link ImageGridTile#selected} as the value
     */
    public void setSelected(boolean selected){
        this.selected.set(selected);
    }


    /**
     * Sets the tooltip text as "Reject reason: " + the comment
     *
     * @param comment   reason for image rejection
     */
    public void setRejectComment(String comment){
        tooltip.setText("Reject reason: " + comment);
        Tooltip.install(this, tooltip);
    }

    /**
     * Removes the tooltip from this tile.
     */
    public void removeRejectComment(){
        Tooltip.uninstall(this, tooltip);
    }


    /**
     * @param parent sets {@link ImageGridTile#parent}
     */
    public void setParent(ScrollableImageGrid parent){
        this.parent = parent;
    }


    /**
     * @return the image in this tile
     */
    public Image getImage(){return imageView.getImage();}


    /**
     * @return {@link ImageGridTile#name}
     */
    public String getName() {
        return name;
    }

    /**
     * @return {@link ImageGridTile#width}
     */
    public int getTileWidth() {
        return width;
    }


    /**
     * @return {@link ImageGridTile#height}
     */
    public int getTileHeight() {
        return height;
    }

}