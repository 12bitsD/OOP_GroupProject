package hk.edu.polyu.comp.comp2021.cvfs.model;
import java.io.Serializable;

//硬盘，初始化有最大容积，和根目录“root”， 可以获取根目录
public class Disk implements Serializable {
    private static final long serialVersionUID = 1L;
    private int maxSize;
    private Directory rootDirectory;
    private int currentSize;

    public Disk(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;
        this.rootDirectory = new Directory("root");
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public int getSize() {return maxSize;}
    public int getCurrentSize() {return currentSize;}
    public void setCurrentSize(int currentSize) {this.currentSize = currentSize;}
}