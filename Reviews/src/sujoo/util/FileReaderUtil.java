package sujoo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderUtil {

    private BufferedReader reader;
    private String splitValue;
    private String currentLine;
    private File file;

    public FileReaderUtil(File file, String splitValue) throws FileNotFoundException {
        this.file = file;
        this.splitValue = splitValue;
        initializeReader();
    }

    public FileReaderUtil(String fileName, String splitValue) throws FileNotFoundException {
        file = new File(fileName);
        this.splitValue = splitValue;
        initializeReader();
    }

    public FileReaderUtil() throws FileNotFoundException {
        file = FileChooserUtil.chooseFile();
        splitValue = FileChooserUtil.chooseSeparator();
        initializeReader();
    }
    
    private void initializeReader() throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
    }
    
    public String getFileName() {
        return file.getName();
    }

    public boolean hasNext() throws IOException {
        if ((currentLine = reader.readLine()) != null) {
            return true;
        } else {
            return false;
        }
    }

    public String[] next() {
        return currentLine.split(splitValue);
    }
    
    public void skipLine() throws IOException {
        reader.readLine();
    }

    public void close() throws IOException {
        reader.close();
    }
    
    public File getFile() {
        return file;
    }
    
    public String getSplitValue() {
        return splitValue;
    }
}
