/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class UnknownSyntaxJTextFieldBorder extends AbstractBorder
{

    private static final int ANIM_TIME = 1500;
    private static final int FADEIN_TIME = 1000;
    private static final int FPS = 30;

    private final int stepTime = 1000 / FPS;
    private final int stepsPerAnim = ANIM_TIME / stepTime;
    private final float fadeInInc = 1.0f / ((float) FADEIN_TIME / stepTime);
    private final double paddingHeightRatio = 0.25;
    private float currentTransparency = fadeInInc;
    private int currentStep = 0;
    private Timer nextPaintTimer = null;
    private boolean incStep = true;

    private AbstractBorder defaultBorder;

    public UnknownSyntaxJTextFieldBorder(AbstractBorder defaultBorder)
    {
        this.defaultBorder = defaultBorder;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        defaultBorder.paintBorder(c, g, x, y, width, height);
        paintAnimation(c, g, x, y, width, height);
    }

    public void paintAnimation(final Component c, Graphics g, int x, int y, int width, int height)
    {
        if (incStep) {
            incStep = false;
            currentStep++;
            if (currentStep > stepsPerAnim) {
                currentStep = 1;
            }
        }
        double currentProgress = (double) currentStep / stepsPerAnim;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (currentTransparency < 1) {
            g2d.setComposite(AlphaComposite.SrcOver.derive(currentTransparency));
            currentTransparency += fadeInInc;
        }

        double padding = paddingHeightRatio * height;
        double shapeSize = height - 2 * padding;
        double outterPadding = padding * 0.3;
        double shapeX = x + width - shapeSize - padding;
        double shapeY = y + padding;

        Rectangle2D.Double outterBounds = new Rectangle2D.Double(
                shapeX - outterPadding,
                shapeY - outterPadding,
                shapeSize + outterPadding * 2,
                shapeSize + outterPadding * 2);

        Ellipse2D.Double circleOutter = new Ellipse2D.Double(
                outterBounds.x,
                outterBounds.y,
                outterBounds.width,
                outterBounds.height);
        g2d.setColor(c.getBackground());
        g2d.fill(circleOutter);
        Ellipse2D.Double circle = new Ellipse2D.Double(
                shapeX,
                shapeY,
                shapeSize,
                shapeSize);
        g2d.setColor(Color.BLACK);
        g2d.draw(circle);
        Arc2D.Double arcMask = new Arc2D.Double(
                outterBounds,
                -360.0 * currentProgress,
                70,
                Arc2D.PIE);
        g2d.setColor(c.getBackground());
        g2d.fill(arcMask);

        if (nextPaintTimer == null) {
            nextPaintTimer = new Timer(stepTime, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    UnknownSyntaxJTextFieldBorder.this.incStep = true;
                    if (c.isShowing()) {
                        c.repaint();
                    }
                }
            });
            nextPaintTimer.setRepeats(false);
        }
        nextPaintTimer.start(); // schedule next paintBorder
    }

    public AbstractBorder getDefaultBorder()
    {
        return defaultBorder;
    }

    public void initFadeIn()
    {
        currentTransparency = fadeInInc;
    }

    public void setDefaultBorder(AbstractBorder defaultBorder, Component c, boolean fadeIn)
    {
        this.defaultBorder = defaultBorder;
        if (fadeIn) {
            currentTransparency = fadeInInc;
        }
        c.repaint();
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
