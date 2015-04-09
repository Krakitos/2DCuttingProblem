package etu.polytech.optim.api.lang;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Morgan on 14/03/2015.
 */
public class CuttingConfiguration {

    private final CuttingSheet sheet;
    private final Collection<CuttingElement> elements;

    public CuttingConfiguration(@NotNull final CuttingSheet sheet, @NotNull final Collection<CuttingElement> elements) {
        this.sheet = sheet;
        this.elements = Collections.unmodifiableCollection(elements);
    }

    /**
     * Sheet configuration
     * @return
     */
    public CuttingSheet sheet(){
        return sheet;
    }

    /**
     * Pieces to layout, with the number of each of them
     * @return
     */
    public Collection<CuttingElement> elements(){
        return elements;
    }

    public static class Builder {
        private final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

        private final Collection<CuttingElement> elements;
        private CuttingSheet sheet;

        public Builder() {
            elements = new ArrayList<>();
        }

        public void addElement(float width, float height, int asking){
            final Collection<CuttingElement> alreadyDefined =
                    elements.parallelStream()
                            .filter(e -> (e.width() == width && e.height() == height) || (e.width() == height && e.height() == width))
                            .collect(Collectors.toList());

            if(alreadyDefined.size() != 0) {
                asking += alreadyDefined.parallelStream().mapToInt(CuttingElement::asking).sum();
                elements.removeAll(alreadyDefined);
            }

            elements.add(new CuttingElement(ID_GENERATOR.getAndIncrement(), width, height, asking));
        }

        public void setSheet(int width, int height, int price){
            sheet = new CuttingSheet(width, height, price);
        }

        public CuttingConfiguration build() {
            if(Objects.isNull(sheet))
                throw new IllegalArgumentException("No sheet defined");

            if(elements.size() == 0)
                throw new IllegalArgumentException("No pieces are set");

            return new CuttingConfiguration(sheet, elements);
        }
    }
}
