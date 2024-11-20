package hk.edu.polyu.comp.comp2021.cvfs.model;

import java.util.HashMap;
import java.util.Map;
//Directory -一张HashMap
//File的一种

//method：



public class Directory extends File {
    private Map<String, File> files;
    private Directory parent;
    public Directory(String name) {
        super(name,"Directory");
        this.files = new HashMap<>();
        this.parent = null;
    }
    //add() HashMap.put进去 增加文件，add功能- 如果增加的是目录，会把新加的目录设置父目录为当前目录
    public void addFile(File file) {
        files.put(file.getName(), file);
        if (file instanceof Directory) {
            ((Directory) file).setParent(this);
        }
    }

    //deleteFile，删除文件，直接从HashMap里移除
    public void deleteFile(String name) {
        files.remove(name);
    }

    //renameFile（） 在目录里重新设置文件名称
    //需要在HashMap里去remove旧的键值，并重新插入新文件，因为需要update file 的 Hashcode
    public void renameFile(String oldName, String newName) {
        File file = files.get(oldName);
        if (file != null) {
            file.setName(newName);
            files.remove(oldName);
            files.put(newName, file);
        }
    }

    //获取文件名
    public File getFile(String name) {
        return files.get(name);
    }
    //获取所有文件
    public Map<String, File> getFiles() {
        return files;
    }
    //获取父目录
    public Directory getParent() {
        return parent;
    }
    //设置父目录
    public void setParent(Directory parent) {
        this.parent = parent;
    }

    //列出所有的文件
    //用For 取出所有HashMap的值，然后打印出来
    public void listFiles() {
        for (File file : files.values()) {
            if(file instanceof Directory) {
                System.out.println(file.getName() + "  " + file.getSize());
            }else{
                System.out.println(file.getName() + "  " + file.getSize()+"  "+file.getType());
        }}
    }
    //递归的方式列出文件，默认从该层出发列举出所有文件
    public void rListFiles() {
        rListFiles(0);
    }

    //也可以设定从子层出发去递归所有文件
    private void rListFiles(int level) {
        for (File file : files.values()) {
            for (int i = 0; i < level; i++) {
                System.out.print("  ");
            }

            if (file instanceof Directory) {
                System.out.println(file.getName() + " " + file.getSize());
                ((Directory) file).rListFiles(level + 1);
            }else{
                System.out.println(file.getName() + "  " + file.getSize()+"  "+file.getType());
            }
        }
    }


    //获取size
    @Override
    public int getSize() {
        int totalSize = 40;
        for (File file : files.values()) {
            totalSize += file.getSize();
        }
        return totalSize;
    }
}