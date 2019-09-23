package com.philips.bootcamp.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class FileUtilsTest {
    
    // test cases for getFileContents
    @Test
    public void nullFile() {
        String result = FileUtils.getFileContents(null);
        assertEquals(null, result);
    }

    @Test
    public void emptyFileContents() {
        File emptyFile = new File("emptyTextFile.txt");
        try {
            if (!emptyFile.exists()) {
                emptyFile.createNewFile();
            }
            String result = FileUtils.getFileContents(emptyFile);
            assertEquals("", result);
            emptyFile.delete();
        } catch (IOException ioe) {
            throw new RuntimeException("[ERROR] Couldn't execute test case\n[ERROR] Cause: " + ioe.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void fileIsADirectory() {
        File aDirectory = new File("src");
        FileUtils.getFileContents(aDirectory);
    }

    @Test
    public void fileDoesntExist() {
        File whatFile = new File("doesntexist");
        assertEquals(null, FileUtils.getFileContents(whatFile));
    }

    @Test
    public void readABinaryFile() throws IOException {
        File file = new File("AutoBuildTest.exe");
        file.createNewFile();
        assertEquals("", FileUtils.getFileContents(file));
        file.delete();
    }

    // Test cases for writeFileContents
    @Test(expected = RuntimeException.class)
    public void nullFileButProperContents() {
        FileUtils.writeFileContents(null, "contents");
    }

    @Test
    public void properFileButNullContents() {
        File properFile = new File("hello.txt");
        FileUtils.writeFileContents(properFile, null);
        String actual = FileUtils.getFileContents(properFile);
        properFile.delete();
        assertEquals("", actual);
    }

    @Test(expected = RuntimeException.class)
    public void bothParamsBeNull() {
        FileUtils.writeFileContents(null, null);
    }

    @Test
    public void properArgs() {
        File file = new File("hello.txt");
        String contents = "Some contents\nHallelujah!1234@$$5";
        FileUtils.writeFileContents(file, contents);
        String actual = FileUtils.getFileContents(file);
        file.delete();
        assertEquals(contents+"\n", actual);
    }

    @Test(expected = RuntimeException.class)
    public void directoryAndproperContents() {
        File file = new File("src");
        String contents = "Some contents\nHallelujah!1234@$$5";
        FileUtils.writeFileContents(file, contents);
    }

    @Test
    public void fileandEmptyContent() {
        File file = new File("hello.txt");
        FileUtils.writeFileContents(file, "");
        String actual = FileUtils.getFileContents(file);
        file.delete();
        assertEquals("", actual);
    }

    // Test cases for deleteDirectoryRecursion
    @Test
    public void nullPath() throws IOException {
        FileUtils.deleteDirectoryRecursion(null);
    }

    @Test
    public void nonExistantPath() throws IOException {
        FileUtils.deleteDirectoryRecursion(new File("ksjbdkd").toPath());
    }

    @Test
    public void pathPointsToFile() throws IOException {
        File file = new File("temp.txt");
        file.createNewFile();
        FileUtils.deleteDirectoryRecursion(file.toPath());
        if (file.exists()) {
            file.delete();
            throw new IOException("File was to be deleted!");
        }
    }

    @Test
    public void pathPointsToEmptyDirectory() throws IOException {
        File emptyDir = new File("emptyDir");
        emptyDir.mkdir();
        FileUtils.deleteDirectoryRecursion(emptyDir.toPath());
        if (emptyDir.exists()) {
            emptyDir.delete();
            throw new IOException("Directory not deleted!");
        }
    }

    @Test
    public void pathPointsToDirectoryWithFiles() throws IOException {
        File dir = new File("emptyDir");
        dir.mkdir();
        File tempfile = new File(dir, "temp.txt");
        tempfile.createNewFile();
        FileUtils.deleteDirectoryRecursion(dir.toPath());
        if (dir.exists()) {
            throw new IOException("Directory not deleted!");
        }
    }
}