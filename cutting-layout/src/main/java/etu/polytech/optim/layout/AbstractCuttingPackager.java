package etu.polytech.optim.layout;

import etu.polytech.optim.api.lang.CuttingConfiguration;

/**
 * Created by Morgan on 08/04/2015.
 */
public abstract class AbstractCuttingPackager implements CuttingPackager {
    protected final CuttingConfiguration configuration;

    protected AbstractCuttingPackager(CuttingConfiguration configuration) {
        this.configuration = configuration;
    }
}
