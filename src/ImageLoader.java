
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
public class ImageLoader {
    
    public static BufferedImage loadImage(String filepath){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filepath));
        } catch (IOException e) {
            System.err.println(e);
        }
        return img;
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
}
