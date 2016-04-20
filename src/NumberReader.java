
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

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
    
    private HashMap<String, BufferedImage[]> examples;
    
    /**
     * Loads all of the examples.
     */
    public void init(){
        examples = new HashMap();
        
        // get all of the example folders
        File[] exampleFolders = FileManager.getFileList("images/examples");
        
        // parse each folder for the actual example images
        for (File folder: exampleFolders){
            String exampleName = folder.getName();
            BufferedImage[] imgs = new BufferedImage[FileManager.countFiles(folder)];
            // get each example image in the folder
            File[] files = folder.listFiles();
            for (int i = 0; i < imgs.length; i ++){
                imgs[i] = FileManager.loadImage(files[i].getPath());
            }
            // populate examples hashmap
            examples.put(exampleName, imgs);
        }
    }
    
    /**
     * Check each example image against input
     * @param img image to be tested
     * @return a collection of all the possibilities with a percentage confidence for each possibility
     */
    public HashMap<String, Double> getProbs(BufferedImage img){
        
        HashMap<String, Double> probabilities = new HashMap();
        
        img = Bounds.cropImage(img);
        
        // loop through all possible symbols
        for (String symbol: examples.keySet()){
            // best match for current example symbol
            double maxProb = 0d;
            // loop through all examples of current symbol
            for (BufferedImage example: examples.get(symbol)){
                double prob = analyze(example, img);
                if (prob > maxProb)
                    maxProb = prob;
            }
            probabilities.put(symbol, maxProb);
        }
        return probabilities;
    }
    
    
    /**
     * If just all pixels equal, then bias towards one (cause the most "correct" whites)
     * If just black, then bias away from one (cause the least "correct" blacks)
     */
    private double analyze(BufferedImage example, BufferedImage test){
        
        double probAllPixels = 0d;
        
        for (int x = 0; x < test.getWidth(); x ++){
            for (int y = 0; y < test.getHeight(); y ++){
                probAllPixels += analyzePixel(example, test, x, y);
            }
        }
        
        return probAllPixels/(test.getWidth()*test.getHeight());
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
        return (double)(example.getWidth()*example.getHeight()-offset)/(example.getWidth()*example.getHeight());
    }
    
    public String getResult(HashMap<String, Double> probs){
        //double min = Double.MAX_VALUE;
        double max = 0d;
        String symbol = "";
        
        for (String str: probs.keySet()){
            if (probs.get(str) > max){
                max = probs.get(str);
                symbol = str;
            }
        }
        return symbol;
    }
    
    public void remember(BufferedImage test, String symbol, HashMap<String, Double> probs){
        
        if (probs.get(symbol) == 1)
            return;
        
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the symbol I should have guessed\n>> ");
        String realSym = input.next();
        
        FileManager.assertFolderExists("images/examples/" + realSym);
        int newIndex = FileManager.countFiles("images/examples/" + realSym);
        
        //FileManager.duplicateFile("images/tests/test.png", "images/examples/" + realSym + "/" + realSym + "_" + newIndex + ".png");
        FileManager.saveImage(test, "images/examples/" + realSym + "/" + realSym + "_" + newIndex + ".png");
    }
    
}
