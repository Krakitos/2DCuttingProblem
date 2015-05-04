package etu.polytech.optim.genetic.strategies.mutation;

import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.genetic.strategies.MutationPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Morgan on 04/05/2015.
 */
public class MultipointMutation implements MutationPolicy {

    private final Random RANDOM = new Random();
    private final int maxGeneValue;
    private final double mutationRate;

    public MultipointMutation(int maxGeneValue){
        this(maxGeneValue, DEFAULT_MUTATION_RATE);
    }
    public MultipointMutation(int maxGeneValue, double mutationRate){
        this.mutationRate = mutationRate;
        this.maxGeneValue = maxGeneValue;
    }


    @Override
    public Chromosome mutate(@NotNull Chromosome chromosome) {
        int[] c = new int[chromosome.length()];

        Arrays.setAll(c, i -> {
            if (RANDOM.nextGaussian() <= mutationRate())
                return 1 + RANDOM.nextInt(maxGeneValue);
            else
                return chromosome.genomeAt(i);
        });

        return new FixedSizeChromosome(c);
    }

    @Override
    public double mutationRate() {
        return mutationRate;
    }
}
