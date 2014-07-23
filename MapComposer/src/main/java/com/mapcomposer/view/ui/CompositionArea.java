package com.mapcomposer.view.ui;

import com.mapcomposer.model.utils.LinkToOrbisGIS;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * Area for the map document composition.
 */
public class CompositionArea extends JPanel{
    
    /**Unique instance of the class.*/
    private static CompositionArea INSTANCE=null;
    
    /**Private constructor.*/
    private CompositionArea(){
        super();
    }
    
    /**
     * Returns the unique instance of the class.
     * @return 
     */
    public static CompositionArea getInstance(){
        if(INSTANCE==null){
            INSTANCE = new CompositionArea();
        }
        
        /**Test code to display an OwsMapContext
        
        //File
        File file = new File(LinkToOrbisGIS.getInstance().getViewWorkspace().getCoreWorkspace().getWorkspaceFolder()+"/maps/landcover2000.ows");      
        //OWS
        OwsMapContext omc = new OwsMapContext(LinkToOrbisGIS.getInstance().getDataManager());
        omc.setLocation(file.toURI());
        try {
            omc.read(new FileInputStream(file));
            System.out.println("read OK");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CompositionArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            omc.open(new NullProgressMonitor());
            System.out.println("open OK");
        } catch (LayerException ex) {
            Logger.getLogger(CompositionArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Draw in buffered image
        MapTransform mapTransform = new MapTransform();
        mapTransform.setExtent(omc.getBoundingBox());
        BufferedImage outImage = new BufferedImage(50, 150, BufferedImage.TYPE_INT_RGB);
        mapTransform.setImage(outImage);
        omc.draw(mapTransform, new NullProgressMonitor());
        JLabel label = new JLabel(new ImageIcon(outImage));
        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        label.setBorder(raisedbevel);
        INSTANCE.setLayout(new BorderLayout());
        INSTANCE.add(label, BorderLayout.CENTER);
        **/

        
        
        return INSTANCE;
    }
}
