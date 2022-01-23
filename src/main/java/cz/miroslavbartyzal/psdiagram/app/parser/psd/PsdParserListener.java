package cz.miroslavbartyzal.psdiagram.app.parser.psd;

import cz.miroslavbartyzal.psdiagram.app.parser.ParseResult;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface PsdParserListener
{
    
    void onValidationComplete(boolean isValid);
    
    void onRecoveryFinished(ParseResult parseResult);
    
}
