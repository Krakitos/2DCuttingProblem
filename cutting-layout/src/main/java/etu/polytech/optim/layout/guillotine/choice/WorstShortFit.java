package etu.polytech.optim.layout.guillotine.choice;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 12/04/2015.
 */
public class WorstShortFit extends BestShortSideFit {

    private static final String NAME = "Worst Short Fit";

    @Override
    public double score(Rectangle dest, double width, double height) {
        return -super.score(dest, width, height);
    }

    @Override
    public String name() {
        return NAME;
    }
}
