/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ErrorReportingParseRunner;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
//@BuildParseTree // TODO only for debug
public class ParboiledGrammar extends BaseParser<Double>
{

    static final String ERROR_MARK = ErrorReportingParseRunner.SYNTAX_ERROR_RULE_MARKER;

    /**
     * Because of how Parboiled works, it is possible that actions are run more than once
     * at the same point at the input. This method searches current error list and cancels
     * insertion of ParseError that would cause duplicates.
     * <p>
     * @param psdSyntaxError
     */
    void addPSDSyntaxError(ParboiledSyntaxError psdSyntaxError)
    {
        for (ParseError parseError : getContext().getParseErrors()) {
            if (parseError.getStartIndex() == psdSyntaxError.getStartIndex()
                    && parseError.getEndIndex() == psdSyntaxError.getEndIndex()
                    && parseError instanceof ParboiledSyntaxError
                    && parseError.getErrorMessage().equals(psdSyntaxError.getErrorMessage())) {
                // already present
                return;
            }
        }
        getContext().getParseErrors().add(psdSyntaxError);
    }

    boolean addError(String message)
    {
        if (getContext().hasError() && ((MatcherContext<Double>) getContext()).getBasicSubContext().getNode() == null) {
            // we are in an artificialy created context.. let's not report any errors from this state
            return true;
        }

        InputBuffer inputBuffer = getContext().getInputBuffer();
        ParboiledSyntaxError myError = new ParboiledSyntaxError(message,
                inputBuffer.getOriginalIndex(
                        matchStart()), inputBuffer.getOriginalIndex(matchEnd()), inputBuffer);
        addPSDSyntaxError(myError);
        return true;
    }

    boolean addErrorBefore(String message)
    {
        if (getContext().hasError() && ((MatcherContext<Double>) getContext()).getBasicSubContext().getNode() == null) {
            // we are in an artificialy created context.. let's not report any errors from this state
            return true;
        }

        InputBuffer inputBuffer = getContext().getInputBuffer();
        ParboiledSyntaxError myError = new ParboiledSyntaxError(message,
                inputBuffer.getOriginalIndex(
                        matchStart()), inputBuffer.getOriginalIndex(matchStart()), inputBuffer);
        addPSDSyntaxError(myError);
        return true;
    }

    enum OP
    {

        ADD,
        SUB,
        MUL,
        DIV,
        MOD

    }

    boolean eval(OP operator)
    {
//        if (getContext().getValueStack().size() < 2) {
//            getContext().getValueStack().clear();
//            push(null);
//            return true;
//        }

        Double rightOperand = pop();
        Double leftOperand = pop();

        if (leftOperand == null || rightOperand == null) {
            push(null);
        }

        switch (operator) {
            case ADD:
                push(leftOperand + rightOperand);
                break;
            case SUB:
                push(leftOperand - rightOperand);
                break;
            case MUL:
                push(leftOperand * rightOperand);
                break;
            case DIV:
                push(leftOperand / rightOperand);
                break;
            case MOD:
                push(leftOperand % rightOperand);
                break;
            default:
                push(null);
                break;
        }

        return true;
    }

//    boolean addErrorAfter(String message)
//    {
//        if (getContext().hasError() && ((MatcherContext<Void>) getContext()).getBasicSubContext().getNode() == null) {
//            // we are in an artificialy created context.. let's not report any errors from this state
//            return true;
//        }
//
//        InputBuffer inputBuffer = getContext().getInputBuffer();
//        PSDSyntaxError myError = new PSDSyntaxError(message, inputBuffer.getOriginalIndex(
//                matchEnd()), inputBuffer.getOriginalIndex(matchEnd()), inputBuffer);
//        addPSDSyntaxError(myError);
//        return true;
//    }
    // ***********************************************************************************
    // ***********************************  SOLO RULES ***********************************
    // ***********************************************************************************
    @Label("seznam konstant")
    public Rule solo_ListOf_Constants()
    {
        return sequence(
                spacing(),
                firstOf(
                        constants_Solo_Signed(),
                        test(
                                COMMA, addErrorBefore(
                                        "Nalezeno prázdné místo ve výčtu konstant.")
                        ).label(ERROR_MARK) // probably not neccessary because of test rule but anyway...
                ),
                zeroOrMore(
                        firstOf(
                                sequence(COMMA, constants_Solo_Signed()),
                                sequence(
                                        COMMA,
                                        test(firstOf(COMMA, EOI), addErrorBefore(
                                                        "Nalezeno prázdné místo ve výčtu konstant."))
                                ).label(ERROR_MARK)
                        )
                ),
                EOI
        );
    }

    @Label("seznam číselných konstant")
    public Rule solo_ListOf_NumberConstants()
    {
        return sequence(
                spacing(),
                firstOf(
                        constant_Number_Signed(),
                        test(
                                COMMA, addErrorBefore(
                                        "Nalezeno prázdné místo ve výčtu konstant.")
                        ).label(ERROR_MARK) // probably not neccessary because of test rule but anyway...
                ),
                zeroOrMore(
                        firstOf(
                                sequence(COMMA, constant_Number_Signed()),
                                sequence(
                                        COMMA,
                                        test(firstOf(COMMA, EOI), addErrorBefore(
                                                        "Nalezeno prázdné místo ve výčtu konstant."))
                                ).label(ERROR_MARK)
                        )
                ),
                EOI
        );
    }

    @Label("proměnná")
    public Rule solo_NoArrayVariableToAssignTo()
    {
        return sequence(
                spacing(),
                firstOf(
                        variable_Identifier(),
                        solo_VariableErrors()
                ),
                optional(
                        oneOrMore(arrayLookup_BracketsPart()),
                        addError("Zde není indexace pole povolena.")
                ).label(ERROR_MARK),
                EOI
        );
    }

    @Label(ERROR_MARK)
    Rule solo_VariableErrors()
    {   // testNot(Variable_Identifier())
        return firstOf(
                sequence(
                        sequence(digit(), zeroOrMore(nonSeparator())),
                        addError(
                                "Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru."),
                        spacing()
                ),
                sequence(
                        firstOf(
                                constant_Boolean(),
                                nullLiteral()
                        ),
                        addError(
                                "'" + match() + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.")
                )
        );
    }

    @Label("proměnná")
    public Rule solo_VariableToAssignTo()
    {
        return sequence(
                spacing(),
                firstOf(
                        sequence(
                                variable_Identifier(),
                                zeroOrMore(arrayLookup_BracketsPart())
                        ),
                        sequence(
                                solo_VariableErrors(),
                                zeroOrMore(arrayLookup_BracketsPart())
                        ).label(ERROR_MARK)
                ),
                EOI
        );
    }

    @Label("číselný výraz")
    public Rule solo_NumericExpression()
    {
        return sequence(
                spacing(),
                firstOf(
                        numericOrString_Expression_Guarded(),
                        numeric_Expression(),
                        unknownTypeValue()
                ),
                EOI
        );
    }

    @Label("logický výraz")
    public Rule solo_BooleanExpression()
    {
        return sequence(
                spacing(),
                firstOf(
                        boolean_Expression(),
                        unknownTypeValue()
                ),
                EOI
        );
    }

    @Label("řetězcový výraz")
    public Rule solo_StringExpression()
    {
        return sequence(
                spacing(),
                firstOf(
                        string_Expression(),
                        numericOrString_Expression(),
                        unknownTypeValue()
                ),
                EOI
        );
    }

    @Label("jakýkoliv výraz")
    public Rule solo_Expression()
    {
        return sequence(
                spacing(),
                expression(),
                EOI
        );
    }
    // ***********************************************************************************
    // ******************************** END OF SOLO RULES ********************************
    // ***********************************************************************************

    @Label("konstanta")
    Rule constants_Solo_Signed()
    {
        return firstOf(
                constant_Number_Signed(),
                constant_Boolean(),
                constant_String()
        );
    }

    @Label("číselná konstanta")
    Rule constant_Number_Signed()
    {
        return sequence(
                optional(unaryPlusOrMinusOperator()),
                firstOf(
                        floatLiteral(),
                        integerLiteral()
                )
        );
    }

    Rule constant()
    {
        return firstOf(
                constant_Number(),
                constant_Boolean(),
                constant_String()
        );
    }

    Rule constant_Number()
    {
        return firstOf(
                floatLiteral(),
                integerLiteral()
        );
    }

    Rule constant_Boolean()
    {
        return booleanLiteral();
    }

    Rule constant_String()
    {
        return sequence(
                stringLiteral(), optional(arrayLookup_BracketsPart())
        );
    }

    Rule constant_Array()
    {
        return sequence(
                LBRK, expression(), zeroOrMore(COMMA, expression()), RBRK
        );
    }

    Rule arrayLookup_BracketsPart()
    {
        return sequence(
                LBRK, arrayIntegerIndex_Expression(), RBRK
        );
    }

    Rule arrayIntegerIndex_Expression()
    {
        return firstOf(
                numericOrString_Expression_Guarded(),
                numeric_Expression(),
                unknownTypeValue()
        );
    }

    Rule variableOrProperty()
    {
        return firstOf(
                variableOrProperty_WithoutConstants(),
                sequence(
                        firstOf(
                                constant_String(),
                                sequence(LPAR, string_Expression(), RPAR, optional(
                                                arrayLookup_BracketsPart())),
                                sequence(LPAR, numericOrString_Expression(), RPAR, optional(
                                                arrayLookup_BracketsPart()))
                        ),
                        sequence(DOT, variable_Identifier()),
                        zeroOrMore(variableOrProperty_RightPart())
                ),
                sequence(
                        array_Expression(),
                        oneOrMore(variableOrProperty_RightPart())
                ),
                sequence(
                        LPAR, variableOrProperty(), RPAR,
                        zeroOrMore(variableOrProperty_RightPart())
                )
        );
    }

    Rule variableOrProperty_RightPart()
    {
        return firstOf(
                arrayLookup_BracketsPart(),
                sequence(DOT, variable_Identifier())
        );
    }

    Rule variableOrProperty_WithoutConstants()
    {
        return sequence(
                variable_Identifier(),
                zeroOrMore(variableOrProperty_RightPart())
        );
    }

    @Label("identifikátor proměnné")
    Rule variable_Identifier()
    {
        return firstOf(
                identifier(), // Identifier has its tests for keywords, booleans, and null already included
                sequence(
                        keyword(),
                        addError(
                                "'" + match() + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.")
                ).label(ERROR_MARK),
                sequence(
                        testNot(
                                firstOf(
                                        constant_Boolean(),
                                        nullLiteral(),
                                        sequence(
                                                oneOrMore(digit()), testNot(nonSeparator())
                                        )
                                )
                        ),
                        firstOf(
                                sequence(
                                        sequence(
                                                letter(), oneOrMore(nonSeparator())
                                        ), addError(
                                                "Název proměnné může obsahovat jen písmena bez diakritiky, číslice, podtržítka nebo znaky dolaru."),
                                        spacing()
                                ),
                                sequence(
                                        oneOrMore(nonSeparator()),
                                        addError(
                                                "Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru."),
                                        spacing()
                                )
                        )
                ).label(ERROR_MARK)
        );
    }

    Rule nonSeparator()
    {
        return noneOf(" \t\r\f\n%/*-+|&!=><.,()[]{}\"\'^;");
    }

    Rule functionReturnValue()
    {
        return firstOf(
                sequence(
                        variableOrProperty(),
                        LPAR,
                        firstOf(
                                RPAR,
                                sequence(
                                        expression(),
                                        zeroOrMore(COMMA, expression()),
                                        RPAR
                                )
                        ),
                        zeroOrMore(arrayLookup_BracketsPart()),
                        optional(DOT, function_WithoutConstants())
                ),
                sequence(
                        LPAR, functionReturnValue(), RPAR,
                        zeroOrMore(arrayLookup_BracketsPart()),
                        optional(DOT, function_WithoutConstants())
                ) // (l()).l()
        );
    }

    Rule function_WithoutConstants()
    {
        return sequence(
                variableOrProperty_WithoutConstants(),
                optional(
                        LPAR,
                        firstOf(
                                RPAR,
                                sequence(
                                        expression(),
                                        zeroOrMore(COMMA, expression()),
                                        RPAR
                                )
                        ),
                        zeroOrMore(arrayLookup_BracketsPart()),
                        optional(DOT, function_WithoutConstants())
                )
        );
    }

    Rule unknownTypeValue()
    {
        return firstOf(
                functionReturnValue(),
                variableOrProperty()
        );
    }

    Rule expression()
    {
        // This order is needed to respect throughout the grammar!
        // However, longestOf is sometimes needed as:
        // (String || NumericOrString) & Boolean expressions can still colide:
        //      - "" = "" a+a = ""
        //      - a+a = ""
        //      - true + ""
        // StringOrNumeric & Numeric expressions can still colide:
        //	- prom + 1 - 1 -> here Numeric should be first
        //	- 1 + 1 + prom -> here StringOrNumeric should be first
        return firstOf(
                sequence(boolean_Expression(), testNot(PLUS)),
                string_Expression(),
                numericOrString_Expression_Guarded(),
                numeric_Expression(),
                unknownTypeValue(),
                array_Expression()
        );
    }

    Rule array_Expression()
    {
        return firstOf(
                constant_Array(),
                sequence(LPAR, array_Expression(), RPAR)
        );
    }

    Rule boolean_Expression()
    {
        return conditionalOr_Expression();
    }

    Rule numeric_Expression()
    {
        return additive_Expression();
    }

    Rule string_Expression()
    {
        return sequence(
                firstOf(
                        atomic_String_Expression(),
                        sequence(
                                unary_Boolean_Expression(),
                                sequence(PLUS,
                                        firstOf(
                                                atomic_String_Expression(),
                                                numericOrString_Expression__WithParetheses(),
                                                unknownTypeValue()
                                        )
                                )
                        ),
                        sequence(
                                firstOf(
                                        numericOrString_Expression_Guarded(),
                                        numeric_Expression(),
                                        unknownTypeValue()
                                ),
                                PLUS,
                                atomic_String_Expression()
                        ),
                        sequence(
                                firstOf(
                                        numericOrString_Expression(),
                                        unknownTypeValue()
                                ),
                                PLUS,
                                unary_Boolean_Expression()
                        )
                ),
                string_Expression_RightPart()
        );
    }

    /**
     * Used after we know for sure that the part before this is a string.
     * <p>
     * @return
     */
    Rule string_Expression_RightPart()
    {
        return zeroOrMore(
                sequence(
                        PLUS,
                        firstOf(
                                atomic_String_Expression(),
                                numericOrString_Expression__WithParetheses(),
                                unary_Boolean_Expression(),
                                multiplicative_Expression(),
                                unknownTypeValue()
                        )
                )
        );
    }

    Rule atomic_String_Expression()
    {
        return sequence(
                firstOf(
                        constant_String(),
                        sequence(LPAR, string_Expression(), RPAR, optional(
                                        arrayLookup_BracketsPart())),
                        sequence(LPAR, numericOrString_Expression(), RPAR,
                                arrayLookup_BracketsPart()) // this has to be a plain String because of the array lookup
                ),
                testNot(
                        DOT // it would have been UnknownType, not Atomic_String
                )
        );
    }

    /**
     * Guarded against Numeric //(, Equality, and Relational expressions)
     * <p>
     * @return
     */
    Rule numericOrString_Expression_Guarded()
    {
        return sequence(
                numericOrString_Expression(),
                testNot(
                        //                        firstOf(MINUS, EQUAL, NOTEQUAL, LE, GE, LT, GT),
                        MINUS,
                        testNot(EOI)
                )
        );
    }

    Rule numericOrString_Expression()
    {
        return firstOf(
                sequence(
                        numeric_Expression(),
                        PLUS,
                        firstOf(
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        ),
                        zeroOrMore(numericOrString_Expression_RightPart())
                ),
                numericOrString_Expression_Without_Numeric_Prefix_Rule()
        );
    }

    Rule numericOrString_Expression_Without_Numeric_Prefix_Rule()
    {
        return firstOf(
                sequence(
                        unknownTypeValue(),
                        oneOrMore(numericOrString_Expression_RightPart())
                ),
                sequence(
                        numericOrString_Expression__WithParetheses(),
                        zeroOrMore(numericOrString_Expression_RightPart())
                )
        );
    }

    Rule numericOrString_Expression__WithParetheses()
    {
        return sequence(
                LPAR, numericOrString_Expression(), RPAR,
                testNot(
                        firstOf(
                                DOT, // it would have been UnknownType, not Atomic_String
                                arrayLookup_BracketsPart() //not bracket because that would be just String
                        )
                )
        );
    }

    Rule numericOrString_Expression_RightPart()
    {
        return sequence(
                PLUS,
                firstOf(
                        numericOrString_Expression__WithParetheses(),
                        multiplicative_Expression(),
                        unknownTypeValue()
                )
        );
    }

    Rule conditionalOr_Expression()
    {
        return sequence(
                conditionalOr_Expression_LeftPart(),
                zeroOrMore(
                        OR,
                        firstOf(
                                conditionalAnd_Expression(),
                                unknownTypeValue()
                        )
                )
        );
    }

    /**
     * Used to determine a valid expression of its type, even if present solely
     * <p>
     * @return
     */
    Rule conditionalOr_Expression_LeftPart()
    {
        return firstOf(
                sequence(
                        unknownTypeValue(),
                        OR,
                        firstOf(
                                conditionalAnd_Expression(),
                                unknownTypeValue()
                        )
                ),
                conditionalAnd_Expression()
        );
    }

    Rule conditionalAnd_Expression()
    {
        return sequence(
                conditionalAnd_Expression_LeftPart(),
                zeroOrMore(
                        AND,
                        firstOf(
                                equality_Expression(),
                                unknownTypeValue()
                        )
                )
        );
    }

    Rule conditionalAnd_Expression_LeftPart()
    {
        return firstOf(
                sequence(
                        unknownTypeValue(),
                        AND,
                        firstOf(
                                equality_Expression(),
                                unknownTypeValue()
                        )
                ),
                equality_Expression()
        );
    }

    Rule equality_Expression()
    {
        return sequence(
                equality_Expression_LeftPart(),
                zeroOrMore(
                        firstOf(EQUAL, NOTEQUAL),
                        firstOf(
                                relational_Expression(),
                                unknownTypeValue()
                        )
                )
        );
    }

    Rule equality_Expression_LeftPart()
    {
        return firstOf(
                sequence(
                        string_Expression(),
                        firstOf(EQUAL, NOTEQUAL),
                        firstOf(
                                string_Expression(),
                                numericOrString_Expression(),
                                unknownTypeValue()
                        )
                ),
                sequence(
                        numericOrString_Expression(),
                        firstOf(EQUAL, NOTEQUAL),
                        firstOf(
                                string_Expression(),
                                numericOrString_Expression_Guarded(),
                                numeric_Expression(),
                                unknownTypeValue()
                        )
                ),
                sequence(
                        numeric_Expression(),
                        firstOf(EQUAL, NOTEQUAL),
                        firstOf(
                                numericOrString_Expression_Guarded(),
                                numeric_Expression(),
                                unknownTypeValue()
                        )
                ),
                sequence(
                        unknownTypeValue(),
                        firstOf(EQUAL, NOTEQUAL),
                        firstOf(
                                sequence(relational_Expression(), testNot(PLUS)),
                                string_Expression(),
                                numericOrString_Expression_Guarded(),
                                numeric_Expression(),
                                unknownTypeValue()
                        )
                ),
                relational_Expression()
        );
    }

    Rule relational_Expression()
    {
        return firstOf(
                sequence(
                        firstOf(
                                numericOrString_Expression_Guarded(),
                                numeric_Expression(),
                                unknownTypeValue()
                        ),
                        firstOf(LE, GE, LT, GT),
                        firstOf(
                                numericOrString_Expression_Guarded(),
                                numeric_Expression(),
                                unknownTypeValue()
                        )
                ),
                unary_Boolean_Expression()
        );
    }

    Rule additive_Expression()
    {
        return sequence(
                additive_Expression_LeftPart(),
                zeroOrMore(
                        firstOf(
                                sequence(
                                        MINUS,
                                        firstOf(
                                                multiplicative_Expression(),
                                                numericOrString_Expression__WithParetheses(), // parenthesis are needed because of the ordering, which is on the same level: 1 - 2 + pom + 1 <- the part after 1 is NumberOrString otherwise
                                                unknownTypeValue()
                                        )
                                ),
                                sequence(
                                        PLUS,
                                        firstOf(
                                                multiplicative_Expression(),
                                                sequence(
                                                        firstOf(
                                                                numericOrString_Expression__WithParetheses(),
                                                                unknownTypeValue()
                                                        ),
                                                        MINUS,
                                                        firstOf(
                                                                multiplicative_Expression(),
                                                                numericOrString_Expression__WithParetheses(),
                                                                unknownTypeValue()
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    Rule additive_Expression_LeftPart()
    {
        return firstOf(
                sequence(
                        firstOf(
                                numericOrString_Expression_Without_Numeric_Prefix_Rule(), // stackoverflow otherwise (parboiled)
                                unknownTypeValue()
                        ),
                        MINUS, firstOf(
                                multiplicative_Expression(),
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        )
                ),
                multiplicative_Expression()
        );
    }

    Rule multiplicative_Expression()
    {
        return sequence(
                multiplicative_Expression_LeftPart(),
                zeroOrMore(
                        firstOf(STAR, DIV, MOD),
                        firstOf(
                                unary_Numeric_Expression(),
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        )
                )
        );
    }

    Rule multiplicative_Expression_LeftPart()
    {
        return firstOf(
                sequence(
                        firstOf(
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        ),
                        firstOf(STAR, DIV, MOD),
                        firstOf(
                                unary_Numeric_Expression(),
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        )
                ),
                unary_Numeric_Expression()
        );
    }

    Rule unary_Numeric_Expression()
    {
        return firstOf(
                sequence(
                        unaryPlusOrMinusOperator(),
                        firstOf(
                                atomic_Numeric_Expression(),
                                numericOrString_Expression__WithParetheses(),
                                unknownTypeValue()
                        )
                ),
                atomic_Numeric_Expression()
        );
    }

    Rule atomic_Numeric_Expression()
    {
        return firstOf(
                constant_Number(),
                sequence(LPAR, numeric_Expression(), RPAR)
        );
    }

    Rule unary_Boolean_Expression()
    {
        return firstOf(
                sequence(
                        BANG, firstOf(
                                atomic_Boolean_Expression(),
                                unknownTypeValue()
                        )
                ),
                atomic_Boolean_Expression()
        );
    }

    Rule atomic_Boolean_Expression()
    {
        return firstOf(
                constant_Boolean(),
                sequence(LPAR, boolean_Expression(), RPAR)
        );
    }

    @Label("znaménko")
    Rule unaryPlusOrMinusOperator()
    {
        return firstOf(
                sequence(
                        zeroOrMore(firstOf(PLUS, MINUS)),
                        oneOrMore(
                                oneOrMore(firstOf(INC, DEC), addError(
                                                "Zdvojené znaménko není v PS Diagramu povoleno.")),
                                zeroOrMore(firstOf(PLUS, MINUS))
                        )
                ).label(ERROR_MARK),
                oneOrMore(firstOf(PLUS, MINUS))
        );
    }

    //-------------------------------------------------------------------------
    //  JLS 3.6-7  Spacing
    //-------------------------------------------------------------------------
    @SuppressNode
    @Label("mezera")
    Rule spacing()
    {
        return zeroOrMore(
                // whitespace
                anyOf(" \t\r\n\f").label("Whitespace")
        );
    }

    //-------------------------------------------------------------------------
    //  JLS 3.8  Identifiers
    //-------------------------------------------------------------------------
    @SuppressSubnodes
    @MemoMismatches
    @Label("identifikátor")
    Rule identifier()
    {
        return sequence(
                testNot(
                        firstOf(
                                keyword(),
                                booleanLiteral(),
                                nullLiteral()
                        )
                ),
                letter(), zeroOrMore(letterOrdigit()),
                spacing()
        );
    }

    Rule letter()
    {
        return firstOf(
                charRange('a', 'z'),
                charRange('A', 'Z'),
                '_',
                '$');
    }

    @MemoMismatches
    Rule letterOrdigit()
    {
        return firstOf(
                charRange('a', 'z'),
                charRange('A', 'Z'),
                charRange('0', '9'),
                '_',
                '$');
    }

    //-------------------------------------------------------------------------
    //  JLS 3.9  Keywords
    //-------------------------------------------------------------------------
    @Label("klíčové slovo")
    Rule keyword()
    {
        return sequence(firstOf(
                ignoreCase("arguments"),
                ignoreCase("assert"),
                ignoreCase("boolean"),
                ignoreCase("break"),
                ignoreCase("byte"),
                ignoreCase("case"),
                ignoreCase("catch"),
                ignoreCase("char"),
                ignoreCase("class"),
                ignoreCase("const"),
                ignoreCase("continue"),
                ignoreCase("debugger"),
                ignoreCase("default"),
                ignoreCase("delete"),
                ignoreCase("do"),
                ignoreCase("double"),
                ignoreCase("else"),
                ignoreCase("enum"),
                ignoreCase("export"),
                ignoreCase("extends"),
                ignoreCase("finally"),
                ignoreCase("final"),
                ignoreCase("float"),
                ignoreCase("for"),
                ignoreCase("function"),
                ignoreCase("goto"),
                ignoreCase("if"),
                ignoreCase("implements"),
                ignoreCase("import"),
                ignoreCase("interface"),
                ignoreCase("int"),
                ignoreCase("instanceof"),
                ignoreCase("in"),
                ignoreCase("let"),
                ignoreCase("long"),
                ignoreCase("new"),
                ignoreCase("package"),
                ignoreCase("private"),
                ignoreCase("protected"),
                ignoreCase("public"),
                ignoreCase("return"),
                ignoreCase("short"),
                ignoreCase("static"),
                ignoreCase("super"),
                ignoreCase("switch"),
                ignoreCase("synchronized"),
                ignoreCase("this"),
                ignoreCase("throws"),
                ignoreCase("throw"),
                ignoreCase("try"),
                ignoreCase("typeof"),
                ignoreCase("var"),
                ignoreCase("void"),
                ignoreCase("while"),
                ignoreCase("with"),
                ignoreCase("yield")
        ),
                testNot(letterOrdigit()),
                spacing()
        );
    }

    //-------------------------------------------------------------------------
    //  JLS 3.10  Literals
    //-------------------------------------------------------------------------
    @Label("null")
    Rule nullLiteral()
    {
        return sequence("null", testNot(letterOrdigit()), spacing());
    }

    @Label("logická hodnota")
    Rule booleanLiteral()
    {
        return sequence(
                firstOf(
                        "true",
                        "false"
                ),
                testNot(letterOrdigit()),
                spacing()
        );
    }

    @SuppressSubnodes
    @Label("celé číslo")
    Rule integerLiteral()
    {
        return sequence(
                decimalNumeral(),
                testNot(nonSeparator()), // test that there is no clutter behind the number (1 - 1ů)
                spacing()
        );
    }

    Rule decimalNumeral()
    {
        return firstOf(
                '0',
                sequence(charRange('1', '9'), zeroOrMore(digit()))
        );
    }

    @SuppressSubnodes
    @Label("desetinné číslo")
    Rule floatLiteral()
    {
        return sequence(
                decimalFloat(),
                testNot(nonSeparator()), // test that there is no clutter behind the number (1 - 1.01ů)
                spacing()
        );
    }

    Rule decimalFloat()
    {
        return sequence(
                decimalNumeral(), '.', oneOrMore(digit())
        );
    }

    @Label("číslo")
    @Override
    public Rule digit()
    {
        return charRange('0', '9');
    }

    @Label("řetězcová hodnota")
    Rule stringLiteral()
    {
        return sequence('"',
                zeroOrMore(
                        firstOf(
                                escape(),
                                sequence(testNot(anyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                '"',
                spacing()
        );
    }

    Rule escape()
    {

//      \t Insert a tab in the text at this point.
//      \b Insert a backspace in the text at this point.
//      \n Insert a newline in the text at this point.
//      \r Insert a carriage return in the text at this point.
//      \f Insert a formfeed in the text at this point.
//      \' Insert a single quote character in the text at this point.
//      \" Insert a double quote character in the text at this point.
//      \\ Insert a backslash character in the text at this point.
        return sequence('\\', anyOf("tbnrf\'\"\\"));
    }

    //-------------------------------------------------------------------------
    //  JLS 3.11-12  Separators, Operators
    //-------------------------------------------------------------------------
    final Rule LBRK = terminal("[");
    final Rule RBRK = terminal("]");
    final Rule LPAR = terminal("(");
    final Rule RPAR = terminal(")");
    final Rule COMMA = terminal(",");
    final Rule DOT = terminal(".");

    final Rule INC = terminal("++");
    final Rule DEC = terminal("--");
    final Rule BANG = terminal("!", ch('='));
    final Rule GT = terminal(">", ch('='));
    final Rule LT = terminal("<", ch('='));
    final Rule NOTEQUAL = terminal("!=");
    final Rule EQUAL = terminal("=");
    final Rule LE = terminal("<=");
    final Rule GE = terminal(">=");
    final Rule AND = terminal("&");
    final Rule OR = terminal("|");
    final Rule PLUS = terminal("+", ch('+'));
    final Rule MINUS = terminal("-", ch('-'));
    final Rule STAR = terminal("*");
    final Rule DIV = terminal("/");
    final Rule MOD = terminal("%");

    //-------------------------------------------------------------------------
    //  helper methods
    //-------------------------------------------------------------------------
    @Override
    @DontLabel
    protected Rule fromCharLiteral(char c)
    {
        // turn of creation of parse tree nodes for single characters
        return super.fromCharLiteral(c).suppressNode();
    }

    @SuppressNode
    @DontLabel
    Rule terminal(String string)
    {
        return sequence(string, spacing().label(ERROR_MARK)).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    Rule terminal(String string, Rule mustNotFollow)
    {
        return sequence(
                string,
                testNot(mustNotFollow),
                spacing().label(ERROR_MARK)
        ).label('\'' + string + '\'');
    }

}
