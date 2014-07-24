/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapcomposer.model.configurationattribute.attribute;

import com.mapcomposer.model.configurationattribute.ConfigurationAttribute;
import com.mapcomposer.model.configurationattribute.utils.interfaces.CARefresh;
import com.mapcomposer.model.utils.LinkToOrbisGIS;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.orbisgis.coremap.layerModel.OwsMapContext;

/**
 * ConfigurationAttribute representing a specified OwsMapContext given by OrbisGIS.
 */
public class OwsContext extends Source implements CARefresh{

    /** Instance of the OwsMapContext corresponding to the path of the Source*/
    private OwsMapContext omc;
    
    /**
     * Main constructor.
     * @param name Name of the OwsContext in its GraphicalElement.
     */
    public OwsContext(String name) {
        super(name);
        omc=new OwsMapContext(LinkToOrbisGIS.getInstance().getDataManager());
    }
    
    @Override
    public void setValue(String path){
        super.setValue(path);
        //verification of the file
        if(path.contains(".ows")){
            try {
                omc.read(new FileInputStream(new File(path)));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OwsContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Returns the OwsMapContext generated with it's source path.
     * @return The OwsMapContext corresponding to the source.
     */
    public OwsMapContext getOwsContext(){
        return omc;
    }

    @Override
    public void refresh() {
        try {
            omc.read(new FileInputStream(new File(this.getValue())));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OwsContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
