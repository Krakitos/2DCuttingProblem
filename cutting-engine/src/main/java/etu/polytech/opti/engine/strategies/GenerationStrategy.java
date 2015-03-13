package etu.polytech.opti.engine.strategies;

import etu.polytech.opti.engine.lang.GenerationState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 13/03/2015.
 */
public interface GenerationStrategy {

    /**
     * Use to be aware of the starting of the generation
     */
    public void handleStart();


    /**
     * Indicate if the generation can go on
     * @return
     */
    public default boolean canContinue(@NotNull final GenerationState state){
        return true;
    }

}
