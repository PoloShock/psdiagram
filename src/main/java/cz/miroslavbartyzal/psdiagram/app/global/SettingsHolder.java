/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.AbstractLayout;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts.EnumLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Tato třída nese globální nastavení aplikace.</p>
 * <p>Nastavení je po jeho změně automaticky ihned ukládáno na pevný disk do
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
    private static final File settingsFile = new File(WORKING_DIR, "settings.xml");

    // automaticke loadovani nastaveni
    static {
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
            try {
                settings = (Settings) jAXBcontext.createUnmarshaller().unmarshal(settingsFile);
                if (!settings.loadLastFlowchart || (settings.actualFlowchartFile != null && !settings.actualFlowchartFile.exists())) {
                    settings.actualFlowchartFile = null;
                }
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        } else {
            settings = new Settings();
            saveSettings();
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
    public static final String TIMESERVER = "http://seznam.cz";
    public static final String PSDIAGRAM_SERVER = ResourceBundle.getBundle("appliaction").getString(
            "psdiagramWebUrl");

    /**
     * Uloží aktuální nastavení do souboru (user.home)/.psdiagram/settings.xml.
     */
    private static void saveSettings()
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

    /**
     * Tato třída reprezentuje nastavení aplikace.
     */
    @XmlRootElement(name = "settings")
    @XmlAccessorType(XmlAccessType.NONE)
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
        // nastavení filtrů funkcí
        @XmlElement(name = "functionFilters")
        private boolean functionFilters = true;
        // poslední použitý adresář - informace pro nacitani/ukladani
        @XmlElement(name = "lastDir")
        private String lastDir;
        @XmlElement(name = "loadLastFlowchart")
        private boolean loadLastFlowchart = true;
        // aktuální, uložený soubor diagramu - slouží také k načtení při příštím spuštění
        @XmlElement(name = "actualFlowchartFile")
        private File actualFlowchartFile;
        @XmlElement(name = "exportTransparency")
        private boolean exportTransparency = true;
        @XmlElement(name = "exportFlowchartPadding")
        private int exportFlowchartPadding = AbstractLayout.flowchartPadding;
        @XmlElement(name = "ltlt")
        private long lastTrialLaunchedTime = 1350252860443l;
        @XmlElement(name = "proxyHost", defaultValue = "")
        private String proxyHost = "";
        @XmlElement(name = "proxyPort")
        private int proxyPort = -1;

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
         * Vrátí, zda jsou povoleny syntaktické filtry.
         *
         * @return true, když jsou povoleny syntaktické filtry
         */
        public boolean isFunctionFilters()
        {
            return functionFilters;
        }

        /**
         * Nastaví, zda mají být používány syntaktické filtry.
         *
         * @param functionFilters true, když mají být použity syntaktické filtry
         */
        public void setFunctionFilters(boolean functionFilters)
        {
            this.functionFilters = functionFilters;
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

        /**
         * Vrátí čas předchozího spuštění aplikace v milisekundách.
         * <p/>
         * @return čas předchozího spuštění aplikace v milisekundách
         */
        public long getLastTrialLaunchedTime()
        {
            return lastTrialLaunchedTime;
        }

        /**
         * Nastaví čas předchozího spuštění aplikace v milisekundách
         * <p/>
         * @param lastTrialLaunchedTime nový čas předchozího spuštění aplikace v milisekundách
         */
        public void setLastTrialLaunchedTime(long lastTrialLaunchedTime)
        {
            this.lastTrialLaunchedTime = lastTrialLaunchedTime;
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

    }

}
