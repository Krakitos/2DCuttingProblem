package etu.polytech.optim.layout.guillotine.choice;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 12/04/2015.
 */
public class BestAreaFit implements RectChoiceHeuristic {

    private static final String NAME = "Best Area Fit";

    @Override
    public double score(Rectangle dest, double width, double height) {
        return dest.area() - width * height;
    }

    @Override
    public String name() {
        return NAME;
    }
}
