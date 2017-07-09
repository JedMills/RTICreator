package main;

/**
 * Created by Jed on 06-Jul-17.
 */
public class ProjectProperty {

    private String field;
    private String value;

    public ProjectProperty(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setVale(String value) {
        this.value = value;
    }
}
