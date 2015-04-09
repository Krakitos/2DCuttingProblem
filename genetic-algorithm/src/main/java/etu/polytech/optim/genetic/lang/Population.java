package etu.polytech.optim.genetic.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Morgan on 07/04/2015.
 */
public class Population {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Random RANDOM = new Random();

    private final List<Chromosome> chromosomes;
    private final int maxSize;

    public Population(final int maxSize) {
        LOGGER.info("Population fixed to {}", maxSize);

        this.maxSize = maxSize;
        chromosomes = new ArrayList<>(maxSize);
    }

    /**
     * Add the specified chromosome to the population.
     * If the size of the population has reached to maxSize, eviction is done
     * removing chromosomes with the worst fitness
     * @param chromosome
     */
    public void addChromosome(@NotNull final Chromosome chromosome){
        if(chromosomes.parallelStream().filter(second -> chromosome.equals(second)).count() == 0) {

            LOGGER.info("Adding chromosome {} to the population ({} / {})", chromosome, chromosomes.size(), maxSize);

            if (chromosomes.size() == maxSize) {
                Collections.sort(chromosomes);
                Chromosome worst = chromosomes.remove(chromosomes.size() - 1);

                LOGGER.debug("Evictiong of chromosome {} with fitness {}", worst, worst.fitness());
            }

            chromosomes.add(chromosome);
        }else{
            LOGGER.debug("Chromosome {} already present in the population, skipping add to the list", chromosome);
        }
    }

    /**
     * Return a random chromosome
     * @return
     */
    public Chromosome getRandom(){
        return chromosomes.get(RANDOM.nextInt(chromosomes.size()));
    }

    /**
     * Remove the specified chromosome from the population
     * @param chromosome
     */
    public boolean removeChromosome(@NotNull final Chromosome chromosome){
        return chromosomes.remove(chromosome);
    }

    /**
     * Return the fittest chromosome
     * @return
     */
    public @NotNull Chromosome fittestChromosome() {
        Collections.sort(chromosomes);
        return chromosomes.get(0);
    }

    /**
     * Number of chromosome in the population
     * @return
     */
    public int size() {
        return chromosomes.size();
    }
}
