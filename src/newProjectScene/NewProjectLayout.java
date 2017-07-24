package newProjectScene;

import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGrid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
 * This layout allows the user to open a a folder of images, and browse them. In the highlight detection project, the
 * user can reject individual photos before they move on to the highlight detection layout. This layoutos different
 * depending on the RTIProject's type.
 *
 * @see RTIProject.ProjectType
 * @see highlightDetectionScene.HighlightDetectionLayout
 * @see initialScene.InitialLayout
 *
 * @author Jed Mills
 */
public class NewProjectLayout extends VBox implements CreatorScene{

    /** Contains the open resources dialog and the image grid(s)*/
    private HBox mainLayout;

    /** Bar containing the next and back buttons at the bottomof the layout*/
    private HBox nextBackBox;

    /** Button to open the load project resources dialog*/
    private Button openFolderBtn;

    /** Text field shown the the highlight detection version of the layout for typing in image removal reasons */
    private TextArea removeRsnTxtField;

    /** Button to remove pictures in the highlight detection layout */
    private Button removePicBtn;

    /** Button to replace pictures in the highlight detection layout */
    private Button replaceBtn;

    /** Field on the left of the layout that displays the project title */
    private TextField projectNameField;

    /** Field on the left of the layout that displays the project type */
    private TextField projectTypeField;

    /** Field on the left of the layout that displays the width of loaded images */
    private TextField imageWidthField;

    /** Field on the left of the layout that displays the height of loaded images */
    private TextField imageHeightField;

    /** Button to go back to the initial layout*/
    private Button backBtn;

    /** Button to move forwards to the highlight detection or CropExecuteLayout */
    private Button nextBtn;

    /** Toolbar on the left of the layout containin gthe load project resource button and info fields*/
    private VBox toolbarLayout;

    /** Image grid for selected photos */
    private ScrollableImageGrid selectedImages;

    /** Pane containing the removal reason text field and buttons*/
    private VBox removePicPane;

    /** Image grid for rejected photos */
    private ScrollableImageGrid rejectedImages;

    /** Image folder selected through the LoadProjRsrcsDialog */
    private File imgsFolder;

    /** Current RTIProjet the user is making */
    private RTIProject rtiProject;

    /** LP data loaded through the LoadProjRsrcsDialog */
    private HashMap<String, Utils.Vector3f> lpData;

    /** Whether the user has loaded the project resources through the LoadProjRsrcsDialog */
    private boolean resourcesSet = false;

    /** The singleton instance of this class*/
    private static NewProjectLayout ourInstance = new NewProjectLayout();


    /**
     * @return {@link NewProjectLayout#ourInstance}
     */
    public static NewProjectLayout getInstance() {
        return ourInstance;
    }


    /**
     * Creates a new NewProjectLayout.Sets the listener for this layout.
     */
    private NewProjectLayout() {
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        NewProjectLayoutListener.getInstance().init(this);
        LoadProjRsrcsDialog.getInstance().init(this);
    }


    /**
     * Creates all the components for the layout.
     */
    private void createLayout(){
        //only create the components rather then also adding them as they layout for this
        //scene changes depending on the RTIProject type
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




    /**
     * Creates the layout for the project resources toolbar on the left of this layout.
     *
     * @return  the project resources toolbar
     */
    private VBox createToolbarLayout(){
        //just layout stuff
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

    /**
     * Convenience method to create a text field that is laid out in a grid pane.
     *
     * @param col       grid pane column
     * @param row       grid pane row
     * @return          the new text field
     */
    private TextField createTextField(int col, int row){
        TextField textField = new TextField();
        //100 is a good width for the project resources dialog
        textField.setPrefWidth(100);
        textField.setEditable(false);
        GridPane.setConstraints(textField, col, row);

        return textField;
    }

    /**
     * Convenience method tp create a label that is laid out in a grid pane.
     *
     * @param col       grid pane column
     * @param row       grid pane row
     * @return          the new label
     */
    private Label createLabel(String text, int col, int row){
        Label label = new Label(text);
        GridPane.setConstraints(label, col, row);
        return label;
    }


    /**
     * Creates the remove pic pane that exists in the middle of the scene when it is in the highlight detection layout.
     *
     * @return  the remove picture pane
     */
    private VBox createRemovePicPane(){
        //put this inside two panes so the outer pane can have a nice border
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


    /**
     * Creates the bar that exists at the bottom of the layout containing the next button and the back button.
     *
     * @return  the bottom bar
     */
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

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backBtn, Utils.createSpacer(), nextBtn);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }


    /**
     * Creates the {@link ScrollableImageGrid} for the images that the user has selected to use in their project.
     *
     * @return  the selected images grid
     */
    private ScrollableImageGrid createSelectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Selected Images", false,
                                                                true, true);
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(180);

        return imageGrid;
    }



    /**
     * Creates the {@link ScrollableImageGrid} for the images that the user has rejected from their project. Only
     * appears in the highlight detection layout.
     *
     * @return  the rejected images grid
     */
    private ScrollableImageGrid createRejectedImagesView(){
        ScrollableImageGrid imageGrid  = new ScrollableImageGrid("Rejected Images", false,
                                                                    true, true);
        imageGrid.setMaxHeight(Double.MAX_VALUE);
        imageGrid.setMaxWidth(Double.MAX_VALUE);
        imageGrid.setMinWidth(180);

        return imageGrid;
    }


    /**
     * This method is called when the images in a directory want to be loaded into the selected images grid. Will load
     * all the images in the folder, checking they ar of the same format, width and height, and create the
     * ImageGridTiles to display in the {@link NewProjectLayout#selectedImages} grid.
     *
     * @param directory     directory of images to load
     */
    public void loadImageDirectory(File directory){
        Main.showLoadingDialog("Loading images...");

        try{
            File[] images = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    //only load files that are images with the accepted format
                    if(Utils.checkIn(Utils.getFileExtension(name.toLowerCase()), Main.acceptedFormats)){
                        return true;
                    }
                    return false;
                }
            });

            //if there were no images in the folder, return
            if(images.length == 0){
                Main.hideLoadingDialog();
                Main.showFileReadingAlert("There are no images in the chosen directory.");
                return;
            }

            //these will hold all the in for to generate the lp data in memory
            Image[] imagesInDir = new Image[images.length];
            String[] imageLocations = new String[images.length];
            String[] imageNames = new String[images.length];

            String format = Utils.getFileExtension(images[0].getName());

            //used to find what format all the images should be
            Image firstImg;
            if(Utils.checkIn(format.toLowerCase(), new String[]{"jpg", "png"})){
                firstImg = new Image("file:" + images[0].getAbsolutePath());

            }else{
                firstImg = Utils.readUnusualImage(images[0].getAbsolutePath());
            }

            //there was some big mistake that happened here so quit out
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

            Image currentImg;
            for(int i = 1; i < imagesInDir.length; i++){
                //id it's a png or jpg javafx can load these fine
                if(Utils.checkIn(format.toLowerCase(), new String[]{"jpg", "png"})){
                    currentImg = new Image("file:" + images[i].getAbsolutePath());
                }else{
                    //otherwise we have to use the JAI API to load it
                    currentImg = Utils.readUnusualImage(images[i].getAbsolutePath());
                }

                //make sure the width and the height of all images are the same
                if(currentImg.getWidth() != imageWidth || currentImg.getHeight() != imageHeight){
                    Main.showFileReadingAlert("The width and height of all images in the folder do not match.");
                    Main.hideLoadingDialog();
                    return;
                }

                //make sure the format of all images is the same
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

            //put the newly created images in the preview
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


    /**
     * Called when the images are loaded from the images folder, display the width and height of all the images.
     *
     * @param width     width of the loaded images
     * @param height    height of the loaded images
     */
    private void setWidthHeightFields(double width, double height){
        imageWidthField.setText(String.valueOf((int)width));
        imageHeightField.setText(String.valueOf((int)height));
    }


    /**
     * Creats {@link ImageGridTile}s of the given images and names and loads them into the
     * {@link NewProjectLayout#selectedImages} grid.
     *
     * @param images        all the images
     * @param imageNames    namesof all the images in the same order
     */
    private void loadImagesIntoPreview(Image[] images, String[] imageNames){
        for(int i = 0; i < images.length; i++){
            selectedImages.addImageTile(imageNames[i], images[i], 150, 150);
        }
        Main.hideLoadingDialog();
    }


    /**
     * Sets the RTIProject type to be displayed in the type field on the left ofthe layout.
     *
     * @param rtiProject    RTIProject the user is currently making
     */
    public void setProject(RTIProject rtiProject){
        this.rtiProject = rtiProject;
        setupLayoutForType(rtiProject.getProjectType());
        projectNameField.setText(rtiProject.getName());
        projectTypeField.setText(rtiProject.getProjectType().toString());
    }


    /**
     * Changes the layout of this scene depending on the RTIProject type. The lp type only has the selected images grid,
     * but the highlight type has the selected image grid, removal reason pane, and the rejected images pane.
     *
     * @param type      type of the project the layout should be setup for
     */
    private void setupLayoutForType(RTIProject.ProjectType type){
        mainLayout.getChildren().clear();
        getChildren().clear();

        if(type.equals(RTIProject.ProjectType.HIGHLIGHT)){
            mainLayout.getChildren().addAll(toolbarLayout, selectedImages, removePicPane, rejectedImages);
        }else{
            mainLayout.getChildren().addAll(toolbarLayout, selectedImages);
        }

        getChildren().addAll(mainLayout, nextBackBox);
    }


    /**
     * @return min width of the scene
     */
    @Override
    public int getSceneMinWidth() {
        return 720;
    }


    /**
     * @return max width of the scene
     */
    @Override
    public int getSceneMaxWidth() {
        return Integer.MAX_VALUE;
    }


    /**
     * @return min height of the scene
     */
    @Override
    public int getSceneMinHeight() {
        return 500;
    }


    /**
     * @return max height of the scene
     */
    @Override
    public int getSceneMaxHeight() {
        return Integer.MAX_VALUE;
    }


    /**
     * Updates the size of the the components in this layout, called when the window is resized.
     *
     * @param width     width of the window
     * @param height    height of the window
     */
    @Override
    public void updateSize(double width, double height) {
        toolbarLayout.setPrefHeight(height);
        selectedImages.setTheHeight(height);
        rejectedImages.setTheHeight(height);
    }


    /**
     * Called when the user has chosen the resources fo this project. Tries to load the imags from the given folder
     * location, and sets the assembly file location for when assembly files are put there in the future.
     *
     * @param imgsLocation              location of the folder containing the images for this project
     * @param assemblyFilesLocation     location of the folder where assembly files will be put
     */
    public void setResources(String imgsLocation, String assemblyFilesLocation){
        File imgsFolder = new File(imgsLocation);
        File assemblyFileFolder = new File(assemblyFilesLocation);

        //check the things actually exist as a preliminary check
        if((!imgsFolder.exists()) || (!assemblyFileFolder.exists()) ||
                (!imgsFolder.isDirectory()) || (!assemblyFileFolder.isDirectory())){
            Main.showFileReadingAlert("Cannot find specified directories. Check that they still exist.");
            return;
        }

        this.imgsFolder = imgsFolder;
        Main.currentImagesFolder = imgsFolder;
        Main.currentAssemblyFolder = assemblyFileFolder;

        //try and read all the images in the images folder
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImageDirectory(imgsFolder);
            }
        }).start();

        resourcesSet = true;
    }


    /**
     * Called when the user has chosen the resources fo this project. Tries to load the images from the given folder
     * location, and sets the assembly file location for when assembly files are put there in the future, and
     * tries to read the LP data from the file.
     *
     * @param imgsLocation              location of the folder containing the images for this project
     * @param assemblyFilesLocation     location of the folder where assembly files will be put
     * @param lpLocation                location of the lp file for this project
     */
    public void setResources(String imgsLocation, String lpLocation, String assemblyFilesLocation){
        File lpFile = new File(lpLocation);

        Main.currentLPFile = lpFile;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //read the lp file and load the images
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


    /**
     * @return the project type of the {@link NewProjectLayout#rtiProject}
     */
    public RTIProject.ProjectType getProjectType(){
        return rtiProject.getProjectType();
    }


    /**
     * @return {@link NewProjectLayout#removeRsnTxtField}
     */
    public TextArea getRemoveRsnTxtField() {
        return removeRsnTxtField;
    }


    /**
     * Removes the selected tile from the {@link NewProjectLayout#selectedImages} gridand returns it. Used for
     * moving images to the rejected images grid.
     *
     * @return  the removed tile.
     */
    public ImageGridTile removeGridTileSelected(){
        ImageGridTile tile = selectedImages.getSelectedTile();
        selectedImages.removeSelectedTile();
        return tile;
    }


    /**
     * Adds the given tile to the {@link NewProjectLayout#rejectedImages} grid.
     *
     * @param tile  tile to add
     */
    public void addTileToRejected(ImageGridTile tile){
        tile.setParent(rejectedImages);
        rejectedImages.addImageTile(tile);
    }


    /**
     * Removes the selected tile from the {@link NewProjectLayout#rejectedImages} gridand returns it. Used for
     * moving images to the selected images grid from the rejected grid.
     *
     * @return  the removed tile.
     */
    public ImageGridTile removeGridTileRejected(){
        ImageGridTile tile = rejectedImages.getSelectedTile();
        rejectedImages.removeSelectedTile();
        return tile;
    }


    /**
     * Adds the given tile to the {@link NewProjectLayout#selectedImages} grid.
     *
     * @param tile  tile to add
     */
    public void addTileToSelected(ImageGridTile tile){
        tile.setParent(selectedImages);
        selectedImages.addImageTile(tile);
    }

    /**
     * Adds the given tile to the {@link NewProjectLayout#selectedImages} grid.
     *
     * @param tiles  tiles to add
     */
    public void addTilesToSelected(ArrayList<ImageGridTile> tiles){
        for(ImageGridTile tile : tiles){addTileToSelected(tile);}
    }


    /**
     * Removes all the tiles and resets all the text fields, used for when the user has switched back to this layout
     * after going back to the {@link initialScene.InitialLayout}.
     */
    public void resetScene(){
        selectedImages.clearTiles();
        rejectedImages.clearTiles();
        removeRsnTxtField.setText("");
    }


    /**
     * @return {@link NewProjectLayout#lpData}
     */
    public HashMap<String, Utils.Vector3f> getLpData() {
        return lpData;
    }


    /**
     * @return {@link NewProjectLayout#resourcesSet}
     */
    public boolean isResourcesSet() {
        return resourcesSet;
    }


    /**
     * @return {@link NewProjectLayout#imgsFolder}
     */
    public File getImgsFolder() {
        return imgsFolder;
    }


    /**
     * @return {@link NewProjectLayout#selectedImages}
     */
    public ScrollableImageGrid getSelectedImages(){
        return selectedImages;
    }

    /**
     * Returns all the grid tiles in the {@link NewProjectLayout#selectedImages} that have a name in
     * the passed array of strings.
     *
     * @param filter        names all of the tiles you want to get
     * @return              allthe tiles that have their name in the filter array
     */
    public ArrayList<ImageGridTile> getSelectedImages(ArrayList<String> filter){
        ArrayList<ImageGridTile> tiles = new ArrayList<>();

        for(ImageGridTile tile : selectedImages.getGridTiles()){
            if(Utils.checkIn(tile.getName(), filter)){
                tiles.add(tile);
            }
        }
        return tiles;
    }


    /**
     * @return the file type extension for the loaded images
     */
    public String getImagesExtension(){
        String s = selectedImages.getGridTiles()[0].getName().split("[.]")[1].toLowerCase();
        return s;
    }


}
