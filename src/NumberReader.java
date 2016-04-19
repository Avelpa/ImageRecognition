
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
    
    private boolean needConsolidate = true;
    
    private HashMap<Integer, BufferedImage[]> examples;
    public void init(){
        examples = new HashMap();
        File[] exampleList = (new File("images/examples")).listFiles();
        for (File file: exampleList){
            int i = Integer.parseInt(file.toPath().toString().replaceFirst("^.*\\D",""));
            int numExamples = ImageLoader.countFiles(file);
            BufferedImage[] imgs = new BufferedImage[numExamples];
            for (int j = 0; j < numExamples; j ++){
                imgs[j] = ImageLoader.loadImage("images/examples/" + i + "/" + i + "_" + j + ".png");
            }
            examples.put(i, imgs);
        }
        /*
        for (int i = 0; i < numNums; i ++){
            
        }*/
    }
    
    public HashMap<Integer, Double> getProbs(BufferedImage img){
        HashMap<Integer, Double> probabilities = new HashMap();
        for (Integer num: examples.keySet()){
            double maxProb = 0d;
            for (BufferedImage example: examples.get(num)){
                double prob = analyze(example, img);
                if (prob > maxProb)
                    maxProb = prob;
            }
            //probabilities.put(num, totalProbs/examples.get(num).length);
            probabilities.put(num, maxProb);
        }
        return probabilities;
    }
    
    public void consolidateResult(int num){
        
        if (!needConsolidate)
            return;
        
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number I should have guessed\n>> ");
        int realNum = input.nextInt();
        
        File exampleFolder = new File("images/examples/" + realNum);
        if(!exampleFolder.exists()) {
            exampleFolder.mkdirs();
        } 
        
        int newIndex = ImageLoader.countFiles("images/examples/" + realNum);
        
        try {
            Files.copy((new File("images/tests/test.png")).toPath(), new File((new File("images/examples/" + realNum + "/" + realNum + "_" + newIndex + ".png")).getAbsolutePath()).toPath(), StandardCopyOption.REPLACE_EXISTING);
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
        if (probSum/numBlack*100 == 100.0)
            needConsolidate = false;
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
