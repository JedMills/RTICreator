package newProjectScene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import main.CreatorScene;
import main.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Jed on 06-Jul-17.
 */
public class NewProjectLayout extends HBox implements CreatorScene{

    private Button openFolderBtn;

    private Button addPropertyBtn;
    private Button delPropertyBtn;
    private Button savePropertiesBtn;
    private TableView<ProjectProperty> projectPropertyTableView;

    private TextField removeRsnTxtField;
    private Button removePicBtn;

    private Button backBtn;
    private Button nextBtn;

    private VBox toolbarLayout;
    private ScrollableImageGrid selectedImages;
    private ScrollableImageGrid rejectedImages;

    private static NewProjectLayout ourInstance = new NewProjectLayout();

    public static NewProjectLayout getInstance() {
        return ourInstance;
    }

    private NewProjectLayout() {
        toolbarLayout = createToolbarLayout();
        selectedImages = createSelectedImagesView();
        rejectedImages = createRejectedImagesView();

        setHgrow(toolbarLayout, Priority.SOMETIMES);
        setHgrow(selectedImages, Priority.ALWAYS);
        setHgrow(rejectedImages, Priority.ALWAYS);
        getChildren().addAll(toolbarLayout, selectedImages, rejectedImages);
        getStylesheets().add("stylesheets/newProjectScene.css");

        NewProjectLayoutListener.getInstance().init(this);
    }


    private VBox createToolbarLayout(){
        VBox vBox = new VBox();

            HBox openBtnBox = new HBox();
                openFolderBtn = new Button("Open folder");
                openFolderBtn.setId("openFolder");
                openFolderBtn.setOnAction(NewProjectLayoutListener.getInstance());
            openBtnBox.getChildren().add(openFolderBtn);
            openBtnBox.setAlignment(Pos.CENTER);
            openBtnBox.setPadding(new Insets(5, 5, 5, 5));


            GridPane projectPropertiesGrid = new GridPane();
                ColumnConstraints constraints = new ColumnConstraints();
                constraints.setPercentWidth(33.33);
                projectPropertyTableView = new TableView<>();
                projectPropertyTableView.setMinHeight(0);
                projectPropertyTableView.setMinWidth(0);
                GridPane.setConstraints(projectPropertyTableView, 0, 0, 3, 2);

                addPropertyBtn = new Button("Add");
                GridPane.setConstraints(addPropertyBtn, 0, 2, 1, 1);

                delPropertyBtn = new Button("Delete");
                GridPane.setConstraints(delPropertyBtn, 1, 2, 1, 1);

                savePropertiesBtn = new Button("Save");
                GridPane.setConstraints(savePropertiesBtn, 2, 2, 1, 1);
            projectPropertiesGrid.getChildren().addAll(projectPropertyTableView,
                                                addPropertyBtn, delPropertyBtn, savePropertiesBtn);
            projectPropertiesGrid.setHgap(5);
            projectPropertiesGrid.setVgap(5);
            projectPropertiesGrid.setAlignment(Pos.CENTER_RIGHT);
            projectPropertiesGrid.getColumnConstraints().addAll(constraints, constraints, constraints);
            projectPropertiesGrid.setPadding(new Insets(5, 5, 5, 5));


            HBox removePicBox = new HBox();
                removeRsnTxtField = new TextField();
                removePicBtn = new Button("Remove picture");
            removePicBox.getChildren().addAll(removeRsnTxtField, removePicBtn);
            removePicBox.setSpacing(5);
            removePicBox.setAlignment(Pos.CENTER);
            removePicBox.setPadding(new Insets(5, 5, 5, 5));

            HBox nextBackBox = new HBox();
                backBtn = new Button("Back");
                Pane spacer = new Pane();
                spacer.setPrefSize(50, 1);
                nextBtn = new Button("Next");
            nextBackBox.getChildren().addAll(backBtn, spacer, nextBtn);
            nextBackBox.setAlignment(Pos.CENTER);
            nextBackBox.setSpacing(5);
            nextBackBox.setPadding(new Insets(5, 5, 5, 5));

        vBox.getChildren().addAll(openBtnBox, projectPropertiesGrid, removePicBox, nextBackBox);

        vBox.setSpacing(20);
        vBox.setPadding(new Insets(10, 10, 10, 10));


        return vBox;
    }


    private ScrollableImageGrid createSelectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Selected Images", false, true);
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(250);


        return imageGrid;
    }


    private ScrollableImageGrid createRejectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Rejected Images", false, true);
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(250);

        return imageGrid;
    }



    public void loadImageDirectory(File directory){
        File[] images = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".jpg")){
                    return true;
                }
                return false;
            }
        });


        if(images.length == 0){
            Main.showFileReadingAlert("There are no images in the chosen directory.");
            return;
        }



        Image[] imagesInDir = new Image[images.length];
        String[] imageLocations = new String[images.length];
        String[] imageNames = new String[images.length];

        try {
            Image firstImg = new Image("file:" + images[0].getAbsolutePath());
            imagesInDir[0] = firstImg;
            imageLocations[0] = images[0].getAbsolutePath();
            imageNames[0] = images[0].getName();

            double imageHeight = firstImg.getHeight();
            double imageWidth = firstImg.getWidth();

            for(int i = 1; i < imagesInDir.length; i++){
                Image currentImg = new Image("file:" + images[i].getAbsolutePath());

                if(currentImg.getWidth() != imageWidth || currentImg.getHeight() != imageHeight){
                    Main.showFileReadingAlert("The width and height of all images in the directory do not match.");
                    return;
                }

                imagesInDir[i] = currentImg;
                imageLocations[i] = images[i].getAbsolutePath();
                imageNames[i] = images[i].getName();
            }

            loadImagesIntoPreview(imagesInDir, imageLocations, imageNames);

        }catch(Exception e){
            e.printStackTrace();
            Main.showFileReadingAlert("There was an error reading files in the chosen directory.");
        }
    }



    private void loadImagesIntoPreview(Image[] images, String[] imagePaths, String[] imageNames){
        for(int i = 0; i < images.length; i++){
            selectedImages.addImageTile(imageNames[i], images[i], 150, 150);
        }
    }




    @Override
    public int getSceneMinWidth() {
        return 500;
    }

    @Override
    public int getSceneMaxWidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSceneMinHeight() {
        return 500;
    }

    @Override
    public int getSceneMaxHeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void updateSize(double width, double height) {
        toolbarLayout.setPrefHeight(height);
        selectedImages.setPrefHeight(height);
        rejectedImages.setPrefHeight(height);
    }


}
