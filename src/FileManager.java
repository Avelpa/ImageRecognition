
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class FileManager {
    
    public static BufferedImage loadImage(String filepath){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            System.err.println(e);
        }
        return img;
    }
    
    public static void assertFolderExists(String folderpath){
        File exampleFolder = new File(folderpath);
        if(!exampleFolder.exists()) {
            exampleFolder.mkdirs();
        } 
    }
    
    public static int countFiles(String filepath){
        int numFiles = 0;
        File[] listFiles = new File(filepath).listFiles();
        for (int i = 0; i < listFiles.length; i ++){
            if (!listFiles[i].getName().endsWith(".db"))
                numFiles ++;
        }
        
        return numFiles;
    }
    public static int countFiles(File file){
        int numFiles = 0;
        File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; i ++){
            if (!listFiles[i].getName().endsWith(".db"))
                numFiles ++;
        }
        
        return numFiles;
    }

    static void duplicateFile(String oldfile, String newfile) {
        try {
            Files.copy((new File(oldfile)).toPath(), new File(newfile).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(NumberReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static File[] getFileList(String imagesexamples) {
        return (new File("images/examples")).listFiles();
    }
}
