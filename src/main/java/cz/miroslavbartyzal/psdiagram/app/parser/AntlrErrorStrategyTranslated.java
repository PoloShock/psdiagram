package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.List;

/**
 * This class does only translation of ANTLR error messages.
 * The rest is the same. Needs to be checked on every ANTLR update!
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class AntlrErrorStrategyTranslated extends DefaultErrorStrategy
{

    private final boolean includeMessageForNoViableAlternative;
    
    public AntlrErrorStrategyTranslated()
    {
        this(true);
    }
    
    public AntlrErrorStrategyTranslated(boolean includeMessageForNoViableAlternative)
    {
        this.includeMessageForNoViableAlternative = includeMessageForNoViableAlternative;
    }
    
    @Override
    public void reportError(Parser recognizer, RecognitionException e)
    {
        // if we've already reported an error and have not matched a token
        // yet successfully, don't report any errors.
        if (inErrorRecoveryMode(recognizer)) {
//			System.err.print("[SPURIOUS] ");
            return; // don't report spurious errors
        }
        super.beginErrorCondition(recognizer);
        if (e instanceof NoViableAltException) {
            reportNoViableAlternative(recognizer, (NoViableAltException) e);
        } else if (e instanceof InputMismatchException) {
            reportInputMismatch(recognizer, (InputMismatchException) e);
        } else if (e instanceof FailedPredicateException) {
            reportFailedPredicate(recognizer, (FailedPredicateException) e);
        } else {
            System.err.println("unknown recognition error type: " + e.getClass().getName());
            recognizer.notifyErrorListeners(e.getOffendingToken(), "Nalezena syntaktická chyba.",
                    e);
        }
    }

    @Override
    protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e)
    {
        if (!includeMessageForNoViableAlternative) {
            recognizer.notifyErrorListeners(e.getOffendingToken(), null, e);
            return;
        }
        
        String msg = "Nalezena syntaktická chyba.";

        TokenStream tokens = recognizer.getInputStream();
        if (tokens != null) {
            if (e.getStartToken().getType() == Token.EOF) {
                msg = "Nalezena syntaktická chyba v části na konci řádku.";
            } else {
                msg = "Nalezena syntaktická chyba v části " + escapeWSAndQuote(
                        tokens.getText(e.getStartToken(), e.getOffendingToken())) + ".";
            }
        }

        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    @Override
    protected void reportInputMismatch(Parser recognizer, InputMismatchException e)
    {
        String expecting = filterExpectedTokens(e.getExpectedTokens(), recognizer);
//        String expecting = e.getExpectedTokens().toString(recognizer.getVocabulary());

        String msg = "Část příkazu " + getTokenErrorDisplay(e.getOffendingToken())
                + " na tomto místě nebyla očekávána.";
        if (expecting != null) {
            msg += "\nOčekáváné možnosti: " + expecting + ".";
        }

        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }

    @Override
    protected void reportUnwantedToken(Parser recognizer)
    {
        if (super.inErrorRecoveryMode(recognizer)) {
            return;
        }
        super.beginErrorCondition(recognizer);

        Token t = recognizer.getCurrentToken();
        String tokenName = getTokenErrorDisplay(t);
        String expecting = filterExpectedTokens(super.getExpectedTokens(recognizer), recognizer);
//        String expecting = getExpectedTokens(recognizer).toString(recognizer.getVocabulary());
        String msg = "Část příkazu " + tokenName + " se zdá být na tomto místě nadbytečná.";
        if (expecting != null) {
            msg += "\nBylo očekáváno: " + expecting + ".";
        }
        recognizer.notifyErrorListeners(t, msg, null);
    }

    @Override
    protected void reportMissingToken(Parser recognizer)
    {
        if (inErrorRecoveryMode(recognizer)) {
            return;
        }

        super.beginErrorCondition(recognizer);

        Token t = recognizer.getCurrentToken();
        String expecting = filterExpectedTokens(super.getExpectedTokens(recognizer), recognizer);
//        String expecting = getExpectedTokens(recognizer).toString(recognizer.getVocabulary());
        String msg;
        if (expecting != null) {
            msg = "Postrádám " + expecting
                    + " v části příkazu " + getTokenErrorDisplay(t) + ".";
        } else {
            msg = "Syntaktická chyba v části příkazu " + getTokenErrorDisplay(t) + ".";
        }

        recognizer.notifyErrorListeners(t, msg, null);
    }

    @Override
    protected String getTokenErrorDisplay(Token t)
    {
        if (t == null) {
            return "<null>";
        }
        if (t.getType() == Token.EOF) {
            return "<konec řádku>";
        }
        if (t.getType() == Token.EPSILON) {
            return "<prázdný řetěz>";
        }
        
        String s = getSymbolText(t);
        if (s == null) {
            s = "<" + getSymbolType(t) + ">";
        }
        return escapeWSAndQuote(s);
    }

    @Override
    protected String escapeWSAndQuote(String s)
    {
        s = s.replace("\n", "<konec řádku>");
        s = s.replace("\r", "<nový řádek>");
        s = s.replace("\t", "<tabulátor>");
        return "'" + s + "'";
    }

    private String filterExpectedTokens(IntervalSet tokens, Parser recognizer)
    {
//        if (!(recognizer instanceof PSDGrammarParser)) {
//            return tokens.toString(recognizer.getVocabulary());
//        }

        Vocabulary vocabulary = recognizer.getVocabulary();
        List<Integer> tokenInts = tokens.toList();
        String out = "{";
        for (Integer tokenInt : tokenInts) {
////            if (tokenInt != PSDGrammarParser.INC && tokenInt != PSDGrammarParser.DEC) {
            String tokenName;
            if (tokenInt == Token.EOF) {
                tokenName = "<konec řádku>";
            } else if (tokenInt == Token.EPSILON) {
                tokenName = "<prázdný řetěz>";
            } else {
                tokenName = vocabulary.getDisplayName(tokenInt);
            }

            if (tokenName != null) {
                out += tokenName + ", ";
            } //else if (tokenInt == PSDGrammarParser.CONSTANT_INTEGER) {
//                out += "celé číslo, ";
//            } else if (tokenInt == PSDGrammarParser.CONSTANT_FLOATING_POINT) {
//                out += "desetinné číslo, ";
//            } else if (tokenInt == PSDGrammarParser.CONSTANT_BOOLEAN) {
//                out += "true, false, ";
//            } else if (tokenInt == PSDGrammarParser.CONSTANT_STRING) {
//                out += "řetězcová hodnota, ";
//            } else if (tokenInt == PSDGrammarParser.INDENTIFIER) {
//                out += "proměnná(identifikátor), ";
//            }
////            }
        }
        if (out.length() > 1) {
            // some tokens are present
            out = out.substring(0, out.length() - 2) + "}";
            return out;
        } else {
            return null;
        }
    }

}
