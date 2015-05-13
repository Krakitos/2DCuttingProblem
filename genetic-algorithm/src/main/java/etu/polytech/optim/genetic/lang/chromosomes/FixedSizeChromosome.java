package etu.polytech.optim.genetic.lang.chromosomes;

import etu.polytech.optim.genetic.lang.Chromosome;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by Morgan on 07/04/2015.
 */
public class FixedSizeChromosome implements Chromosome {

    public static final double NOT_COMPUTED_FITNESS = Double.NEGATIVE_INFINITY;

    private final int[] representation;
    private double fitness;

    public FixedSizeChromosome(final int[] representation) {
        this(representation, NOT_COMPUTED_FITNESS);
    }

    public FixedSizeChromosome(final int[] representation, double fitness) {
        this.representation = representation;
        this.fitness = fitness;


        for (int v : representation) {
            assert v != 0;
        }
    }

    @Override
    public double fitness() {
        return fitness;
    }

    @Override
    public void fitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public int genomeAt(int index) {
        assert index >= 0 && index < representation.length : "Out of range index : (" + index + "/" + representation.length + ")";
        return representation[index];
    }

    @Override
    public int length() {
        return representation.length;
    }

    @Override
    public int compareTo(Chromosome o) {
        return fitness == o.fitness() ? 0 : fitness < o.fitness() ? -1 : 1;
    }

    @Override
    public boolean equals(@NotNull Chromosome chromosome) {
        if(chromosome.length() != length()){
            return false;
        }

        for (int i = 0; i < representation.length; i++) {
            if(chromosome.genomeAt(i) != representation[i]){
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(representation).replaceAll(", ", "");
    }
}
