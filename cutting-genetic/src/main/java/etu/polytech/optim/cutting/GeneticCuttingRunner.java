package etu.polytech.optim.cutting;

import etu.polytech.opti.layout.CuttingPackager;
import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.cutting.fitness.SimplexFitnessEvaluator;
import etu.polytech.optim.cutting.lang.GeneticSolution;
import etu.polytech.optim.genetic.GeneticAlgorithm;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Population;
import etu.polytech.optim.genetic.strategies.CrossoverPolicy;
import etu.polytech.optim.genetic.strategies.MutationPolicy;
import etu.polytech.optim.genetic.strategies.SelectionPolicy;
import etu.polytech.optim.genetic.strategies.StoppingCondition;
import etu.polytech.optim.genetic.utils.observers.GeneticAlgorithmObserver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by Morgan on 08/04/2015.
 */
public class GeneticCuttingRunner implements GeneticAlgorithmObserver {

    private final GeneticAlgorithm genetic;

    public GeneticCuttingRunner(@NotNull final CuttingConfiguration configuration,
                                @NotNull final CuttingPackager packager,
                                @NotNull final StoppingCondition stop,
                                @NotNull final SelectionPolicy select,
                                @NotNull final CrossoverPolicy crossover,
                                @NotNull final MutationPolicy mutate) {

        genetic = new GeneticAlgorithm(stop, select, crossover, mutate, new SimplexFitnessEvaluator(configuration, packager));
    }


    public CuttingSolution start(){
        final Population population = new Population(1000);

        genetic.addObserver(this);

        final Chromosome fittest = genetic.start(population);
        final GeneticSolution geneticSolution = null;

        return geneticSolution.solution();
    }

    @Override
    public void onGenerationStarted() {

    }

    @Override
    public void onGenerationNewIteration(long index) {

    }

    @Override
    public void onBetterSolutionFound(@NotNull Chromosome chromosome) {

    }

    @Override
    public void onGenerationFinished(@NotNull Chromosome chromosome) {

    }

    public static class Builder {
        private CuttingConfiguration configuration;
        private CuttingPackager packager;
        private StoppingCondition stoppingCondition;
        private SelectionPolicy selectionPolicy;
        private CrossoverPolicy crossoverPolicy;
        private MutationPolicy mutationPolicy;

        public CuttingConfiguration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(CuttingConfiguration configuration) {
            this.configuration = configuration;
        }

        public CuttingPackager getPackager() {
            return packager;
        }

        public void setPackager(CuttingPackager packager) {
            this.packager = packager;
        }

        public StoppingCondition getStoppingCondition() {
            return stoppingCondition;
        }

        public void setStoppingCondition(StoppingCondition stoppingCondition) {
            this.stoppingCondition = stoppingCondition;
        }

        public SelectionPolicy getSelectionPolicy() {
            return selectionPolicy;
        }

        public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
            this.selectionPolicy = selectionPolicy;
        }

        public CrossoverPolicy getCrossoverPolicy() {
            return crossoverPolicy;
        }

        public void setCrossoverPolicy(CrossoverPolicy crossoverPolicy) {
            this.crossoverPolicy = crossoverPolicy;
        }

        public MutationPolicy getMutationPolicy() {
            return mutationPolicy;
        }

        public void setMutationPolicy(MutationPolicy mutationPolicy) {
            this.mutationPolicy = mutationPolicy;
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
