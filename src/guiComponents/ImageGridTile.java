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
 * Created by jed on 07/07/2017.
 */
public class ImageGridTile extends VBox {

    private String name;
    private int width;
    private int height;
    private boolean hasTickBox;
    private boolean clickable;
    private BorderPane borderPane;
    private ImageView imageView;
    private ScrollableImageGrid parent;
    private String rejectComment;
    private Tooltip tooltip;
    private CheckBox checkBox;

    private static PseudoClass TILE_SELECTED_CLASS = PseudoClass.getPseudoClass("selected");

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



    public ImageGridTile(ScrollableImageGrid parent, String name, Image image, int width,
                                            int height, boolean hasTickBox, boolean clickable, boolean preview){
        this.parent = parent;
        this.name = name;
        this.width = width;
        this.height = height;
        this.hasTickBox = hasTickBox;
        this.clickable = clickable;

        tooltip = new Tooltip();
        tooltip.setTextAlignment(TextAlignment.JUSTIFY);

        createLayout(image);
        getStyleClass().add("imageGridTile");
        getStylesheets().add("stylesheets/default.css");

        if(preview){addPreviewListener(image);}
        if(clickable){addSelectableListener();}
    }

    private void createLayout(Image image){
        setPrefHeight(height);
        setPrefWidth(width);

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

        Label label = new Label(name);
        label.setPadding(new Insets(2, 2, 2, 2));
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);

        if(hasTickBox){
            checkBox = new CheckBox();
            labelBox.getChildren().addAll(checkBox, label);
        }else{
            labelBox.getChildren().add(label);

        }
        getChildren().addAll(borderPane, labelBox);
        setAlignment(Pos.TOP_CENTER);

        setPadding(new Insets(5, 5, 5, 5));
    }


    private void addPreviewListener(Image image){
        borderPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        BorderPane borderPane = new BorderPane();
                        ImageView imageView = new ImageView();
                        imageView.setImage(image);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setCache(true);
                        borderPane.setCenter(imageView);
                        borderPane.setStyle("-fx-background-color: BLACK");
                        Stage newStage = new Stage();
                        newStage.setWidth(300);
                        newStage.setHeight(300);

                        borderPane.prefHeightProperty().bind(newStage.heightProperty());
                        borderPane.prefWidthProperty().bind(newStage.widthProperty());
                        imageView.fitHeightProperty().bind(borderPane.heightProperty());
                        imageView.fitWidthProperty().bind(borderPane.widthProperty());

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


    private void addSelectableListener(){
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 1){
                        parent.setSelectedTile(ImageGridTile.this);
                    }
                }
            }
        });
    }


    public void setSelected(boolean selected){
        this.selected.set(selected);
    }

    public void setRejectComment(String comment){
        rejectComment = comment;
        tooltip.setText("Reject reason: " + comment);
        Tooltip.install(this, tooltip);
    }

    public void removeRejectComment(){
        Tooltip.uninstall(this, tooltip);
    }

    public void setParent(ScrollableImageGrid parent){
        this.parent = parent;
    }

    public Image getImage(){return imageView.getImage();}

    public String getName() {
        return name;
    }

    public int getTileWidth() {
        return width;
    }

    public int getTileHeight() {
        return height;
    }


    public void setTickBoxActive(boolean active){
        if(hasTickBox){checkBox.setDisable(!active);}
    }
}