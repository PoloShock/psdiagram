/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.flowchart.layouts;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.GotoLabel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Joint;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Goto;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 * Tato abstraktní třída zajišťuje základní implementaci rozhraní Layout.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public abstract class AbstractLayout implements Layout
{
    // TODO mozne nastaveni: gradient - barvy, stiny, rozestup symbolu, odsazeni diagramu, mrizka symbolova, mrizka pravidelna
    // TODO multiselect?
    // TODO pridat do nastaveni nastaveni layoutu

    public static int flowchartPadding = 15;
    private int symbolPadding = 14;
    private int arrowLength = 12;
    private boolean editMode = true;
    private final Color pathColor = Color.BLACK;
    private final Color shadowColor = new Color(0, 0, 0, 100);
    private final BasicStroke commentStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, new float[]{10, 5}, 0);
    private final BasicStroke commentBoldStroke = new BasicStroke(4, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 10.0f, new float[]{10, 5}, 0);
    private final BasicStroke highlightStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, new float[]{4, 4}, 0);
    private final BasicStroke gotoStroke = highlightStroke;
    private Flowchart<LayoutSegment, LayoutElement> flowchart;
    private JComponent canvas;
    private ArrayList<Joint> lJoints = new ArrayList<>(30);
    private LayoutElement focusedElement = null; // kdyz neni null, je oznacen prioritne
    private Joint focusedJoint = null;
    private Comment boldPathComment = null; // uklada komentar, ktereho cesta se ma vykreslit zvyraznene
    private boolean focusJointOnly = false;
    private boolean noFocusPaint = false;

    /**
     * Základní konstruktor s parametrem, určující plátno, na které má být
     * vývojový diagram vykreslován.<br />
     * Vývojový diagram je vytvořen automaticky, obsahuje pouze počáteční a
     * koncový symbol.
     *
     * @param canvas plátno, na které má být vývojový diagram vykreslován
     */
    public AbstractLayout(JComponent canvas)
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
    public AbstractLayout(JComponent canvas, Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        this.canvas = canvas;
        if (flowchart != null) {
            this.flowchart = flowchart;
        } else {
            this.flowchart = new Flowchart<>(new LayoutSegment(null));
            focusedElement = this.flowchart.getMainSegment().addSymbol(null,
                    EnumSymbol.STARTEND.getInstance("Začátek"));
            this.flowchart.getMainSegment().addSymbol(focusedElement,
                    EnumSymbol.STARTEND.getInstance("Konec"));

            //TEST
                /*
             * Comment comment;
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 3);
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 6);
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 6);
             * focusedElement.getInnerSegment(5).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(4).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(2).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(2).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 4);
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(2).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(2).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(2).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(2).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONDOWN.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(0).getElement(0).getSymbol();
             * comment.setRelativeX(-20);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).getElement(0).getSymbol();
             * comment.setRelativeX(-20);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.IO.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setHasPairSymbol(true);
             * comment.setRelativeX(100);
             * comment.setRelativeY(-50);
             * comment.getRelativeMiddlePointsToSymbol().add(new
             * Point2D.Double(50, -50));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(1).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(-10);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(20);
             * comment.setRelativeY(0);
             * comment.getRelativeMiddlePointsToSymbol().add(new
             * Point2D.Double(10, -10));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(30);
             * comment.setRelativeY(15);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(10);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(15);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(80);
             * comment.setRelativeY(-30);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(-20);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.IO.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.GOTO.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment = (Comment)focusedElement.getSymbol();
             * comment.setHasPairSymbol(true);
             * comment.setRelativeX(110);
             * comment.setRelativeY(10);
             * comment.getRelativeMiddlePointsToSymbol().add(new
             * Point2D.Double(20, 20));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.GOTOLABEL.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.GOTO.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(0).getElement(0).getSymbol();
             * comment.setHasPairSymbol(true);
             * comment.setRelativeX(-30);
             * comment.setRelativeY(10);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.GOTO.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * new LoopEnd(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * new LoopEnd(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setHasPairSymbol(true);
             * comment.setRelativeX(60);
             * comment.setRelativeY(70);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONDOWN.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONDOWN.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONDOWN.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONDOWN.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(80);
             * comment.setRelativeY(-30);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement, new
             * LoopEnd(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.LOOPCONDITIONUP.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setHasPairSymbol(true);
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(80);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(0);
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SUBROUTINE.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.ELLIPSIS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.ELLIPSIS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.ELLIPSIS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.GOTOLABEL.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.SWITCH.getInstance(null), 6);
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(0).getElement(0).getSymbol();
             * comment.setRelativeX(20);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment =
             * (Comment)focusedElement.getInnerSegment(0).getElement(0).getSymbol();
             * comment.setRelativeX(-20);
             * comment.setRelativeY(0);
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(0).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(0).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.SUBROUTINE.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.SUBROUTINE.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.GOTOLABEL.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.DECISION.getInstance(null));
             * focusedElement.getInnerSegment(1).getElement(0).getInnerSegment(1).getElement(0).getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.SUBROUTINE.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(2).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(3).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(5).addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(5).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.COMMENT.getInstance(null));
             * comment = (Comment)focusedElement.getSymbol();
             * comment.setRelativeX(50);
             * comment.setRelativeY(20);focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.FOR.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement.getInnerSegment(1).addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             * focusedElement =
             * this.flowchart.getMainSegment().addSymbol(focusedElement,
             * EnumSymbol.PROCESS.getInstance(null));
             */
        }
        focusedElement = null;
    }

    /**
     * Metoda pro získání možného nastavení konkrétního layoutu.
     *
     * @return možné nastavení konkrétního layoutu
     */
    @Override
    public abstract ArrayList<JMenuItem> getSettings();

    /**
     * Vrací aktuální výšku diagramu.
     *
     * @return aktuální výška diagramu
     */
    @Override
    public abstract double getHeight();

    /**
     * Vrací aktuální šířku diagramu.
     *
     * @return aktuální šířka diagramu
     */
    @Override
    public abstract double getWidth();

    /**
     * Stěžejní metoda layoutu. Voláním této metody layout přepočítá veškeré
     * rozmístění symbolů, propojovacích cest a popisků.
     */
    public abstract void prepareMyFlowchart();

    /**
     * Stěžejní metoda layoutu. Voláním této metody layout přepočítá veškeré
     * rozmístění symbolů, propojovacích cest a popisků. Zároveň je obnoven
     * aktuální označený Joint se symbolem.
     */
    @Override
    public void prepareFlowchart()
    { // z vnejsku volana jen pokud je to opravdu nutne..
        prepareMyFlowchart();
        for (Joint joint : lJoints) {
            if (joint.getParentElement().equals(focusedJoint.getParentElement()) && joint.getParentSegment().equals(
                    focusedJoint.getParentSegment())) {
                focusedJoint = joint;
                return;
            }
        }
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
    public abstract Path2D getCommentPathFromRelative(Comment commentSymbol, Symbol fromSymbol);

    /**
     * Metoda pro získání všech komentářových symbolů diagramu.
     *
     * @return kolekce všech komentářových symbolů diagramu
     */
    @Override
    public abstract ArrayList<Comment> getlCommentSymbols();

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
        ArrayList<Path2D> lCommentsPaths = new ArrayList<>();
        ArrayList<Path2D> lGotoPaths = new ArrayList<>();
        ArrayList<LayoutElement> lCommentElements = new ArrayList<>(); // komentare vykreslim az naposled, aby se pak neprekryvali pri posouvani s ostatnimi symboly

        g2d.setColor(pathColor);
        for (LayoutSegment segment : getFlowchart()) {
            if (segment != null) {
                drawPath(g2d, segment.getPathToThisSegment());
                if (segment.getDescriptionLayout() != null) {
                    segment.getDescriptionLayout().draw(g2d,
                            (float) segment.getDescriptionLocation().getX(),
                            (float) segment.getDescriptionLocation().getY()); // vykresleni deskripce segmentu
                }

                boolean lastGoto = false;
                for (LayoutElement element : segment) {
                    if (element.getSymbol() instanceof Comment) {
                        lCommentElements.add(element);
                    } else {
                        drawSymbol(g2d, element.getSymbol(), element, clipShadow);
                    }
                    boolean EndStartEnd = element.getSymbol() instanceof StartEnd && (element.getParentSegment().getParentElement() != null || element.getParentSegment().indexOfElement(
                            element) > 1 || (element.getParentSegment().indexOfElement(element) == 1 && !(element.getParentSegment().getElement(
                                    0).getSymbol() instanceof Comment)));
                    if (!(element.getSymbol() instanceof Comment) && !(element.getSymbol() instanceof Goto) && !(EndStartEnd)) {
                        drawPath(g2d, element.getPathToNextSymbol());
                        lastGoto = false;
                    } else if (element.getSymbol() instanceof Goto || (EndStartEnd)) {
                        lGotoPaths.add(element.getPathToNextSymbol());
                        lastGoto = true;
                    } else if (!element.getSymbol().equals(boldPathComment)) {
                        lCommentsPaths.add(element.getPathToNextSymbol());
                    }
                }
                if (!lastGoto) {
                    drawPath(g2d, segment.getPathFromThisSegment());
                } else {
                    lGotoPaths.add(segment.getPathFromThisSegment());
                }
            }
        }
        // dodatecne nakresleni spoju od goto
        if (lGotoPaths.size() > 0) {
            Stroke stroke = g2d.getStroke();
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(gotoStroke);
            for (Path2D path : lGotoPaths) {
                g2d.draw(path);
            }
            g2d.setColor(pathColor);
            g2d.setStroke(stroke);
        }
        if (editMode) { // dodatecne vykresleni jointů
            for (Joint joint : lJoints) {
                drawSymbol(g2d, joint, null, clipShadow);
            }
        }
        // dodatecne nakresleni carovych spoju ke komentarum
        if (lCommentsPaths.size() > 0) {
            g2d.setStroke(commentStroke);
            for (Path2D path : lCommentsPaths) {
                g2d.draw(path);
            }
        }
        // dodatecne vykresleni komentaru
        if (lCommentElements.size() > 0) {
            g2d.setStroke(new BasicStroke(1));
            for (LayoutElement commentElement : lCommentElements) {
                if (commentElement.getSymbol().equals(boldPathComment)) { // boldPathCommentu musim dodatecne vyreslit cestu
                    g2d.setStroke(commentBoldStroke);
                    g2d.draw(commentElement.getPathToNextSymbol());
                    g2d.setStroke(new BasicStroke(1));
                }
                drawSymbol(g2d, commentElement.getSymbol(), commentElement, clipShadow);
            }
        }
        // oramovani oznaceneho symbolu
        if (editMode && !focusJointOnly && !noFocusPaint) {
            Symbol focused;
            if (focusedElement != null) {
                focused = focusedElement.getSymbol();
            } else {
                focused = focusedJoint;
            }
            double focusedWidth = focused.getWidth();
            if (focused instanceof GotoLabel) {
                focusedWidth /= 2;
            }
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(highlightStroke);
            g2d.draw(new Rectangle2D.Double(focused.getX() - 5, focused.getY() - 4,
                    focusedWidth + 10, focused.getHeight() + 8));
            g2d.setColor(pathColor);
        }
    }

    void drawSymbol(Graphics2D g2d, Symbol symbol, LayoutElement element, boolean clipShadow)
    {
        AffineTransform af = g2d.getTransform();
        Color shapeUpColor;
        Color shapeDownColor;
        Color borderColor;
        if (SettingsHolder.settings.isFunctionFilters()
                && !symbol.areCommandsValid()
                && EnumSymbol.getEnumSymbol(symbol.getClass()).areAllCommandsPresent(element)) {
            shapeUpColor = symbol.getErrorShapeUpColor();
            shapeDownColor = symbol.getErrorShapeDownColor();
            borderColor = symbol.getErrorBorderColor();
        } else {
            shapeUpColor = symbol.getShapeUpColor();
            shapeDownColor = symbol.getShapeDownColor();
            borderColor = symbol.getBorderColor();
        }

        if ((editMode && element != null && !focusJointOnly && !noFocusPaint && !(symbol instanceof Comment)
                && (element.equals(focusedElement) || (focusedElement == null && focusedJoint != null && element.equals(
                        focusedJoint.getParentElement())))) || (symbol.equals(focusedJoint) && !noFocusPaint)) {

            if (SettingsHolder.settings.isFunctionFilters()
                    && !symbol.areCommandsValid()
                    && EnumSymbol.getEnumSymbol(symbol.getClass()).areAllCommandsPresent(element)) {
//                shapeUpColor = Layout.FOCUSED_ERROR_UP_COLOR;
//                shapeDownColor = Layout.FOCUSED_ERROR_DOWN_COLOR;
//                borderColor = symbol.getBorderColor();
            } else {
                shapeUpColor = Layout.FOCUSED_UP_COLOR;
                shapeDownColor = Layout.FOCUSED_DOWN_COLOR;
            }
            if (symbol.getShapeUpColor() == null) {
                borderColor = Layout.FOCUSED_UP_COLOR;
            }
        }
        if (symbol.getShapeUpColor() != null) {
            if (symbol.hasShadow()) {
                // následuje vykreslení stínu
                g2d.setColor(shadowColor);
                if (clipShadow) {
                    Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
                    outside.subtract(new Area(symbol.getShape()));
                    g2d.setClip(outside);
                }
                g2d.translate(2, 2);
                g2d.fill(symbol.getShape());
                if (clipShadow) {
                    g2d.setClip(null);
                }
                g2d.setTransform(af);
            }

            // inicializace barev (gradient ci nikoliv)
            if (symbol.getShapeUpColor().equals(symbol.getShapeDownColor())) {
                g2d.setColor(shapeUpColor);
            } else {
                //g2d.setPaint(new GradientPaint((float)symbol.getX(), (float)symbol.getY(), symbol.getShapeUpColor(), (float)symbol.getX(), (float)(symbol.getY() + symbol.getHeight()), symbol.getShapeDownColor()));
                //g2d.setPaint(new GradientPaint((float)symbol.getX(), (float)symbol.getY(), symbol.getShapeUpColor(), (float)(symbol.getX() + symbol.getWidth()), (float)(symbol.getY() + symbol.getHeight()), symbol.getShapeDownColor()));
                if (!(symbol instanceof GotoLabel)) {
                    g2d.setPaint(new GradientPaint(
                            (float) (symbol.getX() + symbol.getWidth() * 0.25),
                            (float) symbol.getY(), shapeUpColor,
                            (float) (symbol.getX() + symbol.getWidth() * 0.75),
                            (float) (symbol.getY() + symbol.getHeight()), shapeDownColor));
                } else {
                    GotoLabel gotoLabel = (GotoLabel) symbol;
                    g2d.setPaint(new GradientPaint(
                            (float) (gotoLabel.getX() + (gotoLabel.getWidth() / 2 - gotoLabel.getMyHair()) * 0.25),
                            (float) gotoLabel.getY(), shapeUpColor,
                            (float) (gotoLabel.getX() + (gotoLabel.getWidth() / 2 - gotoLabel.getMyHair()) * 0.75),
                            (float) (gotoLabel.getY() + gotoLabel.getHeight()), shapeDownColor));
                }
            }

            // vykreslení symbolu
            g2d.fill(symbol.getShape());
        } else if (symbol.hasShadow()) {
            // následuje vykreslení stínu transparentního symbolu
            g2d.setColor(shadowColor);
            g2d.translate(1, 1);
            g2d.draw(symbol.getShape());
            g2d.setTransform(af);
        }
        // vykreslení okraje symbolu
        g2d.setColor(borderColor);
        g2d.draw(symbol.getShape());

        // vykresleni textu symbolu
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        for (int i = 0; i < symbol.getTextLayoutLines().size(); i++) {
            Point2D p = symbol.getTextLayoutOrigins().get(i);
            symbol.getTextLayoutLines().get(i).draw(g2d, (float) p.getX(), (float) p.getY());
        }
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(pathColor);
    }

    private void drawPath(Graphics2D g2d, Path2D path)
    {
        Path2D[] newPath = shouldBeArrow(path);
        g2d.draw(newPath[0]);
        if (newPath[1] != null) {
            g2d.fill(newPath[1]);
        }
    }

    /**
     * Metoda, která prověří, zda vstupní cesta má obsahovat šipku v ústí.<br />
     * Výpočet probíhá na základě orientace cesty. Je-li její konec vzhledem k
     * počátku vlevo nebo nahoře, cesta by měla podle české normy být opatřena
     * šipkou.<br />
     * Metoda vrací cestu původní(která je zkrácená o případnou délku
     * šipky) a cestu šipky samotné.
     *
     * @param path cesta, která má být prověřena
     * @return pole s dvěma prvky. První je původní cesta(zkrácená o případnou
     * délku šipky), druhý prvek je samotná šipka; null když šipka není potřeba.
     */
    @Override
    public Path2D[] shouldBeArrow(Path2D path)
    {
        // nasleduje rozhodnuti, zda vykreslit sipku
        Path2D newPath = new Path2D.Double(path.getWindingRule());
        Path2D[] retPath = new Path2D[]{newPath, null};
        PathIterator pathIterator = path.getPathIterator(null);
        double resultX = 0;
        double resultY = 0;
        double[] previousCoordinates = new double[2];
        while (!pathIterator.isDone()) {
            double[] coordinates = new double[2];
            int type = pathIterator.currentSegment(coordinates);
            pathIterator.next();
            switch (type) {
                case PathIterator.SEG_MOVETO: {
                    newPath.moveTo(coordinates[0], coordinates[1]);
                    break;
                }
                case PathIterator.SEG_LINETO: {
                    try {
                        resultX += coordinates[0] - previousCoordinates[0];
                        resultY += coordinates[1] - previousCoordinates[1];
                        if (pathIterator.isDone() && (resultX < -0.0001 || resultY < -0.0001)) { // -0.0001: nestarat se o drobne odchilky double
                            // je treba posunout caru tak, aby nebyla ustila do sipky
                            double lengthX = coordinates[0] - previousCoordinates[0];
                            double lengthY = coordinates[1] - previousCoordinates[1];
                            double length = Math.sqrt(Math.pow(lengthX, 2) + Math.pow(lengthY, 2));
                            newPath.lineTo(coordinates[0] - (lengthX / length) * (arrowLength - 1),
                                    coordinates[1] - (lengthY / length) * (arrowLength - 1));

                            // vypocet sipky
                            double angleL = Math.atan2(previousCoordinates[0] - coordinates[0],
                                    previousCoordinates[1] - coordinates[1]);
                            Path2D arrow = new Path2D.Double();
                            arrow.moveTo(coordinates[0] + Math.sin(angleL + 0.3) * arrowLength,
                                    coordinates[1] + Math.cos(angleL + 0.3) * arrowLength);
                            arrow.lineTo(coordinates[0], coordinates[1]);
                            arrow.lineTo(coordinates[0] + Math.sin(angleL - 0.3) * arrowLength,
                                    coordinates[1] + Math.cos(angleL - 0.3) * arrowLength);
                            retPath[1] = arrow;
                        } else {
                            newPath.lineTo(coordinates[0], coordinates[1]);
                        }
                    } catch (NullPointerException e) {
                        throw new Error("NullPointerException: " + e.getMessage());
                    }
                    break;
                }
                default: {
                    throw new Error("Unexpected currentSegment!");
                }
            }
            if (!pathIterator.isDone()) {
                previousCoordinates = coordinates;
            }
        }
        return retPath;
    }

    /**
     * Metoda přidá daný symbol na místo označeného jointu.
     *
     * @param symbol symbol, který má být přidán
     * @param innerOutCount požadovaný počet vnitřních segmentů přidávaného
     * symbolu
     * @return element, reprezentující přidaný symbol
     */
    @Override
    public LayoutElement addNewSymbol(Symbol symbol, int innerOutCount)
    {
        setFocusPaintToDefault();

        LayoutElement element;
        if (symbol instanceof Comment) {
            if (symbol.hasPairSymbol() && focusedElement != null) {
                int indexOfFocusedElement = focusedElement.getParentSegment().indexOfElement(
                        focusedElement);
                if (focusedElement.getSymbol() instanceof Comment || (indexOfFocusedElement - 1 >= 0 && focusedElement.getParentSegment().getElement(
                        indexOfFocusedElement - 1).getSymbol() instanceof Comment && focusedElement.getParentSegment().getElement(
                                indexOfFocusedElement - 1).getSymbol().hasPairSymbol())) {
                    symbol.setHasPairSymbol(false);
                    element = focusedJoint.getParentSegment().addSymbol(
                            focusedJoint.getParentElement(), symbol, innerOutCount);
                    /*
                     * int i;
                     * for (i = indexOfFocusedElement;
                     * focusedElement.getParentSegment().getElement(i).getSymbol().hasPairSymbol();
                     * i++) {}
                     * element =
                     * focusedElement.getParentSegment().addSymbol(focusedElement.getParentSegment().getElement(i),
                     * symbol);
                     */
                } else if (indexOfFocusedElement - 1 >= 0) {
                    element = focusedElement.getParentSegment().addSymbol(
                            focusedElement.getParentSegment().getElement(indexOfFocusedElement - 1),
                            symbol, innerOutCount);
                } else {
                    element = focusedElement.getParentSegment().addSymbol(
                            focusedElement.getParentSegment().getParentElement(), symbol,
                            innerOutCount);
                }
            } else {
                symbol.setHasPairSymbol(false);
                element = focusedJoint.getParentSegment().addSymbol(focusedJoint.getParentElement(),
                        symbol, innerOutCount);
            }
        } else if (symbol instanceof LoopEnd) {
            return focusedJoint.getParentSegment().addSymbol(focusedJoint.getParentElement(), symbol,
                    innerOutCount); // priste ocekavam dalsi cast smycky, nebudu tedy obnovovat focus
        } else {
            element = focusedJoint.getParentSegment().addSymbol(focusedJoint.getParentElement(),
                    symbol, innerOutCount);
        }
        prepareMyFlowchart();
        setFocusedElement(element);
        return element;
    }

    /**
     * Metoda přidá daný symbol na místo označeného jointu.
     *
     * @param symbol symbol, který má být přidán
     * @return element, reprezentující přidaný symbol
     */
    @Override
    public LayoutElement addNewSymbol(Symbol symbol)
    {
        return addNewSymbol(symbol, 0);
    }

    /**
     * Metoda pro vložení více elementů naráz. Elementy se vloží na místo
     * aktuálně označeného jointu.
     *
     * @param elements kolekce elementů, které mají být vloženy
     */
    @Override
    public void addElements(ArrayList<LayoutElement> elements)
    {
        setFocusPaintToDefault();

        if (elements.size() == 1 && elements.get(0).getSymbol() instanceof Comment) {
            // je-li jediny symbol komentar, musim rozhodnout, zda jej pripnu k symbolu nebo ho necham na samostatne pozici
            if (focusedElement != null) {
                elements.get(0).getSymbol().setHasPairSymbol(true);
            } else {
                elements.get(0).getSymbol().setHasPairSymbol(false);
            }
            addNewSymbol(elements.get(0).getSymbol(), 0); // metoda addNewSymbol zvaliduje nastavene parovani komentare
            return;
        }

        LayoutElement parentElement = focusedJoint.getParentElement();
        LayoutSegment segment = focusedJoint.getParentSegment();
        for (int i = 0; i < elements.size(); i++) {
            parentElement = segment.addElement(parentElement, elements.get(i));
        }

        prepareMyFlowchart();
        setFocusedElement(elements.get(elements.size() - 1));
    }

    /**
     * Metoda pro přesunutí daného elementu v pořadí za daný element.
     *
     * @param elementToMove element, který má být přesunut
     * @param destinationBeforeElement element, za který má být přesouvaný
     * element umístěn
     */
    @Override
    public void moveElement(LayoutElement elementToMove, LayoutElement destinationBeforeElement)
    {
        elementToMove.getParentSegment().moveElement(elementToMove, destinationBeforeElement);
        prepareMyFlowchart();
        setFocusedElement(elementToMove);
    }

    /**
     * Metoda pro vymazání daného elementu. S elementem se smažou i případné
     * další elementy na něj závislé, jako například párové komentáře, párové
     * elementy.
     *
     * @param element element, který má být vymazán
     */
    @Override
    public void removeElement(LayoutElement element)
    {
        int elementIndex = myRemoveElement(element);
        if (elementIndex < 0) {
            return;
        }

        LayoutSegment parentSegment = element.getParentSegment();
        LayoutElement elmet;
        if (parentSegment.isEmpty()) { // jestli byl element jedinacek v segmentu
            elmet = parentSegment.getParentElement(); // parentElement bude pristupny vzdy, nebot v kazdem diagramu jsou minimalne dva symboly (zacatek, konec)
        } else {
            if (elementIndex < parentSegment.size()) { // jestli existuje element pod timto elementem
                elmet = parentSegment.getElement(elementIndex);
            } else {
                elmet = parentSegment.getElement(elementIndex - 1);
            }
        }
        setFocusedElement(elmet);
    }

    /**
     * Metoda pro vymazání daného elementu. S elementem se smažou i případné
     * další elementy na něj závislé, jako například párové komentáře, párové
     * elementy.
     * Označený joint bude ten, pro který by přidávaný symbol zastoupil
     * symbol právě smazaný.
     *
     * @param element element, který má být vymazán
     */
    @Override
    public void removeElementFocusItsJoint(LayoutElement element)
    {
        int elementIndex = myRemoveElement(element);
        if (elementIndex < 0) {
            return;
        }

        LayoutSegment parentSegment = element.getParentSegment();
        LayoutElement elmet;
        if (parentSegment.isEmpty() || elementIndex == 0) { // jestli byl element jedinacek v segmentu
            elmet = parentSegment.getParentElement(); // parentElement bude pristupny vzdy, nebot v kazdem diagramu jsou minimalne dva symboly (zacatek, konec)
        } else {
            elmet = parentSegment.getElement(elementIndex - 1);
        }

        for (Joint joint : lJoints) {
            if (joint.getParentElement().equals(elmet) && joint.getParentSegment().equals(
                    parentSegment)) {
                setFocusedJoint(joint);
            }
        }
    }

    /**
     * Focus označení korespondujícího symbolu nebude vykresleno. Voláním této metody se ruší
     * noFocusPaint.
     * Tento stav je zrusen pridanim noveho symbolu do diagramu.
     */
    @Override
    public void setFocusJointOnly()
    {
        focusedElement = null;
        focusJointOnly = true;
        noFocusPaint = false;
    }

    /**
     * Layout vizuálně přestane vykreslovat jakýkoliv focus.
     * Tento stav je zrusen pridanim noveho symbolu do diagramu.
     */
    @Override
    public void setNoFocusPaint()
    {
        noFocusPaint = true;
    }

    @Override
    public void setFocusPaintToDefault()
    {

        focusJointOnly = false;
        noFocusPaint = false;
    }

    private int myRemoveElement(LayoutElement element)
    {
        if (element.getParentSegment().getParentElement() == null && (element.getParentSegment().indexOfElement(
                element) + 1 == element.getParentSegment().size() || (element.getParentSegment().indexOfElement(
                        element) == 0 && element.getSymbol() instanceof StartEnd))) {
            return -1; // jestli se jedna o prvni nebo koncovy znak
        }
        LayoutSegment parentSegment = element.getParentSegment();

        /*
         * int elementIndex = parentSegment.indexOfElement(element);
         * // nasleduje osetreni pripadnych parovych symbolu zavislych na tomto
         * if (!(element.getSymbol() instanceof Comment)) {
         * if (element.getSymbol().hasPairSymbol()) {
         * for (int i = elementIndex + 1; i < parentSegment.size() &&
         * parentSegment.getElement(i).getSymbol().hasPairSymbol();
         * parentSegment.removeElement(parentSegment.getElement(i))){}
         * parentSegment.removeElement(parentSegment.getElement(elementIndex +
         * 1));
         * }
         * for (int i = elementIndex - 1; i >= 0 &&
         * parentSegment.getElement(i).getSymbol().hasPairSymbol(); i--) {
         * parentSegment.removeElement(parentSegment.getElement(i));
         * elementIndex--;
         * }
         * }
         */
        ArrayList<LayoutElement> arrElementToDelete = getMeAndMyDependants(element);
        int elementIndex = parentSegment.indexOfElement(arrElementToDelete.get(0));
        for (int i = elementIndex - 1; i >= 0; i--) {
            if (!arrElementToDelete.contains(parentSegment.getElement(i))) {
                //fi
                break;
            }
        }
        for (LayoutElement elm : arrElementToDelete) {
            parentSegment.removeElement(elm);
        }
        prepareMyFlowchart();
        return elementIndex;
    }

    /**
     * Metoda pro získání všech závislých elementů na elementu vstupním.<br />
     * Metoda má využití např. při mazání elementu, kdy je třeba element samazat
     * i se všemi jeho závislými elementy.
     *
     * @param element element, jehož závislé elementy je třeba dohledat
     * @return kolekce závislých elementů na elementu vstupním
     */
    @Override
    public ArrayList<LayoutElement> getMeAndMyDependants(LayoutElement element)
    {
        ArrayList<LayoutElement> arr = new ArrayList<>();
        LayoutSegment parentSegment = element.getParentSegment();
        int elementIndex = parentSegment.indexOfElement(element);

        // nasleduje osetreni pripadnych parovych symbolu zavislych na tomto
        if (!(element.getSymbol() instanceof Comment)) {
            for (int i = elementIndex - 1; i >= 0 && parentSegment.getElement(i).getSymbol().hasPairSymbol(); i--) {
                arr.add(0, parentSegment.getElement(i));
            }
            arr.add(element);
            if (element.getSymbol().hasPairSymbol()) {
                int i;
                for (i = elementIndex + 1; i < parentSegment.size() && parentSegment.getElement(i).getSymbol().hasPairSymbol(); i++) {
                    arr.add(parentSegment.getElement(i));
                }
                arr.add(parentSegment.getElement(i));
            }
        } else {
            arr.add(element);
        }

        return arr;
    }

    /**
     * Metoda pro nastavení komentářového symbolu, jehož cesta má být
     * zvýrazněna.
     *
     * @param boldPathComment komentářový symbol, jehož cesta má být zvýrazněna
     */
    @Override
    public void setBoldPathComment(Comment boldPathComment)
    {
        this.boldPathComment = boldPathComment;
    }

    /**
     * Metoda pro získání komentářového symbolu, který má aktuálně zvýrazněnou
     * cestu pro její modelování.
     *
     * @return komentářový symbol, který má aktuálně zvýrazněnou cestu pro
     * modelování
     */
    @Override
    public Comment getBoldPathComment()
    {
        return boldPathComment;
    }

    /**
     * Metoda pro vyhledání druhého z párových elementů.
     *
     * @param element první z párového elementu
     * @return druhý z párového elementu
     */
    @Override
    public LayoutElement findMyPairedElement(LayoutElement element)
    {
        if (element.getSymbol().hasPairSymbol()) {
            for (int i = element.getParentSegment().indexOfElement(element) + 1; i < element.getParentSegment().size(); i++) {
                LayoutElement pairedElement = element.getParentSegment().getElement(i);
                if (element.getSymbol() instanceof Comment || !pairedElement.getSymbol().hasPairSymbol()) {
                    return pairedElement;
                }
            }
        } else {
            for (int i = element.getParentSegment().indexOfElement(element) - 1; i >= 0; i--) {
                LayoutElement pairedElement = element.getParentSegment().getElement(i);
                if (!(pairedElement.getSymbol() instanceof Comment) && pairedElement.getSymbol().hasPairSymbol()) {
                    return pairedElement;
                }
            }
        }
        return null;
    }

    /**
     * Metoda pro vyhledání druhého z párových symbolů.
     *
     * @param symbol první z párového symbolu
     * @return druhý z párového symbolu
     */
    @Override
    public Symbol findMyPairedSymbol(Symbol symbol)
    {
        LayoutElement element = findMyElement(symbol);
        if (element != null) {
            return findMyPairedElement(element).getSymbol();
        }
        return null;
    }

    /**
     * Metoda pro vyhledání korespondujícího elementu, který reprezentuje daný
     * symbol.
     *
     * @param symbol symbol, jehož element má být nalezen
     * @return element reprezentující daný symbol
     */
    @Override
    public LayoutElement findMyElement(Symbol symbol)
    {
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (LayoutElement element : segment) {
                    if (element.getSymbol().equals(symbol)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Metoda pro získání vývojového diagramu v podobě instance Flowchart.
     *
     * @return instance Flowchart, reprezentující vývojový diagram
     */
    @Override
    public Flowchart<LayoutSegment, LayoutElement> getFlowchart()
    {
        return flowchart;
    }

    /**
     * Metoda pro nastavení vývojového diagramu pro vyobrazení.
     * <p/>
     * @param flowchart vývojového diagramu pro vyobrazení
     */
    @Override
    public void setFlowchart(Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        if (flowchart != null) {
            this.flowchart = flowchart;
        } else {
            this.flowchart = new Flowchart<>(new LayoutSegment(null));
            focusedElement = this.flowchart.getMainSegment().addSymbol(null,
                    EnumSymbol.STARTEND.getInstance("Začátek"));
            this.flowchart.getMainSegment().addSymbol(focusedElement,
                    EnumSymbol.STARTEND.getInstance("Konec"));
        }
        focusedElement = null;
        prepareMyFlowchart();
        setFocusedJoint(getlJoints().get(0));
    }

    /**
     * Metoda pro získání nastavené délky šipky cesty.
     *
     * @return nastavená délka šipky cesty
     */
    @Override
    public int getArrowLength()
    {
        return arrowLength;
    }

    /**
     * Metoda pro nastavení délky šipky.
     *
     * @param arrowLength požadovaná délka šipky
     */
    @Override
    public void setArrowLength(int arrowLength)
    {
        this.arrowLength = arrowLength;
    }

    /**
     * Metoda pro získání nastavené hodnoty odsazení diagramu.<br />
     * Odsazení diagramu je odsazení o nastavený počet pixelů na každém kraji
     * diagramu.
     *
     * @return nastavená velikost odsazení diagramu
     */
    @Override
    public int getFlowchartPadding()
    {
        return flowchartPadding;
    }

    /**
     * Metoda vrací nastavenou hodnotu "mezi-symbolového" odsazení.<br />
     * Mezi-symbolové odsazení reprezentuje hodnotu minimálního odsazení mezi
     * symboly.
     *
     * @return nastavená hodnota minimálního mezi-symbolového odsazení
     */
    @Override
    public int getSymbolPadding()
    {
        return symbolPadding;
    }

    JComponent getCanvas()
    {
        return canvas;
    }

    /**
     * Metoda pro získání kolekce všech jointů diagramu.
     *
     * @return kolekce všech jointů diagramu
     */
    @Override
    public ArrayList<Joint> getlJoints()
    {
        return lJoints;
    }

    /**
     * Vrací stav editačního módu.
     *
     * @return stav editačního módu
     */
    @Override
    public boolean getEditMode()
    {
        return editMode;
    }

    /**
     * Metoda pro nastavení hodnoty odsazení diagramu.<br />
     * Odsazení diagramu je odsazení o nastavený počet pixelů na každém kraji
     * diagramu.
     *
     * @param flowchartPadding požadovaná hodnota odsazení diagramu
     */
    @Override
    public void setFlowchartPadding(int flowchartPadding)
    {
        AbstractLayout.flowchartPadding = flowchartPadding;
        prepareFlowchart();
    }

    /**
     * Metoda pro nastavení hodnoty "mezi-symbolového" odsazení..<br />
     * Mezi-symbolové odsazení reprezentuje hodnotu minimálního odsazení mezi
     * symboly.
     *
     * @param symbolPadding požadovaná hodnota minimálního mezi-symbolového
     * odsazení
     */
    @Override
    public void setSymbolPadding(int symbolPadding)
    {
        this.symbolPadding = symbolPadding;
        /*
         * for (LayoutSegment segment: getFlowchart()) {
         * if (segment != null) {
         * for (LayoutElement element: segment) {
         * if (element.getSymbol() instanceof GotoLabel) {
         * GotoLabel gotolabel = (GotoLabel)element.getSymbol();
         * gotolabel.setSymbolPaddingValue(symbolPadding);
         * }
         * }
         * }
         * }
         */
        prepareFlowchart();
    }

    void setlJoints(ArrayList<Joint> lJoints)
    {
        this.lJoints = lJoints;
    }

    private Joint findMeJoint(LayoutElement element)
    {
        return findMeJoint(element, false, false, true);
    }

    /**
     * Rekurzivni metoda, pozor na parametry!
     *
     * @param element element ke kteremu se pokousime hledat joint
     * @param reverse zda prochazet jointy viceJointovych symbolu v opacnem
     * poradi - aktivuje se automaticky, chybi-li alespon jednou joint
     * @param upDirection zdali hledat smerem nahoru ci nikoliv
     * @param firstDirection zdali jsme jiz hledali v opacnem smeru
     * @return nejblizsi joint vstupniho elementu
     */
    private Joint findMeJoint(LayoutElement element, boolean reverse, boolean upDirection,
            boolean firstDirection)
    {
        Joint j = null;
        for (Joint joint : lJoints) {
            if (joint.getParentElement() == element) {
                if (!reverse && (element.getInnerSegmentsCount() < 2 || element.getInnerSegment(0) == null || !element.getInnerSegment(
                        0).equals(joint.getParentSegment()))) {
                    return joint;
                }
                j = joint;
            }
        }
        if (j != null) {
            return j;
        }

        if (element.getParentSegment().size() > 1) { // jestli neni element jedinacek v segmentu
            int elementIndex = element.getParentSegment().indexOfElement(element);
            if (!upDirection) {
                if (elementIndex + 1 < element.getParentSegment().size()) { // jestli existuje element pod timto elementem
                    return findMeJoint(element.getParentSegment().getElement(elementIndex + 1), true,
                            upDirection, firstDirection);
                } else if (firstDirection && elementIndex - 1 >= 0) {
                    return findMeJoint(element.getParentSegment().getElement(elementIndex - 1), true,
                            !upDirection, !firstDirection); // zde bude dochazet k opakovanemu zkontrolovani elementu, ale pri poctech elementu prochazejici touto metodou to nevadi (1-2?)
                }
            } else {
                if (elementIndex - 1 >= 0) { // jestli existuje element nad timto elementem
                    return findMeJoint(element.getParentSegment().getElement(elementIndex - 1), true,
                            upDirection, firstDirection);
                } else if (firstDirection && elementIndex + 1 < element.getParentSegment().size()) {
                    return findMeJoint(element.getParentSegment().getElement(elementIndex + 1), true,
                            !upDirection, !firstDirection); // zde bude dochazet k opakovanemu zkontrolovani elementu, ale pri poctech elementu prochazejici touto metodou to nevadi (1-2?)
                }
            }
        }
        return findMeJoint(element.getParentSegment().getParentElement());
    }

    /**
     * Metoda nastaví vstupní element jako označený.<br />
     * Zároveň nastaví jako označený i korespondující joint.
     *
     * @param focusedElement element, který má být nastaven jako označený
     */
    @Override
    public void setFocusedElement(LayoutElement focusedElement)
    {
        this.focusedElement = focusedElement;
        focusedJoint = findMeJoint(focusedElement);
    }

    /**
     * Metoda pro získání aktuálně označeného (orámovaného) symbolu.<br />
     * Je-li označen joint, vrací null. Pro získání označeného elementu který
     * nemá focus, použijte metodu getFocusedJoint.
     *
     * @return aktuálně označený symbol
     */
    @Override
    public LayoutElement getFocusedElement()
    {
        return focusedElement;
    }

    /**
     * Metoda nastaví vstupní joint jako označený.<br />
     * Zároveň nastaví jako označený i korespondující element.
     *
     * @param focusedJoint joint, který má být nastaven jako označený
     */
    @Override
    public void setFocusedJoint(Joint focusedJoint)
    {
        if (lJoints.contains(focusedJoint)) {
            this.focusedJoint = focusedJoint;
        } else {
            // stary joint, vyhledam korespondujici
            for (Joint joint : lJoints) {
                if (joint.getParentElement().equals(focusedJoint.getParentElement()) && joint.getParentSegment().equals(
                        focusedJoint.getParentSegment())) {
                    this.focusedJoint = joint;
                    break;
                }
            }
        }
        focusedElement = null;
    }

    /**
     * Vrací aktuálně označený (orámovaný) joint.
     *
     * @return aktuálně označený joint
     */
    @Override
    public Joint getFocusedJoint()
    {
        if (focusedElement == null) {
            return focusedJoint;
        } else {
            return null;
        }
    }

    /**
     * Metoda pro nastavení editačního módu.
     *
     * @param editMode požadovaný stav editačního módu
     */
    @Override
    public void setEditMode(boolean editMode)
    {
        this.editMode = editMode;
    }

    /**
     * Metoda pro získání instance Stroke, používané k vykreslení cest za Goto
     * symbolem
     *
     * @return instance Stroke, používaná k vykreslení cest za Goto symbolem
     */
    @Override
    public BasicStroke getGotoStroke()
    {
        return gotoStroke;
    }

    /**
     * Metoda pro získání instance Stroke, používané k vykreslení komentářových
     * cest
     *
     * @return instance Stroke, používaná k vykreslení komentářových cest
     */
    @Override
    public BasicStroke getCommentStroke()
    {
        return commentStroke;
    }

}
