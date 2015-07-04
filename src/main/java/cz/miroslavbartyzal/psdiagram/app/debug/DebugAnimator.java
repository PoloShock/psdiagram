/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.GotoLabel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Goto;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.debug.function.FunctionManager;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * <p>
 * Tato třída zajišťuje vykreslování vývojového diagramu při spuštěném
 * animačním
 * režimu aplikace. Inicializuje barvy, časové intervaly animace a zajišťuje
 * pohyb průchozí kuličky na jehož základě generuje pozice stínů symbolů a
 * jejich ozáření.</p>
 * <p>
 * Vykreslení vývojového diagramu v animačním režimu je zpracováváno jinak,
 * než je tomu u ostatních režimů aplikace.<br />
 * Vývojový diagram je zanalyzován a
 * uložen v paměti, aby nedocházelo ke zbytečným vedlejším operacím při jeho
 * vykreslování a animace tak byla pokud možno plynulá i na starších
 * počítačích.<br />
 * Z tohoto důvodu by po samotné inicializaci diagramu touto třídou již nemělo
 * dojít k jakékoliv změně layoutu, neboť by to mohlo přinést neočekávané chyby
 * ve vykreslování.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class DebugAnimator
{

    //private final int FPS = 40; // orientacni hodnota
    private final int DROPSCENE_DELAYMS = 500;
    private final int PLAYPATH_DELAYMS_MAX = 3500;
    private final int PLAYPATH_DELAYPIXEL_MAX = 350;
    private final int PLAYPATH_DELAYMS_MIN = 1000;
    //private final int PLAYPATHDELAYPIXELMIN = 100;
    private final float SLIDER_DECREASELIMIT = 0.25f;
    private final float SLIDER_INCREASELIMIT = 4f;
    private final int AFTERDONE_DELAYMS = 300;
    //private final int SKIPPATHDELAYMS = 400;
    //private final int FLOATLABELDELAYMS = 1000;
    private final int BACKGROUNDGRAY = 50;
    private final Color PATHCOLOR = new Color(150, 150, 150);
    private final Color PATHBASECOLOR = new Color(50, 106, 169);
    private final Color SHADOWCOLOR = new Color(0, 0, 0, 150);
    private final float PATHMIN_SATURATION_PERCENTAGE = 0.1f; // 1 = nejsytejsi, 0 = bila
    private final float PATHMAX_BRIGHTNESS_PERCENTAGE = 1.0f; // 1 = nejjasnejsi, 0 = cerna
    private final int BALLSHINE_SIZE = 50; // prumer i se zari
    private final float BALLRADIUS = BALLSHINE_SIZE / 2f; // polomer i se zari
    private final float BALLSIZE_PERCENTAGE = 0.22f; // velikost maximalne symbolpadding
    private Ellipse2D ball = null;
    private final float[] BALLDIST = {0.0f, BALLSIZE_PERCENTAGE * 0.6f, BALLSIZE_PERCENTAGE, 0.35f,
        1.0f};
    private Color ballColor;//new Color(50, 106, 169);
    private Color[] ballGradientColors;
    private RadialGradientPaint ballGradient = null;
    private RadialGradientPaint ballShineGradient = null;
    private Layout layout;
    private JPanel jPanelDiagram;
    private JSlider jSliderSpeed;
    private final PlayTimer playTimer = new PlayTimer();
    private DropSceneTimer dropSceneTimer;
    private FunctionManager functionManager;
    private Symbol activeSymbol = null;
    private Color[] pathColors = new Color[25 + 1]; // +1 pro neproslou cestu
    private final Color[] SEGMENTDESCS_COLORS = new Color[]{new Color(170, 170, 170), new Color(200,
        200, 200)
    };
    private final Color PROGRESSDESCS_COLOR = new Color(207, 190, 255);//new Color(231, 215, 75);
    private ArrayList<Color[]> symbolColors = new ArrayList<>(); // vzdy dva index color - horni a dolni gradient
    private final Color BREAKPOINT_COLOR = new Color(159, 33, 32);//new Color(141, 46, 48);
    private final Color ACTIVESYMBOL_COLOR = new Color(86, 192, 233);
    private final Color ERROR_SYMBOL_COLOR = new Color(141, 46, 48);
    private TreeMap<Integer, ArrayList<Path2D[]>> paths = new TreeMap<>(); // bez komentarovych a goto cest
    //private HashMap<Path2D[], Integer> paths = new HashMap<>(70); // bez komentarovych a goto cest
    //private LinkedHashMap<Integer, ArrayList<AnimSymbol>> symbols = new LinkedHashMap<>();
    private HashMap<DebugSymbol, Integer> symbols = new HashMap<>(30); // bez komentaru
    private LinkedHashMap<Integer, HashMap<TextLayout, Point2D>> segmentDescs = new LinkedHashMap<>();
    private ArrayList<Path2D> commentPaths = new ArrayList<>();
    private ArrayList<Path2D> gotoPaths = new ArrayList<>();
    private ArrayList<DebugSymbol> commentSymbols = new ArrayList<>();
    private BasicStroke mainStroke = new BasicStroke(2);
    private BasicStroke gotoStroke;
    private BasicStroke commentStroke;
    private boolean reinitSymbols = true;

    /**
     * Parametry tohoto konstruktoru inicializují potřebné spojení s komponenty,
     * ovlivňující zobrazení animace.
     *
     * @param layout instance třídy Layout, obsahující vývojový diagram k
     * procházení
     * @param jPanelDiagram JPanel, který má sloužit jako kreslící plátno
     * @param jSliderSpeed JSlider ovlivňující rychlost kuličky
     * @param functionManager FunctionManager zajišťující logickou funkčnost
     * diagramu
     */
    public DebugAnimator(Layout layout, JPanel jPanelDiagram, JSlider jSliderSpeed,
            FunctionManager functionManager)
    {
        this.layout = layout;
        this.jPanelDiagram = jPanelDiagram;
        this.jSliderSpeed = jSliderSpeed;
        this.functionManager = functionManager;

        //barvy levelu symbolu
        symbolColors.add(new Color[]{new Color(BACKGROUNDGRAY, BACKGROUNDGRAY, BACKGROUNDGRAY) // upGrad
            , new Color(BACKGROUNDGRAY, BACKGROUNDGRAY, BACKGROUNDGRAY) // downGrad
            , SEGMENTDESCS_COLORS[0] // text
        });
        symbolColors.add(new Color[]{new Color(150, 153, 200) // upGrad
            , new Color(104, 120, 186) // downGrad
            , new Color(49, 34, 94) // text
        });

        // vypocet barevnych levelu cest
        pathColors[0] = PATHCOLOR;
        pathColors[1] = PATHBASECOLOR;
        float hsbVals[] = Color.RGBtoHSB(PATHBASECOLOR.getRed(), PATHBASECOLOR.getGreen(),
                PATHBASECOLOR.getBlue(), null);
        // je treba 1. a 2. stopu odlisit vyrazneji
        hsbVals[1] += (PATHMIN_SATURATION_PERCENTAGE - hsbVals[1]) / (pathColors.length - 2) * (pathColors.length / 2);
        hsbVals[2] += (PATHMAX_BRIGHTNESS_PERCENTAGE - hsbVals[2]) / (pathColors.length - 2) * (pathColors.length / 2);
        float fractionH = 1.0f / (pathColors.length - 2);
        float fractionS = (PATHMIN_SATURATION_PERCENTAGE - hsbVals[1]) / (pathColors.length - 2);
        float fractionB = (PATHMAX_BRIGHTNESS_PERCENTAGE - hsbVals[2]) / (pathColors.length - 2);
        for (int i = 1; i + 1 < pathColors.length; i++) {
            pathColors[i + 1] = Color.getHSBColor(fractionH * i + hsbVals[0],
                    fractionS * i + hsbVals[1], fractionB * i + hsbVals[2]);
        }

        gotoStroke = new BasicStroke(mainStroke.getLineWidth(), layout.getGotoStroke().getEndCap(),
                layout.getGotoStroke().getLineJoin(), layout.getGotoStroke().getMiterLimit(),
                layout.getGotoStroke().getDashArray(), layout.getGotoStroke().getDashPhase());
        commentStroke = new BasicStroke(mainStroke.getLineWidth(),
                layout.getCommentStroke().getEndCap(), layout.getCommentStroke().getLineJoin(),
                layout.getCommentStroke().getMiterLimit(), layout.getCommentStroke().getDashArray(),
                layout.getCommentStroke().getDashPhase());

        dropSceneTimer = new DropSceneTimer(BACKGROUNDGRAY, (int) DROPSCENE_DELAYMS / (1000 / 25));
        dropSceneTimer.setDelay(1000 / 25);
    }

    /**
     * <p>
     * Inicializuje vývojový diagram pro animační režim aplikace a spustí
     * přechodový efekt.</p>
     * <p>
     * Inicializace zahrnuje načtení všech symbolů diagramu a jeho spojnic.
     * Po této operaci by mělo být zajištěno, aby nedošlo k jakékoliv změně
     * layoutu v průběhu animace.</p>
     */
    public void init()
    {
        symbols.clear();
        commentSymbols.clear();
        paths.clear();
        paths.put(0, new ArrayList<Path2D[]>());
        paths.put(1, new ArrayList<Path2D[]>()); // vytvorim rovnou i misto pro prosle cesty
        gotoPaths.clear();
        commentPaths.clear();
        segmentDescs.clear();
        segmentDescs.put(0, new HashMap<TextLayout, Point2D>());

        for (LayoutSegment segment : layout.getFlowchart()) {
            if (segment != null) {
                if (!segment.getPathToThisSegment().getPathIterator(null).isDone()) {
                    paths.get(0).add(layout.shouldBeArrow(segment.getPathToThisSegment()));
                }
                if (segment.getDescriptionLayout() != null) {
                    segmentDescs.get(0).put(segment.getDescriptionLayout(),
                            segment.getDescriptionLocation());
                }

                boolean lastGoto = false;
                for (LayoutElement element : segment) {
                    if (!(element.getSymbol() instanceof Comment)) {
                        symbols.put(initAnimSymbol(new DebugSymbol(element.getSymbol()),
                                symbolColors.get(0)[0], symbolColors.get(0)[1]), 0);
                    }
                    boolean EndStartEnd = element.getSymbol() instanceof StartEnd && (element.getParentSegment().getParentElement() != null || element.getParentSegment().indexOfElement(
                            element) > 1 || (element.getParentSegment().indexOfElement(element) == 1 && !(element.getParentSegment().getElement(
                                    0).getSymbol() instanceof Comment)));
                    if (!(element.getSymbol() instanceof Comment) && !(element.getSymbol() instanceof Goto) && !(EndStartEnd)) {
                        if (!element.getPathToNextSymbol().getPathIterator(null).isDone()) {
                            paths.get(0).add(layout.shouldBeArrow(element.getPathToNextSymbol()));
                        }
                        lastGoto = false;
                        if (element.getSymbol() instanceof GotoLabel) { // je treba goto vytvorit cestu z jeho Hair
                            GotoLabel gotoLabel = (GotoLabel) element.getSymbol();
                            Path2D gotoLabelPath = new Path2D.Double();
                            gotoLabelPath.moveTo(gotoLabel.getCenterX() - gotoLabel.getMyHair(),
                                    gotoLabel.getCenterY());
                            gotoLabelPath.lineTo(gotoLabel.getCenterX(), gotoLabel.getCenterY());
                            paths.get(0).add(new Path2D[]{gotoLabelPath, null});
                        }
                    } else if (element.getSymbol() instanceof Goto || (EndStartEnd)) {
                        gotoPaths.add(element.getPathToNextSymbol());
                        lastGoto = true;
                    } else {
                        commentSymbols.add(initAnimSymbol(new DebugSymbol(element.getSymbol()),
                                symbolColors.get(0)[0], symbolColors.get(0)[0]));
                        commentPaths.add(element.getPathToNextSymbol());
                    }
                }
                if (!lastGoto) {
                    paths.get(0).add(layout.shouldBeArrow(segment.getPathFromThisSegment()));
                } else {
                    if (!segment.getPathFromThisSegment().getPathIterator(null).isDone()) {
                        gotoPaths.add(segment.getPathFromThisSegment());
                    }
                }
            }
        }

        reinitSymbols = true;
        jPanelDiagram.repaint();
        //reInitPathsAndDesc(); // zde si dovolim diagram projet znovu kvuli sipkam, aby nevznikla duplicita kodu. Stejne metoda entryAnimMode() se vola jen na zacatku, pri vstupu do animacniho modu

        /*
         * UIManager.put("nimbusBase", symbolColors.get(0)[0]);
         * UIManager.put("nimbusBlueGrey", symbolColors.get(0)[0]);
         * UIManager.put("control", symbolColors.get(0)[0]);
         */
        dropSceneTimer.start();
    }

    /**
     * Vrací stav animace průchodu diagramem.
     *
     * @return true - probíhá animace<br />false - animace neprobíhá
     */
    public boolean isPlaying()
    {
        return playTimer.isRunning();
    }

    /**
     * Nastaví středovou pozici kuličky na dané souřadnice.
     *
     * @param x Xová souřadnice
     * @param y Yová souřadnice
     */
    public void setBallToPos(double x, double y)
    {
        if (ball != null && !playTimer.isRunning()) {
            playTimer.setBall(x, y);
            jPanelDiagram.repaint();
        }
    }

    /**
     * Vrací aktuální středovou pozici kuličky.
     *
     * @return aktuální středová pozice kuličky
     */
    public Point2D getBallCoordenates()
    {
        if (ball != null) {
            return new Point2D.Double(ball.getCenterX(), ball.getCenterY());
        } else {
            return null;
        }
    }

    /**
     * Metoda pro zjištění, zda kulička obsahuje zadané souřadnice.
     *
     * @param x Xová souřadnice
     * @param y Yová souřadnice
     * @return true, když kulička obsahuje zadané souřadnice<br />false, když
     * kulička zadané souřadnice neobsahuje
     */
    public boolean ballContains(double x, double y)
    {
        if (ball != null) {
            return (new Point2D.Double(ball.getCenterX(), ball.getCenterY())).distance(x, y) <= BALLRADIUS * BALLSIZE_PERCENTAGE;
        } else {
            return false;
        }
    }

    /**
     * Metoda, pro odstartování animace.
     */
    public void play()
    {
        playTimer.start();
    }

    /**
     * Vrací stav bufferu animace. Je-li buffer naplněn, znamená to, že animace
     * nebyla dosud dokončena a kulička ještě nedosáhla cílové pozice.
     *
     * @return true, když je buffer naplněn<br />false, když buffer není naplněn
     */
    public boolean isPlayBuffered()
    {
        return playTimer.isBuffered();
    }

    /**
     * Metoda pro okamžité pozastavení animace.
     */
    public void pause()
    {
        playTimer.pause();
        jPanelDiagram.repaint();
    }

    /**
     * Metoda pro resetování celého průchodu vývojového diagramu do původního.
     * neprošlého stavu. Animace je také zastavena a její buffer vyprázdněn.
     */
    public void stop()
    {
        dropSceneTimer.stop();
        playTimer.stop();

        // presypu vsechny do nulte barvy, vymazu progressDescs
        for (DebugSymbol animSymbol : symbols.keySet()) {
            animSymbol.setProgressDesc(null);
            animSymbol.setProgressString(null);
            symbols.put(animSymbol, 0);
        }
        for (Iterator<Integer> it = paths.keySet().iterator(); it.hasNext();) {
            int i = it.next();
            if (i != 0) {
                paths.get(0).addAll(paths.get(i));
                it.remove();
            }
        }
        for (Iterator<Integer> it = segmentDescs.keySet().iterator(); it.hasNext();) {
            int i = it.next();
            if (i != 0) {
                segmentDescs.get(0).putAll(segmentDescs.get(i));
                it.remove();
            }
        }

        reinitSymbols = true;
        jPanelDiagram.repaint();
    }

    /**
     * Metoda, zajišťující vykreslení vývojového diagramu na plátno.
     *
     * @param g2d Instance Graphics2D, která se má použít k vykreslení diagramu
     */
    public void paintFlowchart(Graphics2D g2d)
    {
        Stroke prevStroke = g2d.getStroke();
        g2d.setStroke(mainStroke);

        // vykresleni cest
        for (int i : paths.keySet()) {
            int colorIndex = i;
            if (colorIndex >= pathColors.length) {
                colorIndex = pathColors.length - 1;
            }
            g2d.setColor(pathColors[colorIndex]);
            for (Path2D[] path : paths.get(i)) {
                g2d.draw(path[0]);
                if (path[1] != null) {
                    g2d.fill(path[1]);
                }
            }
        }

        // vykresleni animovane cesty
        if (!playTimer.completedLines.isEmpty()) {
            int colorIndex = playTimer.processElevation;
            if (colorIndex >= pathColors.length) {
                colorIndex = pathColors.length - 1;
            }
            g2d.setColor(pathColors[colorIndex]);
            for (Line2D line : playTimer.completedLines) {
                g2d.draw(line);
            }
        }
        if (playTimer.lineInProcess != null) {
            int colorIndex = playTimer.processElevation;
            if (colorIndex >= pathColors.length) {
                colorIndex = pathColors.length - 1;
            }
            g2d.setColor(pathColors[colorIndex]);
            g2d.draw(playTimer.lineInProcess);
        }

        // vykresleni goto cest
        if (gotoPaths.size() > 0) {
            g2d.setColor(pathColors[0]);
            g2d.setStroke(gotoStroke);
            for (Path2D path : gotoPaths) {
                g2d.draw(path);
            }
            g2d.setStroke(mainStroke);
        }

        // vykresleni stínu symbolů
        for (DebugSymbol debugSymbol : symbols.keySet()) {
            if (reinitSymbols) {
                int colorSchemeIndex = symbols.get(debugSymbol);
                if (colorSchemeIndex >= symbolColors.size()) {
                    colorSchemeIndex = symbolColors.size() - 1;
                }
                initAnimSymbol(debugSymbol, symbolColors.get(colorSchemeIndex)[0], symbolColors.get(
                        colorSchemeIndex)[1]);
            }
            drawAnimSymbolShade(g2d, debugSymbol);
        }
        /*
         * if (commentSymbols.size() > 0) {
         * for (AnimSymbol animCommentSymbol: commentSymbols) {
         * if (reinitSymbols) {
         * initAnimSymbol(animCommentSymbol, symbolColors.get(0)[0],
         * symbolColors.get(0)[1]);
         * }
         * drawAnimSymbolShade(g2d, animCommentSymbol);
         * }
         * }
         */
        reinitSymbols = false;

        // vykresleni kulicky
        if (ball != null) {
            g2d.setPaint(ballGradient);
            g2d.fill(ball);
        }

        // vykreslení symbolů
        for (DebugSymbol debugSymbol : symbols.keySet()) {
            drawAnimSymbolSymbol(g2d, debugSymbol, symbols.get(debugSymbol));
        }

        // vykresleni popisků segmentů
        for (int i : segmentDescs.keySet()) {
            int colorIndex = i;
            if (colorIndex >= SEGMENTDESCS_COLORS.length) {
                colorIndex = SEGMENTDESCS_COLORS.length - 1;
            }
            g2d.setColor(SEGMENTDESCS_COLORS[colorIndex]);
            HashMap<TextLayout, Point2D> descs = segmentDescs.get(i);
            for (Map.Entry<TextLayout, Point2D> entry : descs.entrySet()) {
                entry.getKey().draw(g2d, (float) entry.getValue().getX(),
                        (float) entry.getValue().getY());
            }
        }

        // vykresleni komentarovych cest
        if (commentPaths.size() > 0) {
            g2d.setColor(PATHCOLOR);
            g2d.setStroke(commentStroke);
            for (Path2D path : commentPaths) {
                g2d.draw(path);
            }
            g2d.setStroke(mainStroke);
        }
        // vykresleni komentaru
        if (commentSymbols.size() > 0) {
            for (DebugSymbol animCommentSymbol : commentSymbols) {
                drawAnimSymbolSymbol(g2d, animCommentSymbol, 0);
            }
        }

        // vykresleni breakpointu
        g2d.setColor(BREAKPOINT_COLOR);
        g2d.setStroke(gotoStroke);
        for (Symbol breakSymbol : functionManager.getBreakpointSymbols()) {
            double focusedWidth = breakSymbol.getWidth();
            if (breakSymbol instanceof GotoLabel) {
                focusedWidth /= 2;
            }
            g2d.draw(new Rectangle2D.Double(breakSymbol.getX() - 5, breakSymbol.getY() - 4,
                    focusedWidth + 10 - gotoStroke.getLineWidth() / 2, breakSymbol.getHeight() + 8));
        }
        g2d.setStroke(mainStroke);

        // vyskresleni activeSymbol ramecku
        if (!playTimer.isRunning() && activeSymbol != null) {
            g2d.setColor(ACTIVESYMBOL_COLOR);
            g2d.setStroke(gotoStroke);
            double focusedWidth = activeSymbol.getWidth();
            if (activeSymbol instanceof GotoLabel) {
                focusedWidth /= 2;
            }
            g2d.draw(new Rectangle2D.Double(activeSymbol.getX() - 3, activeSymbol.getY() - 2,
                    focusedWidth + 6 - gotoStroke.getLineWidth() / 2, activeSymbol.getHeight() + 4));

            g2d.setStroke(mainStroke);
        }

        // vykresleni udaju o prubehu
        for (DebugSymbol animSymbol : symbols.keySet()) {
            if (animSymbol.getProgressDesc() != null) {
                g2d.setColor(new Color(0, 0, 0, 100));
                animSymbol.getProgressDesc().draw(g2d,
                        (float) animSymbol.getProgressDescPoint().getX() + 0.5f,
                        (float) animSymbol.getProgressDescPoint().getY() + 0.5f);
                g2d.setColor(PROGRESSDESCS_COLOR);
                animSymbol.getProgressDesc().draw(g2d,
                        (float) animSymbol.getProgressDescPoint().getX(),
                        (float) animSymbol.getProgressDescPoint().getY());
            }
        }

        g2d.setStroke(prevStroke);
    }

    /**
     * Vrací text s informací o zpracované funkci daného symbolu.
     *
     * @param symbol symbol, jehož text s informací o zpracované funkci má být
     * vrácen
     * @return text s informací o zpracované funkci daného symbolu
     */
    public String getSymbolProgressDesc(Symbol symbol)
    {
        return findAnimSymbol(symbol).getProgressString();
    }

    /**
     * Metoda, která zajistí animaci daných spojnicí a daného symbolu, kterému
     * zároveň nastaví daný text s informací o jeho zpracované funkci.<br />
     * Jedná se funkčně o stejnou metodu jako metoda doPath, s tím rozdílem, že
     * elevace je dosaženo animací.
     *
     * @param fromSymbol symbol, který má bát elevován jako zpracovaný
     * @param progressDesc text s informací o zpracované funkci, který se
     * přiřadí danému symbolu
     * @param throughPaths spojnice, které mají být animací elevovány
     * @param segmentDesc deskripce segmentu, která má být elevována (Ano/Ne u
     * podmínky)
     */
    public void animPath(Symbol fromSymbol, String progressDesc, Path2D[] throughPaths,
            TextLayout segmentDesc)
    {
        playTimer.animPath(fromSymbol, progressDesc, throughPaths, segmentDesc);
    }

    /**
     * Metoda, která zajistí bezanimační elevaci daných spojnicí, daného symbolu
     * a dané deskripce segmentu. Danému symbolu zároveň nastaví text s
     * informací o zpracované funkci. Je možné elevovat i záporně, čehož se dá
     * využít jako funkce "krok zpět".
     *
     * @param fromSymbol symbol, který má bát elevován jako zpracovaný
     * @param progressDesc text s informací o zpracované funkci, který se
     * přiřadí danému symbolu
     * @param throughPaths spojnice, které mají být elevovány
     * @param segmentDesc deskripce segmentu, která má být elevována (Ano/Ne u
     * podmínky)
     * @param elevate true - elevovat údaje jako prošlé<br />false - elevovat o
     * krok směrem zpět
     */
    public void doPath(Symbol fromSymbol, String progressDesc, Path2D[] throughPaths,
            TextLayout segmentDesc, boolean elevate)
    {
        if (playTimer.isRunning() || playTimer.isBuffered()) {
            if (elevate) {
                playTimer.finalizeIt();
            }
            playTimer.stop();
        }

        elevateSymbolProgressSdescs(fromSymbol, progressDesc, throughPaths, segmentDesc, elevate);
        elevateAnalyzedPaths(getAnalyzedPaths(throughPaths), elevate);

        jPanelDiagram.repaint();
    }

    private void elevateSymbolProgressSdescs(Symbol fromSymbol, String progressDesc,
            Path2D[] throughPaths, TextLayout segmentDesc, boolean elevate)
    {
        DebugSymbol animSymbol = findAnimSymbol(fromSymbol);
        if (progressDesc == null || progressDesc.equals("")) {
            animSymbol.setProgressDesc(null);
        } else {
            animSymbol.setProgressDesc(new TextLayout(progressDesc, SettingsHolder.CODEFONT,
                    SettingsHolder.FONTRENDERCONTEXT));
        }
        animSymbol.setProgressString(progressDesc);

        // elevace symbolu
        boolean doSymbol = true;
        if (fromSymbol instanceof GotoLabel) {
            if (throughPaths != null) {
                GotoLabel gotoLabel = (GotoLabel) fromSymbol;
                Path2D gotoLabelPath = new Path2D.Double();
                gotoLabelPath.moveTo(gotoLabel.getCenterX() - gotoLabel.getMyHair(),
                        gotoLabel.getCenterY());
                gotoLabelPath.lineTo(gotoLabel.getCenterX(), gotoLabel.getCenterY());
                doSymbol = equalPath(throughPaths[0], gotoLabelPath); // elevovat GotoLabel jen kdyz se vychazi primo z nej
            } else {
                doSymbol = false;
            }
        }
        if (doSymbol) {
            int symbolElevation;
            if (elevate) {
                symbolElevation = symbols.get(animSymbol) + 1;
            } else {
                symbolElevation = symbols.get(animSymbol) - 1;
            }
            if (symbolElevation >= 0) {
                if (symbolElevation >= symbolColors.size()) {
                    symbols.put(animSymbol, symbolElevation); // symbol jiz kdysi dosahl maximama, nemusim ho znovu inicializovat
                } else {
                    symbols.put(initAnimSymbol(animSymbol, symbolColors.get(symbolElevation)[0],
                            symbolColors.get(symbolElevation)[1]), symbolElevation);
                }
            }
        }

        // elevace segmentDesc
        if (segmentDesc != null) {
            search:
            for (int i : segmentDescs.keySet()) {
                for (TextLayout textLayout : segmentDescs.get(i).keySet()) {
                    if (textLayout.equals(segmentDesc)) {
                        if (elevate) {
                            if (segmentDescs.get(i + 1) == null) {
                                segmentDescs.put(i + 1, new HashMap<TextLayout, Point2D>());
                            }
                            segmentDescs.get(i + 1).put(textLayout, segmentDescs.get(i).remove(
                                    textLayout));
                        } else if (i > 0) {
                            segmentDescs.get(i - 1).put(textLayout, segmentDescs.get(i).remove(
                                    textLayout));
                        }
                        break search;
                    }
                }
            }
        }
    }

    private void elevateAnalyzedPaths(ArrayList<Map.Entry<Path2D[], Integer>> analyzedPaths,
            boolean elevate)
    {
        // elevace cesty
        for (Map.Entry<Path2D[], Integer> entry : analyzedPaths) {
            if (entry != null) {
                if (elevate) {
                    if (paths.get(entry.getValue() + 1) == null) {
                        paths.put(entry.getValue() + 1, new ArrayList<Path2D[]>());
                    }
                    paths.get(entry.getValue() + 1).add(paths.get(entry.getValue()).remove(
                            paths.get(entry.getValue()).indexOf(entry.getKey())));
                } else if (entry.getValue() > 0) {
                    paths.get(entry.getValue() - 1).add(paths.get(entry.getValue()).remove(
                            paths.get(entry.getValue()).indexOf(entry.getKey())));
                }
            }
        }
    }

    private ArrayList<Map.Entry<Path2D[], Integer>> getAnalyzedPaths(Path2D[] throughPaths)
    {
        ArrayList<Map.Entry<Path2D[], Integer>> analyzedPaths = new ArrayList<>();

        if (throughPaths != null && throughPaths.length > 0) {
            search:
            for (int i = 0; i < throughPaths.length; i++) {
                Path2D[] pth = layout.shouldBeArrow(throughPaths[i]);
                boolean createIt = false;

                if (i > 0) {
                    Path2D continualP = getContinualCurrentPath(throughPaths[i - 1], throughPaths[i]);
                    if (!equalPath(throughPaths[i], continualP)) {
                        createIt = true;
                        pth = layout.shouldBeArrow(continualP);
                    }
                }

                for (int j : paths.keySet()) {
                    for (Path2D[] path : paths.get(j)) {
                        if (equalPath(path[0], pth[0])) {
                            analyzedPaths.add(new AbstractMap.SimpleEntry<>(path, j));
                            continue search;
                        }
                    }
                }
                if (createIt) {
                    paths.get(0).add(pth);
                    analyzedPaths.add(new AbstractMap.SimpleEntry<>(pth, 0));
                } else {
                    // cesta nebyla nalezena a nema byt vytvorena
                    analyzedPaths.add(null);
                }
            }
        }

        return analyzedPaths;
    }

    private Path2D getContinualCurrentPath(Path2D prevPath, Path2D currentPath)
    {
        // nasleduje overeni, zda aktualni cesta navazuje na predchozi - kdyz ne a koncovy bod predchozi je obsazen v aktualni, nahradim aktualni novou, navazujici
        // tento postup se pouziva u expandovaneho switche
        double[] coordinates = new double[2];
        PathIterator pathIterator = currentPath.getPathIterator(null);
        pathIterator.currentSegment(coordinates);
        Point2D beginPoint = new Point2D.Double(coordinates[0], coordinates[1]);
        pathIterator.next();
        pathIterator.currentSegment(coordinates);
        Point2D nextPoint = new Point2D.Double(coordinates[0], coordinates[1]);

        Point2D prevEndPoint = prevPath.getCurrentPoint();

        if (!beginPoint.equals(prevEndPoint) && Line2D.ptSegDist(beginPoint.getX(),
                beginPoint.getY(), nextPoint.getX(), nextPoint.getY(), prevEndPoint.getX(),
                prevEndPoint.getY()) == 0) {
            Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO);
            path.moveTo(prevEndPoint.getX(), prevEndPoint.getY());
            path.lineTo(nextPoint.getX(), nextPoint.getY());
            pathIterator.next();
            while (!pathIterator.isDone()) {
                int type = pathIterator.currentSegment(coordinates);
                switch (type) {
                    case PathIterator.SEG_LINETO: {
                        path.lineTo(coordinates[0], coordinates[1]);
                        break;
                    }
                    default: {
                        throw new Error("Unexpected currentSegment!");
                    }
                }
                pathIterator.next();
            }
            return path;
        } else {
            return currentPath;
        }
    }

    /*
     * public void setProgressDesc(Symbol symbol, TextLayout textLayout) {
     * findAnimSymbol(symbol).setProgressDesc(textLayout);
     * }
     */
    /**
     * Nastaví daný symbol jako aktivní. Tímto se označí symbol, který čeká na
     * zpracování (např čeká na stisk tlačítka Krok vpřed)
     *
     * @param activeSymbol symbol, který má být nastaven jako aktivní
     */
    public void setActiveSymbol(Symbol activeSymbol)
    {
        this.activeSymbol = activeSymbol;
        jPanelDiagram.repaint();
    }

    private boolean equalPath(Path2D path1, Path2D path2)
    {
        return path1.getBounds2D().equals(path2.getBounds2D());
    }

    private DebugSymbol findAnimSymbol(Symbol symbol)
    {
        for (DebugSymbol animSymbol : symbols.keySet()) {
            if (animSymbol.getSymbol().equals(symbol)) {
                return animSymbol;
            }
        }
        return null;
    }

    private void drawAnimSymbolSymbol(Graphics2D g2d, DebugSymbol animSymbol, int colorSchemeIndex)
    {
        Color col = g2d.getColor();

        if (animSymbol.getSymbolGradient() != null) {
            g2d.setPaint(animSymbol.getSymbolGradient());
            g2d.fill(animSymbol.getSymbol().getShape()); // vykreslení symbolu s gradientem
        } else if (animSymbol.getSymbolColor() != null) {
            g2d.setPaint(animSymbol.getSymbolColor());
            g2d.fill(animSymbol.getSymbol().getShape()); // vykreslení symbolu bez gradientu
        }
        if (animSymbol.getBallShineGradient() != null) {
            g2d.setPaint(animSymbol.getBallShineGradient());
            g2d.fill(animSymbol.getSymbol().getShape()); // vykreslení záře od kulicky
        }
        if (colorSchemeIndex < pathColors.length) {
            g2d.setColor(pathColors[colorSchemeIndex]);
        } else {
            g2d.setColor(pathColors[pathColors.length - 1]);
        }
        if (animSymbol.getSymbol() instanceof GotoLabel) { // u goto nechci vykreslit Hair
            GotoLabel gotoLabel = (GotoLabel) animSymbol.getSymbol();
            g2d.draw(gotoLabel.getMyCircle());
        } else {
            if (SettingsHolder.settings.isFunctionFilters() && colorSchemeIndex == 0
                    && !animSymbol.getSymbol().areCommandsValid()
                    && EnumSymbol.getEnumSymbol(animSymbol.getSymbol().getClass()).areAllCommandsPresent(
                            layout.findMyElement(animSymbol.getSymbol()))) {
                g2d.setColor(ERROR_SYMBOL_COLOR);
                g2d.draw(animSymbol.getSymbol().getShape()); // vykreslení okraje symbolu
            } else {
                g2d.draw(animSymbol.getSymbol().getShape()); // vykreslení okraje symbolu
            }
        }
        // vykresleni textu symbolu
        if (colorSchemeIndex < symbolColors.size()) {
            g2d.setColor(symbolColors.get(colorSchemeIndex)[2]);
        } else {
            g2d.setColor(symbolColors.get(symbolColors.size() - 1)[2]);
        }
        for (int i = 0; i < animSymbol.getSymbol().getTextLayoutLines().size(); i++) {
            Point2D p = animSymbol.getSymbol().getTextLayoutOrigins().get(i);
            animSymbol.getSymbol().getTextLayoutLines().get(i).draw(g2d, (float) p.getX(),
                    (float) p.getY());
        }

        g2d.setColor(col);
    }

    private void drawAnimSymbolShade(Graphics2D g2d, DebugSymbol animSymbol)
    {
        AffineTransform af = g2d.getTransform();
        Color col = g2d.getColor();

        if (animSymbol.getShadeColor() != null) {
            g2d.translate(animSymbol.getShadeTransX(), animSymbol.getShadeTransY());
            g2d.setColor(animSymbol.getShadeColor());
            if (!(animSymbol.getSymbol() instanceof Comment)) {
                g2d.fill(animSymbol.getSymbol().getShape());
            } else {
                g2d.draw(animSymbol.getSymbol().getShape());
            }
        }

        g2d.setColor(col);
        g2d.setTransform(af);
    }

    private DebugSymbol initAnimSymbol(DebugSymbol animSymbol, Color shapeFirstColor,
            Color shapeSecondColor)
    {
        Symbol symbol = animSymbol.getSymbol();
        double symbolCenterX;
        double symbolWidth;
        // vypocet sirky a centerX symbolu
        if (!(symbol instanceof GotoLabel)) {
            symbolCenterX = symbol.getCenterX();
            symbolWidth = symbol.getWidth();
        } else {
            GotoLabel gotoLabel = (GotoLabel) symbol;
            symbolWidth = gotoLabel.getWidth() / 2 - gotoLabel.getMyHair();
            symbolCenterX = gotoLabel.getX() + symbolWidth / 2;
        }

        if (SettingsHolder.settings.isBallShine() && ballShineGradient != null && symbol.getShapeUpColor() != null && !symbol.contains(
                ballShineGradient.getCenterPoint())) {
            Point2D symbolCenterPoint = new Point2D.Double(symbolCenterX, symbol.getCenterY());
            Point2D ballShineCenterPoint = ballShineGradient.getCenterPoint();
            float ballShineRad = ballShineGradient.getRadius();
            if (ballShineCenterPoint.distance(symbolCenterPoint) < ballShineRad + symbolCenterPoint.distance(
                    symbol.getX(), symbol.getY())) { // kalkulovat jen kdyz kulicka dosviti, jinak to je wasting.. (priblizna kalkulace)
                Point2D interNearPoint = symbol.getIntersectionPoint(ballShineCenterPoint.getX(),
                        ballShineCenterPoint.getY());
                if (interNearPoint == null) { // nikdy by se to nemelo stat, ale pro jistotu ponechavam
                    interNearPoint = symbolCenterPoint;
                }
                //if (symbol.contains(ballShineCenterPoint) || ballShineCenterPoint.distance(interNearPoint) < ballShineRad) {// kalkulovat jen kdyz kulicka dosviti, jinak to je wasting.. (priblizna kalkulace)
                if (ballShineCenterPoint.distance(interNearPoint) < ballShineRad) {// kalkulovat jen kdyz kulicka dosviti, jinak to je wasting.. (priblizna kalkulace)

                    final double SHADOWMAXTRANSX = symbolWidth / 2;
                    final double SHADOWMAXTRANSY = symbol.getHeight() / 2;
                    /*
                     * double transX = 0;
                     * double transY = 0;
                     */

                    Point2D interFarPoint = symbol.getIntersectionPoint(
                            (symbolCenterX - ballShineCenterPoint.getX()) + symbolCenterX,
                            (symbol.getCenterY() - ballShineCenterPoint.getY()) + symbol.getCenterY());
                    if (interFarPoint == null) { // nikdy by se to nemelo stat, ale pro jistotu ponechavam
                        interFarPoint = symbolCenterPoint;
                    }

                    // vypocet gradientu symbolu vuci zari kulicky
                    double shineDist = interFarPoint.distance(ballShineCenterPoint);
                    Color[] colors = ballShineGradient.getColors();
                    if (shineDist > ballShineRad) {
                        shineDist = ballShineRad;
                    } else if (shineDist == 0) {
                        shineDist = symbol.getHeight();
                    }
                    if (shapeFirstColor.equals(shapeSecondColor) && colors.length == 2) {
                        colors = new Color[]{colors[0], shapeFirstColor}; // melo by byt rychlejsi vykresleni solidni barvy, nez-li transparentni
                    }
                    animSymbol.setBallShineGradient(new RadialGradientPaint(ballShineCenterPoint,
                            (float) shineDist, ballShineGradient.getFocusPoint(),
                            ballShineGradient.getFractions(), colors,
                            ballShineGradient.getCycleMethod()));

                    // vypocet stinu
                    //if (!symbol.contains(ballShineCenterPoint)) {
                    shineDist = interNearPoint.distance(ballShineCenterPoint);
                    double transX = SHADOWMAXTRANSX * (interNearPoint.getX() - ballShineCenterPoint.getX()) / ballShineRad;
                    double transY = SHADOWMAXTRANSY * (interNearPoint.getY() - ballShineCenterPoint.getY()) / ballShineRad;
                    animSymbol.setShadeColor(new Color(0, 0, 0,
                            (int) (SHADOWCOLOR.getAlpha() * (1 - shineDist / ballShineRad))));
                    /*
                     * } else {
                     * debugSymbol.setShadeColor(null);
                     * }
                     */
                    animSymbol.setShadeTransX(transX);
                    animSymbol.setShadeTransY(transY);

                } else { //nedosviti
                    setNoShine(animSymbol);
                }
            } else { //nedosviti
                setNoShine(animSymbol);
            }
        } else { //nedosviti
            setNoShine(animSymbol);
        }

        if (symbol.getShapeUpColor() != null) { // neni-li symbol transparentni
            if (shapeFirstColor.equals(shapeSecondColor)) {
                animSymbol.setSymbolGradient(null);
                animSymbol.setSymbolColor(shapeFirstColor);
            } else {
                animSymbol.setSymbolColor(null);
                animSymbol.setSymbolGradient(new GradientPaint(
                        (float) (symbol.getX() + symbolWidth * 0.25), (float) symbol.getY(),
                        shapeFirstColor, (float) (symbol.getX() + symbolWidth * 0.75),
                        (float) (symbol.getY() + symbol.getHeight()), shapeSecondColor));
            }
        } else {
            animSymbol.setSymbolGradient(null);
            animSymbol.setSymbolColor(null);
        }

        return animSymbol;
    }

    private void setNoShine(DebugSymbol animSymbol)
    {
        animSymbol.setBallShineGradient(null);
        animSymbol.setShadeColor(null);
        animSymbol.setShadeTransX(0);
        animSymbol.setShadeTransY(0);
    }

    private void changeBallColor(Color color)
    {
        ballColor = color;
        ballGradientColors = new Color[]{color, color.darker(), color.darker().darker(), new Color(
            color.getRed(), color.getGreen(), color.getBlue(), 80), new Color(color.getRed(),
            color.getGreen(), color.getBlue(), 0)};
    }

    private double functionHalfCosinPercentage(double fragment, double count)
    {
        return 0.5 - Math.cos(fragment / count * Math.PI) / 2;
    }

    private double functionPower3Percentage(double fragment, double count)
    {
        return Math.pow((fragment / count - 1), 3) + 1;
        //return Math.sin(fragment/count/2*Math.PI);
        //return -Math.pow((fractionB/count-1),2)+1;
    }

    private final class DropSceneTimer extends Timer
    {

        private int loopcount;
        private int[] lColorsRow;

        public DropSceneTimer(int targetGrayColor, int loopsCount)
        {
            super(0, null);
            super.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    myActionPerformed();
                }
            });
            lColorsRow = new int[loopsCount];
            int backgroundColor = jPanelDiagram.getBackground().getRed();
            targetGrayColor = backgroundColor - targetGrayColor;
            for (int i = 0; i < loopsCount; i++) {
                lColorsRow[i] = (int) (backgroundColor - targetGrayColor * functionPower3Percentage(
                        i + 1, loopsCount));
            }
        }

        @Override
        public void start()
        {
            loopcount = 0;
            super.start();
        }

        private void myActionPerformed()
        {
            if (loopcount + 1 > lColorsRow.length) {
                super.stop();
                return;
            }
            jPanelDiagram.setBackground(new Color(lColorsRow[loopcount], lColorsRow[loopcount],
                    lColorsRow[loopcount]));
            loopcount++;
        }

    }

    private final class PlayTimer extends Timer
    {

        private ArrayList<Map.Entry<Path2D[], Integer>> analyzedPaths = new ArrayList<>(); // pro finalizaci
        private ArrayList<Line2D> completedLines = new ArrayList<>();
        private Line2D lineInProcess;
        private int processElevation = 0;
        private ArrayDeque<ArrayDeque<Line2D>> ballPaths = new ArrayDeque<>();
        private ArrayDeque<Point2D> arrows = new ArrayDeque<>();
        private double currentPathLength = 0;
        private double currentLineStartPointDist = 0;
        private double currentLineLength = 0;
        private int timePased = 0;
        private int timeToPassWholePath = 0;
        private boolean completeBegining = true;

        public PlayTimer()
        {
            super(0, null);
            super.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ae)
                {
                    myActionPerformed();
                }
            });
        }

        public void setBall(double x, double y)
        {
            if (ball == null) {
                ball = new Ellipse2D.Double(0, 0, BALLSHINE_SIZE, BALLSHINE_SIZE);
            }
            int colorIndex = processElevation;
            if (colorIndex >= pathColors.length) {
                colorIndex = pathColors.length - 1;
            }
            if (ballColor == null || !ballColor.equals(pathColors[colorIndex])) {
                changeBallColor(pathColors[colorIndex]);
            }

            Point2D centerP = new Point2D.Double(x, y);
            Point2D focusP = centerP;

            float sizePerc = 1;

            if (completeBegining && completedLines.isEmpty()) { // na uplnem zacatku
                double startDist = Point2D.distance(lineInProcess.getP1().getX(),
                        lineInProcess.getP1().getY(), x, y);
                if (startDist < BALLRADIUS * BALLSIZE_PERCENTAGE) {
                    double space = BALLRADIUS * BALLSIZE_PERCENTAGE;
                    if (currentPathLength == currentLineLength && currentPathLength < (BALLRADIUS * BALLSIZE_PERCENTAGE) * 2 && (activeSymbol == null || !(activeSymbol instanceof GotoLabel))) {
                        // jestli je nedostatek prostoru mezi dvema symboly, z nichz druhy neni gotoLabel
                        // budu resit jak vystup ze symbolu, tak vstup
                        if (startDist > currentLineLength / 2) {
                            startDist = currentLineLength - startDist;
                        }
                        space = currentLineLength / 2;
                    } else if (currentLineLength < space) {
                        // resim jen vystup ze symbolu
                        space = currentLineLength;
                    }

                    sizePerc = (float) (startDist / space);
                } else {
                    completeBegining = false;
                }
            } else if (ballPaths.size() == 1 && ballPaths.peek().size() == 1 && (activeSymbol == null || !(activeSymbol instanceof GotoLabel))) { // na uplnem konci, // resim goto jenom na konci, na zacatku budu mit completeBegining false, protoze se nevolal resetVars
                double endDist = Point2D.distance(x, y, ballPaths.peek().peek().getX2(),
                        ballPaths.peek().peek().getY2());
                if (endDist < BALLRADIUS * BALLSIZE_PERCENTAGE) {
                    // resim jen vstup do symbolu
                    double space = BALLRADIUS * BALLSIZE_PERCENTAGE;
                    if (currentLineLength < endDist) {
                        space = currentLineLength;
                    }

                    sizePerc = (float) (endDist / space);
                }
            }

            ball.setFrame(x - BALLSHINE_SIZE / 2 * sizePerc, y - BALLSHINE_SIZE / 2 * sizePerc,
                    BALLSHINE_SIZE * sizePerc, BALLSHINE_SIZE * sizePerc);

            if (sizePerc <= 0) {
                ballGradient = null;
                ballShineGradient = null;
            } else {
                ballGradient = new RadialGradientPaint(centerP, BALLRADIUS * sizePerc, focusP,
                        BALLDIST, ballGradientColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
                if (SettingsHolder.settings.isBallShine()) {
                    ballShineGradient = new RadialGradientPaint(centerP,
                            SettingsHolder.settings.getBallShineRadius() * sizePerc, new float[]{
                                0.0f, 1.0f}, new Color[]{ballColor, new Color(ballColor.getRed(),
                                        ballColor.getGreen(), ballColor.getBlue(), 0)},
                            MultipleGradientPaint.CycleMethod.NO_CYCLE);
                }
            }

            reinitSymbols = true;

//            // TODO nebo nastavovat primarne focus point, na zaklade vzdalenosti center point odsazovat aby byl daleko jako BALLRADIUS/2?
//
//            // TODO na misto center point dosadim focus point
//            // TODO center point musi byt ve spravnem miste, na care!
//            /*if (completeBegining && completedLines.isEmpty()) { // na uplnem zacatku
//                double startDist = Point2D.distance(lineInProcess.getP1().getX(), lineInProcess.getP1().getY(), x, y);
//                if (startDist < BALLRADIUS/2) {
//
//
//                    if (currentPathLength < BALLRADIUS && currentPathLength == currentLineLength && (activeSymbol == null || !(activeSymbol instanceof GotoLabel))) {
//                        // jestli je nedostatek prostoru mezi dvema symboly, z nich druhy neni gotoLabel
//                        // budu resit jak vystup ze symbolu, tak vstup
//
//
//
//                    } else {
//                        // resim jen vystup ze symbolu
//                        // TODO urcit hranici - kdyz mam misto prirozenou, kdyz ne upravenou
//                        double space = BALLRADIUS/2;
//                        if (currentLineLength < space) {
//                            space = currentLineLength;
//                        }
//
//
//
//
//
//
//                    }
//
//
//                } else {
//                    completeBegining = false;
//                }
//
//            } else if (ballPaths.size() == 1 && ballPaths.peek().size() == 1 && (activeSymbol == null || !(activeSymbol instanceof GotoLabel))) { // na uplnem konci, // resim goto jenom na konci, na zacatku budu mit completeBegining false, protoze se nevolal resetVars
//                double endDist = Point2D.distance(x, y, ballPaths.peek().peek().getX2(), ballPaths.peek().peek().getY2());
//                if (endDist < BALLRADIUS/2) {
//                    // resim jen vstup do symbolu
//
//
//                    if (endDist > currentLineLength) {
//
//                        centerP = lineInProcess.getP1();
//
//                        //ball.setFrame(x-BALLSHINE_SIZE/2, y-BALLSHINE_SIZE/2, BALLSHINE_SIZE, BALLSHINE_SIZE);
//
//
//                    } else {
//                        double percCenter = endDist/currentLineLength;
//
//                    }
//
//
//
//
////                    double perc = endDist/(BALLRADIUS/2);
////                    double focusX = ballPaths.peek().peek().getX2() - (ballPaths.peek().peek().getX2() - x)*perc;
////
////                    focusP = new Point2D.Double(, )
////
////                    Line2D.
//
//
//                }
//            }*/
//
//
//            //RadialGradientPaint pC = new RadialGradientPaint(centerP, BALLRADIUS, new Point2D.Double(centerP.getX()-BALLRADIUS, centerP.getY()), BALLDIST, ballGradientColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//            ballGradient = new RadialGradientPaint(centerP, BALLRADIUS, focusP, BALLDIST, ballGradientColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//
//            float perc = 1;
//            if (!centerP.equals(focusP)) {
//                perc = (float)centerP.distance(focusP)/BALLRADIUS;
//                if (perc != 0) {
//                    double cX = (centerP.getX()-focusP.getX()) * perc;
//                    double cY = (centerP.getY()-focusP.getY()) * perc;
//                    centerP = new Point2D.Double(centerP.getX()+cX,centerP.getY()+cY);
//                    perc = 1-perc;
//                }
//            }
//
//            if (perc <= 0 || !SettingsHolder.settings.isBallShine()) {
//                ballShineGradient = null;
//            } else {
//                ballShineGradient = new RadialGradientPaint(centerP, SettingsHolder.settings.getBallShineRadius()*perc, new float[]{0.0f, 1.0f}, new Color[]{ballColor, new Color(ballColor.getRed(), ballColor.getGreen(), ballColor.getBlue(), 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//            }
//
//
//            //if (centerP.equals(focusP)) {
//                //ballShineGradient = new RadialGradientPaint(centerP, BALLSHINERADIUS, new float[]{0.0f, 1.0f}, new Color[]{ballColor, new Color(ballColor.getRed(), ballColor.getGreen(), ballColor.getBlue(), 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//            /*} else {
//                double difX = centerP.getX()-focusP.getX();
//                double difY = centerP.getY()-focusP.getY();
//                double precX = difX/BALLRADIUS;
//                double precY = difY/BALLRADIUS;
//
//                double cX = centerP.getX() - difX + precX*BALLSHINERADIUS;
//                double cY = centerP.getY() - difY + precY*BALLSHINERADIUS;
//                centerP = new Point2D.Double(cX, cY);
//
//                double fX = centerP.getX() - precX*BALLSHINERADIUS;
//                double fY = centerP.getY() - precY*BALLSHINERADIUS;
//                focusP = new Point2D.Double(fX, fY);
//
//
//                ballShineGradient = new RadialGradientPaint(centerP, BALLSHINERADIUS, focusP, new float[]{0.0f, 1.0f}, new Color[]{ballColor, new Color(ballColor.getRed(), ballColor.getGreen(), ballColor.getBlue(), 0)}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//            }*/
        }

        private float getSliderModif()
        {
            float half = (jSliderSpeed.getMaximum() + jSliderSpeed.getMinimum()) / 2;
            if (jSliderSpeed.getValue() > half) {
                return 1 + (jSliderSpeed.getValue() - half) / (jSliderSpeed.getMaximum() - half) * (SLIDER_INCREASELIMIT - 1);
            } else if (jSliderSpeed.getValue() < half) {
                return SLIDER_DECREASELIMIT + (1 - (jSliderSpeed.getValue() - half) / (jSliderSpeed.getMinimum() - half)) * (1 - SLIDER_DECREASELIMIT);
            } else {
                return 1;
            }
        }

        private int getTimeToPassWholePath()
        {
            float modif = getSliderModif();
//            float pixelMax = PLAYPATH_DELAYPIXEL_MAX*modif;
//            float pixelMin = PLAYPATHDELAYPIXELMIN*modif;
            float msMax = PLAYPATH_DELAYMS_MAX * modif;
            float msMin = PLAYPATH_DELAYMS_MIN * modif;

            if (currentPathLength >= PLAYPATH_DELAYPIXEL_MAX) {
                return (int) (msMax);
//            } else if (currentPathLength <= PLAYPATHDELAYPIXELMIN) {
//                return (int)(msMin);
            } else {
//                return (int)((currentPathLength-PLAYPATHDELAYPIXELMIN)/(PLAYPATH_DELAYPIXEL_MAX-PLAYPATHDELAYPIXELMIN)*(msMax-msMin)+msMin);
                int ret = (int) (currentPathLength / PLAYPATH_DELAYPIXEL_MAX * msMax);
                if (ret < msMin) {
                    return (int) (msMin);
                } else {
                    return ret;
                }
            }
        }

        private boolean setNextPath()
        {
            // formalni elevace cesty
            ArrayList<Map.Entry<Path2D[], Integer>> analyzedPath = new ArrayList<>();
            analyzedPath.add(analyzedPaths.remove(0));
            elevateAnalyzedPaths(analyzedPath, true);

            completedLines.clear();
            lineInProcess = null;

            ballPaths.poll();
            arrows.poll();
            completeBegining = false;

            if (ballPaths.isEmpty()) {
                return false;
            } else {
                initNextPath();
                return true;
            }
        }

        private void initNextPath()
        {
            // TODO sizekulicka
            //actualPathLength = -BALLSHINE_SIZE*BALLSIZE_PERCENTAGE;
            currentPathLength = 0;
            int i = 0;
            for (Iterator<Line2D> it = ballPaths.peek().iterator(); it.hasNext(); i++) {
                Line2D line = it.next();
                if (i == 0) {
                    currentLineLength = Point2D.distance(line.getX1(), line.getY1(), line.getX2(),
                            line.getY2());
                }
                currentPathLength += Point2D.distance(line.getX1(), line.getY1(), line.getX2(),
                        line.getY2());
            }
            processElevation = analyzedPaths.get(0).getValue() + 1;
            timeToPassWholePath = getTimeToPassWholePath();
            currentLineStartPointDist = 0;
            timePased = 0;
        }

        // kdyz neni buffer, jede naprazdno.. (ale kazdou chvili se zavola animpath)
        private void myActionPerformed()
        {
            // TODO sipka

            int nextDelay = 1000 / SettingsHolder.settings.getFps();

            if (isBuffered()) {
                int updatedTimeToPassWholePath = getTimeToPassWholePath();
                if (timeToPassWholePath != updatedTimeToPassWholePath) {
                    // uzivatel pohnul sliderem rychlosti
                    timePased = (int) ((double) (timePased) / timeToPassWholePath * updatedTimeToPassWholePath);
                    timeToPassWholePath = updatedTimeToPassWholePath;
                }

                int timeRemaining = timeToPassWholePath - timePased;
                if (timeRemaining <= 0) {
                    // na priste uz nezbyva cas, musim koncit nyni
                    if (setNextPath()) {
                        timePased = Math.abs(timeRemaining); // presypu presahnuty cas do dalsi cesty
                        myActionPerformed();
                        return;
                    } else {
                        if (activeSymbol == null || !(activeSymbol instanceof GotoLabel)) {
                            resetVariables();
                        }
                        jPanelDiagram.repaint();
                        callManagersNext();
                        return;
                    }
                } else {
                    double targetPathProgress = currentPathLength * functionHalfCosinPercentage(
                            timePased, timeToPassWholePath);
                    lineInProcess = ballPaths.peek().peek();

                    while (currentLineStartPointDist + currentLineLength <= targetPathProgress) {
                        completedLines.add(ballPaths.peek().poll());
                        currentLineStartPointDist += currentLineLength;
                        lineInProcess = ballPaths.peek().peek();
                        currentLineLength = Point2D.distance(lineInProcess.getX1(),
                                lineInProcess.getY1(), lineInProcess.getX2(), lineInProcess.getY2());
                    }

                    double targetLinePerc = (targetPathProgress - currentLineStartPointDist) / currentLineLength;
                    double newX = lineInProcess.getX1() + (lineInProcess.getX2() - lineInProcess.getX1()) * targetLinePerc;
                    double newY = lineInProcess.getY1() + (lineInProcess.getY2() - lineInProcess.getY1()) * targetLinePerc;
                    lineInProcess = new Line2D.Double(lineInProcess.getX1(), lineInProcess.getY1(),
                            newX, newY);
                    setBall(newX, newY);

                    jPanelDiagram.repaint();
                    timePased += nextDelay;
                }
            }

            if (nextDelay != super.getDelay()) {
                super.setDelay(nextDelay);
            }
        }

        private void callManagersNext()
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        Thread.sleep(AFTERDONE_DELAYMS);
                    } catch (InterruptedException ex) {
                    }
                    SwingUtilities.invokeLater(new Runnable()
                    { // JTreeTable ma problem s manipulovanim z jineho nez EDT threadu
                        @Override
                        public void run()
                        {
                            if (playTimer.isRunning()) {
                                if (functionManager.next().debugShouldBeHalted) {
                                    functionManager.globalPause();
                                }
                            }
                        }
                    });
                }
            }).start();
        }

        public void animPath(Symbol fromSymbol, String progressDesc, Path2D[] throughPaths,
                TextLayout segmentDesc)
        {
            super.stop();

            if (isBuffered()) {
                finalizeIt();
            }
            elevateSymbolProgressSdescs(fromSymbol, progressDesc, throughPaths, segmentDesc, true);
            jPanelDiagram.repaint();
            if (throughPaths != null) {
                analyzedPaths = getAnalyzedPaths(throughPaths);

                int i = 0;
                for (Iterator<Entry<Path2D[], Integer>> it = analyzedPaths.iterator(); it.hasNext(); i++) {
                    Map.Entry<Path2D[], Integer> entry = it.next();
                    if (entry == null) {
                        it.remove();
                        continue;
                    }

                    ballPaths.add(new ArrayDeque<Line2D>());
                    Path2D ballPath;
                    if (i > 0) {
                        ballPath = getContinualCurrentPath(throughPaths[i - 1], throughPaths[i]);
                    } else {
                        ballPath = throughPaths[i];
                    }
                    Path2D animPath = entry.getKey()[0];

                    double[] prevCoordinates = new double[2];
                    PathIterator ballPathIterator = ballPath.getPathIterator(null);
                    PathIterator animPathIterator = animPath.getPathIterator(null);
                    ballPathIterator.currentSegment(prevCoordinates);
                    ballPathIterator.next();
                    animPathIterator.next();

                    while (!ballPathIterator.isDone()) {
                        double[] coordinates = new double[2];
                        int type = ballPathIterator.currentSegment(coordinates);
                        switch (type) {
                            case PathIterator.SEG_LINETO: {
                                ballPaths.getLast().add(new Line2D.Double(prevCoordinates[0],
                                        prevCoordinates[1], coordinates[0], coordinates[1]));
                                prevCoordinates = coordinates;
                                ballPathIterator.next();

                                if (ballPathIterator.isDone()) {
                                    animPathIterator.currentSegment(coordinates);
                                    arrows.add(new Point2D.Double(coordinates[0], coordinates[1]));
                                } else {
                                    animPathIterator.next();
                                }
                                break;
                            }
                            default: {
                                throw new Error("Unexpected currentSegment!");
                            }
                        }
                    }
                }
                initNextPath();

                super.start();
            } else {
                super.start();
                callManagersNext();
            }
        }

        public void pause()
        {
            super.stop();
        }

        public boolean isBuffered()
        {
            return !analyzedPaths.isEmpty();
        }

        public void finalizeIt()
        {
            if (isBuffered()) {
                elevateAnalyzedPaths(analyzedPaths, true);
                resetVariables();
                jPanelDiagram.repaint();
            }
        }

        @Override
        public void stop()
        {
            super.stop();
            resetVariables();
        }

        private void resetVariables()
        {
            analyzedPaths.clear();
            completedLines.clear();
            lineInProcess = null;
            processElevation = 0;
            ballPaths.clear();
            arrows.clear();
            currentPathLength = 0;
            currentLineStartPointDist = 0;
            currentLineLength = 0;
            timePased = 0;
            timeToPassWholePath = 0;
            completeBegining = true;
            ball = null;
            if (ballShineGradient != null) {
                ballShineGradient = null;
                reinitSymbols = true;
            }
        }

    }

}
