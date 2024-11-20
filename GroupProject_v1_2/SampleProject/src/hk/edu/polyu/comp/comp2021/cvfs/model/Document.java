package hk.edu.polyu.comp.comp2021.cvfs.model;



//文件的类，可以获取内容，类型，文件大小
public class Document extends File {
    private String content;

    public Document(String name, String type, String content) {
        super(name,type);
        this.content = content;
    }

    @Override
    public int getSize() {
        return 40 + content.length() * 2;
    }

    public String getContent() {
        return content;
    }
}