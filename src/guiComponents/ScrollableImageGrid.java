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
 * Created by Jed on 07-Jul-17.
 */
public class ScrollableImageGrid extends BorderPane {

    private ScrollPane scrollPane;
    private TilePane tilePane;
    private boolean tickBox;
    private boolean clickable;
    private ImageGridTile selectedTile;
    private boolean preview;

    public ScrollableImageGrid(String title, boolean tickBox, boolean clickable, boolean preview){
        this.tickBox = tickBox;
        this.clickable = clickable;
        this.preview = preview;

        createLayout(title);
        getStylesheets().add("stylesheets/newProjectScene.css");
        getStyleClass().add("scrollableImageGrid");
    }



    private void createLayout(String title){
        HBox titleBox = new HBox();

        Label gridTitle = new Label(title);
        gridTitle.setFont(Font.font(null, FontWeight.BOLD, 12));
        gridTitle.setPadding(new Insets(5, 5, 5, 5));
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(gridTitle);
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

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


    public void addImageTile(ImageGridTile tile){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().add(tile);
            }
        });
    }


    public void addImageTile(String name, Image image, int width, int height){
        ImageGridTile imageGridTile = new ImageGridTile(this, name, image, width, height,
                                                                tickBox, clickable, preview);

        addImageTile(imageGridTile);
    }


    public void setTheHeight(double height) {
        setPrefHeight(height);
        tilePane.setPrefHeight(height);
        scrollPane.setPrefHeight(height);
    }


    public void clearTiles(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().clear();
                selectedTile = null;
            }
        });
    }

    public void setSelectedTile(ImageGridTile tile){
        ImageGridTile currentTile;
        for(Node node : tilePane.getChildren()){
            if(node instanceof ImageGridTile){
                currentTile = (ImageGridTile) node;
                currentTile.setSelected(false);
            }
        }
        selectedTile = tile;
        selectedTile.setSelected(true);
    }

    public ImageGridTile getSelectedTile(){
        return selectedTile;
    }

    public void removeSelectedTile(){
        tilePane.getChildren().remove(selectedTile);
        selectedTile = null;
    }

}
