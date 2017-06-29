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
public interface PSDParser
{

    public void stopParsing();
    
    public String translatePSDToJavaScript(String input);

    public boolean parseExpression(String input);

    public boolean parseBooleanExpression(String input);

    public boolean parseListOfConstants(String input);

    public boolean parseListOfNumericConstants(String input);

    public boolean parseNumericExpression(String input);

    public boolean parseStringExpression(String input);

    public boolean parseNoArrayVariableToAssignTo(String input);

    public boolean parseVariableToAssignTo(String input);

    public void parseExpressionReportErrors(String input, PSDParserListener listener);

    public void parseBooleanExpressionReportErrors(String input, PSDParserListener listener);

    public void parseListOfConstantsReportErrors(String input, PSDParserListener listener);

    public void parseListOfNumericConstantsExpressionReportErrors(String input,
            PSDParserListener listener);

    public void parseNumericExpressionReportErrors(String input, PSDParserListener listener);

    public void parseStringExpressionReportErrors(String input, PSDParserListener listener);

    public void parseNoArrayVariableToAssignToReportErrors(String input, PSDParserListener listener);

    public void parseVariableToAssignToReportErrors(String input, PSDParserListener listener);

}
