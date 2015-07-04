/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.parboiled;

import org.parboiled.support.ParsingResult;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 * @param <V>
 */
public interface ParboiledRecoveryListener<V>
{

    public void onValidationComplete(boolean isValid);

    public void onRecoverySuccess(ParsingResult<V> result, String input);

    public void onRecoveryException(ParsingResult<V> result, String input);

}
