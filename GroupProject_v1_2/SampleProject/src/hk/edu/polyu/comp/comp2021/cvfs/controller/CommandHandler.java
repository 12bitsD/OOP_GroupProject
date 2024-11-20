package hk.edu.polyu.comp.comp2021.cvfs.controller;

import hk.edu.polyu.comp.comp2021.cvfs.model.CVFS;
import hk.edu.polyu.comp.comp2021.cvfs.model.Directory;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private CVFS cvfs;

    public CommandHandler() {
        this.cvfs = new CVFS();
    }
    public void handleCommand(String command) {
        String[] parts = splitCommand(command);
        switch (parts[0]) {
            case "newDisk":
                handleNewDisk(parts);
                break;
            case "newDoc":
                handleNewDoc(parts);
                break;
            case "newDir":
                handleNewDir(parts);
                break;
            case "delete":
                handleDelete(parts);
                break;
            case "rename":
                handleRename(parts);
                break;
            case "changeDir":
                handleChangeDir(parts);
                break;
            case "list":
                cvfs.listFiles();
                break;
            case "rList":
                cvfs.rListFiles();
                break;
            case "save":
                handleSave(parts);
                break;
            case "load":
                handleLoad(parts);
                break;
            case "quit":
                handleQuit();
                break;
            case "newSimpleCri":
                handleNewSimpleCri(parts);
                break;
            case "newNegation":
                handleNewNegation(parts);
                break;
            case "newBinaryCri":
                handleNewBinaryCri(parts);
                break;
            case "printAllCriteria":
                cvfs.printAllCriteria();
                break;
            case "search":
                handleSearch(parts);
                break;
            case "rSearch":
                handlerSearch(parts);
                break;
            case "undo":
                cvfs.undo();
                break;
            case "redo":
                cvfs.redo();
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    private String[] splitCommand(String command) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : command.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    parts.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            parts.add(current.toString());
        }
        return parts.toArray(new String[0]);
    }
    private void handleNewDisk(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: newDisk command requires exactly one argument: diskSize.");
            return;
        }
        try {
            int diskSize = Integer.parseInt(parts[1]);
            if (diskSize <= 0) {
                System.out.println("Error: diskSize must be a positive integer.");
                return;
            }
            cvfs.newDisk(diskSize);
        } catch (NumberFormatException e) {
            System.out.println("Error: diskSize must be a positive integer.");
        }
    }
    private void handleNewDoc(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Error: newDoc command requires exactly three arguments: docName, docType, docContent.");
            return;
        }
        String docName = parts[1];
        String docType = parts[2];
        String docContent = parts[3];
        // Validate docName: only letters and digits, up to 10 characters
        if (!docName.matches("[a-zA-Z0-9]{1,10}")) {
            System.out.println("Error: docName must consist of letters and digits, up to 10 characters.");
            return;
        }
        // Validate docType: must be one of txt, java, html, css
        if (!docType.equals("txt") && !docType.equals("java") && !docType.equals("html") && !docType.equals("css")) {
            System.out.println("Error: docType must be one of txt, java, html, css.");
            return;
        }
        // Check if a file with the same name already exists
        if (cvfs.getCurrentDirectory().getFile(docName)!=null) {
            System.out.println("Error: A file with the name " + docName + " already exists in this directory.");
            return;
        }
        // Check disk space
        int docSize = 40 + 2 * docContent.length();
        if (cvfs.getCurrentDisk().getCurrentSize() + docSize > cvfs.getCurrentDisk().getSize()) {
            System.out.println("Error: Not enough disk space to create the document.");
            return;
        }
        cvfs.newDocument(docName, docType, docContent);
    }
    private void handleNewDir(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: newDir command requires exactly one argument: dirName.");
            return;
        }
        String dirName = parts[1];
        // Validate dirName: only letters and digits, up to 10 characters
        if (!dirName.matches("[a-zA-Z0-9]{1,10}")) {
            System.out.println("Error: dirName must consist of letters and digits, up to 10 characters.");
            return;
        }
        // Check if a directory with the same name already exists
        if (cvfs.getCurrentDirectory().getFile(dirName)!=null&&cvfs.getCurrentDirectory().getName()!=dirName) {
            System.out.println("Error: A directory with the name " + dirName + " already exists.");
            return;
        }
        // Check disk space
        if (cvfs.getCurrentDisk().getCurrentSize() + 40 > cvfs.getCurrentDisk().getSize()) {
            System.out.println("Error: Not enough disk space to create the directory.");
            return;
        }
        cvfs.newDirectory(dirName);
    }

    private void handleDelete(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: delete command requires exactly one argument: fileName.");
            return;
        }
        String fileName = parts[1];
        // Check if the file exists
        if (cvfs.getCurrentDirectory().getFile(fileName)==null) {
            System.out.println("Error: File " + fileName + " does not exist.");
            return;
        }
        cvfs.deleteFile(fileName);
    }
    private void handleRename(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Error: rename command requires exactly two arguments: oldFileName, newFileName.");
            return;
        }
        String oldFileName = parts[1];
        String newFileName = parts[2];
        // Validate file names: only letters and digits, up to 10 characters
        if (!oldFileName.matches("[a-zA-Z0-9]{1,10}") || !newFileName.matches("[a-zA-Z0-9]{1,10}")) {
            System.out.println("Error: File names must consist of letters and digits, up to 10 characters.");
            return;
        }
        // Check if oldFileName exists
        if (cvfs.getCurrentDirectory().getFile(oldFileName)==null) {
            System.out.println("Error: File " + oldFileName + " does not exist.");
            return;
        }
        // Check if newFileName is unique
        if (cvfs.getCurrentDirectory().getFile(newFileName)!=null) {
            System.out.println("Error: A file with the name " + newFileName + " already exists.");
            return;
        }
        cvfs.renameFile(oldFileName, newFileName);
    }

    private void handleChangeDir(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: changeDir command requires exactly one argument: dirName.");
            return;
        }
        String dirName = parts[1];
        if (dirName.equals("..")) {
            if (cvfs.getCurrentDirectory().getName()=="root") {
                System.out.println("Error: Cannot move above the root directory.");
                return;
            }
            cvfs.changeDirectory(dirName);
        } else {
            // Check if the directory exists

            if (!(cvfs.getCurrentDirectory().getFile(dirName) instanceof Directory)) {
                System.out.println("Error: Directory " + dirName + " does not exist.");
                return;
            }
            cvfs.changeDirectory(dirName);
        }
    }
    private void handleSave(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: save command requires exactly one argument: path.");
            return;
        }
        String path = parts[1];
        // Validate path: ensure it's a valid file path
        // For simplicity, we assume the path is valid and handle exceptions internally
        try {
            cvfs.saveDisk(path);
        } catch (Exception e) {
            System.out.println("Error: Failed to save disk to path " + path + ". " + e.getMessage());
        }
    }
    private void handleLoad(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: load command requires exactly one argument: path.");
            return;
        }
        String path = parts[1];
        // Validate path: ensure it's a valid file path and the file exists
        // For simplicity, we assume the path is valid and handle exceptions internally
        try {
            cvfs.loadDisk(path);
        } catch (Exception e) {
            System.out.println("Error: Failed to load disk from path " + path + ". " + e.getMessage());
        }
    }
    private void handleQuit() {
        // Optionally prompt to save changes
        System.out.println("Exiting the system.");
        cvfs.quit();
    }

    private void handleNewSimpleCri(String[] parts) {
        if (parts.length != 5) {
            System.out.println("Error: newSimpleCri command requires exactly four arguments: criName, attrName, op, val.");
            return;
        }
        String criName = parts[1];
        String attrName = parts[2];
        String op = parts[3];
        String val = parts[4];
        // Validate criName: exactly two English letters
        if (!criName.matches("[a-zA-Z]{2}")) {
            System.out.println("Error: criName must be exactly two English letters.");
            return;
        }

        // Validate attrName: must be name, type, or size
        if (!attrName.equals("name") && !attrName.equals("type") && !attrName.equals("size")) {
            System.out.println("Error: attrName must be name, type, or size.");
            return;
        }
        // Validate op based on attrName
        if (attrName.equals("name")) {
            if (!op.equals("contains")) {
                System.out.println("Error: op must be contains for attrName name.");
                return;
            }
            // Ensure val is a quoted string
            if (!val.startsWith("\"") || !val.endsWith("\"")) {
                System.out.println("Error: val must be a quoted string for attrName name.");
                return;
            }
        } else if (attrName.equals("type")) {
            if (!op.equals("equals")) {
                System.out.println("Error: op must be equals for attrName type.");
                return;
            }
            // Ensure val is a quoted string
            if (!val.startsWith("\"") || !val.endsWith("\"")) {
                System.out.println("Error: val must be a quoted string for attrName type.");
                return;
            }
        } else if (attrName.equals("size")) {
            // Validate op: >, <, >=, <=, ==, !=
            if (!op.equals(">") && !op.equals("<") && !op.equals(">=") && !op.equals("<=") && !op.equals("==") && !op.equals("!=")) {
                System.out.println("Error: op must be one of >, <, >=, <=, ==, != for attrName size.");
                return;
            }
            // Ensure val is an integer
            try {
                Integer.parseInt(val);
            } catch (NumberFormatException e) {
                System.out.println("Error: val must be an integer for attrName size.");
                return;
            }
        }
        cvfs.NewSimpleCri(criName, attrName, op, val);
    }
    private void handleNewNegation(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Error: newNegation command requires exactly two arguments: criName1, criName2.");
            return;
        }
        String criName1 = parts[1];
        String criName2 = parts[2];
        // Validate criName1: exactly two English letters
        if (!criName1.matches("[a-zA-Z]{2}")) {
            System.out.println("Error: criName1 must be exactly two English letters.");
            return;
        }
        // Validate criName2: must refer to an existing criterion
        if (cvfs.getCri_set().get(criName2) == null) {
            System.out.println("Error: Criterion " + criName2 + " does not exist.");
            return;
        }
        cvfs.newNegation(criName1, criName2);
    }

    private void handleNewBinaryCri(String[] parts) {
        if (parts.length != 5) {
            System.out.println("Error: newBinaryCri command requires exactly four arguments: criName1, criName3, logicOp, criName4.");
            return;
        }
        String criName1 = parts[1];
        String criName3 = parts[2];
        String logicOp = parts[3];
        String criName4 = parts[4];
        // Validate criName1: exactly two English letters
        if (!criName1.matches("[a-zA-Z]{2}")) {
            System.out.println("Error: criName1 must be exactly two English letters.");
            return;
        }
        // Validate logicOp: must be && or ||
        if (!logicOp.equals("&&") && !logicOp.equals("||")) {
            System.out.println("Error: logicOp must be && or ||.");
            return;
        }
        // Validate criName3 and criName4: must refer to existing criteria
        if (cvfs.getCri_set().get(criName3)==null || cvfs.getCri_set().get(criName4)==null) {
            System.out.println("Error: Criteria " + criName3 + " or " + criName4 + " do not exist.");
            return;
        }
        cvfs.newBinaryCri(criName1, criName3, logicOp, criName4);
    }
    private void handleSearch(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: search command requires exactly one argument: criName.");
            return;
        }
        String criName = parts[1];
        // Validate criName: must refer to an existing criterion
        if (cvfs.getCri_set().get(criName)==null) {
            System.out.println("Error: Criterion " + criName + " does not exist.");
            return;
        }
        cvfs.search(criName);
    }
    private void handlerSearch(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Error: rSearch command requires exactly one argument: criName.");
            return;
        }
        String criName = parts[1];
        // Validate criName: must refer to an existing criterion
        if (cvfs.getCri_set().get(criName)==null) {
            System.out.println("Error: Criterion " + criName + " does not exist.");
            return;
        }
        cvfs.rSearch(criName);
    }

}