package etu.polytech.optim.genetic.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by Morgan on 07/04/2015.
 */
public interface Population {

    /**
     * Add the specified chromosome to the population.
     * If the size of the population has reached to maxSize, eviction is done
     * removing chromosomes with the worst fitness
     * @param chromosome
     */
    void addChromosome(@NotNull final Chromosome chromosome);

    /**
     * Return a random chromosome
     * @return
     */
    Chromosome getRandom();


    /**
     * Return a random chromosome among the first limit fittest chromosomes
     * @param limit Nth fittest chromosome
     * @return
     */
    Chromosome getRandomAmongFittest(int limit);

    /**
     * Remove the specified chromosome from the population
     * @param chromosome
     */
    boolean removeChromosome(@NotNull final Chromosome chromosome);

    /**
     * Return the fittest chromosome
     * @return
     */
    Chromosome fittestChromosome();

    /**
     * Number of chromosome in the population
     * @return
     */
    int size();

    /**
     * Return the hits of each chromosomes
     * @return
     */
    Map<String, Long> hits();
}
