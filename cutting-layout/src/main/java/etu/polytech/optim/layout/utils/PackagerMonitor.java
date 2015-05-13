package etu.polytech.optim.layout.utils;

import etu.polytech.optim.layout.CuttingPackager;

/**
 * Created by Morgan on 12/05/2015.
 */
public abstract class PackagerMonitor implements CuttingPackager {

    protected CuttingPackager packager;

    protected PackagerMonitor(CuttingPackager packager){
        this.packager = packager;
    }
}
