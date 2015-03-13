package etu.polytech.opti.controllers;

import etu.polytech.optim.api.lang.CuttingSolution;
import etu.polytech.optim.api.observers.CuttingEngineObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Morgan on 11/03/2015.
 */
public class MainController implements CuttingEngineObserver {

    @Override
    public void onNewSolution(@NotNull CuttingSolution solution) {

    }

    @Override
    public void onGenerationStarted() {

    }

    @Override
    public void onGenerationProgress(int iteration) {

    }

    @Override
    public void onGenerationFinished(@NotNull CuttingSolution bestSolution) {

    }
}
