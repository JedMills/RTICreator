package utils;

import main.ProjectType;

/**
 * Created by Jed on 09-Jul-17.
 */
public class Utils {


    public static boolean checkIn(ProjectType projectType, ProjectType[] projectTypes){
        for(ProjectType type : projectTypes) {
            if (type.equals(projectType)) {
                return true;
            }
        }
        return false;
    }


    public static boolean checkIn(String string, String[] strings){
        for(String s : strings) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }


}
