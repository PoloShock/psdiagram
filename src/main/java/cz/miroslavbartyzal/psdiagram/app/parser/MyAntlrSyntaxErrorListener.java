package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.util.BitSet;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public abstract class MyAntlrSyntaxErrorListener extends DiagnosticErrorListener
{
    private boolean logWarnings = true;
    
    protected MyAntlrSyntaxErrorListener()
    {
    }
    
    protected MyAntlrSyntaxErrorListener(boolean logWarnings)
    {
        this.logWarnings = logWarnings;
    }
    
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e)
    {
        if (e instanceof FailedPredicateException) {
            if (!SettingsHolder.IS_DEPLOYMENT_MODE) {
                System.err.println("Recieved FailedPredicateException.");
            }
            return;
        } else if (e instanceof LexerNoViableAltException && recognizer instanceof Lexer) {
            Lexer l = (Lexer) recognizer;
            String invalidChar = l.getErrorDisplay(l._input.getText(Interval.of(
                    l._tokenStartCharIndex, l._input.index())));
            if (invalidChar.length() == 1 || invalidChar.matches("<[^>]*>")) {
                msg = "Znak '" + invalidChar + "' není na této pozici podporován.";
            } else {
                msg = "Znaky '" + invalidChar + "' nejsou na této pozici podporovány.";
            }
            onSyntaxError(msg, charPositionInLine, charPositionInLine + invalidChar.length());
            return;
        }

        if (offendingSymbol != null && offendingSymbol instanceof Token) {
            Token offendingToken = (Token) offendingSymbol;
            onSyntaxError(msg, offendingToken.getStartIndex(), offendingToken.getStopIndex() + 1);
        } else {
            onSyntaxError(msg, charPositionInLine, charPositionInLine + 1);
        }
    }

    public abstract void onSyntaxError(String errorMessage, int beginIndex, int endIndex);

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
            boolean exact, BitSet ambigAlts, ATNConfigSet configs)
    {
        if (!logWarnings || SettingsHolder.IS_DEPLOYMENT_MODE) {
            return;
        }

        String format = "reportAmbiguity d=%s: ambigAlts=%s, ambigInput='%s', input='%s'";
        String decision = getDecisionDescription(recognizer, dfa);
        BitSet conflictingAlts = getConflictingAlts(ambigAlts, configs);
        String ambigText = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        String text = recognizer.getTokenStream().getText();
        String message = String.format(format, decision, conflictingAlts, ambigText, text);
        System.err.println();
        System.err.println(message);
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex,
            int stopIndex, BitSet conflictingAlts, ATNConfigSet configs)
    {
        if (!logWarnings || SettingsHolder.IS_DEPLOYMENT_MODE) {
            return;
        }

        String format = "reportAttemptingFullContext d=%s, localInput='%s', input='%s'";
        String decision = getDecisionDescription(recognizer, dfa);
        String localText = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        String text = recognizer.getTokenStream().getText();
        String message = String.format(format, decision, localText, text);
        System.err.println();
        System.err.println(message);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
            int prediction, ATNConfigSet configs)
    {
        if (!logWarnings || SettingsHolder.IS_DEPLOYMENT_MODE) {
            return;
        }

        String format = "reportContextSensitivity d=%s, localInput='%s', input='%s'";
        String decision = getDecisionDescription(recognizer, dfa);
        String localText = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
        String text = recognizer.getTokenStream().getText();
        String message = String.format(format, decision, localText, text);
        System.err.println();
        System.err.println(message);
    }

}
