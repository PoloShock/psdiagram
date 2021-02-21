/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.dialog;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.UNINITIALIZED_VALUE;
import static javax.swing.JOptionPane.getRootFrame;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class EnhancedJOptionPane extends JOptionPane
{

    public static String showInputDialog(Component parentComponent, Object message, String title,
            int messageType, Object[] options, Object initialValue)
    {
        if (options.length != 2) {
            throw new IllegalArgumentException(
                    "options parameter have to consist exacly 2 elements");
        }

        JOptionPane pane = new JOptionPane(message, messageType, OK_CANCEL_OPTION, null, options,
                initialValue);

        pane.setWantsInput(true);
        pane.setComponentOrientation(
                ((parentComponent == null) ? getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);

        pane.selectInitialValue();
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();
        Object selectedValue = pane.getValue();

        if (value == UNINITIALIZED_VALUE || selectedValue == null || selectedValue.equals(
                options[1])) {
            return null;
        }
        return (String) value;
    }

}
