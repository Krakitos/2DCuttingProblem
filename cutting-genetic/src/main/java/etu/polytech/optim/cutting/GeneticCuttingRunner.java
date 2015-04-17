package etu.polytech.optim.cutting;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.api.observers.CuttingEngineObservable;
import etu.polytech.optim.api.observers.ProgressObservable;
import etu.polytech.optim.cutting.fitness.SimplexFitnessEvaluator;
import etu.polytech.optim.cutting.lang.GeneticSolution;
import etu.polytech.optim.cutting.lang.stop.StoppingConditionObservable;
import etu.polytech.optim.genetic.GeneticAlgorithm;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Population;
import etu.polytech.optim.genetic.lang.chromosomes.FixedSizeChromosome;
import etu.polytech.optim.genetic.strategies.CrossoverPolicy;
import etu.polytech.optim.genetic.strategies.MutationPolicy;
import etu.polytech.optim.genetic.strategies.SelectionPolicy;
import etu.polytech.optim.genetic.strategies.StoppingCondition;
import etu.polytech.optim.genetic.utils.observers.GeneticAlgorithmObserver;
import etu.polytech.optim.layout.CuttingPackager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Morgan on 08/04/2015.
 */
public class GeneticCuttingRunner extends CuttingEngineObservable implements GeneticAlgorithmObserver {

    private final CuttingConfiguration configuration;
    private final CuttingPackager packager;
    private final GeneticAlgorithm genetic;
    private final SimplexFitnessEvaluator evaluator;

    private final ProgressObservable progressObservable;

    public GeneticCuttingRunner(@NotNull final CuttingConfiguration configuration,
                                @NotNull final CuttingPackager packager,
                                @NotNull final StoppingConditionObservable stop,
                                @NotNull final SelectionPolicy select,
                                @NotNull final CrossoverPolicy crossover,
                                @NotNull final MutationPolicy mutate) {

        this.configuration = configuration;
        this.packager = packager;
        this.progressObservable = stop;
        this.evaluator = new SimplexFitnessEvaluator(configuration, packager);
        this.genetic = new GeneticAlgorithm(stop, select, crossover, mutate, evaluator);
    }


    public CuttingSolution start(){
        final Population population = new Population(1000);

        initPopulation(population);

        genetic.addObserver(this);

        final Chromosome fittest = genetic.start(population);
        final GeneticSolution solution = evaluator.computeFitness(fittest);

        assert solution != null : "Invalid null solution";

        fireGenerationFinished(solution.solution());

        return solution.solution();
    }

    private void initPopulation(Population population) {
        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            int[] rep = new int[configuration.elements().size()];
            Arrays.setAll(rep, index -> 1 + random.nextInt(3));

            Chromosome c = new FixedSizeChromosome(rep);
            c.fitness(evaluator.computeFitness(c).fitness());

            population.addChromosome(c);
        }
    }

    @Override
    public void onGenerationStarted() {
        fireGenerationStarted();
    }

    @Override
    public void onGenerationProgress() {
        fireGenerationProgress(progressObservable);
    }

    @Override
    public void onBetterSolutionFound(long iteration, @NotNull Chromosome chromosome) {
        fireNewSolution(iteration, chromosome.fitness());
    }

    public static class Builder {
        private CuttingConfiguration configuration;
        private CuttingPackager packager;
        private StoppingConditionObservable stoppingCondition;
        private SelectionPolicy selectionPolicy;
        private CrossoverPolicy crossoverPolicy;
        private MutationPolicy mutationPolicy;

        public CuttingConfiguration getConfiguration() {
            return configuration;
        }

        public Builder setConfiguration(CuttingConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public CuttingPackager getPackager() {
            return packager;
        }

        public Builder setPackager(CuttingPackager packager) {
            this.packager = packager;
            return this;
        }

        public StoppingCondition getStoppingCondition() {
            return stoppingCondition;
        }

        public Builder setStoppingCondition(StoppingConditionObservable stoppingCondition) {
            this.stoppingCondition = stoppingCondition;
            return this;
        }

        public SelectionPolicy getSelectionPolicy() {
            return selectionPolicy;
        }

        public Builder setSelectionPolicy(SelectionPolicy selectionPolicy) {
            this.selectionPolicy = selectionPolicy;
            return this;
        }

        public CrossoverPolicy getCrossoverPolicy() {
            return crossoverPolicy;
        }

        public Builder setCrossoverPolicy(CrossoverPolicy crossoverPolicy) {
            this.crossoverPolicy = crossoverPolicy;
            return this;
        }

        public MutationPolicy getMutationPolicy() {
            return mutationPolicy;
        }

        public Builder setMutationPolicy(MutationPolicy mutationPolicy) {
            this.mutationPolicy = mutationPolicy;
            return this;
        }

        public GeneticCuttingRunner build(){
            Objects.requireNonNull(configuration, "No Cutting Configuration found");
            Objects.requireNonNull(packager, "No Cutting Packager found");
            Objects.requireNonNull(stoppingCondition, "Stopping condition has to be defined");
            Objects.requireNonNull(selectionPolicy, "Selection policy has to be defined");
            Objects.requireNonNull(crossoverPolicy, "Crossover operator has to be defined");
            Objects.requireNonNull(mutationPolicy, "Mutation operator has to be defined");

            return new GeneticCuttingRunner(configuration, packager, stoppingCondition, selectionPolicy, crossoverPolicy, mutationPolicy);
        }
    }
}
