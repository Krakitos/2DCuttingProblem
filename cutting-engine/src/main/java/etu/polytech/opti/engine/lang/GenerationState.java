package etu.polytech.opti.engine.lang;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by Morgan on 13/03/2015.
 */
public class GenerationState {
    private long iteration;
    private CuttingSolution bestSolution;

    /**
     * Define the number of iteration
     * @param iteration
     * @return
     */
    public long iteration(final long iteration){
        return this.iteration = iteration;
    }

    /**
     * Current number of iteration
     * @return
     */
    public long iteration(){
        return iteration;
    }

    public CuttingSolution bestSolution(@NotNull final CuttingSolution solution){
        return this.bestSolution = solution;
    }

    /**
     * Current best solution
     * @return
     */
    public Optional<CuttingSolution> bestSolution(){
        return Optional.ofNullable(bestSolution);
    }

    /**
     * Reset the state
     */
    public void reset(){
        bestSolution = null;
        iteration = 0;
    }
}
