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

    /**
     * Use this property if you want to backtrack a solution into the population.
     * Useful the computation of the fitness showed an egal solution with less genes
     * @return NULL if no backtracking
     */
    default Chromosome backtracked(){
        return null;
    }
}
