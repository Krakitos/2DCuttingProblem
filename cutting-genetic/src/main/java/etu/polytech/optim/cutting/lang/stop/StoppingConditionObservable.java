package etu.polytech.optim.cutting.lang.stop;

import etu.polytech.optim.api.observers.ProgressObservable;
import etu.polytech.optim.genetic.strategies.StoppingCondition;

/**
 * Created by Morgan on 17/04/2015.
 */
public interface StoppingConditionObservable extends StoppingCondition, ProgressObservable {
}
