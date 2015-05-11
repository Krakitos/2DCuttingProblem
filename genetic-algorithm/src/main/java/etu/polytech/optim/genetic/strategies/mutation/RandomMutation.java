package etu.polytech.optim.genetic.strategies.mutation;

import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.genetic.strategies.MutationPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Morgan on 08/04/2015.
 */
public class RandomMutation implements MutationPolicy {

    private final Random RANDOM = new Random();
    private final int maxGeneValue;

    public RandomMutation(int maxGeneValue){
        this.maxGeneValue = maxGeneValue;
    }

    @Override
    public Chromosome mutate(@NotNull Chromosome chromosome) {
        int index = RANDOM.nextInt(chromosome.length());
        int[] c = new int[chromosome.length()];

        Arrays.setAll(c, i -> {
            if(i == index)
                return 1 + RANDOM.nextInt(maxGeneValue);
            else
                return chromosome.genomeAt(i);
        });

        for (int i = 0; i < chromosome.length(); i++) {

        }

        return new FixedSizeChromosome(c);
    }
}
