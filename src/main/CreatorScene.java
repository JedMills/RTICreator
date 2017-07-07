package main;

/**
 * Created by Jed on 06-Jul-17.
 */
public interface CreatorScene {

    int getSceneMinWidth();

    int getSceneMaxWidth();

    int getSceneMinHeight();

    int getSceneMaxHeight();

    void updateSize(double width, double height);

}
