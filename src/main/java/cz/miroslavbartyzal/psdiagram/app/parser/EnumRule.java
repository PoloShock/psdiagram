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
public enum EnumRule
{

    EXPRESSION
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseExpression(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseExpressionReportErrors(input, listener);
                }
            },
    BOOLEAN_EXPRESSION
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseBooleanExpression(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseBooleanExpressionReportErrors(input, listener);
                }
            },
    NUMERIC_EXPRESSION
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseNumericExpression(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseNumericExpressionReportErrors(input, listener);
                }
            },
    STRING_EXPRESSION
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseStringExpression(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseStringExpressionReportErrors(input, listener);
                }
            },
    LIST_OF_CONSTANTS
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseListOfConstants(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseListOfConstantsReportErrors(input, listener);
                }
            },
    LIST_OF_NUMERIC_CONSTANTS
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseListOfNumericConstants(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseListOfNumericConstantsExpressionReportErrors(input, listener);
                }
            },
    NO_ARRAY_VARIABLE_TO_ASSIGN_TO
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseNoArrayVariableToAssignTo(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseNoArrayVariableToAssignToReportErrors(input, listener);
                }
            },
    VARIABLE_TO_ASSIGN_TO
            {
                @Override
                public boolean parse(PSDParser parser, String input)
                {
                    return parser.parseVariableToAssignTo(input);
                }

                @Override
                public void parseReportErrors(PSDParser parser, String input,
                        PSDParserListener listener)
                {
                    parser.parseVariableToAssignToReportErrors(input, listener);
                }
            };

    public abstract boolean parse(PSDParser parser, String input);

    public abstract void parseReportErrors(PSDParser parser, String input,
            PSDParserListener listener);

}
