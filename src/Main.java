
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
        // create number reader
        NumberReader reader = new NumberReader();
        reader.init();
        
        BufferedImage testImg = FileManager.loadImage("images/tests/test.png");
        HashMap<String, Double> probs = reader.getProbs(testImg);
        
        for (String symbol: probs.keySet()){
            System.out.print(symbol + ": " + probs.get(symbol) + ", ");
            System.out.println();
        }
        String symbol = reader.getResult(probs);
        System.out.println("Therefore... it's a " + symbol);
        
        reader.remember(symbol, probs);
    }
}
