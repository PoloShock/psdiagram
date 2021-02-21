/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * <p>
 * Tato třída představuje filtr proměnné.</p>
 * <p>
 * <p>
 * Proměnná může začínat pouze písmenem, podtržítkem, nebo znakem dolaru.
 * Další znaky již mohou obsahovat i číslice.<br />
 * Proměnná může představovat i pole, v tom případě bude obsahovat i hranaté
 * závorky, v nichž bude zapsána hodnota indexu pole.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class VariableFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.VARIABLE_TO_ASSIGN_TO;

    public VariableFilter(JTextField parentJTextField, ValidationListener validationListener,
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
