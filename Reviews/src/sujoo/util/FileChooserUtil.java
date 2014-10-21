package sujoo.util;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileChooserUtil {

    public static File chooseFile() throws FileNotFoundException {
        JFileChooser fileChooser = new JFileChooser();
        int chooserResult = fileChooser.showOpenDialog(null);

        if (chooserResult == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            int optionResult = JOptionPane.showConfirmDialog(null,
                    "No file was selected, so the program will terminate." + System.lineSeparator()
                            + "Do you want it to terminate?", "Exit Application?", JOptionPane.YES_NO_OPTION);
            if (optionResult == JOptionPane.NO_OPTION) {
                return chooseFile();
            } else {
                System.exit(0);
                return null;
            }
        }
    }

    public static String chooseSeparator() {
        String splitResult = (String) JOptionPane.showInputDialog(null,
                "What character separates values in a single line? (use \\t for tab)", "Enter Separator Value",
                JOptionPane.PLAIN_MESSAGE, null, null, "");

        if (splitResult != null) {
            return splitResult;
        } else {
            int optionResult = JOptionPane.showConfirmDialog(null,
                    "No separator was selected, so the program will terminate." + System.lineSeparator()
                            + "Do you want it to terminate?", "Exit Application?", JOptionPane.YES_NO_OPTION);
            if (optionResult == JOptionPane.NO_OPTION) {
                return chooseSeparator();
            } else {
                System.exit(0);
                return null;
            }
        }
    }
}
