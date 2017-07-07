package newProjectScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by jed on 07/07/2017.
 */
public class ImageGridTile extends VBox {

    private String name;
    private int width;
    private int height;
    private boolean tickBox;

    public ImageGridTile(String name, Image image, int width, int height, boolean tickBox){
        this.name = name;
        this.width = width;
        this.height = height;
        this.tickBox = tickBox;

        createLayout(image);
    }

    private void createLayout(Image image){
        setPrefHeight(height);
        setPrefWidth(width);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #000000;");
        borderPane.setPrefWidth(getPrefWidth());
        borderPane.setPrefHeight(getPrefHeight() - 20);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(borderPane.getPrefWidth());
            imageView.setFitHeight(borderPane.getPrefHeight());
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);
        borderPane.setCenter(imageView);



        Label label = new Label(name);

        getChildren().addAll(borderPane, label);
        setAlignment(Pos.TOP_CENTER);

        setStyle("-fx-border-color: #dddddd;"    +
                 "-fx-border-radius: 2;"         +
                 "-fx-background-color: #cccccc;"+
                 "-fx-background-radius: 2;"
        );
        setPadding(new Insets(5, 5, 5, 5));

    }
}