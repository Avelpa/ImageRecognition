
import java.awt.Color;
import java.awt.image.BufferedImage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Bounds {
    
    private static int left, right, bottom, top;
    
    /**
     * Finds the smallest possible bounding rectangle for the non-white pixels in an image
     * If the entire image is white, then right < left and top < bottom, which is an invalid combination
     *  - this is caught in cropImage
     * @param canvas the entire image
     */
    private static void setBounds(BufferedImage canvas){
        left = canvas.getWidth();
        right = 0;
        top = canvas.getHeight();
        bottom = 0;
        
        // iterate through every pixel and if it is not white, modify the bounds accordingly...
        for (int x = 0; x < canvas.getWidth(); x ++){
            for (int y = 0; y < canvas.getHeight(); y ++){
                if (canvas.getRGB(x, y) != Color.WHITE.getRGB()){
                    if (x < left)
                        left = x;
                    if (x > right)
                        right = x;
                    if (y < top)
                        top = y;
                    if (y > bottom)
                        bottom = y;
                }
            }
        }
    }
    
    /*
      Return the image cropped to the smallest bounding rectangle enclosing all non-white pixels
      If the image is purely white, return null
    */
    public static BufferedImage cropImage(BufferedImage img){
        Bounds.setBounds(img);
        if (left > right || bottom < top) {
            return null;
        }
        return img.getSubimage(left, top, right-left+1, bottom-top+1);
    }
}
