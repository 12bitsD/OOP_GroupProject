package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.io.Serializable;

public abstract class File implements Serializable {
    private static final long SerialVersionUID = 1L;
    private String name;
    private String type;

    public File(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }

    public abstract int getSize();
}