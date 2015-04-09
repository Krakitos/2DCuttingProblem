package etu.polytech.optim.genetic.strategies;

import etu.polytech.optim.genetic.lang.Population;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public interface SelectionPolicy {
    /**
     * Select two chromosomes among the population
     * @param population
     * @return
     */
    ChromosomePair select(@NotNull final Population population);
}
