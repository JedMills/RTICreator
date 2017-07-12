package main;

/**
 * Created by Jed on 06-Jul-17.
 */
public enum ProjectType {

    HIGHLIGHT   ("Highlight based"),
    DOME_LP     ("Dome LP file"),
    LP          ("LP file");

    private String label;

    ProjectType(String label){
        this.label = label;
    }


    public String toString(){
        return label;
    }
}
