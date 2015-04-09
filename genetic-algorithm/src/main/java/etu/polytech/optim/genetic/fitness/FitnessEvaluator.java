package etu.polytech.optim.genetic.fitness;

import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Fitness;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface FitnessEvaluator<T extends Fitness>{
    /**
     * Compute the fitness of the specified chromosome
     * @param c
     * @return
     */
    T computeFitness(@NotNull final Chromosome c);
}
