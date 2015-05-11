package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents a solution for the cutting problem
 */
public class CuttingSolution {

    private final double[] patterns;
    private final List<Collection<CuttingLayoutElement>> layout;
    private final double fitness;
    private Map<String,Long> hitsMap;

    public CuttingSolution(double[] points, List<Collection<CuttingLayoutElement>> layout, double fitness) {
        this.patterns = points;
        this.layout = layout;
        this.fitness = fitness;
    }

    /**
     * Fitness of the solution
     * Total price of the solution
     * @return
     */
    public double fitness(){
        return fitness;
    }

    /**
     * Pattern and the number of time to print the pattern
     * @return
     */
    public double[] patterns(){
        return patterns;
    }

    public Map<String, Long> getHitsMap() {
        return hitsMap;
    }

    public void setHitsMap(Map<String, Long> hitsMap) {
        this.hitsMap = hitsMap;
    }

    /**
     * Number of piece by pattern
     * @return
     */
    public List<Collection<CuttingLayoutElement>> layout(){
        return layout;
    }
}