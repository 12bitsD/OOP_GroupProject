package hk.edu.polyu.comp.comp2021.cvfs.model;

import hk.edu.polyu.comp.comp2021.cvfs.model.command.NewDocCommand;
import hk.edu.polyu.comp.comp2021.cvfs.model.command.command;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CVFSTest {

    private CVFS cvfs;
    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() {
        cvfs = new CVFS();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testNewDisk() {
        cvfs.newDisk(1024);
        assertNotNull(cvfs.getCurrentDisk());
        assertEquals(1024, cvfs.getCurrentDisk().getSize());
        assertNotNull(cvfs.getCurrentDirectory());
        assertEquals("root", cvfs.getCurrentDirectory().getName());
    }

    @Test
    public void testNewDocument() {
        cvfs.newDisk(1024);
        cvfs.newDocument("doc1", "txt", "Hello World");
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
        File doc = cvfs.getCurrentDirectory().getFiles().get("doc1");
        assertEquals("txt", doc.getType());
        assertEquals("Hello World", ((Document) doc).getContent());
    }

    @Test
    public void testNewDirectory() {
        cvfs.newDisk(1024);
        cvfs.NewDirectory("dir1");
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("dir1"));
        File dir = cvfs.getCurrentDirectory().getFiles().get("dir1");
        assertEquals("Directory", dir.getType());
    }

    @Test
    public void testDeleteFile() {
        cvfs.newDisk(1024);
        cvfs.newDocument("doc1", "txt", "Content");
        cvfs.DeleteFile("doc1");
        assertFalse(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
    }

    @Test
    public void testRenameFile() {
        cvfs.newDisk(1024);
        cvfs.NewDocument("doc1", "txt", "Content");
        cvfs.RenameFile("doc1", "doc2");
        assertFalse(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("doc2"));
    }

    @Test
    public void testChangeDirectory() {
        cvfs.newDisk(1024);
        cvfs.newDirectory("dir1");
        cvfs.ChangeDirectory("dir1");
        assertEquals("dir1", cvfs.getCurrentDirectory().getName());
        cvfs.ChangeDirectory("..");
        assertEquals("root", cvfs.getCurrentDirectory().getName());
    }

    @Test
    public void testListFiles() {
        cvfs.newDisk(1024);
        cvfs.newDocument("doc1", "txt", "");
        cvfs.newDirectory("dir1");
        cvfs.listFiles();
        String output = outContent.toString();
        assertTrue(output.contains("doc1  40  txt"));
        assertTrue(output.contains("dir1  40"));
    }

    @Test
    public void testRListFiles() {
        cvfs.newDisk(1024);
        cvfs.newDirectory("dir1");
        cvfs.changeDirectory("dir1");
        cvfs.newDocument("doc1", "txt", "Content");
        cvfs.changeDirectory("..");
        cvfs.rListFiles();
        String output = outContent.toString().trim();
        assertTrue(output.contains("dir1 94"));
        assertTrue(output.contains("doc1  54  txt"));
    }

    @Test
    public void testNewSimpleCri() {
        cvfs.newDisk(1024);
        cvfs.NewSimpleCri("cri1", "type", "equals", "txt");
        cvfs.NewSimpleCri("cri2", "name", "contains", "doc1");
        cvfs.NewSimpleCri("cri3", "size", ">", "50");
        Criterion cri = cvfs.getCri_set().get("cri1");
        Criterion cri2 = cvfs.getCri_set().get("cri2");
        Criterion cri3 = cvfs.getCri_set().get("cri3");

        assertNotNull(cri);
        assertNotNull(cri2);
        assertNotNull(cri3);
        assertTrue(cri.evaluate(new Document("doc1", "txt", "Content")));
        assertTrue(cri2.evaluate(new Document("doc1", "txt", "Content")));
        assertTrue(cri3.evaluate(new Document("doc1", "txt", "Content")));
        assertFalse(cri.evaluate(new Document("doc2", "java", "")));
        assertFalse(cri2.evaluate(new Document("doc2", "txt", "")));
        assertFalse(cri3.evaluate(new Document("doc2", "txt", "")));
    }

    @Test
    public void testNewCompositeCri() {
        cvfs.newDisk(1024);
        cvfs.NewSimpleCri("cri1", "type", "equals", "txt");
        cvfs.NewNegation("cri2", "cri1");
        Criterion cri2 = cvfs.getCri_set().get("cri2");
        assertFalse(cri2.evaluate(new Document("doc1", "txt", "Content")));

        cvfs.newBinaryCri("cri3", "cri1", "&&", "cri2");
        cvfs.newBinaryCri("cri4", "cri1", "||", "cri2");

        Criterion cri3 = cvfs.getCri_set().get("cri3");
        Criterion cri4 = cvfs.getCri_set().get("cri4");
        assertFalse(cri3.evaluate(new Document("doc1", "txt", "Content")));

        assertTrue(cri4.evaluate(new Document("doc1", "txt", "Content")));
    }
    @Test
    public void testCompositeCriterionBinary2() {
        cvfs.newDisk(1024);
        cvfs.NewDocument("doc1", "txt", "Content");
        cvfs.NewDocument("doc2", "pdf", "Short");
        cvfs.NewDocument("doc3", "txt", "Short");
        cvfs.NewDocument("doc4", "pdf", "LongContent");
        cvfs.NewSimpleCri("cri1", "type", "equals", "txt");
        cvfs.NewSimpleCri("cri2", "size", ">=", "10");
        cvfs.newBinaryCri("cri3", "cri1", "&&", "cri2");
        assertTrue(cvfs.useCriterion("cri3", cvfs.getCurrentDirectory().getFiles().get("doc1")));
        assertFalse(cvfs.useCriterion("cri3", cvfs.getCurrentDirectory().getFiles().get("doc2")));
        assertTrue(cvfs.useCriterion("cri3", cvfs.getCurrentDirectory().getFiles().get("doc3")));
        assertFalse(cvfs.useCriterion("cri3", cvfs.getCurrentDirectory().getFiles().get("doc4")));
    }

    @Test
    public void testDeleteCriterion() {
        cvfs.newDisk(1024);
        cvfs.NewSimpleCri("cri1", "type", "==", "txt");
        cvfs.deleteCriterion("cri1");
        assertFalse(cvfs.getCri_set().containsKey("cri1"));
    }

    @Test
    public void testSearchFiles() {
        cvfs.newDisk(1024);
        cvfs.NewDocument("doc1", "txt", "Content");
        cvfs.NewDocument("doc2", "java", "Content");
        cvfs.NewSimpleCri("cri1", "type", "equals", "txt");
        cvfs.search("cri1");
        String output = outContent.toString().trim();
        assertTrue(output.contains("doc1"));
        assertFalse(output.contains("doc2"));
    }

    @Test
    public void testRSearchFiles() {
        cvfs.newDisk(1024);
        cvfs.newDirectory("dir1");
        cvfs.changeDirectory("dir1");
        cvfs.newDocument("doc1", "txt", "Content");
        cvfs.changeDirectory("..");
        cvfs.rSearch("IsDocument");
        String output = outContent.toString();
        assertTrue(output.contains("dir1 94"));
        assertTrue(output.contains(" doc1 54"));
        assertTrue(output.contains("Total files: 1,Total size: 94"));
    }

    @Test
    public void testSaveLoadDisk() throws IOException {
        cvfs.newDisk(1024);
        cvfs.NewDocument("doc1", "txt", "Content");
        String tempPath = "temp_disk.ser";
        cvfs.saveDisk(tempPath);
        CVFS cvfs2 = new CVFS();
        cvfs2.loadDisk(tempPath);
        assertTrue(cvfs2.getCurrentDirectory().getFiles().containsKey("doc1"));
        Files.delete(Paths.get(tempPath));
    }

    @Test
    public void testUndoRedo() {
        cvfs.newDisk(1024);
        cvfs.excuteCommand(new NewDocCommand(cvfs, "doc1", "txt", "Content"));
        cvfs.undo();
        assertFalse(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
        cvfs.redo();
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
    }
    @Test
    public void testExecuteCommand() {
        cvfs.newDisk(1024);
        command cmd = new command() {
            @Override
            public void redo() {
                cvfs.newDocument("doc1", "txt", "Content");
            }

            @Override
            public void undo() {
                cvfs.deleteFile("doc1");
            }
        };
        cvfs.excuteCommand(cmd);
        assertTrue(cvfs.getUndoStack().contains(cmd));
        assertTrue(cvfs.getRedoStack().isEmpty());
    }

    @Test
    public void testUndoRedo2() {
        cvfs.newDisk(1024);
        command cmd = new command() {
            @Override
            public void redo() {
                cvfs.newDocument("doc1", "txt", "Content");
            }

            @Override
            public void undo() {
                cvfs.deleteFile("doc1");
            }
        };
        cvfs.excuteCommand(cmd);
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
        cvfs.undo();
        assertFalse(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
        cvfs.redo();
        assertTrue(cvfs.getCurrentDirectory().getFiles().containsKey("doc1"));
    }
    @Test
    public void testPrintAllCriteria() {
        cvfs.newDisk(1024);
        cvfs.NewSimpleCri("cri1", "type", "equals", "txt");
        cvfs.printAllCriteria();
        String output = outContent.toString();

        assertTrue(output.contains("cri1:Criterion{attrName=type,op=equals,val=txt}"));
        assertTrue(output.contains("IsDocument: IsDocument"));
    }


}