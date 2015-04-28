package etu.polytech.optim.layout;

import etu.polytech.optim.api.lang.CuttingConfiguration;
import etu.polytech.optim.api.lang.CuttingElement;
import etu.polytech.optim.api.lang.CuttingLayoutElement;
import etu.polytech.optim.layout.lang.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                        elements.add(new CuttingLayoutElement(r.x(), r.y(), element.width() == r.width(), element));
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
