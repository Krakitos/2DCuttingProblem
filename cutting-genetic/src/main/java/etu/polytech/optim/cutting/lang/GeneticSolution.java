package etu.polytech.optim.cutting.lang;

import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.genetic.lang.Chromosome;
import etu.polytech.optim.genetic.lang.Fitness;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public class GeneticSolution implements Fitness {

    private final CuttingSolution solution;
    private final Chromosome backtrack;

    /**
     * Constructor without backtracking
     * @param solution
     */
    public GeneticSolution(@NotNull final CuttingSolution solution) {
        this(solution, null);
    }

    public GeneticSolution(@NotNull final CuttingSolution solution, Chromosome backtrack){
        this.solution = solution;
        this.backtrack = backtrack;
    }

    /**
     * Return the solution backend by this adapter
     * @return
     */
    public CuttingSolution solution(){
        return solution;
    }

    @Override
    public double fitness() {
        return solution.fitness();
    }

    @Override
    public Chromosome backtracked() {
        return backtrack;
    }
}
