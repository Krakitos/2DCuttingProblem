package etu.polytech.optim.genetic.strategies;

import etu.polytech.optim.genetic.lang.Chromosome;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 07/04/2015.
 */
public interface MutationPolicy {

    double DEFAULT_MUTATION_RATE = 0.1d;

    /**
     * Number between 0.0 and 1.0 representing the probability of a chromosome to mutate
     * @return
     */
    default double mutationRate(){
        return DEFAULT_MUTATION_RATE;
    }

    /**
     * Mutate the chromosome and return the mutate one
     * @param chromosome
     * @return
     */
    Chromosome mutate(@NotNull final Chromosome chromosome);
}
