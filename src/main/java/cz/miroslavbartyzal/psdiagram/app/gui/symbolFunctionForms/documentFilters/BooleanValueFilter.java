/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * <p>
 * Tato třída představuje filtr obecné hodnoty.</p>
 *
 * <p>
 * Může se jednat o hodnotu dosazovanou do proměnné, či o hodnotu použitou
 * jako vyhodnocovací podmíněný výraz. Tento filtr je v porovnání s ostatními
 * nejkomplexnější.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class BooleanValueFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.BOOLEAN_EXPRESSION;

    public BooleanValueFilter(JTextField parentJTextField, ValidationListener validationListener)
    {
        super(parentJTextField, validationListener);

        if (!parentJTextField.getText().isEmpty()) {
            super.parseInputAndUpdateGUI(parentJTextField.getText());
        }
    }

    @Override
    EnumRule getRule()
    {
        return RULE;
    }

    /**
     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
     *
     * @param input text, který chceme prověřit
     * @return
     */
    public static boolean isValid(String input)
    {
        return AbstractFilter.parseInput(input, RULE);
    }

}
