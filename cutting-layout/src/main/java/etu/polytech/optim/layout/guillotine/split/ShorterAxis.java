package etu.polytech.optim.layout.guillotine.split;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 13/04/2015.
 */
public class ShorterAxis extends SplitHeuristic {

    private static final String NAME = "Split Along Shorter Axis";

    @Override
    protected boolean isSplittingHorizontally(Rectangle freeRect, Rectangle placedRect, double w, double h) {
        return freeRect.width() <= freeRect.height();
    }

    @Override
    public String name() {
        return NAME;
    }
}
