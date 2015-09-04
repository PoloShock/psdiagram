/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.managers;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartCommentSymbolManager
{

    private final Layout layout;
    private int procesCommentsPointIndex = -1; // uklada index relativniho bodu komentarove cesty, ktery se aktualne presouva
    private Ellipse2D commentPathConnector = null; // uklada relativni bod komentarove cesty, se kterym se aktualne manipuluje
    private int futureRelativePointIndex = -1;
    private Comment procesComment; // uklada komentar, se kterym se aktualne manipuluje
    private LayoutElement procesCommentElement;
    private Symbol pairedSymbol; // uklada parovy symbol komentare, se kterym se manipuluje
    private double cursorRelX; // slouzi k ulozeni relativni souradnice mysi vuci kommentu
    private double cursorRelY; // -||-
    private String commentAction;
    private boolean dragging = false;

    protected FlowchartCommentSymbolManager(Layout layout)
    {
        this.layout = layout;
    }

    protected void analyzeMouseToCommentPathAndConnector(Point2D p)
    {
        Comment boldPathComment = null;
        for (Comment comment : layout.getlCommentSymbols()) {
            // pri hledani commentPathConnector muzu rovnou i hledat potencionalni Path zvyrazneni.. v pripade nalezeni commentPathConnectoru uz je pak nebudu potrebovat, takze se mi ten return hodi
            double origAbsX = comment.getCenterX() - comment.getRelativeX();
            double origAbsY = comment.getCenterY() - comment.getRelativeY();
            Point2D prevPoint = new Point2D.Double(origAbsX, origAbsY);

            for (Point2D pnt : comment.getRelativeMiddlePointsToSymbol()) {
                Point2D point = new Point2D.Double(origAbsX + pnt.getX(), origAbsY + pnt.getY());
                if (point.distance(p.getX(), p.getY()) < 4) {
                    setCommentPathConnector(point.getX(), point.getY());
                    layout.setBoldPathComment(null);
                    //repaintJPanelDiagram();
                    return;
                }
                if (boldPathComment == null) {
                    if (shouldBeBold(comment, prevPoint, point, p)) {
                        boldPathComment = comment;
                        futureRelativePointIndex = comment.getRelativeMiddlePointsToSymbol().indexOf(
                                pnt);
                    } else {
                        prevPoint = point;
                    }
                }
            }
            if (boldPathComment == null) {
                if (shouldBeBold(comment, prevPoint, new Point2D.Double(comment.getCenterX(),
                        comment.getCenterY()), p)) {
                    boldPathComment = comment;
                    futureRelativePointIndex = comment.getRelativeMiddlePointsToSymbol().size();
                }
            }
        }
        if (commentPathConnector != null) { // .., vymazat commentPathConnector jen kdyz neni zobrazeno popup menu pro jeho smazani
            commentPathConnector = null;
        }
        layout.setBoldPathComment(boldPathComment);
        //repaintJPanelDiagram();
    }

    protected void resetVariables()
    {
        procesComment = null;
        procesCommentsPointIndex = -1;
        if (layout.getBoldPathComment() == null) {
            futureRelativePointIndex = -1;
        }
        procesCommentElement = null;
        pairedSymbol = null;
        cursorRelX = 0;
        cursorRelY = 0;
        dragging = false;
    }

    protected boolean wasMousePressedEventRelevantForConnector(Point2D mouseCoords)
    {
        if (commentPathConnector != null) {
            if (commentPathConnector.contains(mouseCoords)) {
                for (Comment comment : layout.getlCommentSymbols()) {
                    int i = 0;
                    for (Point2D point : comment.getRelativeMiddlePointsToSymbol()) {
                        if (comment.getCenterX() - comment.getRelativeX() + point.getX() == commentPathConnector.getCenterX() && comment.getCenterY() - comment.getRelativeY() + point.getY() == commentPathConnector.getCenterY()) {
                            procesComment = comment;
                            procesCommentsPointIndex = i;
                            procesCommentElement = layout.findMyElement(comment);
                            if (comment.hasPairSymbol()) {
                                pairedSymbol = layout.findMyPairedElement(procesCommentElement).getSymbol();
                            }
                            return true;
                        }
                        i++;
                    }
                }
            } else {
                commentPathConnector = null;
            }
        }
        return false;
    }

    protected boolean wereMouseReleasedCoordsInsideConnector(Point2D mouseCoords)
    {
        if (commentPathConnector != null) {
            if (!commentPathConnector.contains(mouseCoords)) {
                commentPathConnector = null;
            } else {
                return true;
            }
        }
        return false;
    }

    protected void mousePressedOnComment(LayoutElement commentElement, Point2D mouseCoords)
    {
        procesCommentElement = commentElement;
        procesComment = (Comment) commentElement.getSymbol();
        setCursorRelCoords(mouseCoords.getX(), mouseCoords.getY());
        if (procesCommentElement.getSymbol().hasPairSymbol()) {
            pairedSymbol = layout.findMyPairedElement(commentElement).getSymbol();
        }
    }

    protected void setCommentPathConnectorFromDraggedMouse(Point2D mouseCoords)
    {
        Comment comment = layout.getBoldPathComment();
        setCommentPathConnector(mouseCoords.getX(), mouseCoords.getY());
        procesComment = comment;
        comment.getRelativeMiddlePointsToSymbol().add(futureRelativePointIndex, null);
        procesCommentsPointIndex = futureRelativePointIndex;
        procesCommentElement = layout.findMyElement(comment);
        if (comment.hasPairSymbol()) {
            pairedSymbol = layout.findMyPairedElement(procesCommentElement).getSymbol();
        }
        layout.setBoldPathComment(null);
        futureRelativePointIndex = -1;
    }

    private void dragCommentPathConnector(Point2D mouseCoords)
    {
        Point2D point = new Point2D.Double(
                mouseCoords.getX() - procesComment.getCenterX() + procesComment.getRelativeX(),
                mouseCoords.getY() - procesComment.getCenterY() + procesComment.getRelativeY());
        procesComment.getRelativeMiddlePointsToSymbol().set(procesCommentsPointIndex, point);
        setCommentPathConnector(mouseCoords.getX(), mouseCoords.getY());
        procesCommentElement.setPathToNextSymbol(layout.getCommentPathFromRelative(procesComment,
                pairedSymbol)); // uprava cesty ke komentari
    }

    private void dragCommentElement(Point2D mouseCoords)
    {
        double relX = (mouseCoords.getX() - procesComment.getCenterX()) - cursorRelX;
        double relY = (mouseCoords.getY() - procesComment.getCenterY()) - cursorRelY;
        procesComment.setCenterX(procesComment.getCenterX() + relX);
        procesComment.setCenterY(procesComment.getCenterY() + relY);
        procesComment.setRelativeX(procesComment.getRelativeX() + relX);
        procesComment.setRelativeY(procesComment.getRelativeY() + relY);
        boolean toRightSite = procesComment.istoRightSite();
        procesCommentElement.setPathToNextSymbol(
                layout.getCommentPathFromRelative(procesComment, pairedSymbol)); // uprava cesty ke komentari
        if (toRightSite != procesComment.istoRightSite()) { // otocil-li se komentar, je treba aktualizovat CursorRelCoords
            setCursorRelCoords(mouseCoords.getX(), mouseCoords.getY());
        }
    }

    protected boolean isCommentPathConnectorDeletable()
    {
        return procesCommentsPointIndex >= 0; // kdyz je na commentPathConnector jen ukazano a je oznacen parovy symbol komentare, pusobilo by to zmatecne..
    }

    protected void deleteCommentPathConnector()
    {
        procesComment.getRelativeMiddlePointsToSymbol().remove(procesCommentsPointIndex);
        procesCommentElement.setPathToNextSymbol(layout.getCommentPathFromRelative(procesComment,
                pairedSymbol)); // uprava cesty ke komentari
        commentPathConnector = null;
        resetVariables();
    }

    protected boolean isCommentPathConnectorVisible()
    {
        return commentPathConnector != null;
    }

    protected boolean isCommentElementBeingProcessed()
    {
        return procesCommentElement != null;
    }

    protected Ellipse2D getCommentPathConnector()
    {
        return commentPathConnector;
    }

    private boolean shouldBeBold(Comment comment, Point2D p1, Point2D p2, Point2D p3)
    {
        return Line2D.ptSegDist(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY()) <= 3 && !comment.contains(
                p3) && (!comment.hasPairSymbol() || !layout.findMyPairedSymbol(comment).contains(p3));
    }

    private void setCommentPathConnector(double centerPointX, double centerPointY)
    {
        double width = 8;
        commentPathConnector = new Ellipse2D.Double(centerPointX - width / 2,
                centerPointY - width / 2, width, width);
    }

    private void setCursorRelCoords(double mouseX, double mouseY)
    {
        cursorRelX = mouseX - procesCommentElement.getSymbol().getCenterX();
        cursorRelY = mouseY - procesCommentElement.getSymbol().getCenterY();
    }

    protected boolean isAbleToDrag()
    {
        return isCommentElementBeingProcessed() || layout.getBoldPathComment() != null || isCommentPathConnectorVisible();
    }

    protected void performDrag(Point2D p)
    {
        if (layout.getBoldPathComment() != null) {
            commentAction = "Vytvoření bodu komentáře";
            dragging = true;

            setCommentPathConnectorFromDraggedMouse(p);
        }
        if (isCommentPathConnectorVisible()) {
            if (!dragging) {
                commentAction = "Přesunutí bodu komentáře";
                dragging = true;
            }

            dragCommentPathConnector(p);
        } else if (isCommentElementBeingProcessed()) {
            if (!dragging) {
                commentAction = "Přesunutí komentáře";
                dragging = true;
            }

            dragCommentElement(p);
        }
    }

    protected boolean isCommentOrJointBeingDragged()
    {
        return dragging;
    }

    public String getCommentAction()
    {
        return commentAction;
    }

    public void paint(Graphics2D grphcs2D)
    {
        if (commentPathConnector != null) {
            float dash[] = {4, 4};
            grphcs2D.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0));
            grphcs2D.setColor(new Color(230, 230, 230));
            grphcs2D.fill(commentPathConnector);
            grphcs2D.setColor(Color.BLACK);
            grphcs2D.draw(commentPathConnector);
        }
    }

}
