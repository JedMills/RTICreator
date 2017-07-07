package main;

/**
 * Created by Jed on 06-Jul-17.
 */
public enum ProjectType {

    HIGHLIGHT_HSH   ("Highlight based - PTM fitter"),
    HIGHLIGHT_PTM   ("Highlight based - HSH fitter"),
    DOME_LP_HSH     ("Dome LP file - PTM fitter"),
    DOME_LP_PTM     ("Dome LP file - HSH fitter"),
    LP_HSH          ("LP file - PTM fitter"),
    LP_PTM          ("LP file - HSH fitter");

    private String label;

    ProjectType(String label){
        this.label = label;
    }


    public String toString(){
        return label;
    }
}
