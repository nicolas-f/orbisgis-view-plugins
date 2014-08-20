package com.mapcomposer.view.configurationattribute;

import com.mapcomposer.model.configurationattribute.ConfigurationAttribute;
import com.mapcomposer.model.configurationattribute.attribute.OwsContext;
import com.mapcomposer.model.configurationattribute.attribute.Source;
import com.mapcomposer.model.utils.LinkToOrbisGIS;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Renderer associated to the OwsContext ConfigurationAttribute.
 */
public class OwsContextRenderer implements CARenderer{

    @Override
    public JPanel render(ConfigurationAttribute ca) {
        JPanel pan = new JPanel();
        pan.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        OwsContext source = (OwsContext)ca;
        pan.add(new JLabel(source.getName()));
        
        File f = new File(LinkToOrbisGIS.getInstance().getViewWorkspace().getCoreWorkspace().getWorkspaceFolder()+"/maps/");
        //Definition of the FilenameFilter
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File file, String string) {
                String name = string.toLowerCase();
                return name.contains(".ows");
            }
        };
        
        JComboBox list = new JComboBox(f.listFiles(filter));
        list.setSelectedItem(ca.getValue());
        pan.add(list);
        return pan;
    }

    @Override
    public void extractValue(JPanel panel, ConfigurationAttribute attribute) {
        OwsContext source = (OwsContext)attribute;
        for(Component c : panel.getComponents()){
            if(c instanceof JComboBox){
                source.setValue(((JComboBox)c).getModel().getSelectedItem().toString());
            }
        }
    }
    
}
