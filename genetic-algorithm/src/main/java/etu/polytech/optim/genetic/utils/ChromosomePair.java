package etu.polytech.optim.genetic.utils;

import etu.polytech.optim.genetic.lang.Chromosome;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by Morgan on 08/04/2015.
 */
public class ChromosomePair implements Iterable<Chromosome>{
    private Chromosome first;
    private Chromosome second;

    public ChromosomePair(@NotNull final Chromosome first, @NotNull final Chromosome second) {
        this.first = first;
        this.second = second;
    }

    public Chromosome first(){
        return first;
    }

    public void first(Chromosome c){
        first = c;
    }

    public Chromosome second(){
        return second;
    }

    public void second(Chromosome c){
        second = c;
    }

    @Override
    public Iterator<Chromosome> iterator() {
        return new Iterator<Chromosome>() {

            private int index = 1;

            @Override
            public boolean hasNext() {
                return index < 2;
            }

            @Override
            public Chromosome next() {
                if(index == 1){
                    index++;
                    return first;
                } else{
                    index = 2;
                    return second;
                }
            }
        };
    }
}
