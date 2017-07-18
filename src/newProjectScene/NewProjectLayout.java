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


    private TextArea removeRsnTxtField;
    private Button removePicBtn;
    private Button replaceBtn;


    private TextField projectNameField;
    private TextField projectTypeField;
    private TextField imageWidthField;
    private TextField imageHeightField;


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

        mainLayout.setSpacing(5);

        nextBackBox = createNextBackBox();
        setSpacing(5);
        setPadding(new Insets(5, 5, 5, 5));
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

                GridPane gridPane = new GridPane();
                    Label nameLabel = createLabel("Name:", 0, 0);
                    projectNameField = createTextField(1, 0);

                    Label typeLabel = createLabel("Type:", 0, 1);
                    projectTypeField = createTextField(1, 1);

                    Label widthLabel = createLabel("Image Width:", 0, 2);
                    imageWidthField = createTextField(1, 2);

                    Label heightLabel = createLabel("Image Height:", 0, 3);
                    imageHeightField = createTextField(1, 3);
                gridPane.getChildren().addAll(nameLabel, projectNameField, typeLabel, projectTypeField,
                                                widthLabel, imageWidthField, heightLabel, imageHeightField);
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                gridPane.setAlignment(Pos.CENTER);
                gridPane.getStyleClass().add("noBorderClass");
                gridPane.setPadding(new Insets(5, 5, 5, 5));

                projectPropertiesPane.getChildren().addAll(propertiesLabel, gridPane);
            projectPropertiesPane.setPadding(new Insets(5, 5, 5, 5));
            projectPropertiesPane.getStyleClass().add("projectPropertiesPane");
            projectPropertiesPane.setSpacing(5);
            projectPropertiesPane.setAlignment(Pos.CENTER);


        vBox.getChildren().addAll(openBtnBox, projectPropertiesPane);

        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinWidth(175);

        return vBox;
    }


    private TextField createTextField(int col, int row){
        TextField textField = new TextField();
        textField.setPrefWidth(100);
        textField.setEditable(false);
        GridPane.setConstraints(textField, col, row);

        return textField;
    }


    private Label createLabel(String text, int col, int row){
        Label label = new Label(text);
        GridPane.setConstraints(label, col, row);
        return label;
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
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(180);

        return imageGrid;
    }




    private ScrollableImageGrid createRejectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Rejected Images", false,
                                                                    true, true);
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
                    if(Utils.checkIn(Utils.getFileExtension(name.toLowerCase()), Main.acceptedFormats)){
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

            String format = Utils.getFileExtension(images[0].getName());

            Image firstImg;
            if(Utils.checkIn(format.toLowerCase(), new String[]{"jpg", "png"})){
                firstImg = new Image("file:" + images[0].getAbsolutePath());
            }else{
                firstImg = Utils.readUnusualImage(images[0].getAbsolutePath());
            }

            if(firstImg == null){
                Main.showFileReadingAlert("Error reading image files. Accepted formats are: " +
                                        "'.jpg', '.png', '.tif', '.bmp'.");
                Main.hideLoadingDialog();
                return;
            }

            imagesInDir[0] = firstImg;
            imageLocations[0] = images[0].getAbsolutePath();
            imageNames[0] = images[0].getName();

            double imageHeight = firstImg.getHeight();
            double imageWidth = firstImg.getWidth();

            for(int i = 1; i < imagesInDir.length; i++){
                Image currentImg; // = new Image("file:" + images[i].getAbsolutePath());
                if(Utils.checkIn(format.toLowerCase(), new String[]{"jpg", "png"})){
                    currentImg = new Image("file:" + images[i].getAbsolutePath());
                }else{
                    currentImg = Utils.readUnusualImage(images[i].getAbsolutePath());
                }

                if(currentImg.getWidth() != imageWidth || currentImg.getHeight() != imageHeight){
                    Main.showFileReadingAlert("The width and height of all images in the folder do not match.");
                    Main.hideLoadingDialog();
                    return;
                }

                String name = images[i].getName();
                if(!Utils.getFileExtension(name).equals(format)){
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
            loadImagesIntoPreview(imagesInDir, imageNames);
            setWidthHeightFields(imagesInDir[0].getWidth(), imagesInDir[0].getHeight());

        }catch(Exception e){
            e.printStackTrace();
            Main.hideLoadingDialog();
            Main.showFileReadingAlert("There was an error reading files in the chosen directory.");
        }
    }



    private void setWidthHeightFields(double width, double height){
        imageWidthField.setText(String.valueOf((int)width));
        imageHeightField.setText(String.valueOf((int)height));
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
        projectNameField.setText(rtiProject.getName());
        projectTypeField.setText(rtiProject.getProjectType().toString());
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

    public void addTilesToSelected(ArrayList<ImageGridTile> tiles){
        for(ImageGridTile tile : tiles){addTileToSelected(tile);}
    }

    public void resetScene(){
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
        return s;
    }


}
