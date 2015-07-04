/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class BadSyntaxJTextFieldBorder extends AbstractBorder
{

    private final AbstractBorder defaultBorder;

    public BadSyntaxJTextFieldBorder(AbstractBorder defaultBorder)
    {
        this.defaultBorder = defaultBorder;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        defaultBorder.paintBorder(c, g, x, y, width, height);
        g.setColor(new Color(255, 51, 51));
        g.drawRect(x, y, width - 1, height - 1); // the lines are drawn below the actual coordenates
    }

    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component c)
    {
        return defaultBorder.getBaselineResizeBehavior(c);
    }

    @Override
    public int getBaseline(Component c, int width, int height)
    {
        return defaultBorder.getBaseline(c, width, height);
    }

    @Override
    public Rectangle getInteriorRectangle(Component c, int x, int y, int width, int height)
    {
        return defaultBorder.getInteriorRectangle(c, x, y, width, height);
    }

    @Override
    public boolean isBorderOpaque()
    {
        return defaultBorder.isBorderOpaque();
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        return defaultBorder.getBorderInsets(c, insets);
    }

    @Override
    public Insets getBorderInsets(Component c)
    {
        return defaultBorder.getBorderInsets(c);
    }

}
