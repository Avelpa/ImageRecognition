
import java.awt.image.BufferedImage;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        NumberReader reader = new NumberReader();
        reader.init();
        
        BufferedImage testImg = ImageLoader.loadImage("images/tests/test.png");
        HashMap<Integer, Double> probs = reader.getProbs(testImg);
        
        for (Integer i: probs.keySet()){
            System.out.print(i + ": " + probs.get(i) + ", ");
            System.out.println();
        }
        int num = reader.getResult(probs);
        System.out.println("Therefore... it's a " + num);
        
        reader.consolidateResult(testImg, num);
    }
}
