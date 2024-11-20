package hk.edu.polyu.comp.comp2021.cvfs.model;
import hk.edu.polyu.comp.comp2021.cvfs.model.command.*;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

//CVFS本体


public class CVFS {
    private Disk currentDisk;
    private Directory currentDirectory;
    private Map<String,Criterion> cri_set;
    private Stack<command> undoStack;
    private Stack<command> redoStack;

    public CVFS() {
        this.currentDisk = null;
        this.currentDirectory = null;
        this.cri_set= new HashMap<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        // [REQ10]  Criterion name: IsDocument
        cri_set.put("IsDocument",null);

    }
    // [REQ1] new disk
    // Command: newDisk diskSize
    public void newDisk(int diskSize) {
        this.currentDisk = new Disk(diskSize);
        this.currentDirectory = this.currentDisk.getRootDirectory();
    }

    //[BON2]
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
    public Disk getCurrentDisk() {return currentDisk;}
    public Map<String,Criterion> getCri_set() {return cri_set;}
    public Stack<command> getUndoStack() {return undoStack;}
    public Stack<command> getRedoStack() {return redoStack;}


    public void deleteCriterion(String criName) {
        Criterion cri = cri_set.get(criName);
        if(cri!=null) {
            cri_set.remove(criName);
        }
    }

    public void undo(){
        if(!undoStack.isEmpty()) {
            command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    public void redo(){
        if(!redoStack.isEmpty()) {
            command command = redoStack.pop();
            command.redo();
            undoStack.push(command);
        }

    }
    public void excuteCommand(command cmd){
        cmd.redo();
        undoStack.push(cmd);
        redoStack.clear();
    }
    public void NewDocument(String name, String type, String content) {
        excuteCommand(new NewDocCommand(this, name, type, content));
    }

    public void NewDirectory(String name) {
        excuteCommand(new NewDirCommand(this, name));
    }

    public void DeleteFile(String name) {
        excuteCommand(new DeleteCommand(this, name));
    }

    public void RenameFile(String oldName, String newName) {
        excuteCommand(new RenameCommand(this, oldName, newName));
    }

    public void ChangeDirectory(String dirName) {
        excuteCommand(new ChangeDirCommand(this, dirName));
    }

    public void NewSimpleCri(String criName, String attrName, String op, String val) {
        excuteCommand(new NewSimpleCriCommand(this, criName, attrName, op, val));
    }

    public void NewNegation(String criName1, String criName2) {
        excuteCommand(new NewNegationCommand(this, criName1, criName2));
    }





    // [REQ2] new Document
    // Command: newDoc docName docType docContent

    public void newDocument(String name, String type, String content) {
        if (currentDirectory != null) {
            Document d = new Document(name, type, content);
            currentDirectory.addFile(d);
            currentDisk.setCurrentSize(currentDisk.getCurrentSize()+d.getSize());
        }
    }

     //[REQ3] new directory
    // Command: newDir dirName
    public void newDirectory(String name) {
        if (currentDirectory != null) {
            currentDisk.setCurrentSize(currentDisk.getCurrentSize()+40);
            currentDirectory.addFile(new Directory(name));
        }
    }

    // [REQ4] delete file
    // Command: delete fileName
    public void deleteFile(String name) {
        if (currentDirectory != null) {
            currentDisk.setCurrentSize(currentDisk.getCurrentSize()+currentDirectory.getFile(name).getSize());
            currentDirectory.deleteFile(name);

        }
    }
    // [REQ5]  rename File
    // Command: rename oldFileName newFileName
    public void renameFile(String oldName, String newName) {
        if (currentDirectory != null) {
            currentDirectory.renameFile(oldName, newName);
        }
    }

     //[REQ6] change directory
     // Command: changeDir dirName
    public void changeDirectory(String dirName) {
        if (currentDirectory != null) {
            if (dirName.equals("..")) {
                if (currentDirectory.getParent() != null) {
                    currentDirectory = currentDirectory.getParent();
                }
            } else {
                File file = currentDirectory.getFile(dirName);
                if (file instanceof Directory) {
                    currentDirectory = (Directory) file;
                }
            }
        }
    }

    //[REQ7]   List all the files (name, type, and size)
    // Command: list
    public void listFiles() {
        if (currentDirectory != null) {
            currentDirectory.listFiles();
        }
    }
    //[REQ8]extends list function and Use indentation to indicate the level of each file in directory
    // Command: rList
    public void rListFiles() {
        if (currentDirectory != null) {
            currentDirectory.rListFiles();
        }
    }
    // [REQ9]a simple criterion
    // Command: newSimpleCri criName attrName op val

    public void newCri(String cirName,String attrName,String op,String val) {
        //cirName  exactly two English letters!!!!!!!!!!  todo
        Criterion cri = new Criterion(attrName,op,val);
        cri_set.put(cirName,cri);
    }
    public boolean useCriterion(String cirName,File f) {
        Criterion cri = cri_set.get(cirName);
        if(cirName.equals("IsDocument")) return Criterion.isDocument(f);
        return cri.evaluate(f);
    }

    // [REQ11] composite criteria.
    //Command: newNegation criName1 criName2
    //Command: newBinaryCri criName1 criName3 logicOp criName4
    public void newNegation(String criName1,String criName2) {
        Criterion cri2 = cri_set.get(criName2);
        if (cri2 != null) {
            compositeCriterion compositeCri = new compositeCriterion(criName1, cri2, null, null, true);
            cri_set.put(criName1,compositeCri);
        }
    }
    //newBinaryCri
    public void newBinaryCri(String criName1,String criName3,String logicOp,String criName4) {
        Criterion cri3 = cri_set.get(criName3);
        Criterion cri4 = cri_set.get(criName4);
        if(cri3!=null && cri4!=null) {
            compositeCriterion compositeCri = new compositeCriterion(criName1, cri3, cri4, logicOp, false);
            cri_set.put(criName1,compositeCri);
        }
    }
    // [REQ12] printing of all defined criteria
    //Command: printAllCriteria
    public void printAllCriteria(){
        for(Map.Entry<String,Criterion> entry : cri_set.entrySet()){
            String criName = entry.getKey();
            Criterion cri = entry.getValue();
            //如果没有对应的cri，就是IsDocument
            if(cri==null) System.out.println(criName+": IsDocument");
            else System.out.println(criName+":"+cri.toString());
        }

    }
    // [REQ13]  searching for files based on an existing criterion
    // Command: search criName
    public void search(String criName){
        if(currentDirectory!=null) {
            int total_f = 0,totalSzie=0;
            for(File f:currentDirectory.getFiles().values()){
                if(useCriterion(criName,f)) {
                    total_f++;
                    totalSzie+=f.getSize();
                    System.out.print(" ");
                    System.out.println(f.getName()+" "+f.getSize());

                }


            }
            System.out.println("Total files: "+total_f+",Total size: "+totalSzie);
        }
    }

    // [REQ14]rSearch  working directory that satisfy the criterion criName.
    // Command: rSearch criName
    public void rSearch(String criName) {
        if (currentDirectory != null) {
            rSearch(criName, currentDirectory, 0);
        }
    }
    private void rSearch(String criName, Directory dir, int level) {
        int total_f = 0,totalSzie=0;
        for(File f:dir.getFiles().values()) {
            if (useCriterion(criName, f)) {
                for (int i = 0; i < level; i++) {
                    System.out.print(" ");
                }
            }
            System.out.println(f.getName() + " " + f.getSize());
            total_f++;
            totalSzie += f.getSize();

            if (f instanceof Directory) rSearch(criName, (Directory) f, level + 1);
        }
        if(level == 0) System.out.println("Total files: "+total_f+",Total size: "+totalSzie);

    }
    // [REQ15] saving the working virtual disk
    // Command: save path
    public void saveDisk(String path) {
        //  save disk logic
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))){
            oos.writeObject(currentDisk);
            oos.writeObject(cri_set);
            System.out.println("Disk and criteria saved to"+ path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //[BON1]  the defined search criteria saved to/loaded from the local file system

    // [REQ16] loading a virtual disk  from a file on the local file system.
    // Command: load path
    public void loadDisk(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Disk loadDisk = (Disk) ois.readObject();
            this.currentDisk = loadDisk;
            System.out.println("Disk loaded successfully.");

            this.cri_set = (Map<String, Criterion>) ois.readObject();
            System.out.println("cri_set loaded successfully.");

            this.currentDirectory = currentDisk.getRootDirectory();
            System.out.println("Disk loaded from " + path);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //[REQ17] Terminate the execution of the system.
    //  Command: quit
    public void quit() {
        System.exit(0);
    }



}