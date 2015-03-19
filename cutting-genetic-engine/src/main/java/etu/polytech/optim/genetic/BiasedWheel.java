package etu.polytech.optim.genetic;

/**
 * Created by Morgan on 19/03/2015.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Implémentation de la roue biaisée
 */
public class BiasedWheel<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Random RANDOM = new Random();

    public void addToPopulation(@NotNull final T element){

    }

    public void removeFromPopulation(@NotNull final T element){

    }

    public T getRandom(){
        int rand = RANDOM.nextInt(100);
        return null;
    }
}
