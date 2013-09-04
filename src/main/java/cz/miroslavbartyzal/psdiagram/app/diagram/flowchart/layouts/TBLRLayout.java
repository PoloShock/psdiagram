/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.diagram.flowchart.symbols.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 * <p>Tato třída představuje Normový layout. Layout byl navržen speciálně tak,
 * aby rozložení a pospojování symbolů co nejvíce odpovídalo české normě ČSN ISO
 * 5807.<br />
 * Směr expanze vývojového diagramu je orientován doprava a dolů, což
 * norma specifikuje jako preferovaný směr.</p>
 *
 * <p>Důraz je kladen na logickou přehlednost uspořádání symbolů, proto je
 * například tělo cyklu odsazováno o úroveň doprava. Při několikanásobném
 * vnoření cyklů tak máme jasný přehled, které tělo patří kterému cyklu.</p>
 *
 * <p>Layout má k dispozici dvě položky nastavení.<br />
 * První nastavení nese název "Smršťování". Tuto možnost dobře využijeme
 * například
 * při tisku exportovaného obrázku diagramu, kdy celková velikost diagramu je
 * menší, než při vypnutí této funkce. Pro samotné editování diagramu je však
 * doporučno mít tuto funkci ve vypnutém stavu, kvůli přehlednějšímu vykreslení
 * Jointů (viz. třída Joint).<br />
 * Druhé nastavení nese název "Expanze Switch symbolu". Zapnutím této funkce,
 * budou ukončovací spojnice symbolu Switch vykresleny jednotlivě, pod sebou,
 * namísto jejich spojení v jedinou.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class TBLRLayout extends AbstractLayout
{

    private ArrayList<Double> lColumnWidth = new ArrayList<>(10);
    private ArrayList<Double> lRowWidth = new ArrayList<>(20);
    private ArrayList<Double> lColumnPos = new ArrayList<>(10);
    private ArrayList<Double> lRowPos = new ArrayList<>(20);
    ArrayList<Line2D> lGrid = new ArrayList<>(20); // uchovava mrizku
    private ArrayList<Joint> lJoints = super.getlJoints(); // slouzi jen pro docasne ulozeni jointu
    private ArrayList<Comment> lCommentSymbols = new ArrayList<>(9);
    private double width = 0;
    private double height = 0;
    private int incrementXSymbol = 0; // inkrementace Xove souradnice k symbolu (pouziti pri posouvani diagramu kvuli vybocujicimu komentari)
    private int incrementYSymbol = 0; // inkrementace Yove souradnice k symbolu (pouziti pri posouvani diagramu kvuli vybocujicimu komentari)
    private int incrementXCanvas = 0; // inkrementace Xove souradnice platna (pouziti pri posouvani diagramu kvuli vybocujicimu komentari)
    private int incrementYCanvas = 0; // inkrementace Yove souradnice platna (pouziti pri posouvani diagramu kvuli vybocujicimu komentari)
    private int descElseSegmentX = -9; // relativni umisteni Xove souradnice deskripce segmentu
    private int descElseSegmentY = 4; // relativni umisteni Xove souradnice deskripce segmentu
    private int descFirstSegmentX = -3; // relativni umisteni Xove souradnice deskripce segmentu vuci MaxX jeho symbolu
    private int descFirstSegmentY = -4; // relativni umisteni Yove souradnice deskripce segmentu vuci usti jeho symbolu
    private int descPadding = 4; // odsazeni od potencionálního vedlejsiho symbolu v pripade else nebo prvni vetve, v pripade casu dolni odsazeni od cary
    private boolean sizeTrimming = false;
    private boolean expandMultiSegment = false;

    /**
     * Základní konstruktor s parametrem, určující plátno, na které má být
     * vývojový diagram vykreslován.<br />
     * Vývojový diagram je vytvořen automaticky, obsahuje pouze počáteční a
     * koncový symbol.
     *
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     */
    public TBLRLayout(JComponent canvas)
    {
        this(canvas, null);
    }

    /**
     * Konstruktor s parametry určující plátno, na které má být
     * vývojový diagram, určený druhým parametrem, vykreslován.
     *
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     * @param flowchart vývojový diagram k vykreslení<br />
     * Je-li zadán null, vývojový diagram je vytvořen automaticky a obsahuje
     * pouze počáteční a koncový symbol.
     */
    public TBLRLayout(JComponent canvas, Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        super(canvas, flowchart);
        prepareMyFlowchart();
        setFocusedJoint(getlJoints().get(0));
    }

    /**
     * Metoda pro získání možného nastavení tohoto layoutu. Posluchače tlačítek
     * jsou anonymní, automaticky svázané s touto třídou.
     *
     * @return možné nastavení tohoto layoutu
     */
    @Override
    public ArrayList<JMenuItem> getSettings()
    {
        ArrayList<JMenuItem> menuItems = new ArrayList<>();
        JCheckBoxMenuItem cbMenuItemSizeTrimming = new JCheckBoxMenuItem("Smršťování", sizeTrimming);
        cbMenuItemSizeTrimming.setSelected(sizeTrimming);
        cbMenuItemSizeTrimming.addActionListener(
                new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                JCheckBoxMenuItem cbMenuItem = (JCheckBoxMenuItem) ae.getSource();
                sizeTrimming = cbMenuItem.isSelected();
                prepareFlowchart();
                getCanvas().repaint();
            }
        });
        menuItems.add(cbMenuItemSizeTrimming);

        JCheckBoxMenuItem cbMenuItemExpandMultiSegment = new JCheckBoxMenuItem(
                "Expanze Switch symbolu", expandMultiSegment);
        cbMenuItemExpandMultiSegment.setSelected(expandMultiSegment);
        cbMenuItemExpandMultiSegment.addActionListener(
                new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                JCheckBoxMenuItem cbMenuItem = (JCheckBoxMenuItem) ae.getSource();
                expandMultiSegment = cbMenuItem.isSelected();
                prepareFlowchart();
                getCanvas().repaint();
            }
        });
        menuItems.add(cbMenuItemExpandMultiSegment);

        return menuItems;
    }

    /**
     * Metoda, která pomocí instance Graphics2D zajistí vykreslení vývojového
     * diagramu.
     *
     * @param g2d instance Graphics2D, pomocí které bude vývojový diagram
     * vykreslen
     * @param clipShadow určuje, jakým způsobem se mají vykreslit stíny
     * symbolů.<br />
     * True - stíny budou vykresleny obrysem symbolu - výpočetně náročnější,
     * používá se při exportu do PDF<br />
     * False - stíny budou vykresleny bez ořezu plnou velikostí symbolu. Symbol
     * bude vykreslen následně, čímž překryje požadovanou část stínu a vznikne
     * totožný efekt jako při metodě ořezávání.
     */
    @Override
    public void paintFlowchart(Graphics2D g2d, boolean clipShadow)
    {
        // MRIZKA
        if (super.getEditMode()) {
            g2d.setColor(new Color(245, 245, 245));
            for (Line2D line : lGrid) {
                g2d.draw(line);
            }
        }

        super.paintFlowchart(g2d, clipShadow);
    }

    /**
     * Stěžejní metoda layoutu. Voláním této metody layout přepočítá veškeré
     * rozmístění symbolů, propojovacích cest a popisků.
     */
    @Override
    public void prepareMyFlowchart()
    {
        lColumnWidth.clear();
        lRowWidth.clear();
        analyzeFlowchartGrid(super.getFlowchart().getMainSegment(), 0, 0);
        lColumnPos = new ArrayList<>(lColumnWidth);
        lRowPos = new ArrayList<>(lRowWidth);
        double overHangInc = getOverHangInc(super.getFlowchart().getMainSegment());
        defineGrid(lColumnPos, overHangInc + +incrementXSymbol);
        defineGrid(lRowPos, +incrementYSymbol);
        lCommentSymbols = new ArrayList<>(lCommentSymbols.size() + 1); // alokace o 1 vetsi nez predtim
        lJoints = new ArrayList<>(super.getlJoints().size() + 10); // alokace o 10 vetsi nez predtim bylo jointu
        deploySymbolsAndArrows(super.getFlowchart().getMainSegment(), 0, 0);

        width = lColumnPos.get(lColumnPos.size() - 1) + lColumnWidth.get(lColumnWidth.size() - 1) / 2 + super.getFlowchartPadding() + 0.5 + 2 + incrementXCanvas;// + 0.5 za strokesize, + 2 za stin
        height = lRowPos.get(lRowPos.size() - 1) + lRowWidth.get(lRowWidth.size() - 1) / 2 + super.getFlowchartPadding() + 0.5 + 2 + incrementYCanvas;// + 0.5 za strokesize, + 2 za stin

        // nasleduje prekontrolovani pozic komentaru vzhledem k posunutemu platnu
        double minX = super.getFlowchartPadding() + incrementXSymbol;
        double maxX = width - super.getFlowchartPadding() - incrementXCanvas - 2; // -2 za strokesize a za stin
        double minY = super.getFlowchartPadding() + incrementYSymbol;
        double maxY = height - super.getFlowchartPadding() - incrementYCanvas - 2; // -2 za strokesize a za stin
        double myIncrementXSymbol = 0;
        double myIncrementYSymbol = 0;
        double myIncrementXCanvas = 0;
        double myIncrementYCanvas = 0;
        for (Comment comment : lCommentSymbols) {
            ArrayList<Point2D> relativeMiddlePointsToSymbol = comment.getRelativeMiddlePointsToSymbol();
            for (int i = -1; i < relativeMiddlePointsToSymbol.size(); i++) {
                double cX;
                double cMaxX;
                double cY;
                double cMaxY;
                if (i == -1) {
                    cX = comment.getX();
                    cY = comment.getY();
                    cMaxY = cY + comment.getHeight();
                } else {
                    cX = comment.getX() - comment.getRelativeX() + relativeMiddlePointsToSymbol.get(
                            i).getX();
                    cY = comment.getY() + comment.getHeight() / 2 - comment.getRelativeY() + relativeMiddlePointsToSymbol.get(
                            i).getY();
                    cMaxY = cY;
                }
                cMaxX = cX + comment.getWidth();

                if (cX < minX - myIncrementXSymbol) {
                    myIncrementXSymbol = minX - cX;
                }
                if (cMaxX > maxX + myIncrementXCanvas) {
                    myIncrementXCanvas = cMaxX - maxX;
                }
                if (cY < minY - myIncrementYSymbol) {
                    myIncrementYSymbol = minY - cY;
                }
                if (cMaxY > maxY + myIncrementYCanvas) {
                    myIncrementYCanvas = cMaxY - maxY;
                }
            }
        }
        if ((int) myIncrementXSymbol != incrementXSymbol || (int) myIncrementYSymbol != incrementYSymbol || (int) myIncrementXCanvas != incrementXCanvas || (int) myIncrementYCanvas != incrementYCanvas) {
            // doslo k premisteni precuhujiciho komentare
            incrementXSymbol = (int) myIncrementXSymbol;
            incrementYSymbol = (int) myIncrementYSymbol;
            incrementXCanvas = (int) myIncrementXCanvas;
            incrementYCanvas = (int) myIncrementYCanvas;
            prepareMyFlowchart();
            return;
        }

        JComponent canvas = super.getCanvas();
        canvas.setPreferredSize(new Dimension((int) width, (int) height)); // 2* + 1 za strokesize
        super.setlJoints(lJoints);
        lJoints = null;
        lGrid.clear();
        for (int i = 0; i < lColumnPos.size(); i++) {
            lGrid.add(new Line2D.Double(lColumnPos.get(i), lRowPos.get(0), lColumnPos.get(i),
                    lRowPos.get(lRowPos.size() - 1)));
        }
        for (int i = 0; i < lRowPos.size(); i++) {
            lGrid.add(new Line2D.Double(lColumnPos.get(0), lRowPos.get(i), lColumnPos.get(
                    lColumnPos.size() - 1), lRowPos.get(i)));
        }
    }

    /**
     * Metoda pro získání všech komentářových symbolů diagramu.
     *
     * @return kolekce všech komentářových symbolů diagramu
     */
    @Override
    public ArrayList<Comment> getlCommentSymbols()
    {
        return lCommentSymbols;
    }

    private double getOverHangInc(LayoutSegment segment)
    {
        for (LayoutElement element : segment) {
            if (element.getSymbol().isOverHang() || (element.getSymbol().hasElseSegment() && getOverHangInc(
                    element.getInnerSegment(0)) > 0)) {
                return super.getSymbolPadding() / 2; //za overhangovy symbol - jeho sipka vybocuje vlevo
            }
        }
        return 0;
    }

    /*
     * private void reanalyzeGrid(FlowchartSegment segment, int inputColumn, int
     * inputRow) {
     * int actualRow = inputRow;
     * for (FlowchartElement element: segment) {
     * while (lColumnPos.size() <= inputColumn) {
     * lColumnPos.add(0d);
     * }
     * double elementWidth = element.getSymbol().getWidth();
     * if (lColumnPos.get(inputColumn) < elementWidth) {
     * lColumnPos.set(inputColumn, elementWidth);
     * }
     * while (lRowPos.size() <= actualRow) {
     * lRowPos.add(0d);
     * }
     * double elementHeight = element.getSymbol().getHeight();
     * if (lRowPos.get(actualRow) < elementHeight) {
     * lRowPos.set(actualRow, elementHeight);
     * }
     * if (element.getInnerSegmentsCount() > 0) {
     * for (int j = 0; j < element.getInnerSegmentsCount(); j++) {
     * FlowchartSegment innerSegment = element.getInnerSegment(j);
     * if (innerSegment != null) {
     * reanalyzeGrid(innerSegment, inputColumn + j +
     * innerSegment.getExtraLayoutData(0), actualRow +
     * innerSegment.getExtraLayoutData(1));
     * }
     * }
     * actualRow = element.getExtraLayoutData(0);
     * } else {
     * actualRow++;
     * }
     * }
     * }
     */
    private void deploySymbolsAndArrows(LayoutSegment segment, int inputColumn, int inputRow)
    {
        // TODO REFACTORING - there are few duplicates in arrow-creation - in future I want to implement it like joint-creation

        int actualRow = inputRow;
        LayoutElement previousElement = null; // slouzi k ulozeni elementu predchazejiciho, vzhledem k aktualnimu v ramci tohoto segmentu
        LayoutElement overHangElement = null; // slouzi pro zachovani hodnoty radku symbolu, z nehoz vybocuje sipka doleva
        LayoutElement firstPairElement = null; // slouzi k uchovani prvniho z parovych symbolu, ktery nema postrani sipku
        LayoutElement commentToInitialize = null; // slouzi k ulozeni paroveho komentare k pozdejsimu zpracovani
        int decrease = 1; // slouzi k rozliseni radkovani parovych symbolu od neparovych
        boolean isLastOverHanged = false; // slouzi pro odsazeni cyklu (vice cyklu za sebou)
        boolean isLastPaired = false; // slouzi k zaznemenani, ze posledni symbol byl parovy
        boolean forFirstTime = true; // slouzi pro inicializaci zacatecni sipky segmentu
        int noPairCommentCount = 0; // slouzi pro zaznamenani poctu neparovych komentaru jdoucich za sebou - pouziti pri tvorbe jointu na spravnych mistech

        if (segment.getDescriptionLayout() != null && segment.getParentElement() != null && segment.getParentElement().getInnerSegmentsCount() > 0) { // prirazeni pozic k popiskum segmentu
            TextLayout description = segment.getDescriptionLayout();
            if (segment.getParentElement().indexOfInnerSegment(segment) == 0) {
                double x = lColumnPos.get(inputColumn) - description.getBounds().getWidth() + descElseSegmentX - description.getBounds().getX();
                double y = lRowPos.get(actualRow - segment.getExtraLayoutData(1)) + segment.getParentElement().getSymbol().getHeight() / 2 - description.getBounds().getHeight() + descElseSegmentY - description.getBounds().getY();
                segment.setDescriptionLocation(new Point2D.Double(x, y));
            } else if (segment.getParentElement().getInnerSegmentsCount() > 2 && segment.getParentElement().indexOfInnerSegment(
                    segment) > 0) {
                double x = lColumnPos.get(inputColumn) - description.getBounds().getWidth() / 2 - description.getBounds().getX();
                double y = lRowPos.get(actualRow - segment.getExtraLayoutData(1)) - description.getBounds().getHeight() - descPadding - description.getBounds().getY();
                segment.setDescriptionLocation(new Point2D.Double(x, y));
            } else if (segment.getParentElement().indexOfInnerSegment(segment) == 1) {
                double x = lColumnPos.get(inputColumn - segment.getExtraLayoutData(0) - 1) + segment.getParentElement().getSymbol().getWidth() / 2 + descFirstSegmentX - description.getBounds().getX();
                double y = lRowPos.get(actualRow - segment.getExtraLayoutData(1)) - description.getBounds().getHeight() + descFirstSegmentY - description.getBounds().getY();
                segment.setDescriptionLocation(new Point2D.Double(x, y));
            }
        }

        int i = 0;
        for (Iterator<LayoutElement> it = segment.iterator(); it.hasNext(); i++) {
            LayoutElement element = it.next();
            if (element.getSymbol() instanceof Comment) { // jedna-li se o element komentare, je treba specialni nakladani
                lCommentSymbols.add((Comment) element.getSymbol());
                if (!element.getSymbol().hasPairSymbol()) {
                    if ((!sizeTrimming && isLastOverHanged && !isLastPaired) && noPairCommentCount == 0) { // jestli symbol pokracuje pomoci vybocene sipky vlevo, je treba tomu prizpusobit layout
                        actualRow++;
                    }
                    Comment comment = (Comment) element.getSymbol();
                    comment.setCenterX(lColumnPos.get(inputColumn) + comment.getRelativeX());
                    comment.setCenterY(lRowPos.get(actualRow) + comment.getRelativeY());
                    element.setPathToNextSymbol(getCommentPathFromRelative(comment, null));
                    if (noPairCommentCount > 0) { // tento komentar nasleduje za predchozim komentarem, predchozimu musim priradit joint
                        Joint joint = new Joint(segment.getElement(
                                segment.indexOfElement(element) - 1), segment);
                        joint.setCenterX(lColumnPos.get(inputColumn));
                        joint.setCenterY(
                                (lRowPos.get(actualRow - 1) - lRowPos.get(actualRow)) / 2 + lRowPos.get(
                                actualRow));
                        lJoints.add(joint);
                    }
                    if (it.hasNext()) {
                        actualRow++; // nema-li komentar parovy symbol, je treba mu j odsadit radek (pouze neni-li komentar poslednim elementem)
                    }
                    noPairCommentCount++;
                } else {
                    commentToInitialize = element; // je-li komentar parovy, nemohu zatim pridelit sipku, protoze druhy symbol, s nimz je sparovan, jeste neni umisten
                }
            } else {
                if ((element.getSymbol().isOverHang() && isLastOverHanged) || (!sizeTrimming && isLastOverHanged && !isLastPaired) || (sizeTrimming && isLastOverHanged && element.getSymbol().isPadded())) { // jestli symbol pokracuje pomoci vybocene sipky vlevo, je treba tomu prizpusobit layout
                    if (noPairCommentCount == 0) {
                        actualRow++;
                    }
                    decrease++;
                }
                element.getSymbol().setCenterX(lColumnPos.get(inputColumn));
                element.getSymbol().setCenterY(lRowPos.get(actualRow));
                if (segment.getParentElement() != null && forFirstTime) { // neni-li segment prazdny, nebo superrodicovsky, pridelim segmentu sipku od puvodce
                    forFirstTime = false;
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 2);
                    int segmentRowIncrease = 0;
                    if (segment.getParentElement().indexOfInnerSegment(segment) != 0) {
                        segmentRowIncrease = segment.getExtraLayoutData(1);
                    }
                    Point2D p1 = getSymbolIntersectionPoint(segment.getParentElement().getSymbol(),
                            lColumnPos.get(inputColumn), lRowPos.get(inputRow - segmentRowIncrease));
                    path.moveTo(p1.getX(), p1.getY());
                    if (segmentRowIncrease != 0 || (inputRow != actualRow && segment.getParentElement().indexOfInnerSegment(
                            segment) != 0)) {
                        p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                inputRow - segmentRowIncrease));
                        path.lineTo(p1.getX(), p1.getY());
                    } else if (segment.getParentElement().indexOfInnerSegment(segment) != 0 && lColumnPos.get(
                            inputColumn - 1) > p1.getX()) { // pri smrstovani je treba upravit p1 aby se joint vykreslil pobliz prvniho symbolu segmentu (ne uprostred cesty)
                        p1 = new Point2D.Double(lColumnPos.get(inputColumn - 1), p1.getY());
                    }
                    Point2D p2 = getSymbolIntersectionPoint(element.getSymbol(), p1.getX(),
                            p1.getY());
                    path.lineTo(p2.getX(), p2.getY());
                    segment.setPathToThisSegment(path);
                    /*
                     * if (segmentRowIncrease == 0 &&
                     * segment.getParentElement().indexOfInnerSegment(segment)
                     * != 0 && (noPairCommentCount > 0 ||
                     * lColumnPos.get(inputColumn-1) > p1.getX())) {
                     * p1 = new Point2D.Double(lColumnPos.get(inputColumn-1),
                     * p1.getY());
                     * }
                     */
                    addJoint(previousElement, segment, commentToInitialize, p1, p2, null,
                            inputColumn, inputRow, actualRow, segmentRowIncrease, i,
                            noPairCommentCount, 0, 0);
                }
                if (overHangElement != null && !isLastPaired) { // (pri parovem symbolu dodatecne) prirazeni vybocujici sipky prislusnemu elementu
                    // postrani sipka
                    if (sizeTrimming) {
                        if (noPairCommentCount > 0) {
                            decrease -= 2 - noPairCommentCount;
                        } else {
                            decrease -= 1;
                        }
                    } else {
                        decrease -= 1 - noPairCommentCount;
                    }
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
                    double overHangX = lColumnPos.get(inputColumn) - (lColumnWidth.get(inputColumn) / 2) - (super.getSymbolPadding() / 2);
                    Point2D p1 = getSymbolIntersectionPoint(overHangElement.getSymbol(), overHangX,
                            overHangElement.getSymbol().getCenterY());
                    path.moveTo(p1.getX(), p1.getY());
                    path.lineTo(overHangX, overHangElement.getSymbol().getCenterY());
                    p1 = new Point2D.Double(overHangX, lRowPos.get(actualRow - decrease));
                    path.lineTo(p1.getX(), p1.getY());
                    Point2D p2;
                    if (actualRow - decrease != actualRow) {
                        p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                actualRow - decrease));
                        path.lineTo(p1.getX(), p1.getY());
                        p2 = getSymbolIntersectionPoint(element.getSymbol(), lColumnPos.get(
                                inputColumn), lRowPos.get(actualRow - decrease));
                        path.lineTo(p2.getX(), p2.getY());
                    } else {
                        p2 = getSymbolIntersectionPoint(element.getSymbol(), overHangX, lRowPos.get(
                                actualRow - decrease));
                        path.lineTo(p2.getX(), p2.getY());
                    }
                    overHangElement.setPathToNextSymbol(path);
                    overHangElement = null;
                    addJoint(previousElement, segment, commentToInitialize, p1, p2, null,
                            inputColumn, inputRow, actualRow, 0, i, noPairCommentCount, 0, overHangX);
                } else if (previousElement != null && isLastPaired) { // vykresleni zpatecni sipky od druheho z parovych symbolu do prvniho
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 1);
                    Point2D p = getSymbolIntersectionPoint(element.getSymbol(),
                            previousElement.getSymbol().getCenterX(),
                            previousElement.getSymbol().getCenterY());
                    path.moveTo(p.getX(), p.getY());
                    p = getSymbolIntersectionPoint(previousElement.getSymbol(), p.getX(), p.getY());
                    path.lineTo(p.getX(), p.getY());
                    element.setPathToNextSymbol(path);
                } else if (previousElement != null && previousElement.getInnerSegmentsCount() == 0) { // vykresleni standardni sipky mezi symboly (bez slozitych zakrutu)
                    Point2D p1;
                    Point2D p2;
                    // nakonec precijen vykreslim sipku od goto, pote ji nejak barevne a stylem odlisim
                    //if (!(previousElement.getSymbol() instanceof Goto)) {
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 1);
                    p1 = getSymbolIntersectionPoint(previousElement.getSymbol(),
                            element.getSymbol().getCenterX(), element.getSymbol().getCenterY());
                    path.moveTo(p1.getX(), p1.getY());
                    p2 = getSymbolIntersectionPoint(element.getSymbol(), p1.getX(), p1.getY());
                    path.lineTo(p2.getX(), p2.getY());
                    if (firstPairElement == null) {
                        previousElement.setPathToNextSymbol(path);
                    } else { // mame-li ulozeny prvni z parovych symbolu bez postranni sipky, je mu treba jeste zapsat sipku
                        firstPairElement.setPathToNextSymbol(path);
                        firstPairElement = null;
                    }
                    //}
                    addJoint(previousElement, segment, commentToInitialize, p1, p2, null,
                            inputColumn, inputRow, actualRow, 0, i, noPairCommentCount, 0, 0);
                } else if (previousElement != null && previousElement.getSymbol().hasElseSegment()) { // vykresleni sipky za symbolem majici else vetev
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 1);
                    Point2D p2;
                    if (expandMultiSegment && previousElement.getInnerSegmentsCount() > 1) {
                        p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                previousElement.getExtraLayoutData(0) - previousElement.getInnerSegmentsCount() + 1));
                    } else {
                        p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                previousElement.getExtraLayoutData(0) - 1));
                    }
                    path.moveTo(p2.getX(), p2.getY());
                    p2 = getSymbolIntersectionPoint(element.getSymbol(), p2.getX(), p2.getY());
                    path.lineTo(p2.getX(), p2.getY());
                    previousElement.setPathToNextSymbol(path);
                    Point2D p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                            previousElement.getExtraLayoutData(0) - 1));
                    addJoint(previousElement, segment, commentToInitialize, p1, p2, null,
                            inputColumn, inputRow, actualRow, 0, i, noPairCommentCount, 0, 0);
                }
                if (commentToInitialize != null) { // jestli parovy komentar ceka na prideleni sipky, pridelim ji ted
                    Comment comment = (Comment) commentToInitialize.getSymbol();
                    if (element.getSymbol() instanceof GotoLabel) {
                        GotoLabel gotoLabel = (GotoLabel) element.getSymbol();
                        comment.setCenterX(
                                lColumnPos.get(inputColumn) - gotoLabel.getMyHair() - (gotoLabel.getWidth() - gotoLabel.getMyHair() * 2) * 0.25 + comment.getRelativeX());
                    } else {
                        comment.setCenterX(lColumnPos.get(inputColumn) + comment.getRelativeX());
                    }
                    comment.setCenterY(lRowPos.get(actualRow) + comment.getRelativeY());
                    commentToInitialize.setPathToNextSymbol(getCommentPathFromRelative(comment,
                            element.getSymbol()));
                    commentToInitialize = null;
                }
                if (element.getSymbol().isOverHang()) {
                    isLastOverHanged = true;
                } else if (!isLastPaired) {
                    isLastOverHanged = false;
                } else if (!isLastOverHanged) {
                    firstPairElement = previousElement;
                }
                if (element.getSymbol().hasPairSymbol()) {
                    isLastPaired = true;
                    decrease = 0;
                    // parovy element musi byt jiz umisten, kvuli navratove sipce ze segmentu
                    LayoutElement e = segment.getElement(i + 1);
                    for (int j = 2; e.getSymbol() instanceof Comment; j++) {
                        e = segment.getElement(i + j);
                    }
                    e.getSymbol().setCenterX(lColumnPos.get(inputColumn));
                    e.getSymbol().setCenterY(lRowPos.get(element.getExtraLayoutData(0)));
                } else {
                    isLastPaired = false;
                    decrease = 1;
                }
                if (element.getInnerSegmentsCount() > 0) {
                    if (element.getSymbol().isOverHang()) {
                        overHangElement = element;
                    }
                    for (int j = 0; j < element.getInnerSegmentsCount(); j++) {
                        LayoutSegment innerSegment = element.getInnerSegment(j);
                        if (innerSegment != null) {
                            deploySymbolsAndArrows(innerSegment,
                                    inputColumn + j + innerSegment.getExtraLayoutData(0),
                                    actualRow + innerSegment.getExtraLayoutData(1));
                        }
                    }
                    actualRow = element.getExtraLayoutData(0);
                } else {
                    actualRow++;
                }
                previousElement = element;
                noPairCommentCount = 0;
            }
        }

        // nasledujici kod se provede na konci kazdeho segmentu (vykresli jejich ukoncovaci sipky)
        if (commentToInitialize != null) { // jestli je jako posledni parovy komentar, smazi ho protoze zrejme prisel o par
            segment.removeElement(commentToInitialize);
        }
        if (segment.getParentElement() != null) {
            LayoutElement parentElement = segment.getParentElement();
            int indexOfSegment = parentElement.indexOfInnerSegment(segment);
            if (parentElement.getSymbol().hasPairSymbol() || indexOfSegment == 0) {
                decrease = 0;
            } else {
                decrease = 1;
            }
            double targetY;
            if (expandMultiSegment && parentElement.getInnerSegmentsCount() > 1) {
                targetY = lRowPos.get(
                        parentElement.getExtraLayoutData(0) - decrease - (parentElement.getInnerSegmentsCount() - (indexOfSegment + 1)));
            } else if (indexOfSegment == 0) {
                targetY = lRowPos.get(parentElement.getExtraLayoutData(0) - decrease - 1);
            } else {
                targetY = lRowPos.get(parentElement.getExtraLayoutData(0) - decrease);
            }


            if (previousElement != null) {
                LayoutElement lastElement = previousElement;
                if (previousElement.getSymbol() instanceof LoopEnd) {
                    lastElement = findMyPairedElement(previousElement);
                }
                if (!lastElement.getPathToNextSymbol().getPathIterator(null).isDone()) { // toto se stane po smazani posledniho symbolu segmentu - posledni symbol segmentu by nemel obsahovat cestu k nasledujicimu symbolu
                    lastElement.setPathToNextSymbol(new Path2D.Double(Path2D.WIND_NON_ZERO, 4));
                }
                // nakonec precijen vykreslim sipku od goto, pote ji nejak barevne a stylem odlisim
                /*
                 * if (previousElement.getSymbol() instanceof Goto) { // pri
                 * goto jako poslednim symbolu, nevykresluji sipku konce
                 * segmentu
                 * return;
                 * }
                 */
                if (indexOfSegment != 0) {
                    /*
                     * if (parentElement.getSymbol().hasPairSymbol()) {
                     * decrease = 0;
                     * } else {
                     * decrease = 1;
                     * }
                     */
                    if (lRowPos.get(actualRow - 1) < targetY && (noPairCommentCount > 0 || overHangElement == null || lRowPos.get(
                            actualRow) < targetY)) { // vykreslit vertikalni spojnici pouze kdyz smeruje shora dolu (opak se muze stat napr pri foru na konci podminkove vetve, pri zapnutem smrstovani); doplneno o nesplneni teto podminky pri sizetrimming, kdyz overhang je posledni a nema misto na rozlozeni navratove sipky
                        Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 5);
                        Point2D p1;
                        Point2D p2;
                        Point2D p3;
                        double overHangX = 0;
                        if (overHangElement != null) {
                            // postrani sipka
                            overHangX = lColumnPos.get(inputColumn) - (lColumnWidth.get(inputColumn) / 2) - (super.getSymbolPadding() / 2);
                            Point2D p = getSymbolIntersectionPoint(overHangElement.getSymbol(),
                                    overHangX, overHangElement.getSymbol().getCenterY());
                            path.moveTo(p.getX(), p.getY());
                            path.lineTo(overHangX, overHangElement.getSymbol().getCenterY());
                            path.lineTo(overHangX,
                                    lRowPos.get(previousElement.getExtraLayoutData(0)));
                            p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    previousElement.getExtraLayoutData(0)));
                            path.lineTo(p1.getX(), p1.getY());
                            p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    previousElement.getExtraLayoutData(0) + 1));
                        } else {
                            if (expandMultiSegment && previousElement.getSymbol().hasElseSegment() && previousElement.getInnerSegmentsCount() > 1) {
                                p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                        previousElement.getExtraLayoutData(0) - previousElement.getInnerSegmentsCount() + 1));
                            } else if (previousElement.getSymbol().hasElseSegment()) {
                                p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                        previousElement.getExtraLayoutData(0) - 1));
                            } else {
                                p1 = getSymbolIntersectionPoint(previousElement.getSymbol(),
                                        lColumnPos.get(inputColumn), targetY);
                            }
                            path.moveTo(p1.getX(), p1.getY());
                            if (previousElement.getSymbol().hasElseSegment()) {
                                p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                        previousElement.getExtraLayoutData(0) - 1));
                            }
                            p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    actualRow));
                        }
                        path.lineTo(lColumnPos.get(inputColumn), targetY);
                        if (!parentElement.getSymbol().hasPairSymbol()) {
                            p3 = new Point2D.Double(lColumnPos.get(
                                    inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                    targetY);
                            path.lineTo(p3.getX(), p3.getY());
                            if (parentElement.getSymbol().isOverHang()) {
                                Point2D p = getSymbolIntersectionPoint(parentElement.getSymbol(),
                                        lColumnPos.get(
                                        inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                        targetY);
                                path.lineTo(p.getX(), p.getY());
                            }
                        } else {
                            p3 = getSymbolIntersectionPoint(
                                    getPairElement(parentElement).getSymbol(), lColumnPos.get(
                                    inputColumn), targetY);
                            path.lineTo(p3.getX(), p3.getY());
                        }
                        segment.setPathFromThisSegment(path);
                        //overHangElement = null;
                        addJoint(previousElement, segment, commentToInitialize, p1, p2, p3,
                                inputColumn, inputRow, actualRow, 0, i, noPairCommentCount, targetY,
                                overHangX);
                    } else { // nasleduje zkracena forma navratu ze symbolu
                        Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 5);
                        Point2D p1;
                        Point2D p2;
                        if (overHangElement != null) {
                            // postrani sipka
                            double overHangX = lColumnPos.get(inputColumn) - (lColumnWidth.get(
                                    inputColumn) / 2) - (super.getSymbolPadding() / 2);
                            Point2D p = getSymbolIntersectionPoint(overHangElement.getSymbol(),
                                    overHangX, overHangElement.getSymbol().getCenterY());
                            path.moveTo(p.getX(), p.getY());
                            int inc = 0;
                            if (lRowPos.get(actualRow - 1) < targetY) { // jestli jsem se sem dostal "premiove"
                                inc++;
                            }
                            if (overHangElement.getSymbol().hasPairSymbol()) {
                                p1 = new Point2D.Double(overHangX, lRowPos.get(
                                        overHangElement.getExtraLayoutData(0) + inc));
                            } else {
                                p1 = new Point2D.Double(overHangX, lRowPos.get(
                                        overHangElement.getExtraLayoutData(0) - 1 + inc));
                            }
                            path.lineTo(overHangX, overHangElement.getSymbol().getCenterY());
                            path.lineTo(overHangX, targetY);
                            //p2 = new Point2D.Double(overHangX, lRowPos.get(overHangElement.getExtraLayoutData(0) - 1));
                        } else {
                            p1 = getSymbolIntersectionPoint(previousElement.getSymbol(),
                                    lColumnPos.get(inputColumn - 1), targetY);
                            path.moveTo(p1.getX(), p1.getY());
                        }
                        if (!parentElement.getSymbol().hasPairSymbol()) {
                            p2 = new Point2D.Double(lColumnPos.get(inputColumn - 1), targetY);
                            path.lineTo(lColumnPos.get(
                                    inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                    targetY);
                            if (parentElement.getSymbol().isOverHang()) {
                                Point2D p = getSymbolIntersectionPoint(parentElement.getSymbol(),
                                        lColumnPos.get(
                                        inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                        targetY);
                                path.lineTo(p.getX(), p.getY());
                            }
                        } else {
                            p2 = getSymbolIntersectionPoint(
                                    getPairElement(parentElement).getSymbol(), lColumnPos.get(
                                    inputColumn), targetY);
                            path.lineTo(p2.getX(), p2.getY());
                        }
                        segment.setPathFromThisSegment(path);
                        //overHangElement = null;
                        addJoint(previousElement, segment, commentToInitialize, p1, p2, null,
                                inputColumn, inputRow, actualRow, 0, i, noPairCommentCount, targetY,
                                0);
                    }
                } else { // else vetev
                    Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 5);
                    Point2D p1;
                    Point2D p2;
                    Point2D p3;
                    double overHangX = 0;
                    if (overHangElement != null) {
                        // postrani sipka
                        overHangX = lColumnPos.get(inputColumn) - (lColumnWidth.get(inputColumn) / 2) - (super.getSymbolPadding() / 2);
                        Point2D p = getSymbolIntersectionPoint(overHangElement.getSymbol(),
                                overHangX, overHangElement.getSymbol().getCenterY());
                        path.moveTo(p.getX(), p.getY());
                        path.lineTo(overHangX, overHangElement.getSymbol().getCenterY());
                        path.lineTo(overHangX, lRowPos.get(previousElement.getExtraLayoutData(0)));
                        p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                previousElement.getExtraLayoutData(0)));
                        path.lineTo(p1.getX(), p1.getY());
                        p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                previousElement.getExtraLayoutData(0) + 1));
                    } else {
                        if (expandMultiSegment && previousElement.getSymbol().hasElseSegment() && previousElement.getInnerSegmentsCount() > 1) {
                            p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    previousElement.getExtraLayoutData(0) - previousElement.getInnerSegmentsCount() + 1));
                        } else if (previousElement.getSymbol().hasElseSegment()) {
                            p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    previousElement.getExtraLayoutData(0) - 1));
                        } else {
                            p1 = getSymbolIntersectionPoint(previousElement.getSymbol(),
                                    lColumnPos.get(inputColumn), targetY);
                        }
                        path.moveTo(p1.getX(), p1.getY());
                        if (previousElement.getSymbol().hasElseSegment()) {
                            p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                                    previousElement.getExtraLayoutData(0) - 1));
                        }
                        p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(actualRow));
                    }
                    path.lineTo(lColumnPos.get(inputColumn), targetY);
                    p3 = new Point2D.Double(lColumnPos.get(
                            inputColumn - indexOfSegment - segment.getExtraLayoutData(0)), targetY);
                    path.lineTo(p3.getX(), p3.getY());
                    if (parentElement.getSymbol().isOverHang()) {
                        Point2D p = getSymbolIntersectionPoint(parentElement.getSymbol(),
                                lColumnPos.get(
                                inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                targetY);
                        path.lineTo(p.getX(), p.getY());
                    }
                    segment.setPathFromThisSegment(path);
                    //overHangElement = null;
                    addJoint(previousElement, segment, commentToInitialize, p1, p2, p3, inputColumn,
                            inputRow, actualRow, 0, i, noPairCommentCount, targetY, overHangX);
                }
            } else { // prazdny segment
                Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
                int segmentRowIncrease = 0;
                if (indexOfSegment != 0) {
                    segmentRowIncrease = segment.getExtraLayoutData(1);
                }
                Point2D p1 = getSymbolIntersectionPoint(parentElement.getSymbol(), lColumnPos.get(
                        inputColumn), lRowPos.get(inputRow - segmentRowIncrease));
                path.moveTo(p1.getX(), p1.getY());
                if (indexOfSegment > 0) {
                    p1 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(
                            inputRow - segmentRowIncrease));
                    path.lineTo(p1.getX(), p1.getY());
                }
                Point2D p3 = new Point2D.Double(lColumnPos.get(inputColumn), targetY);
                path.lineTo(p3.getX(), p3.getY());
                if (indexOfSegment > 0) {
                    if (parentElement.getSymbol().hasPairSymbol()) {
                        p3 = getSymbolIntersectionPoint(getPairElement(parentElement).getSymbol(),
                                p3.getX(), p3.getY());
                        path.lineTo(p3.getX(), p3.getY());
                    } else {
                        p3 = new Point2D.Double(lColumnPos.get(
                                inputColumn - indexOfSegment - segment.getExtraLayoutData(0)),
                                p3.getY());
                        path.lineTo(p3.getX(), p3.getY());
                        if (parentElement.getSymbol().isOverHang()) {
                            Point2D p = getSymbolIntersectionPoint(parentElement.getSymbol(),
                                    p3.getX(), p3.getY());
                            path.lineTo(p.getX(), p.getY());
                        }
                    }
                }
                segment.setPathFromThisSegment(path);
                Point2D p2;
                if (indexOfSegment != 0 && segmentRowIncrease == 0 && noPairCommentCount == 0) {
                    p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(actualRow + 1));
                } else {
                    p2 = new Point2D.Double(lColumnPos.get(inputColumn), lRowPos.get(actualRow));
                }
                addJoint(previousElement, segment, commentToInitialize, p1, p2, p3, inputColumn,
                        inputRow, actualRow, segmentRowIncrease, i, noPairCommentCount, targetY, 0);
            }
        } else { // tato podminka bude splnena az uplne na konci diagramu
        }
    }

    private Point2D getSymbolIntersectionPoint(Symbol symbol, double sourceX, double sourceY)
    {
        if (symbol instanceof GotoLabel) {
            return new Point2D.Double(symbol.getCenterX(), symbol.getCenterY());
        }
        return symbol.getIntersectionPoint(sourceX, sourceY);
    }

    /**
     * Vrací aktuální šířku diagramu.
     *
     * @return aktuální šířka diagramu
     */
    @Override
    public double getWidth()
    {
        return width;
    }

    /**
     * Vrací aktuální výšku diagramu.
     *
     * @return aktuální výška diagramu
     */
    @Override
    public double getHeight()
    {
        return height;
    }

    private void addJoint(LayoutElement previousElement, LayoutSegment segment,
            LayoutElement commentToInitialize, Point2D p1, Point2D p2, Point2D p3, int inputColumn,
            int inputRow, int actualRow, int segmentRowIncrease, int i, int noPairCommentCount,
            double targetY, double overHangX)
    {
        Joint joint;
        if (commentToInitialize != null) {
            i--;
        }
        if (noPairCommentCount == 0) { // jestli predchozi symbol nebyl neparovy komentar
            if (previousElement == null) { // vykreslim joint k rodicovskemu elementu
                joint = new Joint(segment.getParentElement(), segment);
            } else { // vykreslim joint k predchozimu elementu
                joint = new Joint(segment.getElement(i - 1), segment);
            }
            joint.setCenterX((p1.getX() - p2.getX()) / 2 + p2.getX());
            joint.setCenterY((p1.getY() - p2.getY()) / 2 + p2.getY());
        } else { // predchozi symbol byl neparovy komentar, musim umistit joint komentari a navic prizpusobit joint rodici
            // joint komentari
            joint = new Joint(segment.getElement(i - 1), segment); // mohu pouzit i, protoze joint je treba priradit jen predchozimu komentari
            if (lRowPos.get(actualRow) < targetY) { // mam misto pod
                joint.setCenterX(lColumnPos.get(inputColumn));
                joint.setCenterY(
                        (lRowPos.get(actualRow + 1) - lRowPos.get(actualRow)) / 2 + lRowPos.get(
                        actualRow));
            } else if (p3 != null) { // nemam misto pod
                if (lColumnPos.get(inputColumn - 1) > p3.getX()) {
                    joint.setCenterX(
                            (p2.getX() - lColumnPos.get(inputColumn - 1)) / 2 + lColumnPos.get(
                            inputColumn - 1));
                } else {
                    joint.setCenterX((p2.getX() - p3.getX()) / 2 + p3.getX());
                }
                joint.setCenterY(lRowPos.get(actualRow));
            } else {
                joint.setCenterX(lColumnPos.get(inputColumn));
                joint.setCenterY((lRowPos.get(actualRow - 1) - p2.getY()) / 2 + p2.getY());
            }
            lJoints.add(joint);

            // joint symbolu
            if (previousElement == null) { // vykreslim joint k rodicovskemu elementu
                joint = new Joint(segment.getParentElement(), segment);
                if (sizeTrimming) { // kdyz je zapnuto a komentar je prvni, sipka se chova jako pri vypnutem. Joint ale musim vykreslit jako v zaplem stavu
                    p1 = getSymbolIntersectionPoint(segment.getParentElement().getSymbol(),
                            lColumnPos.get(inputColumn), lRowPos.get(inputRow - segmentRowIncrease));
                    if (segment.getParentElement().indexOfInnerSegment(segment) != 0 && lColumnPos.get(
                            inputColumn - 1) > p1.getX()) {
                        p1 = new Point2D.Double(lColumnPos.get(inputColumn - 1), p1.getY());
                    }
                }
                joint.setCenterX((p1.getX() - p2.getX()) / 2 + p2.getX());
                joint.setCenterY((p1.getY() - lRowPos.get(inputRow)) / 2 + lRowPos.get(inputRow));
            } else { // vykreslim joint k predchozimu elementu - nekomentari
                joint = new Joint(segment.getElement(i - 1 - noPairCommentCount), segment);
                if (sizeTrimming && overHangX != 0) {
                    joint.setCenterX((lColumnPos.get(inputColumn) - overHangX) / 2 + overHangX);
                } else {
                    joint.setCenterX(lColumnPos.get(inputColumn));
                }
                if (i < segment.size()) {
                    joint.setCenterY(
                            (lRowPos.get(actualRow - noPairCommentCount) - p1.getY()) / 2 + p1.getY());
                } else {
                    joint.setCenterY(
                            (lRowPos.get(actualRow - noPairCommentCount + 1) - p1.getY()) / 2 + p1.getY());
                }
            }
        }
        lJoints.add(joint);
    }

    /**
     * Tato metoda slouzi pro navrat druheho z parovych elementu pri
     * vykreslovani sipky na konci segmentu.
     *
     * @param fistElement prvni z parovych elementu
     * @return druhy z parovych elementu
     */
    private LayoutElement getPairElement(LayoutElement fistElement)
    {
        LayoutElement element = fistElement.getParentSegment().getElement(
                fistElement.getParentSegment().indexOfElement(fistElement) + 1);
        for (int i = 2; element.getSymbol() instanceof Comment; i++) {
            element = fistElement.getParentSegment().getElement(
                    fistElement.getParentSegment().indexOfElement(fistElement) + i);
        }
        return element;
    }

    /**
     * Metoda, která vytvoří a vrátí cestu komentářového symbolu.<br />
     * (cesta komentářového symbolu je určena relativními souřadnicemi vzhledem
     * k jejímu počátku)
     *
     * @param commentSymbol kometářový symbol, jehož cesta má být vrácena
     * @param fromSymbol je-li komentář párový, symbol, k němuž se komentář
     * vztahuje; je-li komentář nepárový, uvádíme hodnotu null
     * @return cesta komentářového symbolu
     */
    @Override
    public Path2D getCommentPathFromRelative(Comment commentSymbol, Symbol fromSymbol)
    {
        Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, 4);
        ArrayList<Point2D> lRelativeMiddlePoints = commentSymbol.getRelativeMiddlePointsToSymbol();
        Point2D centerP = new Point2D.Double(
                commentSymbol.getCenterX() - commentSymbol.getRelativeX(),
                commentSymbol.getCenterY() - commentSymbol.getRelativeY());
        Point2D p;

        if (fromSymbol == null) { // pocatecni bod bude ze stredu
            path.moveTo(centerP.getX(), centerP.getY());
        } else { // pocatecni bod bude ze symbolu
            if (lRelativeMiddlePoints.isEmpty()) {
                p = fromSymbol.getIntersectionPoint(commentSymbol.getCenterX(),
                        commentSymbol.getCenterY());
            } else {
                p = fromSymbol.getIntersectionPoint(
                        centerP.getX() + lRelativeMiddlePoints.get(0).getX(),
                        centerP.getY() + lRelativeMiddlePoints.get(0).getY());
            }
            if (p != null) {
                path.moveTo(p.getX(), p.getY());
            } else {
                path.moveTo(centerP.getX(), centerP.getY());
            }
        }
        for (Point2D point : lRelativeMiddlePoints) {
            path.lineTo(centerP.getX() + point.getX(), centerP.getY() + point.getY());
        }

        /*
         * PathIterator pathIterator = inputPath.getPathIterator(null);
         * if (!pathIterator.isDone()) {
         * pathIterator.next();
         * while (!pathIterator.isDone()) {
         * double[] coordinates = new double[2];
         * pathIterator.next();
         * if (!pathIterator.isDone()) {
         * int type = pathIterator.currentSegment(coordinates);
         * switch(type) {
         * case PathIterator.SEG_LINETO: {
         * path.lineTo(coordinates[0], coordinates[1]);
         * break;
         * }
         * default: {
         * throw new Error("Unexpected currentSegment!");
         * }
         * }
         * }
         * }
         * }
         */
        //p = commentSymbol.getIntersectionPoint(centerP.getX(), centerP.getY());

        p = commentSymbol.getIntersectionPoint(path.getCurrentPoint().getX(),
                path.getCurrentPoint().getY());
        path.lineTo(p.getX(), p.getY());
        return path;
    }

    private void defineGrid(ArrayList<Double> ls, double firstExtraPadding)
    {
        double previousHalf = ls.get(0) / 2;
        ls.set(0, super.getFlowchartPadding() + previousHalf + firstExtraPadding);
        for (int i = 1; i < ls.size(); i++) {
            double actualHalf = ls.get(i) / 2;
            ls.set(i, ls.get(i - 1) + previousHalf + super.getSymbolPadding() + actualHalf);
            previousHalf = actualHalf;
        }
    }

    private int[] analyzeFlowchartGrid(LayoutSegment segment, int inputColumn, int inputRow)
    {

        int maxColumn = inputColumn;
        int actualColumn = inputColumn;
        int actualRow = inputRow;
        boolean isLastOverHanged = false; // slouzi pro odsazeni cyklu za sebou, pripadne pro odsazeni symbolu za cyklem na konci segmentu
        boolean isLastPaired = false; // slouzi k zaznemenani, ze posledni symbol byl parovy
        while (lColumnWidth.size() <= actualColumn) { //nemuze byt uvnitr smycky nize, protoze segment symbolu nemusi mit zadne elementy
            lColumnWidth.add(0d);
        }
        if (segment.getDescriptionLayout() != null && segment.getParentElement() != null && segment.getParentElement().getInnerSegmentsCount() > 2 && segment.getParentElement().indexOfInnerSegment(
                segment) > 0) {
            double descWidth = segment.getDescriptionLayout().getBounds().getWidth();
            if (lColumnWidth.get(actualColumn) < descWidth) { // zajisteni prostoru pro deskripci segmentu u vicesegmentoveho symbolu (CASE)
                lColumnWidth.set(actualColumn, descWidth);
            }
        }
        for (Iterator<LayoutElement> it = segment.iterator(); it.hasNext();) {
            LayoutElement element = it.next();
            if (!(element.getSymbol() instanceof Comment) || !element.getSymbol().hasPairSymbol()) {
                //if (!sizeTrimming && !element.getSymbol().isOverHang() && isLastOverHanged && !isLastPaired && ((segment.getParentElement() != null && segment.getParentElement().indexOfInnerSegment(segment) > 0) || segment.getParentElement() == null)) { // roztahujeme-li, je treba po kazdem symbolu s levou sipkou odsadit
                if (!sizeTrimming && !element.getSymbol().isOverHang() && isLastOverHanged && !isLastPaired) {
                    actualRow++;
                } else if (element.getSymbol().isOverHang()) { // jestli symbol pokracuje pomoci vybocene sipky vlevo, je treba tomu prizpusobit layout
                    if (isLastOverHanged) { // jestli predchozi symbol byl taky symbol s vybocenou sipkou, musime posunout radek, aby se sipky nesrazili
                        actualRow++;
                    } else {
                        isLastOverHanged = true;
                    }
                    if (element.getSymbol().hasPairSymbol()) {
                        isLastPaired = true;
                    } else {
                        isLastPaired = false;
                    }
                } else if (sizeTrimming && isLastOverHanged && element.getSymbol().isPadded()) {
                    actualRow++; // jestli predchozi symbol byl s vybocenou sipkou, musime posunout radek, aby se respektovalo odsazeni padded symbolu
                }
                if (!(element.getSymbol() instanceof Comment)) {
                    double elementWidth = element.getSymbol().getWidth();
                    // vypocet pripadneho odsazeni kvuli sirce popisku segmentů symbolu
                    double descriptionIncrease = 0;
                    if (element.getInnerSegmentsCount() > 0) {
                        if (element.getInnerSegment(0) != null && element.getInnerSegment(0).getDescriptionLayout() != null) {
                            double descWidth = element.getInnerSegment(0).getDescriptionLayout().getBounds().getWidth();
                            double descIncrease = -(elementWidth / 2 - descWidth + descElseSegmentX + (super.getSymbolPadding() - descPadding));
                            if (descIncrease > 0) {
                                descriptionIncrease = descIncrease;
                            }
                        }
                        if (element.getInnerSegmentsCount() > 1 && element.getInnerSegmentsCount() < 3 && element.getInnerSegment(
                                1) != null && element.getInnerSegment(1).getDescriptionLayout() != null) {
                            double descWidth = element.getInnerSegment(1).getDescriptionLayout().getBounds().getWidth();
                            double descIncrease = descFirstSegmentX + descWidth - (super.getSymbolPadding() - descPadding);
                            if (descIncrease > descriptionIncrease) {
                                descriptionIncrease = descIncrease;
                            }
                        }
                    }
                    if (lColumnWidth.get(actualColumn) < elementWidth + descriptionIncrease * 2) {
                        lColumnWidth.set(actualColumn, elementWidth + descriptionIncrease * 2);
                    }

                    while (lRowWidth.size() <= actualRow) {
                        lRowWidth.add(0d);
                    }
                    double elementHeight = element.getSymbol().getHeight();
                    if (lRowWidth.get(actualRow) < elementHeight) {
                        lRowWidth.set(actualRow, elementHeight);
                    }
                }
                if (element.getInnerSegmentsCount() > 0) {
                    int[] returnedValue;
                    int columnIncrease = 0; // udava pocet "sloupcoveho" posunuti (posun se deje v pripade, ze predchozi vetev by nejakym zpusobem zasahovala do dalsi explicitni)
                    int rowIncrease = 0; // udava pocet "radkoveho" posunuti kvuli sipkam, ktere se po nasledujicim cyklu pricte
                    int maxRow = actualRow;
                    int[] maxRows = new int[element.getInnerSegmentsCount()];
                    Arrays.fill(maxRows, 0);
                    for (int i = 0; i < element.getInnerSegmentsCount(); i++) {
                        LayoutSegment innerSegment = element.getInnerSegment(i);
                        if (innerSegment != null) {
                            innerSegment.clearExtraData();
                            if (columnIncrease > 0) { // bude-li se dit sloupcovy posun, ulozim tuto skutecnost do prislusneho segmentu, aby se pruzkum nemusel opakovat
                                innerSegment.setExtraLayoutData(0, columnIncrease);
                            }
                            boolean firstPadded = false;
                            if (innerSegment.size() > 0) {
                                for (LayoutElement elmnt : innerSegment) {
                                    if (!(elmnt.getSymbol() instanceof Comment) || !elmnt.getSymbol().hasPairSymbol()) {
                                        if (elmnt.getSymbol().isPadded()) {
                                            firstPadded = true;
                                        }
                                        break;
                                    }
                                }
                            }
                            if ((!sizeTrimming && innerSegment.size() > 0) || i == 0 || element.getInnerSegmentsCount() > 2 || firstPadded) { // else(null), 1., ...
                                innerSegment.setExtraLayoutData(1, 1); // deje se radkovy posun, ulozim tuto skutecnost do prislusneho segmentu, aby se pruzkum nemusel opakovat
                                returnedValue = analyzeFlowchartGrid(innerSegment,
                                        actualColumn + i + columnIncrease, actualRow + 1);
                            } else {
                                innerSegment.setExtraLayoutData(1, 0); // pro tento layout je treba mit obsazene oba prvni indexy extraLayoutDat
                                returnedValue = analyzeFlowchartGrid(innerSegment,
                                        actualColumn + i + columnIncrease, actualRow);
                            }
                            if (returnedValue[1] > maxRow) {
                                maxRow = returnedValue[1];
                                if (!element.getSymbol().hasPairSymbol()) {
                                    if (expandMultiSegment && element.getInnerSegmentsCount() > 1) {
                                        // nasleduje vypocet zpetnych sipek
                                        rowIncrease = element.getInnerSegmentsCount() - i - 1;
                                        int myMaxRow = maxRow;
                                        for (int j = 1; j <= i; j++) {
                                            if (maxRows[i - j] > myMaxRow - j) {
                                                int increment = maxRows[i - j] - myMaxRow + j;
                                                rowIncrease += increment;
                                                myMaxRow += increment;
                                            }
                                        }
                                        maxRows[i] = maxRow;
                                        if (i == 0) {
                                            maxRows[i]--;
                                        }
                                    } else if (i == 0) {
                                        rowIncrease = 1;
                                    } else {
                                        rowIncrease = 0;
                                    }

                                    if (i > 0 && innerSegment.size() > 0) {
                                        if (!sizeTrimming || element.getInnerSegmentsCount() > 2) {
                                            rowIncrease++;
                                        }
                                    }
                                } else if (sizeTrimming) {
                                    //rowIncrease = element.getInnerSegmentsCount() - (j + 2);
                                    rowIncrease = -1;
                                }
                            }
                            if (returnedValue[0] > maxColumn) {
                                maxColumn = returnedValue[0];
                            }
                            columnIncrease = returnedValue[0] - (actualColumn + i);
                        } else {
                            maxRow++;
                            if (!element.getSymbol().hasPairSymbol()) {
                                rowIncrease = element.getInnerSegmentsCount() - i - 1; // zpetne sipky
                            }
                        }
                    }
                    actualRow = maxRow + rowIncrease;
                    element.setExtraLayoutData(0, actualRow);
                    if (sizeTrimming && !it.hasNext() && element.getSymbol().hasElseSegment() && (segment.getParentElement() == null || (segment.getParentElement().getInnerSegmentsCount() < 3 && segment.getParentElement().indexOfInnerSegment(
                            segment) > 0))) { // posunuti radku pro schod za else vetvi (misto pro puntik)
                        actualRow++;
                    }
                } else {
                    if (sizeTrimming && !it.hasNext() && (segment.getParentElement() == null || segment.getParentElement().indexOfInnerSegment(
                            segment) > 0) && ((isLastOverHanged && !isLastPaired) || element.getSymbol().isPadded())) { // posunuti radku kvuli sipce zleva predchoziho symbolu - srazila by se s touto ukoncovaci - navratovou
                        actualRow++;
                    }
                    actualRow++;
                }
                if (element.getSymbol().isOverHang()) {
                    isLastOverHanged = true;
                } else if (isLastPaired) { // element je tim padem druhy z paroveho
                    isLastPaired = false;
                    element.setExtraLayoutData(0, actualRow); // je treba parovemu symbolu priradit take umisteni o aktualnim radku
                } else {
                    isLastOverHanged = false;
                }
                if (!it.hasNext() && isLastOverHanged && !isLastPaired) { // posunuti radku pro schod za postrani sipkou (misto pro puntik)
                    if (!sizeTrimming || (isLastOverHanged && segment.getParentElement().indexOfInnerSegment(
                            segment) == 0) || (segment.getParentElement().getInnerSegmentsCount() > 2 && isLastOverHanged)) { // jestli neni zaply sizeTrimming nebo se jedna o case s indexem vetsim nez 0 a poslednim overhang symbolem - rozhodl jsem se case odsazovat i pri sizeTrimmingu
                        actualRow++;
                    }
                }
            }
        }
        return new int[]{maxColumn, actualRow};
    }

}
