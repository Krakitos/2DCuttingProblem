package etu.polytech.optim.genetic;

import etu.polytech.optim.genetic.fitness.FitnessEvaluator;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Fitness;
import etu.polytech.optim.genetic.lang.Population;
import etu.polytech.optim.genetic.strategies.CrossoverPolicy;
import etu.polytech.optim.genetic.strategies.MutationPolicy;
import etu.polytech.optim.genetic.strategies.SelectionPolicy;
import etu.polytech.optim.genetic.strategies.StoppingCondition;
import etu.polytech.optim.genetic.utils.ChromosomePair;
import etu.polytech.optim.genetic.utils.observers.GeneticAlgorithmEventDispatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Morgan on 07/04/2015.
 */
public class GeneticAlgorithm extends GeneticAlgorithmEventDispatcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SELECTION_MARKER = MarkerManager.getMarker("[SELECTION]");
    private static final Marker CROSSOVER_MARKER = MarkerManager.getMarker("[CROSSOVER]");
    private static final Marker MUTATION_MARKER = MarkerManager.getMarker("[MUTATION]");
    private static final Marker BACKTRACKING_MARKER = MarkerManager.getMarker("[BACKTRACKING]");
    private static final Marker SOLUTION_MARKER = MarkerManager.getMarker("[SOLUTION]");

    private static final Random RANDOM_GEN = new Random();

    private final StoppingCondition stop;
    private final SelectionPolicy selectOperator;
    private final CrossoverPolicy crossoverOperator;
    private final MutationPolicy mutationOperator;
    private final FitnessEvaluator<? extends Fitness> fitnessEvaluator;

    private AtomicLong start;
    private AtomicLong iterations;

    public GeneticAlgorithm(@NotNull final StoppingCondition stop, @NotNull final SelectionPolicy selectOperator,
                            @NotNull final CrossoverPolicy crossoverOperator, @NotNull final MutationPolicy mutationOperator,
                            @NotNull final FitnessEvaluator<? extends Fitness> fitnessEvaluator) {
        this.stop = stop;
        this.selectOperator = selectOperator;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.fitnessEvaluator = fitnessEvaluator;
        this.iterations = new AtomicLong(0);
        this.start = new AtomicLong(0);
    }

    public Chromosome start(@NotNull final Population population){

        assert population.size() >= 2 : "Invalid initial populations, need to have at least two chromosomes (got " + population.size() + ")";

        start.set(System.nanoTime());

        Chromosome fittest = population.fittestChromosome();

        fireNewSolutionFound(0, fittest);
        fireGenerationStarted();

        while(!stop.isReached()){

            iterations.incrementAndGet();

            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Starting iteration {}", iterations.get());
            }

            fireProgress();

            final ChromosomePair selection = selectOperator.select(population);

            if(LOGGER.isTraceEnabled())
                LOGGER.trace(SELECTION_MARKER, "Selected {{}, {}}", selection.first(), selection.second());

            if(RANDOM_GEN.nextGaussian() <= crossoverOperator.crossoverRate()){

                final ChromosomePair crossover = crossoverOperator.crossover(selection);

                final Fitness firstFitness = fitnessEvaluator.computeFitness(crossover.first());
                final Fitness secondFitness = fitnessEvaluator.computeFitness(crossover.second());

                //Check if the first chromosome evaluation has backtracked a better chromosome
                if(firstFitness.backtracked() != null && !firstFitness.backtracked().equals(crossover.first())){
                    if(LOGGER.isDebugEnabled())
                        LOGGER.debug(BACKTRACKING_MARKER, "Backtracking {} -> {}", crossover.first(), firstFitness.backtracked());

                    crossover.first(firstFitness.backtracked());
                }

                //Check if the first chromosome evaluation has backtracked a better chromosome
                if(secondFitness.backtracked() != null && !secondFitness.backtracked().equals(crossover.second())){
                    if(LOGGER.isDebugEnabled())
                        LOGGER.debug(BACKTRACKING_MARKER, "Backtracking {} -> {}", crossover.second(), secondFitness.backtracked());

                    crossover.second(secondFitness.backtracked());
                }

                if(LOGGER.isTraceEnabled())
                    LOGGER.trace(CROSSOVER_MARKER, "Crossing {{}, {}} -> {{}, {}}", selection.first(), selection.second(), crossover.first(), crossover.second());

                crossover.first().fitness(firstFitness.fitness());
                crossover.second().fitness(secondFitness.fitness());

                population.addChromosome(crossover.first());
                population.addChromosome(crossover.second());

                crossover.forEach(c -> {
                    if (RANDOM_GEN.nextGaussian() <= mutationOperator.mutationRate()) {
                        Chromosome mutated = mutationOperator.mutate(c);
                        Fitness mutatedFitness = fitnessEvaluator.computeFitness(mutated);

                        if(LOGGER.isTraceEnabled())
                            LOGGER.trace(MUTATION_MARKER, "Mutated {} -> {}", c, mutated);

                        if(mutatedFitness.backtracked() != null && !mutatedFitness.backtracked().equals(mutated)){
                            if(LOGGER.isDebugEnabled())
                                LOGGER.debug(BACKTRACKING_MARKER, "Backtracking {} -> {}", mutated, mutatedFitness.backtracked());

                            mutated = mutatedFitness.backtracked();
                        }

                        mutated.fitness(mutatedFitness.fitness());

                        population.addChromosome(mutated);
                    }
                });

                final Chromosome fittestInPopulation = population.fittestChromosome();
                if(fittestInPopulation.compareTo(fittest) < 0){

                    LOGGER.info(SOLUTION_MARKER, "New solution found (previous : {} ({}), current {} ({}))",
                            fittest, fittest.fitness(), fittestInPopulation, fittestInPopulation.fitness());

                    fittest = fittestInPopulation;
                    fireNewSolutionFound(iterations.get(), fittest);
                }
            }
        }

        long duration = System.nanoTime() - start.get();

        LOGGER.info("Done after {} iterations ({}) -> Best ({}, {})", iterations.get(),
                String.format("%d hour, %d min, %d sec, %d ms", TimeUnit.NANOSECONDS.toHours(duration), TimeUnit.NANOSECONDS.toMinutes(duration),
                        TimeUnit.NANOSECONDS.toSeconds(duration), TimeUnit.NANOSECONDS.toMillis(duration)
                ), fittest, fittest.fitness()
        );

        return population.fittestChromosome();
    }
}
