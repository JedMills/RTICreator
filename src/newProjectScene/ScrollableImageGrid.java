package newProjectScene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.Main;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by Jed on 07-Jul-17.
 */
public class ScrollableImageGrid extends VBox {

    private ScrollPane scrollPane;
    private TilePane tilePane;
    private boolean tickBox;
    private boolean clickable;

    public ScrollableImageGrid(String title, boolean tickBox, boolean clickable){
        this.tickBox = tickBox;
        this.clickable = clickable;

        createLayout(title);
    }



    private void createLayout(String title){
        HBox titleBox = new HBox();
        Label gridTitle = new Label(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(gridTitle);
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tilePane = new TilePane();
        scrollPane.setContent(tilePane);
        tilePane.setAlignment(Pos.TOP_LEFT);
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPadding(new Insets(10, 10, 10, 10));

        getChildren().addAll(titleBox, scrollPane);
    }




    public void addImageTile(String name, Image image, int width, int height){
        ImageGridTile imageGridTile = new ImageGridTile(name, image, width, height, tickBox);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().add(imageGridTile);

                if(clickable) {
                    tilePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
                                    newStage.setWidth(image.getWidth());
                                    newStage.setHeight(image.getHeight());

                                    borderPane.prefHeightProperty().bind(newStage.heightProperty());
                                    borderPane.prefWidthProperty().bind(newStage.widthProperty());
                                    imageView.fitHeightProperty().bind(borderPane.heightProperty());
                                    imageView.fitWidthProperty().bind(borderPane.widthProperty());

                                    newStage.setTitle("Preview: " + name);
                                    Scene scene = new Scene(borderPane, Color.BLACK);
                                    newStage.setScene(scene);
                                    newStage.show();

                                }
                            }
                        }
                    });
                }
            }
        });
    }

}
