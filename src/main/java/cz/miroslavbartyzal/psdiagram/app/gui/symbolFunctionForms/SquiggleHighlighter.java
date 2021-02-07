/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class SquiggleHighlighter extends DefaultHighlighter.DefaultHighlightPainter
{

    private static final int SIZE = 2;
    private static final int MIN_BORDER = 3;
    private int p0;
    private int p1;

    public SquiggleHighlighter(Color color, int p0, int p1) throws BadLocationException
    {
        super(color);

        if (p0 < 0) {
            throw new BadLocationException("Invalid start offset", p0);
        }
        if (p1 < p0) {
            throw new BadLocationException("Invalid end offset", p1);
        }
        this.p0 = p0;
        this.p1 = p1;
    }

    public int getP0()
    {
        return p0;
    }

    public int getP1()
    {
        if (p1 == 0) {
            return 1; // make sure this highlighter is going to be picked for highlighting
        }
        return p1;
    }

    @Override
    public Shape paintLayer(Graphics g, int offs0, int offs1,
            Shape bounds, JTextComponent c, View view)
    {
        Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

        if (r != null) {
            Color color = getColor();
            if (color == null) {
                g.setColor(c.getSelectionColor());
            } else {
                g.setColor(color);
            }
            paintSquiggle(g, r);
        }

        return r;

    }

    private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view)
    {
        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            // Contained in view, can just use bounds.
            Rectangle alloc;
            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();
            }
            return alloc;
        } else {
            // Should only render part of View.
            try {
                // determine locations
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1,
                        Position.Bias.Backward, bounds);
                Rectangle r;
                if (shape instanceof Rectangle) {
                    r = (Rectangle) shape;
                } else {
                    r = shape.getBounds();
                }

                return r;
            } catch (BadLocationException e) {
                MyExceptionHandler.handle(e); // can't render
            }
        }

        return null; // Can't render
    }

    private void paintSquiggle(Graphics g, Rectangle r)
    {
        Rectangle clipRec = g.getClipBounds();
        if (clipRec.y > MIN_BORDER) {
            // there is some spare space below so let's take advantage of it and add 3 px if possible
            int hInc = 1;
            if (hInc > clipRec.y - MIN_BORDER) {
                hInc = clipRec.y - MIN_BORDER; // leave some pxs as border
            }
            r.height += hInc;
            g.setClip(clipRec.x, clipRec.y, clipRec.width, r.height);
        }
        if (p1 == 0) {
            // most left margin has to be marked
            r.width = 0; // set width to 0 so the following code can make a squiggle in the midd
            g.setClip(r.x - SIZE, clipRec.y, clipRec.width + SIZE, clipRec.height);
        }

        if (r.width < SIZE * 2) {
            // always paint at least one squiggle in the middle of the area
            r.x = r.x + r.width / 2 - SIZE;
            r.width = SIZE * 2 + 1;
        }

        int x = r.x;
        int y = r.y + r.height - SIZE;
        int delta = -SIZE;
        if ((r.width - 1) / SIZE % 2 == 1) {
            // odd number of squiggles -> it is nicer when squiggles end by squiggle downwards
            y -= SIZE;
            delta = SIZE;
        }

        while (x + SIZE < r.x + r.width) {
            g.drawLine(x, y, x + SIZE, y + delta);
            x += SIZE;
            y += delta;
            delta = -delta;
        }

        // set clipping back to the original values
        g.setClip(clipRec.x, clipRec.y, clipRec.width, clipRec.height);
    }

}
