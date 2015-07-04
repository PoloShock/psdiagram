/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.antlr;

import cz.miroslavbartyzal.psdiagram.app.parser.PSDGrammarLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDGrammarLexerTranslated extends PSDGrammarLexer
{

    public PSDGrammarLexerTranslated(CharStream input)
    {
        super(input);
    }

    @Override
    public String getErrorDisplay(int c)
    {
        String s = String.valueOf((char) c);
        switch (c) {
            case Token.EOF:
                s = "<konec řádku>";
                break;
            case '\n':
                s = "<nový řádek>";
                break;
            case '\t':
                s = "<tabulátor>";
                break;
            case '\r':
                s = "<\\r>";
                break;
        }
        return s;
    }

}
