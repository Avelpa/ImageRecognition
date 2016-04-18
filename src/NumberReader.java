
import java.awt.Color;
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
public class NumberReader {
    
    private HashMap<Integer, BufferedImage> examples;
    public void init(){
        examples = new HashMap();
        examples.put(0, ImageLoader.loadImage("images/examples/zero.png"));
        examples.put(1, ImageLoader.loadImage("images/examples/one.png"));
        examples.put(2, ImageLoader.loadImage("images/examples/two.png"));
    }
    
    public HashMap<Integer, Double> readNumber(BufferedImage img){
        HashMap<Integer, Double> probabilities = new HashMap();
        for (Integer num: examples.keySet()){
            double prob = analyze(examples.get(num), img);
            probabilities.put(num, prob);
        }
        return probabilities;
    }
    
    private double analyze(BufferedImage example, BufferedImage test){
        
        double probSum = 0d;
        int numBlack = 0;
        
        for (int x = 0; x < test.getWidth(); x ++){
            for (int y = 0; y < test.getHeight(); y ++){
                if (test.getRGB(x, y) == Color.BLACK.getRGB()){
                    probSum += analyzePixel(example, test, x, y);
                    numBlack ++;
                }
            }
        }
        return probSum/numBlack*100;
        //return probSum*example.getWidth()*example.getHeight();
    }
    
    private double analyzePixel(BufferedImage example, BufferedImage test, int x, int y){
        
        int offset = 0;
        
        while (x - offset >= 0 || x + offset < example.getWidth() && y - offset >= 0 && y + offset < example.getHeight()){
            
            if (x-offset >= 0){
                if (test.getRGB(x, y) == example.getRGB(x-offset, y))
                    break;
            }
            if (x+offset < example.getWidth()){
                if (test.getRGB(x, y) == example.getRGB(x+offset, y))
                    break;
            }
            if (y-offset >= 0){
                if (test.getRGB(x, y) == example.getRGB(x, y-offset))
                    break;
            }
            if (y+offset < example.getHeight()){
                if (test.getRGB(x, y) == example.getRGB(x, y+offset))
                    break;
            }
            offset ++;
        }
        //return (double)offset/Math.pow(2*offset+1, 2);
        return (double)(example.getWidth()*example.getHeight()-offset)/(example.getWidth()*example.getHeight());
        //return (double)(example.getWidth()-offset)/example.getWidth();
    }
}
