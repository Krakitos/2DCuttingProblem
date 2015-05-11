package etu.polytech.optim.genetic.strategies;

import etu.polytech.optim.genetic.utils.ChromosomePair;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface CrossoverPolicy {

    double DEFAULT_CROSSOVER_RATE = 1.0d;

    /**
     * Number between 0.0 and 1.0 representing the probability for a pair of chromosome to cross
     * @return
     */
    default double crossoverRate(){
        return DEFAULT_CROSSOVER_RATE;
    }

    /**
     * Operate a crossover operator on the the two chromosomes
     * @param pair
     * @return
     */
    ChromosomePair crossover(@NotNull final ChromosomePair pair);
}
