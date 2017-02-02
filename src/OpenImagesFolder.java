import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class OpenImagesFolder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File imgsFolder = new File("images/").getAbsoluteFile();
            Desktop.getDesktop().open(imgsFolder);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}