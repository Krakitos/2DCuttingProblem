package etu.polytech.optim.layout.guillotine.split;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 13/04/2015.
 */
public class MaximizeArea extends SplitHeuristic{

    private static final String NAME = "Split for Maximizing Area";

    @Override
    protected boolean isSplittingHorizontally(Rectangle freeRect, Rectangle placedRect, double w, double h) {
        return placedRect.width() * h <= placedRect.height() * w;
    }

    @Override
    public String name() {
        return NAME;
    }
}
