package etu.polytech.optim.genetic.lang;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface Fitness {

    double INVALID_FITNESS = Double.MAX_VALUE;

    /**
     * Return the fitness
     * @return
     */
    double fitness();
}
