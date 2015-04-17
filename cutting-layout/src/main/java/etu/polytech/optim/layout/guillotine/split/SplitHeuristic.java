package etu.polytech.optim.layout.guillotine.split;

import etu.polytech.optim.layout.lang.Rectangle;

/**
 * Created by Morgan on 12/04/2015.
 */
public abstract class SplitHeuristic {

    public SplitDirection split(final Rectangle freeRect, final Rectangle placedRect){
        final double w = freeRect.width() - placedRect.width();
        final double h = freeRect.height() - placedRect.height();

        return isSplittingHorizontally(freeRect, placedRect, w, h) ? SplitDirection.HORIZONTAL : SplitDirection.VERTICAL;
    }

    /**
     *
     * @param freeRect
     * @param placedRect
     * @param w Remaining width
     * @param h Remaining height
     * @return
     */
    protected abstract boolean isSplittingHorizontally(final Rectangle freeRect, final Rectangle placedRect, final double w, final double h);

    /**
     * Name of this heuristic
     * @return
     */
    public abstract String name();

    public enum SplitDirection {
        HORIZONTAL,
        VERTICAL
    }
}
