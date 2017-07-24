package main;

/**
 * All the scenes that the app switches to implement this interface. It allows the main method to get any scene that
 * it is currently on by calling the methods here.
 *
 * @see Main
 * @see newProjectScene.NewProjectLayout
 * @see highlightDetectionScene.HighlightDetectionLayout
 * @see cropExecuteScene.CropExecuteLayout
 *
 * @author Jed Mills
 */
public interface CreatorScene {

    /**
     * @return min width of this scene
     */
    int getSceneMinWidth();

    /**
     * @return max width of this scene
     */
    int getSceneMaxWidth();

    /**
     * @return min height of this scene
     */
    int getSceneMinHeight();

    /**
     * @return max height of this scene
     */
    int getSceneMaxHeight();

    /**
     * Updates the layout of this scene.
     *
     * @param width     width of the window this scene is in
     * @param height    height of the window this scene is in
     */
    void updateSize(double width, double height);

}
