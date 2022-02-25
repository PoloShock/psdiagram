package cz.miroslavbartyzal.psdiagram.app.parser.psd;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.function.Function;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface PsdParser
{
    
    void stopParsing();
    
    String translatePSDToJavaScript(String input, Function<PSDGrammarParser, ParseTree> parserRuleChoice);
    
    String translatePSDToJava(String input, Function<PSDGrammarParser, ParseTree> parserRuleChoice);
    
    boolean parseExpression(String input);
    
    boolean parseBooleanExpression(String input);
    
    boolean parseListOfConstants(String input);
    
    boolean parseListOfNumericConstants(String input);
    
    boolean parseNumericExpression(String input);
    
    boolean parseStringExpression(String input);
    
    boolean parseNoArrayVariableToAssignTo(String input);
    
    boolean parseVariableToAssignTo(String input);
    
    void parseExpressionReportErrors(String input, PsdParserListener listener);
    
    void parseBooleanExpressionReportErrors(String input, PsdParserListener listener);
    
    void parseListOfConstantsReportErrors(String input, PsdParserListener listener);
    
    void parseListOfNumericConstantsExpressionReportErrors(String input,
            PsdParserListener listener);
    
    void parseNumericExpressionReportErrors(String input, PsdParserListener listener);
    
    void parseStringExpressionReportErrors(String input, PsdParserListener listener);
    
    void parseNoArrayVariableToAssignToReportErrors(String input, PsdParserListener listener);
    
    void parseVariableToAssignToReportErrors(String input, PsdParserListener listener);
    
}
