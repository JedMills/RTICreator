package newProjectScene;

import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGrid;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.*;
import utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jed on 06-Jul-17.
 */
public class NewProjectLayout extends VBox implements CreatorScene{

    private HBox mainLayout;
    private HBox nextBackBox;

    private Button openFolderBtn;

    private Button addPropertyBtn;
    private Button delPropertyBtn;
    private TableView<ProjectProperty> projectPropertyTableView;

    private TextArea removeRsnTxtField;
    private Button removePicBtn;
    private Button replaceBtn;

    private Button backBtn;
    private Button nextBtn;

    private VBox toolbarLayout;
    private ScrollableImageGrid selectedImages;
    private VBox removePicPane;
    private ScrollableImageGrid rejectedImages;

    private static NewProjectLayout ourInstance = new NewProjectLayout();

    public static NewProjectLayout getInstance() {
        return ourInstance;
    }

    private File imgsFolder;
    private File assemblyFileFolder;
    private File lpFile;

    private RTIProject rtiProject;

    private HashMap<String, Utils.Vector3f> lpData;

    private boolean resourcesSet = false;

    private NewProjectLayout() {
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        NewProjectLayoutListener.getInstance().init(this);
        LoadProjRsrcsDialog.getInstance().init(this);
        AddPropertyDialog.getInstance().init(this);
    }


    private void createLayout(){
        mainLayout = new HBox();
        toolbarLayout = createToolbarLayout();
        selectedImages = createSelectedImagesView();
        removePicPane = createRemovePicPane();
        rejectedImages = createRejectedImagesView();

        HBox.setHgrow(toolbarLayout, Priority.SOMETIMES);
        HBox.setHgrow(selectedImages, Priority.ALWAYS);
        HBox.setHgrow(removePicPane, Priority.NEVER);
        HBox.setHgrow(rejectedImages, Priority.ALWAYS);

        HBox.setMargin(toolbarLayout, new Insets(5, 5, 5, 5));
        HBox.setMargin(selectedImages, new Insets(5, 5, 5, 5));
        HBox.setMargin(removePicPane, new Insets(5, 5, 5, 5));
        HBox.setMargin(rejectedImages, new Insets(5, 5, 5,5 ));

        nextBackBox = createNextBackBox();
    }






    private VBox createToolbarLayout(){
        VBox vBox = new VBox();

            VBox openBtnBox = new VBox();
                Label openProjectImagesLabel = new Label("Open Project Resources");
                openProjectImagesLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
                openFolderBtn = new Button("Open resources");
                openFolderBtn.setId("openFolder");
                openFolderBtn.setOnAction(NewProjectLayoutListener.getInstance());
            openBtnBox.getChildren().addAll(openProjectImagesLabel, openFolderBtn);
            openBtnBox.setAlignment(Pos.CENTER);
            openBtnBox.setPadding(new Insets(5, 5, 5, 5));
            openBtnBox.setId("openBtnBox");
            openBtnBox.setSpacing(5);


            VBox projectPropertiesPane = new VBox();
                Label propertiesLabel = new Label("Project Properties");
                propertiesLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
                projectPropertyTableView = createPropertiesTable();

                addPropertyBtn = new Button("Add");
                addPropertyBtn.setId("addPropertyButton");
                addPropertyBtn.setOnAction(NewProjectLayoutListener.getInstance());

                Pane spacer1 = new Pane();
                HBox.setHgrow(spacer1, Priority.ALWAYS);
                spacer1.setMinSize(10, 1);

                delPropertyBtn = new Button("Delete");
                delPropertyBtn.setId("delPropertyButton");
                delPropertyBtn.setOnAction(NewProjectLayoutListener.getInstance());

                HBox propertiesButtonsBox = new HBox();
                propertiesButtonsBox.getChildren().addAll(addPropertyBtn, spacer1, delPropertyBtn);
                propertiesButtonsBox.setStyle("-fx-border-color: transparent;");
                propertiesButtonsBox.setId("propertiesButtonBox");
                propertiesButtonsBox.setAlignment(Pos.CENTER);
            projectPropertiesPane.getChildren().addAll(propertiesLabel, projectPropertyTableView, propertiesButtonsBox);
            projectPropertiesPane.setPadding(new Insets(5, 5, 5, 5));
            projectPropertiesPane.getStyleClass().add("projectPropertiesPane");
            projectPropertiesPane.setSpacing(5);
            projectPropertiesPane.setAlignment(Pos.CENTER);


        vBox.getChildren().addAll(openBtnBox, projectPropertiesPane);

        vBox.setSpacing(20);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinWidth(175);

        return vBox;
    }



    private TableView createPropertiesTable(){
        TableView<ProjectProperty> table = new TableView<>();
        table.setMinHeight(0);
        table.setMinWidth(0);
        table.getColumns().clear();

        TableColumn fieldColumn = new TableColumn("Field");
        fieldColumn.setCellValueFactory(new PropertyValueFactory<ProjectProperty, String>("field"));
        fieldColumn.setSortable(false);

        TableColumn valueColumn = new TableColumn("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<ProjectProperty, String>("value"));
        valueColumn.setSortable(false);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().add(fieldColumn);
        table.getColumns().add(valueColumn);



        return table;
    }


    private VBox createRemovePicPane(){
        VBox removePicPane = new VBox();

            VBox insidePane = new VBox();
            insidePane.setId("removePicPane");
            Label removeLabel = new Label("Remove Photos");
            removeLabel.setFont(Font.font(null, FontWeight.BOLD, 12));

            removeRsnTxtField = new TextArea();
            removePicBtn = new Button("Remove picture >>");
            removePicBtn.setId("removePicButton");
            removePicBtn.setOnAction(NewProjectLayoutListener.getInstance());

            replaceBtn = new Button("<< Replace picture");
            replaceBtn.setId("replacePicBtn");
            replaceBtn.setOnAction(NewProjectLayoutListener.getInstance());

            insidePane.getChildren().addAll(removeLabel, removeRsnTxtField, removePicBtn, replaceBtn);
            insidePane.setSpacing(5);
            insidePane.setPadding(new Insets(5, 5, 5, 5));
            insidePane.setAlignment(Pos.CENTER);
        removePicPane.getChildren().add(insidePane);
        removePicPane.setPadding(new Insets(5, 5, 5, 5));
        removePicPane.setAlignment(Pos.CENTER);
        removePicPane.setMaxWidth(180);
        removePicPane.setMinWidth(130);

        return removePicPane;
    }




    private HBox createNextBackBox(){
        HBox hBox = new HBox();
        hBox.getStyleClass().add("bottomBar");
        backBtn = new Button("< Back");
        backBtn.setId("backBtn");
        backBtn.setOnAction(NewProjectLayoutListener.getInstance());
        backBtn.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        nextBtn = new Button("Next >");
        nextBtn.setId("nextBtn");
        nextBtn.setOnAction(NewProjectLayoutListener.getInstance());
        nextBtn.setMaxSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinSize(10, 1);

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backBtn, spacer, nextBtn);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }




    private ScrollableImageGrid createSelectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Selected Images", false,
                                                                true, true);
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(180);

        return imageGrid;
    }




    private ScrollableImageGrid createRejectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Rejected Images", false,
                                                                    true, true);
        imageGrid.setPadding(new Insets(5, 5, 5, 5));
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(180);

        return imageGrid;
    }





    public void loadImageDirectory(File directory){
        Main.showLoadingDialog("Loading images...");

        try{
            File[] images = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png")){
                        return true;
                    }
                    return false;
                }
            });


            if(images.length == 0){
                Main.hideLoadingDialog();
                Main.showFileReadingAlert("There are no images in the chosen directory.");
                return;
            }

            Image[] imagesInDir = new Image[images.length];
            String[] imageLocations = new String[images.length];
            String[] imageNames = new String[images.length];

            String format = "";
            if(images[0].getName().toLowerCase().endsWith(".jpg")){format = "jpg";}
            else if(images[0].getName().toLowerCase().endsWith(".png")){format = "png";}

            Image firstImg = new Image("file:" + images[0].getAbsolutePath());
            imagesInDir[0] = firstImg;
            imageLocations[0] = images[0].getAbsolutePath();
            imageNames[0] = images[0].getName();

            double imageHeight = firstImg.getHeight();
            double imageWidth = firstImg.getWidth();

            for(int i = 1; i < imagesInDir.length; i++){
                Image currentImg = new Image("file:" + images[i].getAbsolutePath());

                if(currentImg.getWidth() != imageWidth || currentImg.getHeight() != imageHeight){
                    Main.showFileReadingAlert("The width and height of all images in the folder do not match.");
                    Main.hideLoadingDialog();
                    return;
                }

                String name = images[i].getName().toLowerCase();
                if((name.endsWith(".png") && format.equals("jpg")) ||
                        (name.endsWith(".jpg") && format.equals("png"))){
                    Main.showFileReadingAlert("All images in folder must be of the same format.");
                    Main.hideLoadingDialog();
                    return;
                }

                imagesInDir[i] = currentImg;
                imageLocations[i] = images[i].getAbsolutePath();
                imageNames[i] = images[i].getName();
            }

            selectedImages.clearTiles();
            rejectedImages.clearTiles();
            resetPropertiesTable();
            loadImagesIntoPreview(imagesInDir, imageNames);
            addWidthHeightToTable(imageWidth, imageHeight);

        }catch(Exception e){
            e.printStackTrace();
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("There was an error reading files in the chosen directory.");
        }
    }


    private void addWidthHeightToTable(double width, double height){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                projectPropertyTableView.getItems().add(new ProjectProperty("Width",
                        String.valueOf(width)));

                projectPropertyTableView.getItems().add(new ProjectProperty("Height",
                        String.valueOf(height)));
            }
        });
    }



    private void resetPropertiesTable(){
        projectPropertyTableView.getItems().clear();

        for(ProjectProperty property : rtiProject.getProjectProperties()){
            projectPropertyTableView.getItems().add(property);
        }
    }



    private void loadImagesIntoPreview(Image[] images, String[] imageNames){
        for(int i = 0; i < images.length; i++){
            selectedImages.addImageTile(imageNames[i], images[i], 150, 150);
        }
        Main.hideLoadingDialog();
    }



    public void setProject(RTIProject rtiProject){
        this.rtiProject = rtiProject;
        setupLayoutForType(rtiProject.getProjectType());
        for(ProjectProperty property : rtiProject.getProjectProperties()){
            projectPropertyTableView.getItems().add(property);
        }

    }


    private void setupLayoutForType(ProjectType type){
        mainLayout.getChildren().clear();
        getChildren().clear();

        if(type.equals(ProjectType.HIGHLIGHT)){
            mainLayout.getChildren().addAll(toolbarLayout, selectedImages, removePicPane, rejectedImages);
        }else{
            mainLayout.getChildren().addAll(toolbarLayout, selectedImages);
        }

        getChildren().addAll(mainLayout, nextBackBox);
    }



    @Override
    public int getSceneMinWidth() {
        return 720;
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
        selectedImages.setTheHeight(height);
        rejectedImages.setTheHeight(height);
    }



    public void setResources(String imgsLocation, String assemblyFilesLocation){
        File imgsFolder = new File(imgsLocation);
        File assemblyFileFolder = new File(assemblyFilesLocation);

        if((!imgsFolder.exists()) || (!assemblyFileFolder.exists()) || (!imgsFolder.isDirectory()) || (!assemblyFileFolder.isDirectory())){
            Main.showFileReadingAlert("Cannot find specified directories. Check that they still exist.");
            return;
        }

        this.imgsFolder = imgsFolder;
        Main.currentImagesFolder = imgsFolder;
        this.assemblyFileFolder = assemblyFileFolder;
        Main.currentAssemblyFolder = assemblyFileFolder;

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageDirectory(imgsFolder);
            }
        }).start();

        resourcesSet = true;
    }




    public void setResources(String imgsLocation, String lpLocation, String assemblyFilesLocation){
        File lpFile = new File(lpLocation);

        this.lpFile = lpFile;
        Main.currentLPFile = lpFile;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lpData = Utils.readLPFile(lpFile);
                    setResources(imgsLocation, assemblyFilesLocation);
                }catch(IOException e){
                    e.printStackTrace();
                    Main.showFileReadingAlert("Error accessing LP file at: " + lpLocation);
                }catch(Utils.LPException e){
                    e.printStackTrace();
                    Main.showFileReadingAlert("Error parsing LP file at: " + lpLocation +
                                                                                ", " + e.getMessage());
                }
            }
        }).start();

    }







    public ProjectType getProjectType(){
        return rtiProject.getProjectType();
    }


    public String[] getPropertyNamesLower(){
        String[] names = new String[projectPropertyTableView.getItems().size()];

        for(int i = 0; i < projectPropertyTableView.getItems().size(); i++){
            names[i] = projectPropertyTableView.getItems().get(i).getField().toLowerCase();
        }

        for(String s : names){
            System.out.println(s);
        }

        return names;
    }

    public void addProjectProperty(ProjectProperty property){
        projectPropertyTableView.getItems().add(property);
    }



    public void deleteSelectedProperty(){
        ProjectProperty selected = projectPropertyTableView.getSelectionModel().getSelectedItem();

        if(Utils.checkIn(selected.getField(), new String[]{"Name", "Type", "Width", "Height"})){
            Main.showInputAlert("Not allowed to delete that property.");
            return;
        }

        rtiProject.getProjectProperties().remove(selected);
        projectPropertyTableView.getItems().remove(selected);
    }

    public TextArea getRemoveRsnTxtField() {
        return removeRsnTxtField;
    }


    public ImageGridTile removeGridTileSelected(){
        ImageGridTile tile = selectedImages.getSelectedTile();
        selectedImages.removeSelectedTile();
        return tile;
    }

    public void addTileToRejected(ImageGridTile tile){
        tile.setParent(rejectedImages);
        rejectedImages.addImageTile(tile);
    }


    public ImageGridTile removeGridTileRejected(){
        ImageGridTile tile = rejectedImages.getSelectedTile();
        rejectedImages.removeSelectedTile();
        return tile;
    }

    public void addTileToSelected(ImageGridTile tile){
        tile.setParent(selectedImages);
        selectedImages.addImageTile(tile);
    }


    public void resetScene(){
        projectPropertyTableView.getItems().clear();
        selectedImages.clearTiles();
        rejectedImages.clearTiles();
        removeRsnTxtField.setText("");
    }

    public HashMap<String, Utils.Vector3f> getLpData() {
        return lpData;
    }


    public boolean isResourcesSet() {
        return resourcesSet;
    }


    public File getImgsFolder() {
        return imgsFolder;
    }

    public File getAssemblyFileFolder() {
        return assemblyFileFolder;
    }

    public File getLpFile() {
        return lpFile;
    }

    public ScrollableImageGrid getSelectedImages(){
        return selectedImages;
    }

    public ArrayList<ImageGridTile> getSelectedImages(ArrayList<String> filter){
        ArrayList<ImageGridTile> tiles = new ArrayList<>();

        for(ImageGridTile tile : selectedImages.getGridTiles()){
            if(Utils.checkIn(tile.getName(), filter)){
                tiles.add(tile);
            }
        }
        return tiles;
    }

    public String getImagesExtension(){
        String s = selectedImages.getGridTiles()[0].getName().split("[.]")[1].toLowerCase();
        System.out.println(s);
        return s;
    }


}
