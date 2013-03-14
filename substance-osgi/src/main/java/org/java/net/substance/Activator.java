package org.java.net.substance;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import javax.swing.UIManager;

/**
 * Registers services provided by this plugin bundle.
 */
public class Activator implements BundleActivator {
        /**
         * Starting bundle, register services.
         * @param bc
         * @throws Exception
         */
        @Override
        public void start(BundleContext bc) throws Exception {
            //Substance http://java.net/projects/substance/
            //Samples http://insubstantial.github.com/insubstantial/substance/docs/skins/toneddown.html
            UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
            UIManager.getLookAndFeelDefaults().put("ClassLoader", SubstanceBusinessBlackSteelLookAndFeel.class.getClassLoader());
        }

        /**
         * Called before the bundle is unloaded.
         * @param bc
         * @throws Exception
         */
        @Override
        public void stop(BundleContext bc) throws Exception {
            if(UIManager.getLookAndFeel() instanceof SubstanceBusinessBlackSteelLookAndFeel) {
                UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[0].getClassName());
            }
        }
}
