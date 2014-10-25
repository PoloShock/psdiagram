/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class WatermarkJTextField extends JTextField
{

    private final WatermarkFocusListener focusListener = new WatermarkFocusListener();
    private final WatermarkDocumentListener documentListener = new WatermarkDocumentListener();
    private String watermarkText = null;
    private boolean watermarkVisible = false;
    private boolean hideOnFocus = false;

    public WatermarkJTextField(String watermarkText)
    {
        this.watermarkText = watermarkText;
        super.addFocusListener(focusListener);
        super.getDocument().addDocumentListener(documentListener);
        updateWatermarkVisibility();
    }

    public WatermarkJTextField(String text, String watermarkText)
    {
        super(text);
        this.watermarkText = watermarkText;
        super.addFocusListener(focusListener);
        super.getDocument().addDocumentListener(documentListener);
        updateWatermarkVisibility();
    }

    public WatermarkJTextField(int columns, String watermarkText)
    {
        super(columns);
        this.watermarkText = watermarkText;
        super.addFocusListener(focusListener);
        super.getDocument().addDocumentListener(documentListener);
        updateWatermarkVisibility();
    }

    public WatermarkJTextField(String text, int columns, String watermarkText)
    {
        super(text, columns);
        this.watermarkText = watermarkText;
        super.addFocusListener(focusListener);
        super.getDocument().addDocumentListener(documentListener);
        updateWatermarkVisibility();
    }

    public WatermarkJTextField(Document doc, String text, int columns, String watermarkText)
    {
        super(doc, text, columns);
        this.watermarkText = watermarkText;
        super.addFocusListener(focusListener);
        super.getDocument().addDocumentListener(documentListener);
        updateWatermarkVisibility();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (watermarkVisible) {
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(super.getFont());
                g2d.setColor(new Color(195, 195, 195));
                Insets insets = super.getInsets();
                FontMetrics metrics = g2d.getFontMetrics();
                g2d.drawString(watermarkText,
                        insets.left + metrics.getMaxAdvance() * 0.30f,
                        insets.top + metrics.getAscent() + 1); // Ascent because drawString() draws at baseline; 1 because of nimbus' l&f shadow (looks more centered)
//                g2d.drawString(watermarkText, insets.left + metrics.getMaxAdvance() * 0.30f,
//                        super.getHeight() / 2 + g2d.getFontMetrics().getAscent() / 2); // Ascent because drawString() draws at baseline
            } else {
                throw new Error("Parameter Graphics g is not instance of Graphics2D!");
            }
        }
    }

    public String getWatermarkText()
    {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText)
    {
        this.watermarkText = watermarkText;
        updateWatermarkVisibility();
    }

    public boolean isHideOnFocus()
    {
        return hideOnFocus;
    }

    public void setHideOnFocus(boolean hideOnFocus)
    {
        this.hideOnFocus = hideOnFocus;
        updateWatermarkVisibility();
    }

    public boolean isWatermarkVisible()
    {
        return watermarkVisible;
    }

    private boolean isTextFieldEmpty()
    {
        String text = "";
        try {
            text = WatermarkJTextField.this.getDocument().getText(0,
                    WatermarkJTextField.this.getDocument().getLength());
        } catch (BadLocationException ex) {
        }

        return text.isEmpty();
    }

    private void updateWatermarkVisibility()
    {
        boolean prevWatermarkVisible = watermarkVisible;
        watermarkVisible = isTextFieldEmpty() && (!hideOnFocus || !super.isFocusOwner());
        if (prevWatermarkVisible != watermarkVisible) {
            super.repaint();
        }
    }

    private class WatermarkDocumentListener implements DocumentListener
    {

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            updateWatermarkVisibility();
        }

        @Override
        public void removeUpdate(DocumentEvent e)
        {
            updateWatermarkVisibility();
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            updateWatermarkVisibility();
        }

    }

    private class WatermarkFocusListener implements FocusListener
    {

        @Override
        public void focusGained(FocusEvent e)
        {
            updateWatermarkVisibility();
        }

        @Override
        public void focusLost(FocusEvent e)
        {
            updateWatermarkVisibility();
        }

    }

}
