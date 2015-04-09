package etu.polytech.optim.genetic.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 07/04/2015.
 */
public interface Chromosome extends Comparable<Chromosome> {

    /**
     * Return the fitness of this chromosome
     * @return
     */
    double fitness();

    /**
     * Define the fitness of this chromosome
     * @param fitness
     */
    void fitness(final double fitness);

    /**
     * Return the genome a the specified index
     * @param index
     * @return
     */
    int genomeAt(final int index);

    /**
     * Return the length of the chromosome (number of genes)
     * @return
     */
    int length();

    /**
     * Indicate if two chromosome are equals based on their representation
     * @return
     */
    boolean equals(@NotNull final Chromosome chromosome);

    /**
     * Print the representation of the chromosome
     * @return
     */
    String toString();
}
