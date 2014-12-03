package org.orbisgis.mapcomposer.model.utils;

import org.orbisgis.mapcomposer.model.configurationattribute.interfaces.ConfigurationAttribute;
import org.orbisgis.mapcomposer.model.configurationattribute.utils.CAManager;
import org.orbisgis.mapcomposer.model.graphicalelement.utils.GEManager;
import org.orbisgis.mapcomposer.model.graphicalelement.interfaces.GraphicalElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * This class permits to manage the save and the load of a document project and it uses the sax api to do a save into an xml file.
 * All the saves contain the version of the MapComposer used to do it. The class contain an array of those version to know with which version it is compatible.
 * In the documentation the "reader" correspond to the position where the files is actually read.
 */
public class SaveAndLoadHandler extends DefaultHandler {

    /** String array containing all the version of the MapComposer compatible with the class **/
    String[] compVersions={"1.0.2"};

    /** List of all the GraphicalElement to save **/
    private List<GraphicalElement> listGE;
    /** List of all the class of GraphicalElement that can be used **/
    private List<Class<? extends GraphicalElement>> listClassGE;
    /** List of all the class of ConfigurationAttribute that can be used **/
    private List<Class<? extends ConfigurationAttribute>> listClassCA;

    /** Indicates if the position of the reader is inside a GraphicalElement xml tag **/
    private boolean insideGE = false;
    /** Indicates if the position of the reader is inside a ConfigurationAttributes xml tag **/
    private boolean insideCA = false;
    /** Indicates if the position of the reader is inside a ConfigurationAttributes field xml tag **/
    private boolean insideField = false;

    /** Instance of the GraphicalElement in creation **/
    private GraphicalElement graphicalElement;
    /** Instance of the ConfigurationAttributes in creation **/
    private ConfigurationAttribute configurationAttribute;
    /** StringBuffer used to register everything inside Configuration xml tags **/
    private StringBuffer stringBuffer = null;

    public SaveAndLoadHandler(GEManager geManager, CAManager caManager){
        // Gets the list of GraphicalElement and Configuration Attributes class
        listClassGE = geManager.getRegisteredGEClasses();
        listClassCA = caManager.getRegisteredGEClasses();
    }

    @Override
    public void startDocument() throws SAXException {
        listGE = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //Test if the tag read is the "version" one.
        if (qName.equals("version")) {
            //If the tag is the "version" one, start to register with the StringBuffer
            stringBuffer = new StringBuffer();
        }
        else{
            //Test if the reader is actual inside a GraphicalElement xml tag
            if (insideGE) {
                //Test if the reader is actual inside a ConfigurationAttribute xml tag
                if (insideCA) {
                    //If the reader is inside a GraphicalElement tag and inside a ConfigurationAttribute tag, it's just before a field of the ConfigurationAttribute
                    //Indicate that the reader is entering into a field and start to register with the StringBuffer
                    insideField = true;
                    stringBuffer = new StringBuffer();
                }
                //If not, it means that the reader is just before a ConfigurationAttribute xml tag.
                else {
                    //Test each class in the listClassCA to find which one correspond to the xml tag
                    for (Class<? extends ConfigurationAttribute> c : listClassCA) {
                        if (c.getName().equals(qName)) {
                            //Instantiate the corresponding class and indicate that the reader is inside a ConfigurationAttribute xml tag
                            try {
                                configurationAttribute = c.newInstance();
                                insideCA = true;
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new SAXException(e);
                            }
                        }
                    }
                }
            }
            //If not, it means that the reader is just before a GraphicalElement xml tag.
            else {
                //Test each class in the listClassGE to find which one correspond to the xml tag
                for (Class<? extends GraphicalElement> c : listClassGE) {
                    if (c.getName().equals(qName)) {
                        //Instantiate the corresponding class and indicate that the reader is inside a GraphicalElement xml tag
                        try {
                            graphicalElement = c.newInstance();
                            insideGE = true;
                        } catch (InstantiationException|IllegalAccessException e) {
                            throw new SAXException(e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //If the reader is leaving a field xml tag, sets the ConfigurationAttribute configurationAttribute with the data from the String buffer, stop if and indicates that the reader left the field tag.
        if (insideField) {
            configurationAttribute.setField(qName, stringBuffer.toString().replace("\n", "").replace("\t", ""));
            stringBuffer = null;
            insideField = false;
        }
        //If the reader is leaving a ConfigurationAttribute xml tag, add the fully configured CA to the GraphicalElement graphicalElement and indicate that the reader left the ConfigurationAttribute tag
        else if (insideCA) {
            graphicalElement.setAttribute(configurationAttribute);
            insideCA = false;
        }
        //If the reader is leaving a GraphicalElement xml tag, add the fully configured GE to the listGE and indicate that the reader left the GraphicalElement tag
        else if (insideGE) {
            listGE.add(graphicalElement);
            insideGE = false;
        }

        // Check if the version of the saveProject is actually compatible with the MapComposer version.
        if(qName.equals("version")) {
            boolean flag=false;
            for (int i = 0; i < compVersions.length; i++)
                if (stringBuffer.toString().equals(compVersions[i]))
                    flag = true;
            if (!flag) {
                String message = "File version " + stringBuffer.toString() + " isn't compatible with the MapComposer version. Should be ";
                for (String s : compVersions)
                    message += s + ";";
                throw new SAXException(message);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //If the StringBuffer isn't null, append the character read from to source to it
        if (stringBuffer != null) stringBuffer.append(new String(ch, start, length));
    }

    /**
     * Open a file chooser and load the selected file.
     * @return The list of loaded GraphicalElements
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public List<GraphicalElement> loadProject() throws IOException, ParserConfigurationException, SAXException {
        //Create a JFileChooser
        File f = new File(LinkToOrbisGIS.getInstance().getViewWorkspace().getCoreWorkspace().getWorkspaceFolder());
        JFileChooser fc = createFileChooser(f, "Load document project");
        //If the saveProject is validated, do the marshall
        if (fc.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
            return load(fc.getSelectedFile().getAbsolutePath());
        return listGE;
    }

    /**
     * Loads and returns the project save file corresponding to the given path.
     * @param path Path of the file to load
     * @return The list of GraphicalElement saved
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List<GraphicalElement> load(String path) throws ParserConfigurationException, SAXException, IOException {
        //Load the file of the given path an return the GraphicalElement loaded
        SAXParserFactory.newInstance().newSAXParser().parse(new File(path), this);
        return listGE;
    }

    /**
     * Open a file chooser window and save the given list of GraphicalElement in the selected file.
     * @param list List of GraphicalElement to save
     * @throws IOException
     * @throws NoSuchMethodException
     */
    public void saveProject(List<GraphicalElement> list) throws IOException, NoSuchMethodException {
        //Create a JFileChooser
        File f = new File(LinkToOrbisGIS.getInstance().getViewWorkspace().getCoreWorkspace().getWorkspaceFolder());
        JFileChooser fc = createFileChooser(f, "Save document project");
        //If the saveProject is validated, do the marshall
        if(fc.showSaveDialog(new JFrame())==JFileChooser.APPROVE_OPTION)
            save(list, fc.getSelectedFile().getAbsolutePath());
    }

    /**
     * Save into and xml file the given list of GraphicalElements.
     * The save is shape like that :
     *
     * <version>XXX.XXX.XXX</version>
     * <GEClass1>
     *     <CAClass1>
     *         <CAField1>
     *             value
     *         </CAField1>
     *         <CAField2>
     *             value
     *         </CAField2>
     *         ...
     *     </CAClass1>
     *     <CAClass2>
     *         ...
     *     </CAClass2>
     *     ...
     * </GEClass1>
     * <GEClass2>
     *     ...
     * </GEClass2>
     * ...
     *
     * @param list List of GraphicalElement to save
     * @param path Path of the xml save
     * @throws IOException
     */
    public void save(List<GraphicalElement> list, String path) throws IOException {if(!path.contains(".xml")) path+=".xml";
        FileWriter fw = new FileWriter(path);
        //Write the MapComposer version
        fw.write("<synchronized>\n\t<version>1.0.2</version>\n");
        //Write all the GraphicalElement from the list argument
        for (GraphicalElement ge : list) {
            //Write the GraphicalElement start xml tag
            fw.write("\t<" + ge.getClass().getName() + ">\n");
            //Write all the ConfigurationAttribute get from the GraphicalElement graphicalElement
            for(ConfigurationAttribute ca : ge.getSavableAttributes()){
                //Write the ConfigurationAttribute start xml tag
                fw.write("\t\t<"+ca.getClass().getName()+">\n");
                //Get all the ConfigurationAttribute fields and write them
                Iterator<Map.Entry<String, Object>> it =  ca.getAllFields().entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Object> entry = it.next();
                    fw.write("\t\t\t<" + entry.getKey() + ">\n");
                    fw.write("\t\t\t\t"+entry.getValue().toString()+"\n");
                    fw.write("\t\t\t</" + entry.getKey() + ">\n");
                }
                //Write the ConfigurationAttribute start xml tag
                fw.write("\t\t</"+ca.getClass().getName()+">\n");
            }
            //Write the GraphicalElement start xml tag
            fw.write("\t</"+ ge.getClass().getName()+">\n");
        }
        fw.write("</synchronized>");
        fw.close();
    }

    /**
     * Creates and returns a JFileChooser with the given information
     * @param currentDirectory Directory shown in the JFileChooser
     * @param title Title of the JFileChooser
     * @return The JFileChooser
     */
    private JFileChooser createFileChooser(File currentDirectory, String title){
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(currentDirectory);
        fc.setDialogTitle(title);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override public boolean accept(File file) {
                if(file.isDirectory()) return true;
                return file.getAbsolutePath().toLowerCase().contains(".xml");
            }
            @Override public String getDescription() {return "XML Files (.xml)";}
        });
        return fc;
    }
}