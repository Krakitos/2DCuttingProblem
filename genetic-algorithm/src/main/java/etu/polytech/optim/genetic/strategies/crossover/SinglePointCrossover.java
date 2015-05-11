package etu.polytech.optim.genetic.strategies.crossover;

import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.genetic.strategies.CrossoverPolicy;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public class SinglePointCrossover implements CrossoverPolicy {

    @Override
    public ChromosomePair crossover(@NotNull ChromosomePair pair) {
        int[] c1 = new int[pair.first().length()];
        int[] c2 = new int[pair.second().length()];
        int pivot = c1.length >> 1;

        for (int i = 0; i < pair.first().length(); i++) {
            c1[i] = pair.first().genomeAt(i);
        }

        for (int i = 0; i < pair.second().length(); i++) {
            c2[i] = pair.second().genomeAt(i);
        }

        int[] c3 = new int[pivot + (c2.length - pivot)];
        System.arraycopy(c1, 0, c3, 0, pivot);
        System.arraycopy(c2, pivot, c3, pivot, c2.length - pivot);

        int[] c4 = new int[pivot + (c1.length - pivot)];
        System.arraycopy(c2, 0, c4, 0, pivot);
        System.arraycopy(c1, pivot, c4, pivot, c1.length - pivot);

        return new ChromosomePair(new FixedSizeChromosome(c3), new FixedSizeChromosome(c4));
    }
}
