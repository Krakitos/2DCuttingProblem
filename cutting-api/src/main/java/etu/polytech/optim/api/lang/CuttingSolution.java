package etu.polytech.optim.api.lang;

/**
 * Created by Morgan on 11/03/2015.
 */

/**
 * Represents a solution for the cutting problem
 */
public class CuttingSolution {

    private final double[] patterns;
    private final int[][] layout;
    private final double fitness;

    public CuttingSolution(double[] points, int[][] layout, double fitness) {
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

    /**
     * Number of piece by pattern
     * @return
     */
    public int[][] layout(){
        return layout;
    }
}