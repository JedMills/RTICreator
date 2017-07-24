package main;


/**
 * Represents a whole project that the user sees through in the program. An RTIProject is created in the
 * {@link initialScene.InitialLayout} and it is used by the program to determine what order of scenes are displayed to
 * the user so that they can make an RTI file.
 *
 * @author Jed Mills
 */
public class RTIProject {

    /** Name of the project */
    private String name;

    /** Type of project */
    private ProjectType projectType;

    /**
     * Creates a new RTIProject with the name and type given.
     *
     * @param name          name of the project the user chose
     * @param projectType   type of the project the user chose
     */
    public RTIProject(String name, ProjectType projectType) {
        this.name = name;
        this.projectType = projectType;
    }

    /**
     * @return  {@link RTIProject#name}
     */
    public String getName() {
        return name;
    }

    /**
     * @param name  sets the {@link RTIProject#name} attribute
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return {@link RTIProject#projectType}
     */
    public ProjectType getProjectType() {
        return projectType;
    }


    /**
     * @param projectType set the {@link RTIProject#projectType} attribute
     */
    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }


    /**
     * Type of the RTIProject. This is ultimately the way that the user is going to feed the lp data and images into
     * the fitters at the end of the program, so represent the pathways through the program the user takes.
     */
    public enum ProjectType {

        HIGHLIGHT   ("Highlight based"),
        LP     ("LP file"),
        EXISTING_LP ("LP file from existing project");

        /** Description of the project type*/
        private String label;

        /**
         * Creates a new ProjectType
         *
         * @param label description of this enum value
         */
        ProjectType(String label){
            this.label = label;
        }


        /**
         * @return {@link ProjectType#label}
         */
        public String toString(){
            return label;
        }
    }

}
