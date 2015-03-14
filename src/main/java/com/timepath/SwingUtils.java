package com.timepath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * @author TimePath
 */
public class SwingUtils {

    /**
     * TODO: http://steamredirect.heroku.com or Runtime.exec() on older versions of java for steam:// links
     */
    public static final HyperlinkListener HYPERLINK_LISTENER = new HyperlinkListener() {
        @Override
        public void hyperlinkUpdate(@NotNull HyperlinkEvent e) {
            if (!Desktop.isDesktopSupported()) {
                return;
            }
            @NotNull Desktop desktop = Desktop.getDesktop();
            if (!e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                return;
            }
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    URL url = e.getURL();
                    @NotNull URI u = (url != null) ? url.toURI() : new URI(e.getDescription());
                    desktop.browse(u);
                } catch (@NotNull IOException | URISyntaxException ex) {
                    LOG.log(Level.WARNING, null, ex);
                }
            }
        }
    };
    private static final Logger LOG = Logger.getLogger(SwingUtils.class.getName());

    private SwingUtils() {
    }

    public static void lookAndFeel(@NotNull Preferences settings) {
        //<editor-fold defaultstate="collapsed" desc="Load native extended themes">
        //        switch(OS.get()) {
        //            case OSX:
        //                UIManager.installLookAndFeel("Quaqua", "ch.randelshofer.quaqua.QuaquaLookAndFeel");
        //                break;
        //            case Linux:
        //                UIManager.installLookAndFeel("GTK extended", "org.gtk.laf.extended.GTKLookAndFeelExtended");
        //                break;
        //        }
        //</editor-fold>
        UIManager.installLookAndFeel("Substance", "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
        @Nullable String usrTheme = settings.get("laf", null);
        if (usrTheme != null) { // Validate user theme
            try {
                Class.forName(usrTheme);
            } catch (ClassNotFoundException ignored) {
                LOG.log(Level.WARNING, "Invalid user theme: {0}", usrTheme);
                usrTheme = null;
                settings.remove("laf");
            }
        }
        fallback:
        if (usrTheme == null) { // Still null, pick a default
            // In order of preference
            @NotNull String[] test = {
                    "Nimbus", UIManager.getSystemLookAndFeelClassName(), UIManager.getCrossPlatformLookAndFeelClassName()
            };
            // Build a map for faster querying
            @NotNull Map<String, String> laf = new HashMap<>(0);
            for (@NotNull UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                laf.put(info.getName(), info.getClassName());
            }
            for (String s : test) {
                if ((usrTheme = laf.get(s)) != null) {
                    settings.put("laf", usrTheme);
                    LOG.log(Level.CONFIG, "Set default user theme: {0}", usrTheme);
                    break fallback;
                }
            }
            usrTheme = null;
        }
        String envTheme = System.getProperty("swing.defaultlaf");
        boolean lafOverride = settings.getBoolean("lafOverride", false);
        @Nullable String theme;
        if (lafOverride) {
            theme = usrTheme == null ? envTheme : usrTheme; // usrTheme authorative
        } else {
            theme = envTheme != null ? envTheme : usrTheme; // envTheme authorative
        }
        try {
            UIManager.setLookAndFeel(theme);
        } catch (@NotNull ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        //<editor-fold defaultstate="collapsed" desc="Improve native LaF">
        //        if(UIManager.getLookAndFeel().isNativeLookAndFeel()) {
        //            try {
        //                LOG.log(Level.INFO, "Adding swing enhancements for {0}", new Object[] {OS.get()});
        //                if(OS.isMac()) {
        //                    UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel"); // Apply quaqua if available
        //                } else if(OS.isLinux()) {
        //                    if(UIManager.getLookAndFeel().getClass().getName().equals("com.sun.java.swing.plaf.gtk
        // .GTKLookAndFeel")) {
        //                        GtkFixer.installGtkPopupBugWorkaround(); // Apply clearlooks java menu fix if applicable
        //                        UIManager.setLookAndFeel("org.gtk.laf.extended.GTKLookAndFeelExtended"); // Apply extended
        // gtk theme is available. http://danjared.wordpress.com/2012/05/21/mejorando-la-integracion-de-javaswing-con-gtk/
        //                    }
        //                }
        //                LOG.info("All swing enhancements installed");
        //            } catch(InstantiationException ex) {
        //                LOG.log(Level.SEVERE, null, ex);
        //            } catch(IllegalAccessException ex) {
        //                LOG.log(Level.SEVERE, null, ex);
        //            } catch(UnsupportedLookAndFeelException ex) {
        //                LOG.log(Level.SEVERE, null, ex);
        //            } catch(ClassNotFoundException ex) {
        ////                LOG.log(Level.INFO, null, ex);
        //                LOG.warning("Unable to load enhanced L&F");
        //            }
        //        }
        //</editor-fold>
    }
}
