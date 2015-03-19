package etu.polytech.opti.engine;

import etu.polytech.opti.engine.lang.GenerationState;
import etu.polytech.opti.engine.strategies.GenerationStrategy;
import etu.polytech.opti.layout.CuttingLayoutManager;
import etu.polytech.opti.layout.exceptions.LayoutException;
import etu.polytech.optim.api.lang.CuttingSolution;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by Morgan on 19/03/2015.
 */
public abstract class CuttingGenerator implements Iterator<CuttingSolution> {

    private static final CuttingLayoutManager LAYOUT_MANAGER = new CuttingLayoutManager();

    private final GenerationState state;
    private final GenerationStrategy strategy;

    protected CuttingGenerator(@NotNull final GenerationStrategy strategy) {
        this.strategy = strategy;
        state = new GenerationState();
    }

    /**
     * State of the generator
     * @return
     */
    public GenerationState state(){
        return state;
    }

    @Override
    public boolean hasNext() {
        return strategy.canContinue(state);
    }

    /**
     * Make a layout with the specified generation
     * @param generation
     */
    protected void doLayout(int[][] generation) throws LayoutException{
        LAYOUT_MANAGER.doLayout(generation);
    }
}
