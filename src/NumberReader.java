
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Scanner;
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
public class NumberReader {
    
    private HashMap<Integer, BufferedImage[]> examples;
    public void init(){
        examples = new HashMap();
        int numNums = ImageLoader.countFiles("images/examples/");
        
        for (int i = 0; i < numNums; i ++){
            int numExamples = ImageLoader.countFiles("images/examples/" + i);
            BufferedImage[] imgs = new BufferedImage[numExamples];
            for (int j = 0; j < numExamples; j ++){
                imgs[j] = ImageLoader.loadImage("images/examples/" + i + "/" + i + "_" + j);
            }
            examples.put(i, imgs);
        }
    }
    
    public HashMap<Integer, Double> getProbs(BufferedImage img){
        HashMap<Integer, Double> probabilities = new HashMap();
        for (Integer num: examples.keySet()){
            double totalProbs = 0d;
            for (BufferedImage example: examples.get(num)){
                double prob = analyze(example, img);
                totalProbs += prob;
            }
            probabilities.put(num, totalProbs/examples.get(num).length);
        }
        return probabilities;
    }
    
    public void consolidateResult(BufferedImage img, int num){
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number I should have guessed\n>> ");
        int realNum = input.nextInt();
        try {
            Files.copy((new File("images/tests/test.png")).toPath(), (new File("images/examples/testCopy/test.png")).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(NumberReader.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public int getResult(HashMap<Integer, Double> probs){
        
        //double min = Double.MAX_VALUE;
        double max = 0d;
        int num = -1;
        
        for (Integer i: probs.keySet()){
            if (probs.get(i) > max){
                max = probs.get(i);
                num = i;
            }
        }
        
        return num;
    }
}
