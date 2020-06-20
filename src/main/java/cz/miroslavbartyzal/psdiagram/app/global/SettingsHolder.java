/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

import cz.miroslavbartyzal.psdiagram.app.Main;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.AbstractLayout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.EnumLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Tato třída nese globální nastavení aplikace.</p>
 * <p>
 * Nastavení je po jeho změně automaticky ihned ukládáno na pevný disk do
 * složky (user.home)/.psdiagram/settings.xml. Z tohoto umístění je také po
 * spuštění aplikace automaticky načteno. Není-li nastavení k dispozici,
 * aktivuje se nastavení defaultní.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class SettingsHolder
{

    /**
     * Konstanta udržující aktuální instanci třídy Settings, reprezentující
     * nastavení aplikace.
     */
    public static final Settings settings;
    private static final JAXBContext jAXBcontext;
    private static final Marshaller jAXBmarshaller; // Marshaller se bude vyuzivat frekventovane
    public static final File WORKING_DIR = new File(System.getProperty("user.home"), ".psdiagram");
    public static final File MY_FILE;
    public static final File MY_DIR;
    public static final File MY_WORKING_DIR; // dir in which this instance of PS Diagram was launched at
    private static final File settingsFile = new File(WORKING_DIR, "settings.xml");

    // automaticke loadovani nastaveni
    static {
        try {
            GraphicsEnvironment ge
                    = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font font = Font.createFont(Font.TRUETYPE_FONT, Settings.class.getResourceAsStream(
                    "/fonts/PSDSpecialSymbols.ttf"));
            ge.registerFont(font);
        } catch (IOException | FontFormatException ex) {
            ex.printStackTrace(System.err);
        }

        JAXBContext context = null;
        Marshaller marshaller = null;
        try {
            context = JAXBContext.newInstance(Settings.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
        jAXBcontext = context;
        jAXBmarshaller = marshaller;
        if (jAXBcontext != null && settingsFile.exists()) {
            Settings stngs;
            boolean settingsAreNew = false;
            try {
                stngs = (Settings) jAXBcontext.createUnmarshaller().unmarshal(settingsFile);
                if (!stngs.loadLastFlowchart || (stngs.actualFlowchartFile != null && !stngs.actualFlowchartFile.exists())) {
                    stngs.actualFlowchartFile = null;
                }
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
                stngs = new Settings();
                settingsAreNew = true;
            }
            settings = stngs;
            if (settingsAreNew) {
                saveSettings();
            }
        } else {
            settings = new Settings();
            saveSettings();
        }

        // resolve self location
        File myFile = null;
        try {
            myFile = new File(
                    Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
        MY_FILE = myFile;
        MY_WORKING_DIR = Paths.get("").toAbsolutePath().toFile();
        if (MY_FILE != null) {
            MY_DIR = MY_FILE.getParentFile();
        } else {
            MY_DIR = MY_WORKING_DIR; // assign at least working dir so MY_DIR is not null
        }
    }

    private SettingsHolder()
    {
        throw new AssertionError();
    }
    /**
     * Konstanta udržuje font, používaný pro text uvnitř symbolů. Je-li v
     * systému přítomen font Consolas, je použit ten, jinak je použit font
     * LiberationMono-Regular, který je přibalen v této aplikaci.
     */
    public static final Font CODEFONT = getMyCodeFont(13f);
    /**
     * Konstanta udržuje font, používaný pro text segmentů vývojového diagramu.
     */
    public static final Font SMALL_CODEFONT = getMyCodeFont(10f);
    /**
     * Tento font se používá pro popisky pod grafickou reprezentací symbolu ve
     * formuláři pro nastavení funkce symbolu.
     */
    public static final Font SMALLFONT_SYMBOLDESC = new Font("sansserif", Font.PLAIN, 10); // pouziva se k popisku symbolu v vkladani funkce
    /**
     * Instance FontRenderContext, která se má používat pro vytvoření instancí
     * třídy TextLayout.
     */
    public static final FontRenderContext FONTRENDERCONTEXT = new FontRenderContext(null,
            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB,
            RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    /**
     * Konstanta udržuje URL serveru, kterého se bude aplikace dotazovat na aktuální
     * čas.
     */
    public static final String TIMESERVER = "http://www.seznam.cz";
//    public static final String PSDIAGRAM_SERVER = "http://www.psdiagram.cz";
    public static final String PSDIAGRAM_SERVER = ResourceBundle.getBundle("application").getString(
            "psdiagramWebUrl");
    public static final String PSDIAGRAM_VERSION = ResourceBundle.getBundle("application").getString(
            "version");
    public static final String PSDIAGRAM_BUILD = ResourceBundle.getBundle("application").getString(
            "buildInfo");
    public static final String PSDIAGRAM_BUILD_NUMBER = PSDIAGRAM_BUILD.replaceAll("\\s.*$", "");
    public static final String PSDIAGRAM_BUILD_DATE = PSDIAGRAM_BUILD.replaceAll("^[\\d\\s]+\\(", "").replaceAll(
            "\\)$", "");
    public static final String BUILD_PROFILE = ResourceBundle.getBundle("application").getString(
            "buildProfile");
    public static final boolean IS_DEPLOYMENT_MODE = BUILD_PROFILE.equals("deployment");
    public static final File JAVAW = getJavaw();

    /**
     * Uloží aktuální nastavení do souboru (user.home)/.psdiagram/settings.xml.
     */
    public static void saveSettings()
    {
        if (jAXBcontext == null || jAXBmarshaller == null) {
            return; // nelze vytvořit xml :(
        }
        if (!WORKING_DIR.exists()) {
            if (!WORKING_DIR.mkdir()) {
                return; // nelze vytvořit složku :(
            }
        }
        try {
            jAXBmarshaller.marshal(settings, settingsFile);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static Font getMyCodeFont(float size)
    {
        String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String fontFamily : fontFamilies) {
            if (fontFamily.toLowerCase().equals("consolas")) { // je-li dostupny Consolas font, uprednostnim ten
                return new Font("consolas", Font.PLAIN, (int) size);
            }
        }
        //return getMyFont(size, "LiberationMono-Regular.ttf");
        return getMyFont(size, "DejaVuSansMono.ttf");
    }

    private static Font getMyFont(float size, String fontName)
    {
        try {
            //Returned font is of pt size 1
            Font font = Font.createFont(Font.TRUETYPE_FONT, Settings.class.getResourceAsStream(
                    "/fonts/" + fontName));
            //Derive and return a desired pt version:
            //Need to use float otherwise
            //it would be interpreted as style
            switch (fontName) {
                case "DejaVuSansMono.ttf":
                    if (size > 10) {
                        font = font.deriveFont(Font.PLAIN, size - 2); // DejaVuSansMono.ttf je vetsi
                    } else {
                        font = font.deriveFont(Font.PLAIN, size - 1); // DejaVuSansMono.ttf je vetsi
                    }
                    break;
                case "LiberationMono-Regular.ttf":
                    font = font.deriveFont(Font.PLAIN, size - 1);
                    break;
                default:
                    font = font.deriveFont(Font.PLAIN, size);
                    break;
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            //if (fontName.equals("Anonymous Pro Minus.ttf")) {
            return new Font("monospaced", Font.PLAIN, (int) size);
            /*
             * } else {
             * return new Font("sansserif", Font.PLAIN, (int) size);
             * }
             */
        }
    }

    private static File getJavaw()
    {
        File file = new File(MY_DIR, "jre/bin");
        if (!file.exists()) {
            file = new File(System.getProperty("java.home"), "bin");
        }
        if (isWindows()) {
            file = new File(file, "javaw.exe");
        } else {
            file = new File(file, "javaw");
        }
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static boolean isWindows()
    {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * Tato třída reprezentuje nastavení aplikace.
     */
    @XmlRootElement(name = "settings")
    @XmlAccessorType(XmlAccessType.NONE)
    @XmlType(name = "settings")
    public final static class Settings
    {

        // nastavený layout
        @XmlElement(name = "selectedLayout")
        private String selectedLayout = EnumLayout.TBLRLayout.name();
        // nastavení proměnných
        @XmlElement(name = "blockScopeVariables")
        private boolean blockScopeVariables = false;
        // nastavení animace
        @XmlElement(name = "ballShine")
        private boolean ballShine = true;
        @XmlElement(name = "ballShineRadius")
        private int ballShineRadius = 200;
        @XmlElement(name = "fps")
        private int fps = 25;
        // poslední použitý adresář - informace pro nacitani/ukladani
        @XmlElement(name = "lastDir")
        private String lastDir;
        @XmlElement(name = "loadLastFlowchart")
        private boolean loadLastFlowchart = true;
        // aktuální, uložený soubor diagramu - slouží také k načtení při příštím spuštění
        @XmlElement(name = "actualFlowchartFile")
        private File actualFlowchartFile;
        //Used when diagram from library or backup is opened so it is not saved in its original file destination.
        @XmlElement(name = "dontSaveDirectly")
        private boolean dontSaveDirectly = false;
        @XmlElement(name = "exportTransparency")
        private boolean exportTransparency = true;
        @XmlElement(name = "exportFlowchartPadding")
        private int exportFlowchartPadding = AbstractLayout.flowchartPadding;
        @XmlElement(name = "proxyHost", defaultValue = "")
        private String proxyHost = "";
        @XmlElement(name = "proxyPort")
        private int proxyPort = -1;
        @XmlElement(name = "associateExtension")
        private Boolean associateExtension;

        /**
         * Vrátí, zda má animační kulička zářit, či nikoliv.
         *
         * @return true, když má animační kulička zářit
         */
        public boolean isBallShine()
        {
            return ballShine;
        }

        /**
         * Nastaví, zda má animační kulička zářit, či nikoliv.
         *
         * @param ballShine požadovaný stav záře animační kuličky
         */
        public void setBallShine(boolean ballShine)
        {
            this.ballShine = ballShine;
            saveSettings();
        }

        /**
         * Vrátí nastavený radius dosvitu animační kuličky.
         *
         * @return nastavený radius dosvitu animační kuličky
         */
        public int getBallShineRadius()
        {
            return ballShineRadius;
        }

        /**
         * Nastaví radius dosvitu animační kuličky.
         *
         * @param ballShineRadius požadovaný radius dosvitu kuličky.
         */
        public void setBallShineRadius(int ballShineRadius)
        {
            this.ballShineRadius = ballShineRadius;
            saveSettings();
        }

        /**
         * Vrátí, zda má být použit blokobý přístup k proměnným.
         *
         * @return true, když má být použit blokový přístup k proměnným
         */
        public boolean isBlockScopeVariables()
        {
            return blockScopeVariables;
        }

        /**
         * Nastaví, zda má být použit blokobý přístup k proměnným.
         *
         * @param blockScopeVariables true, má-li být použit blokobý přístup k
         * proměnným
         */
        public void setBlockScopeVariables(boolean blockScopeVariables)
        {
            this.blockScopeVariables = blockScopeVariables;
            saveSettings();
        }

        /**
         * Vrátí nastavené odsazení vývojového diagramu při exportu.
         *
         * @return nastavené odsazení vývojového diagramu při exportu
         */
        public int getExportFlowchartPadding()
        {
            return exportFlowchartPadding;
        }

        /**
         * Nastaví odsazení vývojového diagramu při exportu.
         *
         * @param exportFlowchartPadding požadované odsazení vývojového diagramu
         * při exportu
         */
        public void setExportFlowchartPadding(int exportFlowchartPadding)
        {
            this.exportFlowchartPadding = exportFlowchartPadding;
            saveSettings();
        }

        /**
         * Vrátí, zda je nastaveno transparentní pozadí pro export vývojového
         * diagramu.
         *
         * @return true, když je transparentní pozadí při exportu nastaveno
         */
        public boolean isExportTransparency()
        {
            return exportTransparency;
        }

        /**
         * Nastaví, zda má být použita při exportu vývojového diagramu
         * transparentní barva pozadí.
         *
         * @param exportTransparency true, když má být použita při exportu
         * vývojového diagramu transparentní barva pozadí
         */
        public void setExportTransparency(boolean exportTransparency)
        {
            this.exportTransparency = exportTransparency;
            saveSettings();
        }

        /**
         * Vrátí poslední použitý adresář při otevíracím/ukládacím dialogu.
         *
         * @return poslední použitý adresář při otevíracím/ukládacím dialogu
         */
        public String getLastDir()
        {
            return lastDir;
        }

        /**
         * Nastaví poslední použitý adresář při otevíracím/ukládacím dialogu.
         *
         * @param lastDir poslední použitý adresář při otevíracím/ukládacím
         * dialogu
         */
        public void setLastDir(String lastDir)
        {
            this.lastDir = lastDir;
            saveSettings();
        }

        /**
         * Vrátí umístění souboru aktuálně načteného vývojového diagramu.
         *
         * @return umístění souboru aktuálně načteného vývojového diagramu,
         * null, když vývojový diagram není uložen
         */
        public File getActualFlowchartFile()
        {
            return actualFlowchartFile;
        }

        /**
         * Nastaví umístění souboru aktuálně načteného vývojového diagramu
         *
         * @param actualFlowchartFile umístění souboru aktuálně načteného
         * vývojového diagramu
         */
        public void setActualFlowchartFile(File actualFlowchartFile)
        {
            this.actualFlowchartFile = actualFlowchartFile;
            saveSettings();
        }

        /**
         * Vrátí true, je-li nastaveno načtení posledního diagramu po spuštění
         * aplikace.
         *
         * @return true, je-li nastaveno načtení posledního diagramu po spuštění
         * aplikace
         */
        public boolean isLoadLastFlowchart()
        {
            return loadLastFlowchart;
        }

        /**
         * Nastaví, zda se má po spuštění aplikace načítat poslední uložený
         * vývojový diagram
         *
         * @param loadLastFlowchart true, když se má po spuštění aplikace
         * načítat poslední uložený vývojový diagram
         */
        public void setLoadLastFlowchart(boolean loadLastFlowchart)
        {
            this.loadLastFlowchart = loadLastFlowchart;
            saveSettings();
        }

        public boolean isDontSaveDirectly()
        {
            return dontSaveDirectly;
        }

        public void setDontSaveDirectly(boolean dontSaveDirectly)
        {
            this.dontSaveDirectly = dontSaveDirectly;
            saveSettings();
        }

        /**
         * Vrátí textovou reprezentaci layoutu, který je právě vybrán, nebo má
         * být vybrán.
         *
         * @return textovou reprezentaci layoutu, který je právě vybrán, nebo má
         * být vybrán
         */
        public String getSelectedLayout()
        {
            return selectedLayout;
        }

        /**
         * Nastaví textovou reprezentaci layoutu, který je právě vybrán, nebo má
         * být vybrán.
         *
         * @param selectedLayout textová reprezentaci layoutu, který je právě
         * vybrán, nebo má být vybrán
         */
        public void setSelectedLayout(String selectedLayout)
        {
            this.selectedLayout = selectedLayout;
            saveSettings();
        }

        /**
         * Vrátí hodnotu FPS, kterou má být obnovována animace.
         * <p/>
         * @return hodnota FPS, kterou má být obnovována animace
         */
        public int getFps()
        {
            return fps;
        }

        /**
         * Nastaví hodnotu FPS, kterou má být obnovována animace.
         * <p/>
         * @param fps hodnota FPS, kterou má být obnovována animace
         */
        public void setFps(int fps)
        {
            this.fps = fps;
            saveSettings();
        }

        public String getProxyHost()
        {
            return proxyHost;
        }

        public void setProxyHost(String proxyHost)
        {
            this.proxyHost = proxyHost;
            saveSettings();
        }

        public int getProxyPort()
        {
            return proxyPort;
        }

        public void setProxyPort(int proxyPort)
        {
            this.proxyPort = proxyPort;
            saveSettings();
        }

        public Boolean getAssociateExtension()
        {
            return associateExtension;
        }

        public void setAssociateExtension(Boolean associateExtension)
        {
            this.associateExtension = associateExtension;
        }

    }

}
