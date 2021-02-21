/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.dialog;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Tato třída vytvoří dialog obdobně jako JOptionDialog s tím rozdílem,
 * že vykreslení dialogu bude v pořádku i při UI scale faktoru vetší než jedna (4K rozlišení).
 * <p>
 * Testování probíhalo tak, že se aplikace spustila pod fullHD rozlišením a scale faktoru = 1.
 * Následně bylo rozlišení přepnuto do 4K spolu se změnou faktoru na hodnotu 2. Originální volání
 * JOptionDialog.showConfirmDialog(...) pak vykreslilo dialog useknutý tak, že byla vidět jen nepatrná část tlačítek.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MyJOptionPane
{

    private MyJOptionPane()
    {
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType,
            int messageType)
    {
        JOptionPane pane = new JOptionPane(message, messageType, optionType);

        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        return (int) pane.getValue();
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType)
    {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);

        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();
    }

    public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType,
            Icon icon, Object[] selectionValues, Object initialSelectionValue)
    {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, icon, null, null);
        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);

        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();
        if (value == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType,
            int messageType, Icon icon, Object[] options, Object initialValue)
    {
        JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);

        JDialog dialog = pane.createDialog(parentComponent, title);
        pane.selectInitialValue();
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (options == null) {
            if (selectedValue instanceof Integer) {
                return ((Number) selectedValue).intValue();
            }
            return JOptionPane.CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

}
