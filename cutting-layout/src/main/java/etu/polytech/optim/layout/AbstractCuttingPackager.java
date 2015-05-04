package etu.polytech.optim.layout;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.lang.Rectangle;

import java.util.*;

/**
 * Created by Morgan on 08/04/2015.
 */
public abstract class AbstractCuttingPackager implements CuttingPackager {
    protected final CuttingConfiguration configuration;

    protected AbstractCuttingPackager(CuttingConfiguration configuration) {
        this.configuration = configuration;
    }

    protected List<Collection<CuttingLayoutElement>> pack(List<List<Rectangle>> computed){
        List<Collection<CuttingLayoutElement>> patterns = new ArrayList<>();

        for (List<Rectangle> p : computed) {
            List<CuttingLayoutElement> elements = new ArrayList<>(p.size());
            for (Rectangle r : p) {
                boolean found = false;
                for (CuttingElement element : configuration.elements()) {
                    if((element.width() == r.width() && element.height() == r.height()) || (element.height() == r.width() && element.width() == r.height())) {
                        elements.add(new CuttingLayoutElement(r.x(), r.y(), element.width() != r.width(), element));
                        found = true;
                        break;
                    }

                }

                if(!found)
                    assert false : "Element not found corresponding to rectangle " + r;
            }

            patterns.add(elements);
        }

        return patterns;
    }

    /**
     *
     * @param generation
     * @return
     */
    protected List<CuttingElement> init(int[] generation, boolean shuffle) {
        final int sum = Arrays.stream(generation).sum();

        List<CuttingElement> remaining = new ArrayList<>(sum);

        for (CuttingElement element : configuration.elements()) {
            for (int i = 0; i < generation[element.id()]; i++) {
                remaining.add(element);
            }
        }

        assert sum == remaining.size() : "Not the same number of generated element in remaining";

        //Shuffle the list
        if(shuffle)
            Collections.shuffle(remaining);

        return remaining;
    }

    /**
     * Width of the pattern
     * @return
     */
    protected double binWidth(){
        return configuration.sheet().width();
    }

    /**
     * Height of the pattern
     * @return
     */
    protected double binHeight(){
        return configuration.sheet().height();
    }
}
