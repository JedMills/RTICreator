package main;

/**
 * Created by Jed on 06-Jul-17.
 */
public enum ProjectType {

    HIGHLIGHT   ("Highlight based"),
    LP     ("LP file"),
    EXISTING_LP ("LP file from existing project");

    private String label;

    ProjectType(String label){
        this.label = label;
    }


    public String toString(){
        return label;
    }
}
