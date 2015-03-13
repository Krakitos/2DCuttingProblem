package etu.polytech.opti.engine;

import etu.polytech.opti.engine.lang.GenerationState;
import etu.polytech.opti.engine.strategies.GenerationStrategy;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.api.lang.CuttingSheet;
import etu.polytech.optim.api.observers.CuttingEngineObservable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Morgan on 13/03/2015.
 */
public class CuttingEngine extends CuttingEngineObservable {

    private final GenerationState state;
    private final GenerationStrategy strategy;

    public CuttingEngine(@NotNull final GenerationStrategy strategy) {
        this.state = new GenerationState();
        this.strategy = strategy;
    }

    public Future<List<List<CuttingLayoutElement>>> generate(@NotNull final CuttingSheet sheet){
        return null;
    }
}
