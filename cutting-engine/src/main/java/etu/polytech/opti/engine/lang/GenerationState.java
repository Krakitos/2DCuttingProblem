package etu.polytech.opti.engine.lang;

import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Morgan on 13/03/2015.
 */
public class GenerationState {
    private final AtomicLong iteration;
    private CuttingSolution bestSolution;

    public GenerationState() {
        iteration = new AtomicLong(0l);
    }


    /**
     * Current number of iteration
     * @return
     */
    public AtomicLong iteration(){
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
        iteration.set(0l);
    }
}
