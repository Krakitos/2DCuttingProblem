package etu.polytech.optim.genetic.strategies;

/**
 * Created by Morgan on 07/04/2015.
 */
public interface StoppingCondition {

    /**
     * Indicate if the stopping condition is reached or nor
     * @return TRUE if the iteration need to stop, FALSE otherwise
     */
    boolean isReached();
}
