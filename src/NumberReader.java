
import java.awt.Color;
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
    public double analyze(BufferedImage example, BufferedImage test){
        
        // get size intersect
        int smallestWidth = getMin(example.getWidth(), test.getWidth());
        int smallestHeight = getMin(example.getHeight(), test.getHeight());
        
        // scale each image to the intersect
        double[][] exampleScaled = scaleImage(example, smallestWidth, smallestHeight);
        double[][] testScaled = scaleImage(test, smallestWidth, smallestHeight);
        
        double score = smallestWidth * smallestHeight;
        //double penalty = 0d;
        for (int y = 0; y < testScaled.length; y ++){
            for (int x = 0; x < testScaled[y].length; x ++){
                //score -= Math.abs(exampleScaled[y][x]-testScaled[y][x]);
                score -= analyzePixel(exampleScaled, testScaled, x, y);
            }
        }
        score /= smallestWidth*smallestHeight;
        
        // this chunk is questionable
        //double testScaleWidth = (double)testScaled[0].length/smallestWidth;
        //double testScaleHeight = (double)testScaled.length/smallestHeight;
        
        //score /= testScaleWidth*testScaleHeight;
        
        return score;
    }
    
    public double[][] scaleImage(BufferedImage img, int width, int height){
        
        double[][] scaledImg = new double[height][width];
        double scaleWidth = (double)width/img.getWidth();
        double scaleHeight = (double)height/img.getHeight();
        
        int scaledX = 0, scaledY = 0;
        
        int xPrev = scaledX, yPrev = scaledY;
        int occurrence = 0;
        
        double currentCell = 0;
        
        for (int y = 0; y < img.getHeight(); y ++){
            for (int x = 0; x < img.getWidth(); x ++){
                scaledX = (int)(x*scaleWidth);
                scaledY = (int)(y*scaleHeight);
                
                if (xPrev != scaledX || yPrev != scaledY){
                    currentCell /= occurrence;
                    scaledImg[yPrev][xPrev] = currentCell;
                    currentCell = 0;
                    occurrence = 0;
                    xPrev = scaledX;
                    yPrev = scaledY;
                }
                occurrence ++;
                if (img.getRGB(x, y) == Color.BLACK.getRGB())
                    currentCell ++;
            }
        }
        currentCell /= occurrence;
        scaledImg[scaledY][scaledX] = currentCell;
        
        return scaledImg;
    }
    
    private int getMin(int num1, int num2){
        if (num1 < num2)
            return num1;
        return num2;
    }
    
    public double analyzePixel(double[][] example, double[][] test, int x, int y){
        int offset = 0;
        double penalty = 0;
        
        boolean done = false;
        while (x - offset >= 0 || x + offset < example[0].length || y - offset >= 0 || y + offset < example.length){
            
            // left
            if (x-offset >= 0){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y][x-offset]);
                    done = true;
                }
            }
            // right
            if (x+offset < example[0].length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y][x+offset]);
                    done = true;
                }
                
            }
            // top
            if (y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x]);
                    done = true;
                }
            }
            // bottom
            if (y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x]);
                    done = true;
                }
            }
            // top left
            if (x-offset >= 0 && y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x-offset]);
                    done = true;
                }
            }
            // top right
            if (x+offset < example[0].length && y-offset >= 0 && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y-offset][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y-offset][x+offset]);
                    done = true;
                }
            }
            // bottom left
            if (x-offset >= 0 && y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x-offset])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x-offset]);
                    done = true;
                }
            }
            // bottom right
            if (x+offset < example[0].length && y+offset < example.length && !done){
                if (Math.ceil(test[y][x]) == Math.ceil(example[y+offset][x+offset])){
                    penalty += Math.abs(test[y][x]-example[y+offset][x+offset]);
                    done = true;
                }
            }
            
            if (done)
                break;
            
            penalty ++; 
            offset ++;
        }
        //return (double)(example.getWidth()*example.getHeight()-offset)/(example.getWidth()*example.getHeight());
        return penalty;
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
        
        if (symbol.isEmpty())
            symbol = "unknown";
        return symbol;
    }
    
    public void remember(BufferedImage test, String symbol, HashMap<String, Double> probs){
        
        if (probs.containsKey(symbol) && probs.get(symbol) == 1)
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
