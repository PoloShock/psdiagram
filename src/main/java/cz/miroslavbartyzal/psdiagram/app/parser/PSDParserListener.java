/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface PSDParserListener
{

    public void onValidationComplete(boolean isValid);

    public void onRecoveryFinished(PSDParseResult parseResult);

}
