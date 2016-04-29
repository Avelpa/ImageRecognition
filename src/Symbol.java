
import java.awt.image.BufferedImage;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kobed6328
 */
public class Symbol {
    
    private static final HashMap<String, String> SYMBOL_IDENTIFIERS;
    
    static {
        SYMBOL_IDENTIFIERS = new HashMap();
        SYMBOL_IDENTIFIERS.put("x", "*");
        SYMBOL_IDENTIFIERS.put("div", "/");
        SYMBOL_IDENTIFIERS.put("gt", ">");
        SYMBOL_IDENTIFIERS.put("lt", "<");
    }
    
    private String name;
    private String displayString;
    private HashMap<String, Double> probabilities;
    private double prob;
    private BufferedImage img;
    
    public Symbol(HashMap<String, Double> probabilities, BufferedImage img)
    {  
        this.probabilities = probabilities;
        identifySymbol(probabilities);
        if (SYMBOL_IDENTIFIERS.containsKey(name))
        {
            displayString = SYMBOL_IDENTIFIERS.get(name);
        } else {
            displayString = name;
        }
        this.img = img;
    }
    
    private void identifySymbol(HashMap<String, Double> probabilities)
    {
        for (String sym: probabilities.keySet())
        {
            if (probabilities.get(sym) > prob)
            {
                name = sym;
                prob = probabilities.get(sym);
            }
        }
    }
    
    public String getDisplayString()
    {
        return displayString;
    }
    
    public double getProb()
    {
        return prob;
    }
    
    public String getName()
    {
        return name;
    }
    
    public BufferedImage getImage()
    {
        return img;
    }
}
