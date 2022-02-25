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
 * Tato třída představuje filtr konstanty.<br />
 * Konstanta může obsahovat pouze číselné nebo řetězcové údaje ohraničené
 * uvozovkami. Je možné vepsat i více konstant, v takovém případě je nutné je
 * oddělit čárkami.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ConstantFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.LIST_OF_CONSTANTS;

    public ConstantFilter(JTextField parentJTextField, ValidationListener validationListener,
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
