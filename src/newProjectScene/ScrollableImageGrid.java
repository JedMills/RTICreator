package newProjectScene;

import javafx.event.EventHandler;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by Jed on 07-Jul-17.
 */
public class ScrollableImageGrid extends VBox {

    private ScrollPane scrollPane;
    private TilePane tilePane;
    private ArrayList<ImageView> imageViews;

    public ScrollableImageGrid(String title){
        HBox titleBox = new HBox();
        Label gridTitle = new Label(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(gridTitle);


        imageViews = new ArrayList<>();

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        tilePane = new TilePane();
        scrollPane.setContent(tilePane);

        getChildren().addAll(titleBox, scrollPane);
    }


    public void addImage(Image image){
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        BorderPane borderPane = new BorderPane();
                        ImageView imageView = new ImageView();
                        imageView.setImage(image);
                        imageView.setStyle("-fx-background-color: BLACK");
                        imageView.setFitHeight(Main.primaryStage.getHeight() - 10);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        imageView.setCache(true);
                        borderPane.setCenter(imageView);
                        borderPane.setStyle("-fx-background-color: BLACK");
                        Stage newStage = new Stage();
                        newStage.setWidth(Main.primaryStage.getWidth());
                        newStage.setHeight(Main.primaryStage.getHeight());
                        Scene scene = new Scene(borderPane, Color.BLACK);
                        newStage.setScene(scene);
                        newStage.show();

                    }
                }
            }
        });
    }
}
