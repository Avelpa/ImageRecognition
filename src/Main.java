
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
        
        HashMap<Integer, Double> probs = reader.readNumber(ImageLoader.loadImage("images/tests/one.png"));
        displayResults(probs);
    }
    
    private static void displayResults(HashMap<Integer, Double> results){
        
        //double min = Double.MAX_VALUE;
        double max = 0d;
        int num = -1;
        
        for (Integer i: results.keySet()){
            if (results.get(i) > max){
                max = results.get(i);
                num = i;
            }
            System.out.print(i + ": " + results.get(i) + ", ");
            System.out.println();
        }
        
        System.out.println("Therefore... it's a " + num);
    }
    
}
