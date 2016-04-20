
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
     * sets the bound values for a symbol in an image
     * @param canvas the entire image
     */
    private static void setBounds(BufferedImage canvas){
        left = Integer.MAX_VALUE;
        right = 0;
        top = Integer.MAX_VALUE;
        bottom = 0;
        
        // iterate through every pixel and if it is not white, modify the bounds accordingly...
        for (int x = 0; x < canvas.getWidth(); x ++){
            for (int y = 0; y < canvas.getHeight(); y ++){
                if (canvas.getRGB(x, y) != Color.WHITE.getRGB()){
                    if (x < left)
                        left = x;
                    else if (x > right)
                        right = x;
                    if (y < top)
                        top = y;
                    else if (y > bottom)
                        bottom = y;
                }
            }
        }
    }
    
    public static BufferedImage cropImage(BufferedImage img){
        setBounds(img);
        return img.getSubimage(left, top, right-left+1, bottom-top+1);
    }
    
    @Override
    public String toString(){
        return "left: " + left +  " right: " + right + " top: " + top + " bottom: " + bottom;
    }
}
