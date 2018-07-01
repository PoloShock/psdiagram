/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.antlr;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDGrammarParser;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParseResult;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParser;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParserListener;
import javax.swing.SwingUtilities;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ANTLRParser implements PSDParser
{

    private Thread recoveryParseThread = null;

    public interface RuleCallback
    {

        public void ruleCall(PSDGrammarParser parser);

    }

    private class PSDGrammarBailLexer extends PSDGrammarLexerTranslated
    {

        public PSDGrammarBailLexer(CharStream input)
        {
            super(input);
        }

        @Override
        public void recover(RecognitionException re)
        {
            throw new RuntimeException(re); // Bail out
        }

        @Override
        public void recover(LexerNoViableAltException e)
        {
            throw new RuntimeException(e); // Bail out
        }

    }

    @Override
    public void stopParsing()
    {
        if (recoveryParseThread != null && recoveryParseThread.isAlive()) {
////            recoveryParseThread.stop();
            recoveryParseThread.interrupt();
        }
//        long time = System.currentTimeMillis();
//        while (recoveryParseThread.isAlive()) {
//        }
//        System.out.println("thread died after: " + (System.currentTimeMillis() - time));
    }

    private PSDGrammarParser createBailOutParser(String input, boolean buildParseTree)
    {
        PSDGrammarBailLexer lexer = new PSDGrammarBailLexer(new ANTLRInputStream(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ANTLRMySyntaxErrorListener()
        {
            @Override
            public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
            {
                // syntaxErrors will end up here, the ambiguity and others of that minor kind will be reported (by nonoverriden methods)
                throw new RuntimeException(); // Bail out
            }
        });

        PSDGrammarParser parser = new PSDGrammarParser(new CommonTokenStream(lexer));
        parser.setBuildParseTree(buildParseTree);
        parser.removeErrorListeners();
        parser.addErrorListener(new ANTLRMySyntaxErrorListener()
        {
            @Override
            public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
            {
                // syntaxErrors will end up here, the ambiguity and others of that minor kind will be reported (by nonoverriden methods)
                throw new RuntimeException(); // Bail out
            }
        });

        parser.setErrorHandler(new BailErrorStrategy());

        return parser;
    }

    private boolean parse(String input, RuleCallback ruleCallback)
    {
        try {
            ruleCallback.ruleCall(createBailOutParser(input, false));
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private void parseReportErrors(String input, RuleCallback ruleCallback,
            PSDParserListener listener)
    {
        stopParsing();
        recoveryParseThread = new RecoveryParseThread(input, ruleCallback, listener);
        recoveryParseThread.start();
    }

    @Override
    public String translatePSDToJavaScript(String input)
    {
        try {
            PSDGrammarParser parser = createBailOutParser(input, true);
            PSDToJavaScriptVisitor visitor = new PSDToJavaScriptVisitor(input);
            return visitor.visit(parser.solo_Expression());
        } catch (RuntimeException ex) {
            return input;
        }
    }

    @Override
    public boolean parseExpression(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_Expression();
            }
        });
    }

    @Override
    public boolean parseBooleanExpression(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_BooleanExpression();
            }
        });
    }

    @Override
    public boolean parseListOfConstants(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_ListOf_Constants();
            }
        });
    }

    @Override
    public boolean parseListOfNumericConstants(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_ListOf_NumberConstants();
            }
        });
    }

    @Override
    public boolean parseNumericExpression(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_NumericExpression();
            }
        });
    }

    @Override
    public boolean parseStringExpression(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_StringExpression();
            }
        });
    }

    @Override
    public boolean parseNoArrayVariableToAssignTo(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_NoArrayVariableToAssignTo();
            }
        });
    }

    @Override
    public boolean parseVariableToAssignTo(String input)
    {
        return parse(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_VariableToAssignTo();
            }
        });
    }

    @Override
    public void parseExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_Expression();
            }
        }, listener);
    }

    @Override
    public void parseBooleanExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_BooleanExpression();
            }
        }, listener);
    }

    @Override
    public void parseListOfConstantsReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_ListOf_Constants();
            }
        }, listener);
    }

    @Override
    public void parseListOfNumericConstantsExpressionReportErrors(String input,
            PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_ListOf_NumberConstants();
            }
        }, listener);
    }

    @Override
    public void parseNumericExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_NumericExpression();
            }
        }, listener);
    }

    @Override
    public void parseStringExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_StringExpression();
            }
        }, listener);
    }

    @Override
    public void parseNoArrayVariableToAssignToReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_NoArrayVariableToAssignTo();
            }
        }, listener);
    }

    @Override
    public void parseVariableToAssignToReportErrors(String input, PSDParserListener listener)
    {
        parseReportErrors(input, new RuleCallback()
        {
            @Override
            public void ruleCall(PSDGrammarParser parser)
            {
                parser.solo_VariableToAssignTo();
            }
        }, listener);
    }

    private static class RecoveryParseThread extends Thread
    {

        private final String input;
        private final RuleCallback ruleCallback;
        private final PSDParserListener listener;
        private boolean interrupted = false;

        public RecoveryParseThread(String input, RuleCallback ruleCallback,
                PSDParserListener listener)
        {
            this.input = input;
            this.ruleCallback = ruleCallback;
            this.listener = listener;
        }

        private PSDGrammarParser createRecoveryParser(String input,
                final PSDParseResult parseResultToSaveTo)
        {
            PSDGrammarLexerTranslated lexer = new PSDGrammarLexerTranslated(new ANTLRInputStream(
                    input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ANTLRMySyntaxErrorListener()
            {
                @Override
                public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
                {
                    parseResultToSaveTo.addErrorInfo(errorMessage, beginIndex, endIndex);
                }
            });

            PSDGrammarParser parser = new PSDGrammarParser(new CommonTokenStream(lexer));
            parser.setBuildParseTree(false);
            parser.removeErrorListeners();
            parser.addErrorListener(new ANTLRMySyntaxErrorListener()
            {
                @Override
                public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
                {
                    parseResultToSaveTo.addErrorInfo(errorMessage, beginIndex, endIndex);
                }
            });

            parser.setErrorHandler(new ANTLRErrorStrategyTranslated());

            if (!SettingsHolder.IS_DEPLOYMENT_MODE) {
                parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION); // for debugging -> detect every ambiguosity
            }

            return parser;
        }

        @Override
        public void run()
        {
            runSingleStageParse(); // I did not notice any significant performance gains against two-stage parse so I'm leaving it as the single-stage parse
        }

        private void runSingleStageParse()
        {
            final PSDParseResult parseResult = new PSDParseResult(input);
            ruleCallback.ruleCall(createRecoveryParser(input, parseResult));
            if (!interrupted) {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (parseResult.isInputValid()) {
                            listener.onValidationComplete(true);
                        } else {
                            listener.onRecoveryFinished(parseResult);
                        }
                    }
                });
            }
        }

        private void runTwoStageParse()
        {
            final PSDParseResult parseResult = new PSDParseResult(input);
            PSDGrammarParser parser = createRecoveryParser(input, parseResult);
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

            /*
             * Here I am using two stage parsing in order to get the best parsing performance
             * possible.
             * References:
             * - https://theantlrguy.atlassian.net/wiki/pages/viewpage.action?pageId=1900591
             * - http://www.antlr.org/papers/allstar-techreport.pdf
             */
            try {
                ruleCallback.ruleCall(parser); // STAGE 1
                // if we parse ok, it's SLL (which is faster than LL)
            } catch (Exception ex) {
                if (!interrupted) {
//                    // for some reason listener gets called while reset is happening
//                    Lexer lexer = (Lexer) parser.getTokenStream().getTokenSource();
//                    List<? extends ANTLRErrorListener> lexerListeners = lexer.getErrorListeners();
//                    List<? extends ANTLRErrorListener> parserListeners = parser.getErrorListeners();
//                    ANTLRErrorStrategy errorStrategy = parser.getErrorHandler();
//                    lexer.removeErrorListeners();
//                    parser.removeErrorListeners();
//                    parser.setErrorHandler(new DefaultErrorStrategy());

                    ((CommonTokenStream) parser.getTokenStream()).reset(); // rewind input stream
                    parser.reset();
                    if (!SettingsHolder.IS_DEPLOYMENT_MODE) {
                        parser.getInterpreter().setPredictionMode(
                                PredictionMode.LL_EXACT_AMBIG_DETECTION); // for debugging -> detect every ambiguosity
                    } else {
                        parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                    }

//                    for (ANTLRErrorListener l : lexerListeners) {
//                        lexer.addErrorListener(l);
//                    }
//                    for (ANTLRErrorListener l : parserListeners) {
//                        parser.addErrorListener(l);
//                    }
//                    parser.setErrorHandler(errorStrategy);
                    parseResult.getParseErrors().clear(); // reset parseResult
                    ruleCallback.ruleCall(parser); // STAGE 2
                    if (!SettingsHolder.IS_DEPLOYMENT_MODE) {
                        System.out.println("STAGE2 was needed for input '" + input + "'");
                    }
                    // if we parse ok, it's LL not SLL
                }
            }

            if (!interrupted) {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (parseResult.isInputValid()) {
                            listener.onValidationComplete(true);
                        } else {
                            listener.onRecoveryFinished(parseResult);
                        }
                    }
                });
            }
        }

        @Override
        public void interrupt()
        {
            interrupted = true;
            super.interrupt(); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
