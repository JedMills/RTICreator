package cropExecuteScene;

import guiComponents.ImageCropPane;
import guiComponents.ImageGridTile;
import guiComponents.ScrollableImageGridForCrop;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.CreatorScene;
import main.Main;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * <p>
 *     This layout is the final scene that the user comes to when making an RTI file. The scene is loaded with
 *     {@link ImageGridTile}s, which are displayed in a {@link ScrollableImageGridForCrop} on the left. An
 *     {@link ImageCropPane} in the center of the layout allows the user to crop the final images, before executing
 *     the fitter on them. A pane on the right allows the user to selected the fitter options.
 * </p>
 * <p>
 *     This pane can be arrived to with either the highlight-detection program path, the lp file with images from
 *     folder path, or the full path lp file. Thesefore, the back button on this layout can go to wither the
 *     {@link newProjectScene.NewProjectLayout} or the {@link initialScene.InitialLayout} depending on the type
 *     of project made.
 * </p>
 *
 *
 * @see newProjectScene.NewProjectLayout
 * @see initialScene.InitialLayout
 *
 * @author Jed Mills
 */
public class CropExecuteLayout extends VBox implements CreatorScene {

    /** Button to take the user back to the las tscene */
    private Button backButton;

    /** Button to run the selected fitter on the images */
    private Button runFitterButton;

    /** Pane containing the ScrollableImagePane and crop options pane */
    private VBox leftPane;

    /** The scrollable image pane on the left of the layout */
    private ScrollableImageGridForCrop lpImagesGrid;

    /** The box containing the ImageCropPane in the center of the layout */
    private VBox cropPane;

    /** Pane containing the fitter options on the right of the layout */
    private VBox fitterInterfacePane;

    /** The image crop pane in the center of the view */
    private ImageCropPane imageCropPane;

    /** Field that gets updated with the width of the crop box */
    private TextField cropWidthField;

    /** Field that gets updated with the height of the crop box */
    private TextField cropHeightField;

    /** Check box for whether the user wants to crop the images or not */
    private CheckBox useCropCheckBox;

    /** Selector for the colour of the crop rectangle */
    private ComboBox<ImageCropPane.Colour> cropColourSelector;

    /** Grid containing the radio buttons and labels for the fitter options  */
    private GridPane fitterOptionsGrid;

    /** Label for the type of fitter the user wants top use, in the top right pane of the layout */
    private Label fitterTypeLabel;

    /** Field that the user inputs the fitter location to */
    private TextField fitterLocationField;

    /** "Browse" button for opening the file choose to find the fitter */
    private Button fitterLocationButton;

    /** Field that the user can edit for the path to the file they want to create */
    private TextField outputLocationField;

    /** "Browse" button for finding the location of the file the user wants to save */
    private Button outputLocationButton;

    /** Button to use the ptm fitter*/
    private RadioButton ptmButton;

    /** Button to use the hsh fitter*/
    private RadioButton hshButton;

    /** Button to use the ptm rgb format*/
    private RadioButton ptmRGBButton;

    /** Button to use the ptm lrgb format*/
    private RadioButton ptmLRGBButton;

    /** Button to use the hsh of order 2 */
    private RadioButton hshTerms2;

    /** Button to use the hsh of order 3 */
    private RadioButton hshTerms3;

    /** Label for the RGB / LRGB RTM radio buttons */
    private Label ptmTypeLabel;

    /**Label for the order 2 / 3 HSH radio buttons */
    private Label hshTermsLabel;

    /** Area that the fitter output is pasted into */
    private TextArea fitterOutputArea;

    /** Whether the user has check the Use crop box or not */
    private boolean useCrop;

    /** The last location of the ptm fitter that the user chose, loaded and saved using java preferences */
    private static String ptmFitterLocation = "";

    /** The last location of the hsh fitter that the user chose, loaded and saved using java preferences */
    private static String hshFitterLocation = "";

    /** The singleton instance of this class*/
    private static CropExecuteLayout ourInstance = new CropExecuteLayout();

    /**
     * @return {@link CropExecuteLayout#ourInstance}
     */
    public static CropExecuteLayout getInstance() {
        return ourInstance;
    }




    /**
     * Crates a new CropExecuteLayout. Adds the default stylesheet and sets up the default layout.
     */
    private CropExecuteLayout() {
        createLayout();
        getStylesheets().add("stylesheets/default.css");
        CropExecuteLayoutListener.getInstance().init(this);

        //the default colour is blue, so make the dropdown box have blue already selected as well
        //as making the actual crop rectangle blue
        cropColourSelector.getSelectionModel().select(ImageCropPane.Colour.BLUE);
        imageCropPane.changeColour(ImageCropPane.Colour.BLUE);
        //default selection is hsh
        hshButton.setSelected(true);
    }



    /**
     * Loads the location of the ptm fitter and hsh fitter from using the Java Preferences API. Will just put empty
     * strings as the values for {@link CropExecuteLayout#ptmFitterLocation} and
     * {@link CropExecuteLayout#hshFitterLocation} if they don't already exist or can't be found.
     */
    private void loadFitterLocations(){
        Preferences preferences = Preferences.userNodeForPackage(CropExecuteLayout.class);
        ptmFitterLocation = preferences.get("ptmFitterLocation", "");
        hshFitterLocation = preferences.get("hshFitterLocation", "");
    }




    /**
     * Crates all the components of the scene.
     */
    private void createLayout(){
        //the box containing the scrollable image grid, crop options, crop pane, and fitter pane
        HBox mainLayout = createMainLayout();

        //the box wth the back and run fitter buttons
        HBox bottomBar = createBottomBar();

        getChildren().addAll(mainLayout, bottomBar);
        setSpacing(5);
        setPadding(new Insets(5, 5, 5, 5));
        setFillWidth(true);
    }




    /**
     * Creates the main layout with the scrollable image grid, crop options, crop pane, and fitter pane.
     *
     * @see ScrollableImageGridForCrop
     * @see ImageCropPane
     *
     * @return the main layout with the above items
     */
    private HBox createMainLayout(){
        HBox hBox = new HBox();

        //has the scrollable grid and crop options
        leftPane = createLeftPane();
        leftPane.setMaxWidth(380);
        leftPane.setMinWidth(380);

        //the center crop pane
        cropPane = createCropPane();

        //the fitter pane on the right
        fitterInterfacePane = createFitterPane();
        fitterInterfacePane.setMaxWidth(325);
        fitterInterfacePane.setMinWidth(325);

        //this is the most robust arrangement for having things grow and look ok
        HBox.setHgrow(leftPane, Priority.SOMETIMES);
        HBox.setHgrow(cropPane, Priority.ALWAYS);
        HBox.setHgrow(fitterInterfacePane, Priority.SOMETIMES);

        //add all the things
        hBox.getChildren().addAll(leftPane, cropPane, fitterInterfacePane);
        hBox.setSpacing(5);
        hBox.setFillHeight(true);
        return hBox;
    }


    /**
     * Creates the pane on the left with the scrollable image grid and crop options.
     *
     * @see ScrollableImageGridForCrop
     *
     * @return  the left panel as described above
     */
    private VBox createLeftPane(){
        VBox vBox = new VBox();
        //this is linked to the crop pane so selecting an image on this will display it in the crop pane
        lpImagesGrid = new ScrollableImageGridForCrop("Images in LP File", false,
                                                            true, true, imageCropPane);
        vBox.setMinWidth(200);
        vBox.setMaxWidth(400);

        //the pane with the crop width and height box, etc.
        VBox cropPane = createCropOptionsPane();

        vBox.setSpacing(5);
        vBox.getChildren().addAll(lpImagesGrid, cropPane);

        return vBox;
    }


    /**
     * Creates the pna eon the right of this layout with the fitter options and the fitter output text area.
     *
     * @return  the fitter pane, as described above
     */
    private VBox createFitterPane(){
        VBox vBox = new VBox();
        vBox.setId("fitterPane");
        Label fitterPaneTitle = new Label("RTI Fitter Options");
        fitterPaneTitle.setFont(Font.font(null, FontWeight.BOLD, 12));

        //create the grid for selecting ptm/hsh and rgb/lrgb and fitter order
        fitterOptionsGrid = createFitterOptionsGrid();

        //the grid for the fitter location and output location fields and buttons
        GridPane fitterOutLocPane = new GridPane();
            //fitter location components...
            Label fitterLocLabel = new Label("Fitter location:");
            GridPane.setConstraints(fitterLocLabel, 0, 0);

            fitterLocationField = new TextField();
            fitterLocationField.setEditable(true);
            GridPane.setConstraints(fitterLocationField, 1, 0);

            fitterLocationButton = new Button("Browse");
            fitterLocationButton.setId("browseFitterLocation");
            fitterLocationButton.setOnAction(CropExecuteLayoutListener.getInstance());
            GridPane.setConstraints(fitterLocationButton, 2, 0);

            //output location components...
            Label outLocLabel = new Label("Output file:");
            GridPane.setConstraints(outLocLabel, 0, 1);

            outputLocationField = new TextField();
            outputLocationField.setEditable(true);
            GridPane.setConstraints(outputLocationField, 1, 1);

            outputLocationButton = new Button("Browse");
            outputLocationButton.setId("browseOutputLocation");
            outputLocationButton.setOnAction(CropExecuteLayoutListener.getInstance());
            GridPane.setConstraints(outputLocationButton, 2, 1);

        //add everything to the grid
        fitterOutLocPane.setHgap(10);
        fitterOutLocPane.setVgap(10);
        fitterOutLocPane.setPadding(new Insets(5, 5, 5, 5));
        fitterOutLocPane.getChildren().addAll(fitterLocLabel, fitterLocationField, fitterLocationButton,
                                                outLocLabel, outputLocationField, outputLocationButton);

        //the box containing the fitter output area nad its label
        VBox fitterOutputBox = new VBox();
            Label fitterOutLabel = new Label("Fitter Output:");
            fitterOutLabel.setPadding(new Insets(0, 5, 5,5));

            fitterOutputArea = new TextArea();
            fitterOutputArea.setEditable(false);
            fitterOutputArea.setMinHeight(10);
            fitterOutputArea.setPrefWidth(Double.MAX_VALUE);

        fitterOutputBox.setAlignment(Pos.CENTER);
        fitterOutputBox.getChildren().addAll(fitterOutLabel, fitterOutputArea);

        vBox.setMaxWidth(350);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.getChildren().addAll(fitterPaneTitle, fitterOptionsGrid, fitterOutLocPane, fitterOutputBox);

        return vBox;
    }


    /**
     * Creates the grid that the radio buttons for choosing the fitter type and the fitter parameters are in.
     * The grid is set to be made with the hsh option selected and the hsh parameters showing.
     *
     * @return  the fitter options grid, with the hsh setting selected
     */
    private GridPane createFitterOptionsGrid(){
        //all pretty self explanatory layout stuff...
        GridPane gridPane = new GridPane();

            fitterTypeLabel = new Label("Fitter type:");
            GridPane.setConstraints(fitterTypeLabel, 0, 0);

            ToggleGroup fitterTypeToggle = new ToggleGroup();
            ptmButton = new RadioButton("PTM");
            ptmButton.setId("ptmButton");
            ptmButton.setOnAction(CropExecuteLayoutListener.getInstance());
            ptmButton.setToggleGroup(fitterTypeToggle);
            GridPane.setConstraints(ptmButton, 1, 0);

            hshButton = new RadioButton("HSH");
            hshButton.setId("hshButton");
            hshButton.setOnAction(CropExecuteLayoutListener.getInstance());
            hshButton.setToggleGroup(fitterTypeToggle);
            GridPane.setConstraints(hshButton, 2, 0);

            ptmTypeLabel = new Label("PTM type:");
            GridPane.setConstraints(ptmTypeLabel, 0, 1);

            ToggleGroup ptmTypeToggle = new ToggleGroup();
            ptmRGBButton = new RadioButton("RGB");
            ptmRGBButton.setId("rgbPTMButton");
            ptmRGBButton.setOnAction(CropExecuteLayoutListener.getInstance());
            ptmRGBButton.setToggleGroup(ptmTypeToggle);
            GridPane.setConstraints(ptmRGBButton, 1, 1);


            ptmLRGBButton = new RadioButton("LRGB");
            ptmLRGBButton.setId("lrgbPTMButton");
            ptmLRGBButton.setSelected(true);
            ptmLRGBButton.setOnAction(CropExecuteLayoutListener.getInstance());
            ptmLRGBButton.setToggleGroup(ptmTypeToggle);
            GridPane.setConstraints(ptmLRGBButton, 2, 1);

            hshTermsLabel = new Label("HSH Terms:");
            GridPane.setConstraints(hshTermsLabel, 0, 1);

            ToggleGroup hshToggleGroup = new ToggleGroup();
            hshTerms2 = new RadioButton("2");
            hshTerms2.setToggleGroup(hshToggleGroup);
            GridPane.setConstraints(hshTerms2, 1, 1);

            hshTerms3 = new RadioButton("3");
            hshTerms3.setToggleGroup(hshToggleGroup);
            GridPane.setConstraints(hshTerms3, 2, 1);

            hshTerms2.setSelected(true);

            //the grid is set up to HSH by default
            gridPane.getChildren().addAll(fitterTypeLabel, ptmButton, hshButton);
            gridPane.getChildren().addAll(hshTermsLabel, hshTerms2, hshTerms3);

            gridPane.setAlignment(Pos.CENTER_LEFT);
            gridPane.setPadding(new Insets(5, 5, 5, 5));

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(33.33);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(33.33);
            ColumnConstraints column3 = new ColumnConstraints();
            column3.setPercentWidth(33.33);

        gridPane.setVgap(10);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);

        return gridPane;
    }


    /**
     * Creates the pane containing the {@link ImageCropPane} in the center of the layout, linked
     * to the {@link CropExecuteLayout#cropWidthField} and {@link CropExecuteLayout#cropHeightField}.
     *
     * @return  the crop pane that exists in the center of the layout
     */
    private VBox createCropPane(){
        VBox cropPane = new VBox();
            imageCropPane = new ImageCropPane(cropWidthField, cropHeightField);
            imageCropPane.setId("cropExecutePanePreview");

            //the bars at the sides or top and bottom of the crop pane are black
            imageCropPane.setStyle("-fx-background-color: #000000;");
            imageCropPane.setMinWidth(0);
            imageCropPane.setMinHeight(0);

            //make the image area take up the maximum possible size
            imageCropPane.prefWidthProperty().bind(cropPane.widthProperty());
            imageCropPane.prefHeightProperty().bind(cropPane.heightProperty());

        cropPane.setId("cropExecLayoutCropPane");
        cropPane.setAlignment(Pos.CENTER);
        cropPane.setSpacing(5);
        cropPane.setPadding(new Insets(0, 5, 0, 5));
        cropPane.getChildren().addAll(imageCropPane);

        return cropPane;
    }


    /**
     * Creates the small pane that sits underneath the {@link CropExecuteLayout#lpImagesGrid} in the bottom left of the
     * layout with the crop options widgets in it.
     *
     * @return the crop options pane as described above
     */
    private VBox createCropOptionsPane(){
        //box that everything is in
        VBox cropOptions = new VBox();

            //title for the pane
            Label optionsLabel = new Label("Crop Options");
            optionsLabel.setFont(Font.font(null, FontWeight.BOLD, 12));

            //use crop check box
            useCropCheckBox = new CheckBox("Use crop");
            useCropCheckBox.setId("useCropCheckBox");
            useCropCheckBox.setOnAction(CropExecuteLayoutListener.getInstance());

            //create the components for crop. They are are bound to the check box above so that when it is
            //not ticked, all the widgets are disabled as the user does not want to crop
            GridPane widthHeightGrid = new GridPane();

                Label cropWidthLabel = new Label("Crop width:");
                cropWidthLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());
                GridPane.setConstraints(cropWidthLabel, 0, 0);

                cropWidthField = new TextField();
                cropWidthField.disableProperty().bind(useCropCheckBox.selectedProperty().not());
                cropWidthField.setEditable(false);
                GridPane.setConstraints(cropWidthField, 1, 0);

                Label cropHeightLabel = new Label("Crop height:");
                cropHeightLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());
                GridPane.setConstraints(cropHeightLabel, 0, 1);

                cropHeightField = new TextField();
                cropHeightField.disableProperty().bind(useCropCheckBox.selectedProperty().not());
                cropHeightField.setEditable(false);
                GridPane.setConstraints(cropHeightField, 1, 1);

            widthHeightGrid.setVgap(5);
            widthHeightGrid.setHgap(5);
            widthHeightGrid.setAlignment(Pos.CENTER);
            widthHeightGrid.getChildren().addAll(cropWidthLabel, cropWidthField,
                                                    cropHeightLabel, cropHeightField);

            //dropdown to select the colour of the crop rectangle
            HBox cropButtonsBox = new HBox();
                Label cropColorLabel = new Label("Crop colour:");
                cropColorLabel.disableProperty().bind(useCropCheckBox.selectedProperty().not());

                cropColourSelector = createColourSelector();
                cropColourSelector.disableProperty().bind(useCropCheckBox.selectedProperty().not());
            cropButtonsBox.getChildren().addAll(cropColorLabel, cropColourSelector);
            cropButtonsBox.setAlignment(Pos.CENTER);
            cropButtonsBox.setSpacing(10);

        cropOptions.getChildren().addAll(optionsLabel, useCropCheckBox, widthHeightGrid, cropButtonsBox);
        cropOptions.setAlignment(Pos.CENTER);
        cropOptions.setPadding(new Insets(5, 5, 5, 5));
        cropOptions.setSpacing(5);
        cropOptions.setId("cropExecLayoutCropOptions");
        HBox.setHgrow(cropOptions, Priority.NEVER);

        return cropOptions;
    }


    /**
     * Creates a new dropdown selection box that contains the colours that the user can set the crop rectangle
     * in the {@link CropExecuteLayout#imageCropPane}.
     *
     * @see ImageCropPane
     * @see ImageCropPane.Colour
     *
     * @return  a new colour selector box
     */
    private ComboBox<ImageCropPane.Colour> createColourSelector(){
        ComboBox<ImageCropPane.Colour> box = new ComboBox<>();

        //the string values of these enums are what will be displayed in the box
        box.getItems().addAll(ImageCropPane.Colour.BLACK, ImageCropPane.Colour.GREY, ImageCropPane.Colour.WHITE,
                                ImageCropPane.Colour.RED, ImageCropPane.Colour.GREEN, ImageCropPane.Colour.BLUE);

        box.setId("colourSelectorBox");
        box.setOnAction(CropExecuteLayoutListener.getInstance());
        return box;
    }


    /**
     * Creates a new bar containing the 'Back' and 'Run Fitter' buttons, which is at the bottom of this layout.
     *
     * @return  the bottom bar as described above
     */
    private HBox createBottomBar(){
        //create a box with a space in the middle for the two buttons
        HBox hBox = new HBox();
            hBox.getStyleClass().add("bottomBar");
            backButton = new Button("< Back");
            backButton.setId("backButton");
            backButton.setOnAction(CropExecuteLayoutListener.getInstance());

            runFitterButton = new Button("Run Fitter");
            runFitterButton.setId("runFitterButton");
            runFitterButton.setOnAction(CropExecuteLayoutListener.getInstance());

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(backButton, Utils.createSpacer(), runFitterButton);
        hBox.setPadding(new Insets(5, 5, 5, 5));

        return hBox;
    }


    /**
     * @return the min width of this scene
     */
    @Override
    public int getSceneMinWidth() {
        return 1000;
    }


    /**
     * @return the max width of this scene
     */
    @Override
    public int getSceneMaxWidth() {
        return Integer.MAX_VALUE;
    }

    /**
     * @return the min height of this scne
     */
    @Override
    public int getSceneMinHeight() {
        return 500;
    }

    /**
     * @return the max height of this scene
     */
    @Override
    public int getSceneMaxHeight() {
        return Integer.MAX_VALUE;
    }


    /**
     * This method is called when the main window containing the scene is resized. It updates the sizes of the panes
     * containing widgets in the scene.
     *
     * @param width     width of the window this layout is in
     * @param height    height of the window this layout is in
     */
    @Override
    public void updateSize(double width, double height) {
        //make everything be as tallas it canto fill the height of the screen
        leftPane.setPrefHeight(height);
        lpImagesGrid.setTheHeight(height);
        cropPane.setPrefHeight(height);
        fitterOutputArea.setPrefHeight(height);
        fitterInterfacePane.setPrefHeight(height);

        //the crop pane need to update the size of the image view inside it and stuff
        imageCropPane.updateSize();
    }


    /**
     * Sets the GridTiles that appear inthe {@link ScrollableImageGridForCrop} on the right of the layout. These are
     * the images that will be written to the .lp file that will be fed into the fitter when the user clicks the
     * 'RunFitter' button.
     *
     * @param tiles     the tiles to appear in the scrollable grid
     */
    public void setLPTiles(ArrayList<ImageGridTile> tiles){
        for(ImageGridTile tile : tiles){
            lpImagesGrid.addImageTile(tile);
            tile.setParent(lpImagesGrid);
        }
        //set the crop pane to this image view so that selecting an image in the image grid will make
        //it appear in the crop pane
        lpImagesGrid.setImageView(imageCropPane);
    }


    /**
     * Sets the first grid tile in the {@link CropExecuteLayout#lpImagesGrid} as being selected, and sets
     * the image in the crop pane to that image.
     */
    public void setFirstGridTileSelected(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lpImagesGrid.setSelectedTile(lpImagesGrid.getGridTiles()[0]);
                imageCropPane.setImage(lpImagesGrid.getGridTiles()[0].getImage());
            }
        });
    }



    /**
     * Called when the user check the 'Use Crop' button. Makes the crop rectangle appear.
     */
    public void enableCrop(){
        //if there's no image in the crop pane for some reason, select the first on in the grid pane
        if(imageCropPane.getImage() == null){
            imageCropPane.setImage(lpImagesGrid.getAllImages()[0]);
        }
        //make the crop rectangle appear
        useCrop = true;
        imageCropPane.setCropActive(true);
    }



    /**
     * Make the crop rectangle disappear, called when the user deselects the 'Use Crop' button.
     */
    public void disableCrop(){
        useCrop = false;
        imageCropPane.setCropActive(false);
    }



    /**
     * Sets the colour of the crop rectangle of the {@link CropExecuteLayout#imageCropPane}, called when
     * the use changes the selected colour in the {@link CropExecuteLayout#cropColourSelector}.
     *
     * @param colour    colour to change the crop rectangle to
     */
    public void setCropColour(ImageCropPane.Colour colour){
        imageCropPane.changeColour(colour);
    }




    /**
     * Makes the PTM relevant radio buttons appear, called when the user clicks the PTM radio button.
     */
    public void setPTMOptions(){
        fitterOptionsGrid.getChildren().clear();
        fitterOptionsGrid.getChildren().addAll(fitterTypeLabel, ptmButton, hshButton);
        fitterOptionsGrid.getChildren().addAll(ptmTypeLabel, ptmRGBButton, ptmLRGBButton);
        //load the last location the user input for the ptm fitter
        loadFitterLocations();
        fitterLocationField.setText(ptmFitterLocation);
    }



    /**
     * Makes the HSH relevant radio buttons appear, called when the user clicks the PTM radio button.
     */
    public void setHSHOptions(){
        fitterOptionsGrid.getChildren().clear();
        fitterOptionsGrid.getChildren().addAll(fitterTypeLabel, ptmButton, hshButton);
        fitterOptionsGrid.getChildren().addAll(hshTermsLabel, hshTerms2, hshTerms3);
        loadFitterLocations();
        fitterLocationField.setText(hshFitterLocation);
    }



    /**
     * @return {@link CropExecuteLayout#useCrop}
     */
    public boolean isUseCrop() {
        return useCrop;
    }



    /**
     * @return {@link CropExecuteLayout#lpImagesGrid}
     */
    public ScrollableImageGridForCrop getLpImagesGrid() {
        return lpImagesGrid;
    }


    /**
     * Gets the crop rectangle location and size, mapped to the actual size of the image in the crop pane. This
     * is used to actually cropthe images in the {@link CropExecuteLayoutListener}
     *
     * @return  the crop rectangle details
     */
    public int[] getCropParams(){
        int x = imageCropPane.getCropXInImage();
        int y = imageCropPane.getCropYInImage();
        int width = imageCropPane.getCropWidthInImage();
        int height = imageCropPane.getCropHeightInImage();

        return new int[]{x, y, width, height};
    }


    /**
     * @return the file formatof the images in the {@link CropExecuteLayout#lpImagesGrid} eg "jpg", "png"
     */
    public String getImagesFormat(){
        String imageName = lpImagesGrid.getGridTiles()[0].getName().toLowerCase();
        return imageName.split("[.]")[1];
    }


    /**
     * @return the fitter location, which is the text in the {@link CropExecuteLayout#fitterLocationField}
     */
    public String getFitterLocation(){
        return fitterLocationField.getText();
    }


    /**
     * Sets the text in the {@link CropExecuteLayout#fitterLocationField} to the passed string, and
     * saves the loacation to the java preferences. Called after the user has selected a fitter after clicking
     * 'Browse'.
     *
     * @param location  location of the fitterprogram
     */
    public void setFitterLocation(String location){
        fitterLocationField.setText(location);

        Preferences preferences = Preferences.userNodeForPackage(CropExecuteLayout.class);

        //save the fitter location so it is remembered when the use next uses the program
        if(ptmSelected()){
            preferences.put("ptmFitterLocation", location);
        }else if(hshSelected()){
            preferences.put("hshFitterLocation", location);
        }
    }

    /**
     * @return the text in the {@link CropExecuteLayout#outputLocationField}
     */
    public String getOutputLocation(){
        return outputLocationField.getText();
    }


    /**
     *
     * @param location sets the text in the {@link CropExecuteLayout#outputLocationField}
     */
    public void setOutputLocation(String location){
        outputLocationField.setText(location);
    }

    /**
     * @return whether the {@link CropExecuteLayout#ptmButton} is selected
     */
    public boolean ptmSelected(){
        return ptmButton.isSelected();
    }


    /**
     * return whether the {@link CropExecuteLayout#hshButton} is selected
     */
    public boolean hshSelected(){
        return hshButton.isSelected();
    }


    /**
     * @return whether the {@link CropExecuteLayout#ptmRGBButton} is selected
     */
    public boolean ptmRGBSelected(){return ptmRGBButton.isSelected();}


    /**
     * @return whether the {@link CropExecuteLayout#ptmLRGBButton} is selected
     */
    public boolean ptmLRGBSelected(){return ptmLRGBButton.isSelected();}


    /**
     * @return what order button the user has selected, return 2 or 3. Returns -1 if neither are selected
     */
    public int getHSHOrder(){
        if(hshTerms2.isSelected())     { return 2; }
        else if(hshTerms3.isSelected()){ return 3; }

        return -1;
    }

    /**
     * @return {@link CropExecuteLayout#fitterLocationField}
     */
    public TextField getFitterLocationField() {
        return fitterLocationField;
    }


    /**
     * @return {@link CropExecuteLayout#outputLocationField}
     */
    public TextField getOutputLocationField() {
        return outputLocationField;
    }


    /**
     * Gets rid of all of the tiles in the Images grid on the left, the image in the central crop pane,
     * and deselects the crop option.
     */
    public void resetScene(){
        lpImagesGrid.clearTiles();
        imageCropPane.clearImage();
        useCropCheckBox.setSelected(false);
        useCrop = false;
    }


    /**
     * Prints the given line to the {@link CropExecuteLayout#fitterOutputArea}
     *
     * @param line the line to print
     */
    public void printToFitterOutput(String line){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fitterOutputArea.setText(fitterOutputArea.getText() +
                                            System.lineSeparator() +
                                            line);
            }
        });
    }
}
