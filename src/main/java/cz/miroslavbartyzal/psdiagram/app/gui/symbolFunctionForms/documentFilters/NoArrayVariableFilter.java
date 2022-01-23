/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.EnumRule;
import javax.swing.JTextField;

/**
 * Tato třída představuje filtr proměnné, která nepředstavuje pole.<br />
 * Takováto proměnná by tedy neměla obsahovat jiné znaky, než písmenné a
 * číselné, případně znak podtržítka či dolaru.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class NoArrayVariableFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.NO_ARRAY_VARIABLE_TO_ASSIGN_TO;

    public NoArrayVariableFilter(JTextField parentJTextField, ValidationListener validationListener,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(parentJTextField, validationListener, maxBalloonSizeCallback);

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
     * Pokusí se daný textový řetězec porovnat vůči tomuto filtru.
     *
     * @param input text, který chceme prověřit
     * @return
     */
    public static boolean isValid(String input)
    {
        return AbstractFilter.parseInput(input, RULE);
    }

}
