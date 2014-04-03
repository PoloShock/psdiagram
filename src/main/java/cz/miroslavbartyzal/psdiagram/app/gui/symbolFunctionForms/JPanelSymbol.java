/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

/**
 * Tato třída představuje fromulář s grafickou reprezentací symbolu, jehož
 * funkce má být editována. Zároveň poskytuje možnost přidání informativního
 * textu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class JPanelSymbol extends javax.swing.JPanel
{

    private Symbol symbol;
    private int minWidth;
    private int minHeight;
    private int symbolBottomPadding = 10;
    public JLabel jLabelDescription;

    protected JPanelSymbol(Symbol symbol, JLabel jLabelDescription)
    {
        this.symbol = symbol;
        this.jLabelDescription = jLabelDescription;

        super.setBorder(javax.swing.BorderFactory.createTitledBorder("Symbol"));
        super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        Insets ins = super.getInsets();

        minWidth = (int) symbol.getWidth() + ins.left + ins.right;
        minHeight = (int) symbol.getHeight() + symbolBottomPadding;
        super.add(Box.createRigidArea(new Dimension(0, minHeight)));
        if (jLabelDescription != null) {
            jLabelDescription.setSize(jLabelDescription.getPreferredSize());
            jLabelDescription.addComponentListener(new ComponentAdapter()
            {
                @Override
                public void componentResized(ComponentEvent ce)
                {
                    revalidMinHeight();
                }
            });
            minHeight += jLabelDescription.getHeight() + ins.top + ins.bottom;
            super.add(jLabelDescription);
        }
        Dimension dim = new Dimension(minWidth, minHeight);
        super.setMinimumSize(dim);
        super.setPreferredSize(dim);
    }

    private void revalidMinHeight()
    {
        minHeight = super.getLayout().minimumLayoutSize(this).height;
        super.setMinimumSize(new Dimension(minWidth, minHeight));
        super.revalidate();
    }

    private void initSymbol()
    {
        Insets ins = super.getInsets();
        Dimension dm = super.getSize();
        //symbol.setCenterX((dm.width - ins.left - ins.right)/2 + ins.left);
        symbol.setCenterX(dm.width / 2);
        //symbol.setCenterY((dm.height - ins.top - ins.bottom)/2 + ins.top);
        symbol.setCenterY(ins.top + symbol.getHeight() / 2);
    }

    /**
     * Volá rodičovskou metodu setBounds(x,y,w,h) a poté zinicializuje grafickou
     * reprezentaci symbolu tak, aby byla umístěna na správném místě.
     */
    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
        initSymbol();
    }

    /**
     * Volá rodičovskou metodu setBounds(rctngl) a poté zinicializuje grafickou
     * reprezentaci symbolu tak, aby byla umístěna na správném místě.
     */
    @Override
    public void setBounds(Rectangle rctngl)
    {
        this.setBounds(rctngl.x, rctngl.y, rctngl.width, rctngl.height);
    }

    /**
     * Volá rodičovskou metodu setMinimumSize(dmnsn) s údaji vypočtenými pro
     * grafickou reprezentaci symbolu a jeho informativního textu. Parametr této
     * metody nehraje žádnou roli.
     *
     * @param dmnsn nemá vliv na vykonání metody
     */
    @Override
    public void setMinimumSize(Dimension dmnsn)
    { // je treba zajistit, aby komponent nebyl mensi nez minimalni hodnoty
        super.setMinimumSize(new Dimension(minWidth, minHeight));
    }

    /**
     * Je volána rodičovská metoda setPreferredSize(dmnsn), předtím je ale
     * zkontrolováno, zda dimenze specifikované parametrem metody nejsou menší,
     * než minimální. V tomto případě jsou automaticky upraveny tak, aby se
     * rovnali s minimálními. Poté metoda zinicializuje grafickou reprezentaci
     * symbolu tak, aby byla umístěna na správném místě.
     */
    @Override
    public void setPreferredSize(Dimension dmnsn)
    { // je treba zajistit, aby se vzdy inicializoval symbol a preferedsize nebyl mensi nez minimalni hodnoty
        if (dmnsn.width < minWidth) {
            dmnsn.width = minWidth;
        }
        if (dmnsn.height < minHeight) {
            dmnsn.height = minHeight;
        }
        super.setPreferredSize(dmnsn);
        initSymbol();
    }

    /**
     * Metoda s prázdným tělem.
     */
    @Override
    public void setLayout(LayoutManager lm)
    {
    }

    @Override
    protected void paintComponent(Graphics grphcs)
    {
        super.paintComponent(grphcs);
        if (grphcs instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) grphcs;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            AffineTransform af = g2d.getTransform();
            Color shapeUpColor = symbol.getShapeUpColor();
            Color shapeDownColor = symbol.getShapeDownColor();
            Color borderColor = symbol.getBorderColor();
            Color ShadowColor = new Color(0, 0, 0, 100);
            if (!super.isEnabled()) {
                shapeUpColor = null;
                borderColor = new Color(100, 100, 100);
                ShadowColor = new Color(0, 0, 0, 50);
            }

            if (shapeUpColor != null) {
                if (symbol.hasShadow()) {
                    // následuje vykreslení stínu
                    g2d.setColor(ShadowColor);
                    g2d.translate(2, 2);
                    g2d.fill(symbol.getShape());
                    g2d.setTransform(af);
                }

                // inicializace barev (gradient ci nikoliv)
                if (symbol.getShapeUpColor().equals(symbol.getShapeDownColor())) {
                    g2d.setColor(shapeUpColor);
                } else {
                    g2d.setPaint(new GradientPaint(
                            (float) (symbol.getX() + symbol.getWidth() * 0.25),
                            (float) symbol.getY(), shapeUpColor,
                            (float) (symbol.getX() + symbol.getWidth() * 0.75),
                            (float) (symbol.getY() + symbol.getHeight()), shapeDownColor));
                }
                // vykreslení symbolu
                g2d.fill(symbol.getShape());
            } else if (symbol.hasShadow()) {
                // následuje vykreslení stínu transparentního symbolu
                g2d.setColor(ShadowColor);
                g2d.translate(1, 1);
                g2d.draw(symbol.getShape());
                g2d.setTransform(af);
            }
            // vykreslení okraje symbolu
            g2d.setColor(borderColor);
            g2d.draw(symbol.getShape());
        } else {
            throw new Error("Parameter Graphics g is not instance of Graphics2D!");
        }
    }

}
