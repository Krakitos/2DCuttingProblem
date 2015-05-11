package etu.polytech.optim.genetic.lang.populations;

import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Population;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 07/04/2015.
 */
public class FixedSizePopulation implements Population {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Random RANDOM = new Random();

    //private final List<Chromosome> chromosomes;
    private final SortedSet<Chromosome> chromosomes;
    private final int maxSize;

    private Map<String, AtomicLong> hitCounter;

    public FixedSizePopulation(final int maxSize) {
        LOGGER.info("populations fixed to {}", maxSize);

        this.maxSize = maxSize;
        //chromosomes = new ArrayList<>(maxSize);
        chromosomes = Collections.synchronizedSortedSet(new TreeSet<>((o1, o2) -> o1.equals(o2) ? 0 : o1.fitness() < o2.fitness() ? -1 : 1));

        hitCounter = new HashMap<>();
    }

    /**
     * Add the specified chromosome to the population.
     * If the size of the population has reached to maxSize, eviction is done
     * removing chromosomes with the worst fitness
     * @param chromosome
     */
    public void addChromosome(@NotNull final Chromosome chromosome){
        chromosomes.removeIf(c -> c.equals(chromosome) && c.fitness() > chromosome.fitness());


        if (chromosomes.size() == maxSize) {
            //Collections.sort(chromosomes);
            Chromosome worst = chromosomes.last();
            chromosomes.remove(worst);

            LOGGER.debug("Eviction of chromosome {} with fitness {}", worst, worst.fitness());
        }

        String id = chromosome.toString();
        if(!hitCounter.containsKey(id))
            hitCounter.put(id, new AtomicLong(1l));
        else
            hitCounter.get(id).incrementAndGet(); //Add to the hit counter to see frequencies of values hit

        chromosomes.add(chromosome);
    }

    /**
     * Return a random chromosome
     * @return
     */
    public Chromosome getRandom(){
        //return chromosomes.get(RANDOM.nextInt(chromosomes.size()));

        int index = Math.max(0, RANDOM.nextInt(chromosomes.size()) - 1);
        int i = 0;

        Iterator<Chromosome> it = chromosomes.iterator();

        while (++i < index - 1) {
            it.next();
        }

        return it.next();
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
    public Chromosome fittestChromosome() {
        return size() == 0 ? null : chromosomes.first();
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
