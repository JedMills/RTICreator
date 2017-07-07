package newProjectScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import main.CreatorScene;

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
    private VBox selectedImages;
    private VBox rejectedImages;

    private static NewProjectLayout ourInstance = new NewProjectLayout();

    public static NewProjectLayout getInstance() {
        return ourInstance;
    }

    private NewProjectLayout() {
        toolbarLayout = createToolbarLayout();
        selectedImages = createSelectedImagesView();
        rejectedImages = createRejectedImagesView();

        setHgrow(toolbarLayout, Priority.NEVER);
        setHgrow(selectedImages, Priority.ALWAYS);
        setHgrow(rejectedImages, Priority.ALWAYS);
        getChildren().addAll(toolbarLayout, selectedImages, rejectedImages);
        getStylesheets().add("stylesheets/newProjectScene.css");
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


    private VBox createSelectedImagesView(){
        VBox imageGrid  = new ScrollableImageGrid("Selected Images");
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);


        return imageGrid;
    }


    private VBox createRejectedImagesView(){
        VBox imageGrid  = new ScrollableImageGrid("Rejected Images");
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);

        return imageGrid;
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
