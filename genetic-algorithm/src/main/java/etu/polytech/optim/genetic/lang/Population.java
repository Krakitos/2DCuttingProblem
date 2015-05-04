package etu.polytech.optim.genetic.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 07/04/2015.
 */
public class Population {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Random RANDOM = new Random();

    private final List<Chromosome> chromosomes;
    private final int maxSize;

    private Map<String, AtomicLong> hitCounter;

    public Population(final int maxSize) {
        LOGGER.info("Population fixed to {}", maxSize);

        this.maxSize = maxSize;
        chromosomes = new ArrayList<>(maxSize);

        hitCounter = new HashMap<>();
    }

    /**
     * Add the specified chromosome to the population.
     * If the size of the population has reached to maxSize, eviction is done
     * removing chromosomes with the worst fitness
     * @param chromosome
     */
    public void addChromosome(@NotNull final Chromosome chromosome){
        Optional<Chromosome> c = chromosomes.parallelStream().filter(chromosome::equals).max((Comparator.comparingDouble(Chromosome::fitness)));
        if(c.isPresent()){
            if(c.get().fitness() > chromosome.fitness()){
                chromosomes.remove(c.get());

                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Chromosome {} already present in the population fitness {} > {}, replace the old one",
                            chromosome, c.get().fitness(), chromosome.fitness());
            }else{
                return;
            }
        }else{
            hitCounter.put(chromosome.toString(), new AtomicLong(0l));
        }


        if (chromosomes.size() == maxSize) {
            Collections.sort(chromosomes);
            Chromosome worst = chromosomes.remove(chromosomes.size() - 1);

            LOGGER.debug("Evictiong of chromosome {} with fitness {}", worst, worst.fitness());
        }

        //Add to the hit counter to see frequencies of values hit
        hitCounter.get(chromosome.toString()).incrementAndGet();

        chromosomes.add(chromosome);
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

    /**
     * Return the hits of each chromosomes
     * @return
     */
    public Map<String, Long> hits(){
        return hitCounter.entrySet().parallelStream().collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> e.getValue().longValue()));
    }
}
