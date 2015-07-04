/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.parboiled;

import cz.miroslavbartyzal.psdiagram.app.parser.PSDParseResult;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParser;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParserListener;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ParboiledParser implements PSDParser
{

    private static final ParboiledGrammar PARSER = Parboiled.createParser(ParboiledGrammar.class);
    private final ParboiledParseRunner<Double> parseRunner = new ParboiledParseRunner<>();

    @Override
    public void stopParsing()
    {
        parseRunner.stopParsing();
    }

    private boolean parseInput(String input, Rule rule)
    {
        BasicParseRunner<Double> runner = new BasicParseRunner<>(rule);
        ParsingResult<Double> result = runner.run(input);
        return result.matched && !result.hasCollectedParseErrors();
    }

    private void parseInputReportErrors(String input, Rule rule, PSDParserListener listener)
    {
        parseRunner.run(input, rule, new SyntaxErrorListenerAdapter(listener));
    }

    private class SyntaxErrorListenerAdapter implements ParboiledRecoveryListener<Double>
    {

        private final PSDParserListener listener;

        public SyntaxErrorListenerAdapter(PSDParserListener listener)
        {
            this.listener = listener;
        }

        @Override
        public void onValidationComplete(boolean isValid)
        {
            listener.onValidationComplete(isValid);
        }

        @Override
        public void onRecoverySuccess(ParsingResult<Double> result, String input)
        {
            notifyListener(result, input);
        }

        @Override
        public void onRecoveryException(ParsingResult<Double> result, String input)
        {
            notifyListener(result, input);
        }

        private void notifyListener(ParsingResult<Double> result, String input)
        {
            PSDParseResult parseResult = new PSDParseResult(input);

            for (ParseError parseError : result.parseErrors) {
                if (parseError instanceof ParboiledSyntaxError) {
                    parseResult.addErrorInfo((ParboiledSyntaxError) parseError);
                } else {
                    int startIndex = parseError.getInputBuffer().getOriginalIndex(
                            parseError.getStartIndex());
                    int endIndex = parseError.getInputBuffer().getOriginalIndex(
                            parseError.getEndIndex());
                    if (endIndex > input.length()) {
                        // Parboiled is indexing missing character as an additional column at the end of string
                        endIndex = input.length();
                    }
                    parseResult.addErrorInfo(ErrorUtils.printParseError(parseError),
                            startIndex, endIndex);
                }
            }

            if (parseResult.isInputValid() && !result.matched) {
                // there was no specific error stated but input is still not valid
                parseResult.addErrorInfo("Nalezena syntaktická chyba.", -1, -1);
            }

            listener.onRecoveryFinished(parseResult);
        }

    }

    @Override
    public boolean parseExpression(String input)
    {
        return parseInput(input, PARSER.solo_Expression());
    }

    @Override
    public boolean parseBooleanExpression(String input)
    {
        return parseInput(input, PARSER.solo_BooleanExpression());
    }

    @Override
    public boolean parseListOfConstants(String input)
    {
        return parseInput(input, PARSER.solo_ListOf_Constants());
    }

    @Override
    public boolean parseListOfNumericConstants(String input)
    {
        return parseInput(input, PARSER.solo_ListOf_NumberConstants());
    }

    @Override
    public boolean parseNumericExpression(String input)
    {
        return parseInput(input, PARSER.solo_NumericExpression());
    }

    @Override
    public boolean parseStringExpression(String input)
    {
        return parseInput(input, PARSER.solo_StringExpression());
    }

    @Override
    public boolean parseNoArrayVariableToAssignTo(String input)
    {
        return parseInput(input, PARSER.solo_NoArrayVariableToAssignTo());
    }

    @Override
    public boolean parseVariableToAssignTo(String input)
    {
        return parseInput(input, PARSER.solo_VariableToAssignTo());
    }

    @Override
    public void parseExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_Expression(), listener);
    }

    @Override
    public void parseBooleanExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_BooleanExpression(), listener);
    }

    @Override
    public void parseListOfConstantsReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_ListOf_Constants(), listener);
    }

    @Override
    public void parseListOfNumericConstantsExpressionReportErrors(String input,
            PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_ListOf_NumberConstants(), listener);
    }

    @Override
    public void parseNumericExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_NumericExpression(), listener);
    }

    @Override
    public void parseStringExpressionReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_StringExpression(), listener);
    }

    @Override
    public void parseNoArrayVariableToAssignToReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_NoArrayVariableToAssignTo(), listener);
    }

    @Override
    public void parseVariableToAssignToReportErrors(String input, PSDParserListener listener)
    {
        parseInputReportErrors(input, PARSER.solo_VariableToAssignTo(), listener);
    }

}
