package etu.polytech.optim.cutting.lang;

import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.genetic.lang.Fitness;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 08/04/2015.
 */
public class GeneticSolution implements Fitness {

    private final CuttingSolution solution;

    public GeneticSolution(@NotNull final CuttingSolution solution){
        this.solution = solution;
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
}
