parser grammar PSDGrammarParser;

options { tokenVocab=PSDGrammarLexer; }

@parser::members {
    private static final java.util.regex.Pattern BAD_ESCAPE_PATTERN = java.util.regex.Pattern.compile("\\\\[^tbnrf\"'\\\\]");

    void ruleNotifyErrorListeners(String msg, String expecting, Token start, Token stop)
    {
        CommonToken token = new CommonToken(-1);
        token.setStartIndex(start.getStartIndex());
        token.setStopIndex(stop.getStopIndex());
        getErrorListenerDispatch().syntaxError(this, token, start.getLine(),
                start.getCharPositionInLine(), mixMsgWithExpected(msg, expecting), null);
    }

    void notifyErrorListenersOnPointAfter(String msg, String expecting, Token lastToken)
    {
        CommonToken token = new CommonToken(-1);
        token.setStartIndex(lastToken.getStopIndex() + 1);
        token.setStopIndex(lastToken.getStopIndex());
        getErrorListenerDispatch().syntaxError(this, token, lastToken.getLine(),
                lastToken.getStopIndex(), mixMsgWithExpected(msg, expecting), null);
    }

    void notifyErrorListenersOnPointBefore(String msg, String expecting, Token firstToken)
    {
        CommonToken token = new CommonToken(-1);
        token.setStartIndex(firstToken.getStartIndex());
        token.setStopIndex(firstToken.getStartIndex() - 1);
        getErrorListenerDispatch().syntaxError(this, token, firstToken.getLine(),
                firstToken.getStartIndex() - 1, mixMsgWithExpected(msg, expecting), null);
    }

    void notifyErrorListenersByIndex(String msg, String expecting, int startIndex, int stopIndex,
            int line, int charPositionInLine)
    {
        CommonToken token = new CommonToken(-1);
        token.setStartIndex(startIndex);
        token.setStopIndex(stopIndex);
        getErrorListenerDispatch().syntaxError(this, token, line, charPositionInLine,
                mixMsgWithExpected(msg, expecting), null);
    }

    void notifyErrorListeners(Token offendingToken, String msg, String expecting,
            RecognitionException e)
    {
        notifyErrorListeners(offendingToken, mixMsgWithExpected(msg, expecting), e);
    }

    private String mixMsgWithExpected(String msg, String expecting)
    {
        if (expecting == null || expecting.isEmpty()) {
            return msg;
        }
        return msg + "\nOčekáváné možnosti: {" + expecting + "}.";
    }

    Double eval(Double left, int op, Double right)
    {
        if (left == null || right == null) {
            return null;
        }

        switch ( op ) {
            case PLUS       : return left   +   right;
            case MINUS      : return left   -   right;
            case MUL        : return left   *   right;
            case DIV        : return left   /   right;
            case FLOORDIV   : return Math.floor(left / right);
            case MOD        : return left   %   right;
        }

        return null;
    }

    /**
     * Unescapes a string that contains standard Java escape sequences.
     * Edited from: https://gist.github.com/uklimaschewski/6741769
     * @param st
     *            A string optionally containing standard java escape sequences.
     * @return The translated string.
     */
    public String unescapeJavaString(String st)
    {
        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);

                switch (nextChar) {
                case '\\':
                    ch = '\\';
                    break;
                case 'b':
                    ch = '\b';
                    break;
                case 'f':
                    ch = '\f';
                    break;
                case 'n':
                    ch = '\n';
                    break;
                case 'r':
                    ch = '\r';
                    break;
                case 't':
                    ch = '\t';
                    break;
                case '\"':
                    ch = '\"';
                    break;
                case '\'':
                    ch = '\'';
                    break;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}

// ********** Solo Rules **********
solo_ListOf_Constants
    :   listOf_Constants_RepeatingPart* (
            constant_solo EOF
        |   EOF {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu konstant.", "konstantní hodnota v podobě čísla, logické hodnoty (true, false) nebo řetězcové hodnoty", $EOF);}
        )
    ;
listOf_Constants_RepeatingPart
    :   constant_solo COMMA
    |   COMMA {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu konstant.", "konstantní hodnota v podobě čísla, logické hodnoty (true, false) nebo řetězcové hodnoty", $COMMA);}
    ;
constant_solo
    :   constant
    // following is ambiguous but it is also the way of least resistance...
    |   expression {ruleNotifyErrorListeners("Neplatná konstantní hodnota. Povoleny jsou pouze konstantní hodnoty v podobě čísla, logické hodnoty (true, false) nebo řetězcové hodnoty.", null, $expression.start, $expression.stop);}
    ;

solo_ListOf_NumberConstants
    :   listOf_NumberConstants_RepeatingPart* (
            constant_Number_solo EOF
        |   EOF {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu konstant.", "konstantní hodnota v podobě čísla", $EOF);}
        )
    ;
listOf_NumberConstants_RepeatingPart
    :   constant_Number_solo COMMA
    |   COMMA {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu konstant.", "konstantní hodnota v podobě čísla", $COMMA);}
    ;
constant_Number_solo
    :   constant_Number_Signed
    // following is ambiguous but it is also the way of least resistance...
    |   expression {ruleNotifyErrorListeners("Neplatná konstantní hodnota. Povolena je pouze konstantní hodnota v podobě čísla.", null, $expression.start, $expression.stop);}
    ;

solo_NoArrayVariableToAssignTo
    :   noArrayVariableToAssignTo_solo_part (
            EOF
        |   array_lookup_bracketsPart_multiple EOF {ruleNotifyErrorListeners("Zde není indexace pole povolena.", null, $array_lookup_bracketsPart_multiple.start, $array_lookup_bracketsPart_multiple.stop);}
        )
    ;
array_lookup_bracketsPart_multiple
    :
        arrayLookup_BracketsPart+
    ;
noArrayVariableToAssignTo_solo_part
    :   variable_Identifier
    |   constant_Number {ruleNotifyErrorListeners("Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru.", null, $constant_Number.start, $constant_Number.stop);}
    |   cnsnt=(CONSTANT_BOOLEAN | NULL_LITERAL) {notifyErrorListeners($cnsnt, "'" + $cnsnt.text + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.", null, null);}
    ;

solo_VariableToAssignTo
    :   noArrayVariableToAssignTo_solo_part (
            EOF
        |   arrayLookup_BracketsPart+ EOF
        )
    ;

solo_NumericExpression
    :	numericOrString_Expression EOF
    |	numeric_Expression EOF
    |	unknownTypeValue EOF
    |   boolean_Expression EOF {ruleNotifyErrorListeners("Logická hodnota není na tomto místě očekávána.", "číselná hodnota", $boolean_Expression.start, $boolean_Expression.stop);}
    |   string_Expression EOF {ruleNotifyErrorListeners("Řetězcová hodnota není na tomto místě očekávána.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);}
    |   array_Expression EOF {ruleNotifyErrorListeners("Hodnota v podobě pole není na tomto místě očekávána.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
    |   NULL_LITERAL EOF {notifyErrorListeners($NULL_LITERAL, "Hodnota null není na tomto místě očekávána.", "číselná hodnota", null);}
    ;

solo_BooleanExpression
    :	boolean_Expression EOF
    |	unknownTypeValue EOF
    |	numericOrString_Expression EOF {ruleNotifyErrorListeners("Číselná/řetězcová hodnota není na tomto místě očekávána.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
    |   numeric_Expression EOF {ruleNotifyErrorListeners("Číselná hodnota není na tomto místě očekávána.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
    |   string_Expression EOF {ruleNotifyErrorListeners("Řetězcová hodnota není na tomto místě očekávána.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
    |   array_Expression EOF {ruleNotifyErrorListeners("Hodnota v podobě pole není na tomto místě očekávána.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
    |   NULL_LITERAL EOF {notifyErrorListeners($NULL_LITERAL, "Hodnota null není na tomto místě očekávána.", "logická hodnota", null);}
    ;

solo_StringExpression
    :	string_Expression EOF
    |	numericOrString_Expression EOF
    |	unknownTypeValue EOF
    |   boolean_Expression EOF {ruleNotifyErrorListeners("Logická hodnota není na tomto místě očekávána.", "řetězcová hodnota", $boolean_Expression.start, $boolean_Expression.stop);}
    |   numeric_Expression EOF {ruleNotifyErrorListeners("Číselná hodnota není na tomto místě očekávána.", "řetězcová hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
    |   array_Expression EOF {ruleNotifyErrorListeners("Hodnota v podobě pole není na tomto místě očekávána.", "řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
    |   NULL_LITERAL EOF {notifyErrorListeners($NULL_LITERAL, "Hodnota null není na tomto místě očekávána.", "řetězcová hodnota", null);}
    ;

solo_Expression
    :
	    expression EOF
    ;
// ***********************************************************************************
// ******************************** END OF SOLO RULES ********************************
// ***********************************************************************************

constant
    :	constant_Number_Signed
    |	CONSTANT_BOOLEAN
    |	constant_String
    ;

constant_Number returns [Double value]
    :	CONSTANT_FLOATING_POINT {$value = Double.valueOf($CONSTANT_FLOATING_POINT.text);}
    |	CONSTANT_INTEGER {$value = Double.valueOf($CONSTANT_INTEGER.text);}
    |   ERROR_CONSTANT_FLOATING_POINT_WITHOUT_FLOATING_PART {
            notifyErrorListeners($ERROR_CONSTANT_FLOATING_POINT_WITHOUT_FLOATING_PART, "Desetinnému číslu chybí část za desetinnou tečkou.", "číslo", null);
            $value = null;
        }
    ;

constant_Number_Signed
    :
        unaryPlusOrMinusOperator? constant_Number
    ;

constant_String
    :   CONSTANT_STRING arrayLookup_BracketsPart? {
            if ($arrayLookup_BracketsPart.ctx != null) {
                String stringWithoutQuotes = $CONSTANT_STRING.text.substring(1, $CONSTANT_STRING.text.length()-1);
                int stringLength = unescapeJavaString(stringWithoutQuotes).length();
                if (stringLength == 0) {
                    String msg = "Do prázdné řetězcové hodnoty se není možné odkazovat.";
                    ruleNotifyErrorListeners(msg, null, $arrayLookup_BracketsPart.start, $arrayLookup_BracketsPart.stop);
                } else if ($arrayLookup_BracketsPart.value != null && $arrayLookup_BracketsPart.value > stringLength - 1) {
                    String msg = "Index odkazující do řetězcové hodnoty má vyšší hodnotu, než její délka dovoluje.";
                    if (stringLength == 1) {
                        msg += "\nPro získání jednoho znaku z dané řetězcové hodnoty použijte index 0.";
                    } else {
                        msg += "\nPro získání jednoho znaku z dané řetězcové hodnoty použijte index v rozmezí 0 až " + (stringLength-1) + ".";
                    }
                    notifyErrorListenersByIndex(msg, null, $arrayLookup_BracketsPart.start.getStartIndex()+1, $arrayLookup_BracketsPart.stop.getStopIndex()-1, $arrayLookup_BracketsPart.start.getLine(), $arrayLookup_BracketsPart.start.getCharPositionInLine()+1);
                }
            }
        }
    |   ERROR_CONSTANT_STRING_EOF {
            java.util.regex.Matcher matcher = BAD_ESCAPE_PATTERN.matcher($ERROR_CONSTANT_STRING_EOF.text);
            while (matcher.find()) {
                int parentStart = $ERROR_CONSTANT_STRING_EOF.getStartIndex();
                notifyErrorListenersByIndex("Neplatná řetězcová escape sekvence '" + matcher.group() + "'.\nPovolené escape sekvence jsou: \\t, \\b, \\n, \\r, \\f, \\\", \\', \\\\", null, parentStart + matcher.start(), parentStart + matcher.end()-1, $ERROR_CONSTANT_STRING_EOF.getLine(), $ERROR_CONSTANT_STRING_EOF.getCharPositionInLine() + matcher.start());
            }
            notifyErrorListeners($ERROR_CONSTANT_STRING_EOF, "Řetězcové hodnotě chybí uzavírací uvozovka.", "'\"'", null);
        }
    |   ERROR_CONSTANT_STRING_BAD_ESCAPE arrayLookup_BracketsPart? {
            java.util.regex.Matcher matcher = BAD_ESCAPE_PATTERN.matcher($ERROR_CONSTANT_STRING_BAD_ESCAPE.text);
            while (matcher.find()) {
                int parentStart = $ERROR_CONSTANT_STRING_BAD_ESCAPE.getStartIndex();
                notifyErrorListenersByIndex("Neplatná řetězcová escape sekvence '" + matcher.group() + "'.\nPovolené escape sekvence jsou: \\t, \\b, \\n, \\r, \\f, \\\", \\', \\\\", null, parentStart + matcher.start(), parentStart + matcher.end()-1, $ERROR_CONSTANT_STRING_BAD_ESCAPE.getLine(), $ERROR_CONSTANT_STRING_BAD_ESCAPE.getCharPositionInLine() + matcher.start());
            }
        }
    ;

constant_Array
    :   LBRK constant_Array_RepeatingPart* (
            expression (
                RBRK
            |   EOF {notifyErrorListeners($LBRK, "Chybí ukončovací hranatá závorka.", "']'", null);}
            )
        |   RBRK {
                if ($constant_Array_RepeatingPart.ctx != null) {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu prvků pole.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $RBRK);
                }
            }
        |   EOF {
                Token token = $constant_Array_RepeatingPart.stop;
                if (token == null) {
                    token = $LBRK;
                }
                if ($constant_Array_RepeatingPart.ctx != null) {
                    notifyErrorListenersOnPointAfter("Nalezeno prázdné místo ve výčtu prvků pole.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", token);
                }
                notifyErrorListeners($LBRK, "Chybí ukončovací hranatá závorka.", "']'", null);
            }
        )
    ;
constant_Array_RepeatingPart
    :   expression COMMA
    |   COMMA {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu prvků pole.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $COMMA);}
    ;

arrayLookup_BracketsPart returns [Integer value]
    :   LBRK arrayIntegerIndex_Expression (
            RBRK {$value = $arrayIntegerIndex_Expression.value;}
        |   arrayLookup_BracketsRepeatErrorPart RBRK {
                ruleNotifyErrorListeners("Index pole může tvořit jen jedno celé číslo. Pro indexaci vícerozměrného pole použijte syntaxi 'pole[x][y]'.", null, $arrayLookup_BracketsRepeatErrorPart.start, $arrayLookup_BracketsRepeatErrorPart.stop);
                $value = null;
            }
        |   EOF {
                notifyErrorListeners($LBRK, "Chybí ukončovací hranatá závorka.", "']'", null);
                $value = null;
            }
        )
    |   LBRK RBRK {
            notifyErrorListenersOnPointAfter("Chybí číselné určení (index) prvku pole.", "číselná hodnota", $LBRK);
            $value = null;
        }
    |   LBRK EOF {
            notifyErrorListenersOnPointAfter("Chybí číselné určení (index) prvku pole.", "číselná hodnota", $LBRK);
            notifyErrorListeners($LBRK, "Chybí ukončovací hranatá závorka.", "']'", null);
            $value = null;
        }
    //|   arrayIntegerIndex_Expression RBRK {
    //        notifyErrorListeners($RBRK, "Chybí otevírací hranatá závorka.", "'['", null);
    //        $value = null;
    //    }
    ;
arrayLookup_BracketsRepeatErrorPart
    :   COMMA
    |   (COMMA arrayIntegerIndex_Expression)+ COMMA?
    ;

arrayIntegerIndex_Expression returns [Integer value]
    :	numericOrString_Expression {$value = null;}
    |	numeric_Expression {
            if ($numeric_Expression.value != null) {
                if ($numeric_Expression.value < 0 && $numeric_Expression.value % 1 != 0) {
                    ruleNotifyErrorListeners("K prvkům pole může být přistupováno pouze pomocí kladných celých čísel (nalezeno záporné desetinné číslo).", null, $numeric_Expression.start, $numeric_Expression.stop);
                } else if ($numeric_Expression.value < 0) {
                    ruleNotifyErrorListeners("K prvkům pole může být přistupováno pouze pomocí kladných celých čísel (nalezeno záporné číslo).", null, $numeric_Expression.start, $numeric_Expression.stop);
                } else if ($numeric_Expression.value % 1 != 0) {
                    ruleNotifyErrorListeners("K prvkům pole může být přistupováno pouze pomocí kladných celých čísel (nalezeno desetinné číslo).", null, $numeric_Expression.start, $numeric_Expression.stop);
                }
                $value = $numeric_Expression.value.intValue();
            } else {
                $value = null;
            }
        }
    |	unknownTypeValue {$value = null;}
    |   boolean_Expression {
            ruleNotifyErrorListeners("Logická hodnota není na tomto místě očekávána.", "číselná hodnota", $boolean_Expression.start, $boolean_Expression.stop);
            $value = null;
        }
    |   string_Expression {
            ruleNotifyErrorListeners("Řetězcová hodnota není na tomto místě očekávána.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);
            $value = null;
        }
    |   array_Expression {
            ruleNotifyErrorListeners("Hodnota v podobě pole není na tomto místě očekávána.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
            $value = null;
        }
    |   NULL_LITERAL {
            notifyErrorListeners($NULL_LITERAL, "Hodnota null není na tomto místě očekávána.", "číselná hodnota", null);
            $value = null;
        }
    ;

variableOrProperty
    :	variableOrProperty_WithoutConstants
    |   (	constant_String
	    |   LPAR string_Expression RPAR arrayLookup_BracketsPart?
		|	LPAR numericOrString_Expression RPAR arrayLookup_BracketsPart?
		)
		DOT variableOrProperty_AfterDotPart? variableOrProperty_RightPart* {
		    if ($variableOrProperty_AfterDotPart.ctx == null) {
		        notifyErrorListenersOnPointAfter("Chybí identifikátor v tečkové notaci.", "identifikátor", $DOT);
		    }
		}
	|	array_Expression variableOrProperty_RightPart+
    |	LPAR variableOrProperty (
            RPAR variableOrProperty_RightPart*
        |	EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    ;

variableOrProperty_AfterDotPart
    :	variable_Identifier
    |   constant_Number {ruleNotifyErrorListeners("Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru.", null, $constant_Number.start, $constant_Number.stop);}
    |   cnsnt=(CONSTANT_BOOLEAN | NULL_LITERAL) {notifyErrorListeners($cnsnt, "'" + $cnsnt.text + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.", null, null);}
    ;

variableOrProperty_RightPart
    :	arrayLookup_BracketsPart
    |	DOT variableOrProperty_AfterDotPart
    |	DOT {notifyErrorListenersOnPointAfter("Chybí identifikátor v tečkové notaci.", "identifikátor", $DOT);}
    ;

variableOrProperty_WithoutConstants
    :
        variable_Identifier variableOrProperty_RightPart*
    |   constant_Number variableOrProperty_RightPart+ {ruleNotifyErrorListeners("Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru.", null, $constant_Number.start, $constant_Number.stop);}
    |   cnsnt=(CONSTANT_BOOLEAN | NULL_LITERAL) variableOrProperty_RightPart+ {notifyErrorListeners($cnsnt, "'" + $cnsnt.text + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.", null, null);}
    ;

variable_Identifier
    :   INDENTIFIER
    |   ERROR_INDENTIFIER_INSIDE {notifyErrorListeners($ERROR_INDENTIFIER_INSIDE, "Název proměnné může obsahovat jen písmena bez diakritiky, číslice, podtržítka nebo znaky dolaru.", null, null);}
    |   ERROR_INDENTIFIER_BEGINNING {notifyErrorListeners($ERROR_INDENTIFIER_BEGINNING, "Název proměnné může začínat jen písmenem bez diakritiky, podtržítkem nebo znakem dolaru.", null, null);}
    |   RESERVED_WORD {notifyErrorListeners($RESERVED_WORD, "'" + $RESERVED_WORD.text + "' je rezervované klíčové slovo a nemůže být použito jako název proměnné.", null, null);}
    ;

functionReturnValue
    :   variableOrProperty LPAR (
            (function_Parentheses_RepeatingPart* expression)? (
                RPAR arrayLookup_BracketsPart* function_suffix?
            |   EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
            )
        |   function_Parentheses_RepeatingPart+ (
                RPAR arrayLookup_BracketsPart* function_suffix? {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $RPAR);}
            |   EOF {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $EOF);
                    notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
                }
            )
        )
    |	LPAR functionReturnValue (
            RPAR arrayLookup_BracketsPart* function_suffix? // (l()).l()
        |	EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    |   LPAR function_Parentheses_RepeatingPart+ expression? (
            RPAR arrayLookup_BracketsPart* function_suffix? {
                notifyErrorListenersOnPointBefore("Chybí identifikátor volané funkce.", "identifikátor", $LPAR);
                if ($expression.ctx == null) {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $RPAR);
                }
            }
        |   EOF {
                notifyErrorListenersOnPointBefore("Chybí identifikátor volané funkce.", "identifikátor", $LPAR);
                if ($expression.ctx == null) {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $EOF);
                }
                notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
            }
        )
    ;

function_WithoutConstants
    :   variableOrProperty_WithoutConstants (
            LPAR (function_Parentheses_RepeatingPart* expression)? (
                RPAR arrayLookup_BracketsPart* function_suffix?
            |   EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
            )
        |   // above (= this alternative block) is optional
        |   LPAR function_Parentheses_RepeatingPart+ (
                RPAR arrayLookup_BracketsPart* function_suffix? {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $RPAR);}
            |   EOF {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $EOF);
                    notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
                }
            )
        )
    |   LPAR function_Parentheses_RepeatingPart* expression? (
            RPAR arrayLookup_BracketsPart* function_suffix? {
                if ($function_Parentheses_RepeatingPart.ctx != null || $expression.ctx != null) {
                    notifyErrorListenersOnPointBefore("Chybí identifikátor volané funkce.", "identifikátor", $LPAR);
                } else {
                    notifyErrorListenersOnPointBefore("Chybí identifikátor volané procedury.", "identifikátor", $LPAR);
                }
                if ($expression.ctx == null && $function_Parentheses_RepeatingPart.ctx != null) {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $RPAR);
                }
            }
        |   EOF {
                if ($function_Parentheses_RepeatingPart.ctx != null || $expression.ctx != null) {
                    notifyErrorListenersOnPointBefore("Chybí identifikátor volané funkce.", "identifikátor", $LPAR);
                } else {
                    notifyErrorListenersOnPointBefore("Chybí identifikátor volané procedury.", "identifikátor", $LPAR);
                }
                if ($expression.ctx == null && $function_Parentheses_RepeatingPart.ctx != null) {
                    notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $EOF);
                }
                notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
            }
        )
    ;

function_Parentheses_RepeatingPart
    :   expression COMMA
    |   COMMA {notifyErrorListenersOnPointBefore("Nalezeno prázdné místo ve výčtu parametrů volané funkce.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $COMMA);}
    ;

function_suffix
    :
        DOT function_WithoutConstants? {
          if ($function_WithoutConstants.ctx == null) {
              notifyErrorListenersOnPointAfter("Chybí identifikátor v tečkové notaci.", "identifikátor", $DOT);
          }
      }
    ;

unknownTypeValue
    :	functionReturnValue
    |	variableOrProperty
    |   LPAR EOF {
            notifyErrorListenersOnPointAfter("Chybí hodnota uvnitř závorky.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $LPAR);
            notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
        }
    |   LPAR RPAR {notifyErrorListenersOnPointAfter("Chybí hodnota uvnitř závorek.", "logická hodnota, řetězcová hodnota, číselná hodnota, hodnota v podobě pole", $LPAR);}
    ;

expression
    :
        // This order is needed to respect throughout the grammar!
        // However, longestOf is sometimes needed as:
        // (String || NumericOrString) & Boolean expressions can still colide:
        //      - "" = "" a+a = ""
        //      - a+a = ""
        //      - true + ""
        // StringOrNumeric & Numeric expressions can still colide:
        //	- prom + 1 - 1 -> here Numeric should be first
        //	- 1 + 1 + prom -> here StringOrNumeric should be first
        boolean_Expression
    |	string_Expression
    |	numericOrString_Expression
    |	numeric_Expression
    |	unknownTypeValue
    |	array_Expression
    |   NULL_LITERAL
    ;

array_Expression
    :	constant_Array
    |	LPAR array_Expression (
            RPAR
        |	EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    ;

boolean_Expression
    :
        conditionalOr_Expression
    ;

numeric_Expression returns [Double value]
    :
        additive_Expression {$value = $additive_Expression.value;}
    ;

// todo prevencne:
//  - zase procistit a zrychlit uzavorkovanim opakujicich se pravidel
//  - projdi vzdycky findem tenhle regex (najde kde jsem omylem povolil prazdny retez): \(\s*\|
//  - odkomentuj ambiguous vetve (napr list of constants)
// todo az budu mit hotovy i parsing promennych do jejich typů, dát hint ze v PSD nejde cislo konvertovat do logicke hodnoty (if (1), 1 & 2, ...)
// todo az bude antlr schopny resit vzajemne rekurze, tak pridat hint pri dvou hodnotach bez operatoru (do unknown value - pujdou tam vsechny?)


string_Expression
	:	(   numeric_Expression PLUS atomic_String_Expression
	    |   numericOrString_Expression PLUS (atomic_String_Expression | unary_Boolean_Expression | NULL_LITERAL)
	    |   string_Expression_InCommon
	    ) string_Expression_RightPart*
    ;

string_Expression_Without_Numeric_And_NumericOrString_Prefix
	:	(   numericOrString_Expression_Without_Numeric_Prefix PLUS (atomic_String_Expression | unary_Boolean_Expression | NULL_LITERAL)
        |   string_Expression_InCommon
        ) string_Expression_RightPart*
    ;

string_Expression_InCommon
    :   atomic_String_Expression
    |	(unary_Boolean_Expression | NULL_LITERAL) PLUS (atomic_String_Expression | numericOrString_Expression_WithParetheses | unknownTypeValue)
    |	unknownTypeValue PLUS (atomic_String_Expression | unary_Boolean_Expression | NULL_LITERAL)
	|   PLUS atomic_String_Expression {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota, číselná hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);}
    ;

    /**
     * Used after we know for sure that the part before this is a string.
     * <p>
     * @return
     */
string_Expression_RightPart
	:   PLUS (
	        atomic_String_Expression
        |	numericOrString_Expression_WithParetheses
        |	unary_Boolean_Expression
        |	multiplicative_Expression
        |	unknownTypeValue
        |   NULL_LITERAL
        // wrong combinations follow
        |   array_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (pravá strana operátoru '" + $PLUS.text + "') nelze přímo spojit s řetězcovou hodnotou.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", "řetězcová hodnota, číselná hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $array_Expression.start, $array_Expression.stop);}
        |   {notifyErrorListenersOnPointAfter("Chybí hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota, číselná hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);}
        )
    ;

atomic_String_Expression
	:	constant_String
    |	LPAR numericOrString_Expression RPAR arrayLookup_BracketsPart // this has to be a plain String because of the array lookup
    |	LPAR string_Expression (
            RPAR arrayLookup_BracketsPart?
        |	EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    ;

numericOrString_Expression
	:	numeric_Expression PLUS (
	        (numericOrString_Expression_WithParetheses | unknownTypeValue)
	    |   {notifyErrorListenersOnPointAfter("Chybí hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota", $PLUS);}
	    // wrong combinations follow
        |   unary_Boolean_Expression {ruleNotifyErrorListeners("Číselnou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s logickou hodnotou (pravá označená strana).\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran operátoru by pak ale musela být řetězcová hodnota.", "číselná hodnota, řetězcová hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners("Číselnou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou v podobě pole (pravá označená strana).\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na pravé straně by pak ale musela být řetězcová hodnota.", "číselná hodnota, řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, "Číselnou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou null (pravá označená strana)\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran operátoru by pak ale musela být řetězcová hodnota.", "číselná hodnota, řetězcová hodnota", null);}
	    ) numericOrString_Expression_RightPart*
    |	numericOrString_Expression_Without_Numeric_Prefix
    ;

numericOrString_Expression_Without_Numeric_Prefix
	:	unknownTypeValue numericOrString_Expression_RightPart+
	|   PLUS unknownTypeValue numericOrString_Expression_RightPart* // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);
        }
    |	numericOrString_Expression_WithParetheses numericOrString_Expression_RightPart*
    |   PLUS numericOrString_Expression_RightPart* {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS); notifyErrorListenersOnPointAfter("Chybí hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);}  // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
    // wrong combinations follow
    |   PLUS multiplicative_Expression numericOrString_Expression_RightPart* // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota", $PLUS);
        }

    |   unary_Boolean_Expression PLUS (
            multiplicative_Expression {ruleNotifyErrorListeners("Logickou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s číselnou hodnotou (pravá označená strana).\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran by pak ale musela být řetězcová hodnota.", "řetězcová hodnota, nebo číselná hodnota ne levé straně", $multiplicative_Expression.start, $multiplicative_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($PLUS, "Logické hodnoty (levá i pravá strana operátoru '" + $PLUS.text + "') nelze vzájemně sčítat.\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran by pak ale musela být řetězcová hodnota.", "'||', '&&', '=', '!=', nebo řetězcová hodnota na pravé či levé straně", null);}
        |   array_Expression {ruleNotifyErrorListeners("Logickou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou v podobě pole (pravá strana operátoru).\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na pravé straně by pak ale musela být řetězcová hodnota.", "řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, "Logickou hodnotu (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou null (pravá označená strana)\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran operátoru by pak ale musela být řetězcová hodnota.", "řetězcová hodnota", null);}
        |   {notifyErrorListenersOnPointAfter("Chybí řetězcová hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota", $PLUS);}
        ) numericOrString_Expression_RightPart*
    |   PLUS unary_Boolean_Expression numericOrString_Expression_RightPart* {notifyErrorListenersOnPointBefore("Chybí řetězcová hodnota na levé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota", $PLUS);}

    |   array_Expression PLUS (
            multiplicative_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s číselnou hodnotou (pravá označená strana).\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.", null, $PLUS, $multiplicative_Expression.stop);}
        |   unary_Boolean_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s logickou hodnotou (pravá označená strana).", null, $PLUS, $unary_Boolean_Expression.stop);}
        |   atomic_String_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze přímo spojit s řetězcovou hodnotou (pravá označená strana).\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", null, $PLUS, $atomic_String_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners("Hodnoty v podobě pole (levá i pravá strana operátoru '" + $PLUS.text + "') nelze vzájemně sčítat.\nPokud chcete sčítat jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, $PLUS, $array_Expression.stop);}
        |   unknownTypeValue {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze přímo použít pro sčítání ani přímo spojit s řetězcovou hodnotou.\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", null, $PLUS, $unknownTypeValue.stop);}
        |   numericOrString_Expression_WithParetheses {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze přímo použít pro sčítání ani přímo spojit s řetězcovou hodnotou.\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", null, $PLUS, $numericOrString_Expression_WithParetheses.stop);}
        |   NULL_LITERAL {notifyErrorListeners($PLUS, "Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou null (pravá označená strana).", "'=', '!='", null);}
        |   {
                notifyErrorListeners($PLUS, "Hodnotu v podobě pole (levá strana operátoru '" + $PLUS.text + "') nelze přímo použít pro sčítání ani přímo spojit s řetězcovou hodnotou.\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", "'=', '!='", null);
                notifyErrorListenersOnPointAfter("Chybí hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota, číselná hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);
            }
        ) numericOrString_Expression_RightPart*
    |   PLUS array_Expression numericOrString_Expression_RightPart* // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota, číselná hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);
            notifyErrorListeners($PLUS, "Hodnotu v podobě pole (pravá strana operátoru '" + $PLUS.text + "') nelze přímo použít pro sčítání ani přímo spojit s řetězcovou hodnotou.\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", "'=', '!='", null);
        }

    |   PLUS numericOrString_Expression_WithParetheses numericOrString_Expression_RightPart* // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);
        }

    |   NULL_LITERAL PLUS (
            multiplicative_Expression {ruleNotifyErrorListeners("Hodnotu null (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s číselnou hodnotou (pravá označená strana).\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran by pak ale musela být řetězcová hodnota.", "řetězcová hodnota", $multiplicative_Expression.start, $multiplicative_Expression.stop);}
        |   unary_Boolean_Expression {ruleNotifyErrorListeners("Hodnotu null (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s logickou hodnotou (pravá označená strana).\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran by pak ale musela být řetězcová hodnota.", "řetězcová hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners("Hodnotu null (levá strana operátoru '" + $PLUS.text + "') nelze sčítat s hodnotou v podobě pole (pravá označená strana).", "řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, "Hodnoty null (levá i pravá strana operátoru '" + $PLUS.text + "') nelze vzájemně sčítat.\nOperátor '" + $PLUS.text + "' lze použít také pro zřetězení textu, na jedné ze stran by pak ale musela být řetězcová hodnota.", "řetězcová hodnota", null);}
        |   {notifyErrorListenersOnPointAfter("Chybí řetězcová hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota", $PLUS);}
        ) numericOrString_Expression_RightPart*
    |   PLUS NULL_LITERAL numericOrString_Expression_RightPart*
        {
            notifyErrorListenersOnPointBefore("Chybí řetězcová hodnota na levé straně operátoru '" + $PLUS.text + "'.", "řetězcová hodnota", $PLUS);
        }
    ;

numericOrString_Expression_WithParetheses
	:   LPAR numericOrString_Expression (
	        RPAR
        |   EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    ;

numericOrString_Expression_RightPart
	:	PLUS (
            numericOrString_Expression_WithParetheses
        |	multiplicative_Expression
        |	unknownTypeValue
        // wrong combinations follow
        |   array_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (pravá strana operátoru '" + $PLUS.text + "') nelze přímo použít pro sčítání ani přímo spojit s řetězcovou hodnotou.\nPokud chcete přičítat číselnou hodnotu k jednotlivým prvkům pole, je nutné použít cyklus.\nPokud chcete k řetězcové hodnotě připojit výpis jednotlivých prvků pole, je nutné použít vlastní algoritmus s cyklem.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $array_Expression.start, $array_Expression.stop);}
        )
    |   PLUS {notifyErrorListenersOnPointAfter("Chybí hodnota na pravé straně operátoru '" + $PLUS.text + "'.", "číselná hodnota, řetězcová hodnota, konstantní logická hodnota, uzávorkovaná logická hodnota, hodnota null", $PLUS);}
    ;

conditionalOr_Expression
	:
		conditionalOr_Expression_LeftPart conditionalOr_Expression_RightPart*
    ;

/**
 * Used to determine a valid expression of its type, even if used solely
 * <p>
 * @return
 */
conditionalOr_Expression_LeftPart
	:	conditionalAnd_Expression
    |	unknownTypeValue? OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
            if ($unknownTypeValue.ctx == null) {
                notifyErrorListenersOnPointBefore("Chybí logická hodnota na levé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
            }
        } (
            (conditionalAnd_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.";
                notifyErrorListenersOnPointAfter(msg, "logická hodnota", $OR);
            }
        // wrong combinations follow
        |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické OR (nebo)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        )
    // wrong combinations follow
    |   numeric_Expression OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (conditionalAnd_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   string_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'+'", null);}
        |   array_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   NULL_LITERAL {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota null.", null, null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
            }
        )
    |   string_Expression OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (conditionalAnd_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\nPokud chcete k řetězcové hodnotě připojit výsledek operace 'logické OR (nebo)', vložte tuto operaci do závorky.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'+'", null);}
        |   string_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
            }
        )
    |   array_Expression OR {
           String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
       } (
           (conditionalAnd_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické OR (nebo)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
       |   numeric_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", null, null);}
       |   string_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", null, null);}
       |   array_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické OR (nebo)' provést pro jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, null);}
       |   numericOrString_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", null, null);}
       |   NULL_LITERAL {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
       |   {
               ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.", "logická hodnota", $array_Expression.start, $array_Expression.stop);
               notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
           }
       )
    |   numericOrString_Expression OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (conditionalAnd_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   string_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   NULL_LITERAL {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
            }
        )
    |   NULL_LITERAL OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (conditionalAnd_Expression | unknownTypeValue) {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        |   numeric_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", null, null);}
        |   string_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", "'=', '!='", null);}
        |   numericOrString_Expression {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($OR, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.", "logická hodnota", $OR);
            }
        )
    ;

conditionalOr_Expression_RightPart
    :   OR {
            String errorHeader = "Operátor '" + $OR.text + "' (logické OR (nebo)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (conditionalAnd_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí logická hodnota na pravé straně operátoru '" + $OR.text + "'.";
                notifyErrorListenersOnPointAfter(msg, "logická hodnota", $OR);
            }
        // wrong combinations follow
        |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické OR (nebo)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        )
    ;

conditionalAnd_Expression
	:
        conditionalAnd_Expression_LeftPart conditionalAnd_Expression_RightPart*
    ;

conditionalAnd_Expression_LeftPart
	:	equality_Expression
    |	unknownTypeValue? AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
            if ($unknownTypeValue.ctx == null) {
                notifyErrorListenersOnPointBefore("Chybí logická hodnota na levé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
            }
        } (
            (equality_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.";
                notifyErrorListenersOnPointAfter(msg, "logická hodnota", $AND);
            }
        // wrong combinations follow
        |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické AND (a zároveň)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        )
    // wrong combinations follow
    |   numeric_Expression AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (equality_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   string_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'+'", null);}
        |   array_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   NULL_LITERAL {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota null.", null, null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
            }
        )
    |   string_Expression AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (equality_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\nPokud chcete k řetězcové hodnotě připojit výsledek operace 'logické AND (a zároveň)', vložte tuto operaci do závorky.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'+'", null);}
        |   string_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
            }
        )
    |   array_Expression AND {
           String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
       } (
           (equality_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické AND (a zároveň)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
       |   numeric_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", null, null);}
       |   string_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", null, null);}
       |   array_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické AND (a zároveň)' provést pro jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, null);}
       |   numericOrString_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", null, null);}
       |   NULL_LITERAL {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
       |   {
               ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.", "logická hodnota", $array_Expression.start, $array_Expression.stop);
               notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
           }
       )
    |   numericOrString_Expression AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (equality_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   numeric_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   string_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   numericOrString_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '>', '<', '>=', '<=', '+', '-', '*', '/', '//', '%'", null);}
        |   NULL_LITERAL {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
            }
        )
    |   NULL_LITERAL AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (equality_Expression | unknownTypeValue) {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        |   numeric_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", null, null);}
        |   string_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", "'=', '!='", null);}
        |   numericOrString_Expression {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($AND, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);
                notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.", "logická hodnota", $AND);
            }
        )
    ;

conditionalAnd_Expression_RightPart
    :   AND {
            String errorHeader = "Operátor '" + $AND.text + "' (logické AND (a zároveň)) lze použít pouze v kombinaci s logickými hodnotami.";
        } (
            (equality_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí logická hodnota na pravé straně operátoru '" + $AND.text + "'.";
                notifyErrorListenersOnPointAfter(msg, "logická hodnota", $AND);
            }
        // wrong combinations follow
        |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci 'logické AND (a zároveň)' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        )
    ;

equality_Expression
	:
        equality_Expression_LeftPart equality_Expression_RightPart*
    ;

equality_Expression_LeftPart
	:   relational_Expression
	|	numeric_Expression op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
            String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
	    } (
	        (numericOrString_Expression | numeric_Expression | unknownTypeValue)
	    |   {
	            String msg = "Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
	            notifyErrorListenersOnPointAfter(msg, "číselná hodnota", $op);
	        }
	    // wrong combinations follow
	    |   relational_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $relational_Expression.start, $relational_Expression.stop);}
	    |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);}
	    |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
	    |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná.\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
	    )
	|   string_Expression op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
	    } (
	        (string_Expression | numericOrString_Expression | unknownTypeValue | NULL_LITERAL)
	    |   {
	            String msg = "Chybí řetězcová hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
	            notifyErrorListenersOnPointAfter(msg, "řetězcová hodnota, hodnota null", $op);
	        }
	    // wrong combinations follow
	    |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "řetězcová hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
	    |   relational_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.\nPokud chcete k řetězcové hodnotě připojit výsledek porovnání " + operation + ", vložte tuto operaci do závorky.", "řetězcová hodnota", $relational_Expression.start, $relational_Expression.stop);}
	    |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
	    )
    |	unknownTypeValue op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
        } (
            (relational_Expression | string_Expression | numericOrString_Expression | numeric_Expression | unknownTypeValue | NULL_LITERAL)
        |   {
                String msg = "Chybí hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
                notifyErrorListenersOnPointAfter(msg, "číselná hodnota, řetězcová hodnota, logická hodnota, hodnota null", $op);
            }
	    // wrong combinations follow
	    |   array_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota, řetězcová hodnota, logická hodnota", $array_Expression.start, $array_Expression.stop);}
        )
    |	numericOrString_Expression op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
        } (
            (string_Expression | numericOrString_Expression | numeric_Expression | unknownTypeValue | NULL_LITERAL)
        |   {
                String msg = "Chybí hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
                notifyErrorListenersOnPointAfter(msg, "číselná hodnota, řetězcová hodnota, hodnota null", $op);
            }
	    // wrong combinations follow
	    |   relational_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná/řetězcová hodnota", $relational_Expression.start, $relational_Expression.stop);}
	    |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota číselná/řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná/řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        )
    |   op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
        } (
            string_Expression {notifyErrorListenersOnPointBefore("Chybí řetězcová hodnota na levé straně operátoru '" + $op.text + "'.", "řetězcová hodnota, hodnota null", $op);}
        |   relational_Expression {notifyErrorListenersOnPointBefore("Chybí logická hodnota na levé straně operátoru '" + $op.text + "'.", "logická hodnota", $op);}
        |   numeric_Expression {notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);}
        |   numericOrString_Expression {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota, řetězcová hodnota, hodnota null", $op);}
        |   unknownTypeValue {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota, řetězcová hodnota, logická hodnota, hodnota null", $op);}
        |   array_Expression {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $op.text + "'.", "hodnota null", $op);}
        |   NULL_LITERAL {notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $op.text + "'.", "řetězcová hodnota, hodnota v podobě pole, hodnota null", $op);}
        |   {
                notifyErrorListenersOnPointBefore("Chybí hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota, řetězcová hodnota, logická hodnota", $op);
                String msg = "Chybí hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
                notifyErrorListenersOnPointAfter(msg, "číselná hodnota, řetězcová hodnota, logická hodnota", $op);
            }
        )
    // wrong combinations follow
    |	array_Expression op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
        } (
            NULL_LITERAL
        |   numeric_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $op.text + "') nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
        |   relational_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $op.text + "') nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $op.text + "') nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        |   array_Expression {notifyErrorListeners($op, "Hodnoty v podobě pole (levá i pravá strana operátoru '" + $op.text + "') nelze přímo vzájemně porovnávat.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole mezi sebou, je nutné použít cyklus.", null, null);}
        |   unknownTypeValue {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $op.text + "') nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota, řetězcová hodnota, logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners("Hodnotu v podobě pole (levá strana operátoru '" + $op.text + "') nelze přímo použít pro porovnání.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota, řetězcová hodnota", $array_Expression.start, $array_Expression.stop);}
        |   {
                String msg = "Chybí hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
                notifyErrorListenersOnPointAfter(msg, "hodnota null", $op);
            }
        )
	|   NULL_LITERAL op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
	    } (
	        (string_Expression | array_Expression | numericOrString_Expression | unknownTypeValue | NULL_LITERAL)
	    |   {
	            String msg = "Chybí hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
	            notifyErrorListenersOnPointAfter(msg, "řetězcová hodnota, hodnota v podobě pole, hodnota null", $op);
	        }
	    // wrong combinations follow
	    |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "řetězcová hodnota, hodnota v podobě pole", $numeric_Expression.start, $numeric_Expression.stop);}
	    |   relational_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "řetězcová hodnota, hodnota v podobě pole", $relational_Expression.start, $relational_Expression.stop);}
	    )
    ;

equality_Expression_RightPart
	:   op=(EQUAL | NOTEQUAL) {
            String operation = "chyba!";
            if ($op.getType() == EQUAL) {
                operation = "rovnosti";
            } else if ($op.getType() == NOTEQUAL) {
                operation = "nerovnosti";
            }
	        String errorHeader = "Operátor " + operation + " ('" + $op.text + "') lze použít pouze v kombinaci se stejnými typy hodnot na obou stranách operátoru.";
	    } (
	        (relational_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí logická hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == EQUAL && _input.LA(1) == EQUAL) {
                    msg+= "\nPS: v PS Diagramu je pro operaci porovnání rovnosti používán jediný znak '='";
                }
                notifyErrorListenersOnPointAfter(msg, "logická hodnota", $op);
            }
        // wrong combinations follow
        |   numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota číselná.", "logická hodnota", $numeric_Expression.start, $numeric_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "logická hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete porovnání " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota číselná/řetězcová.", "logická hodnota", $numericOrString_Expression.start, $numericOrString_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota null.", "logická hodnota", null);}
        )
    ;

relational_Expression
    :
        relational_Expression_LeftPart (op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            (numericOrString_Expression | numeric_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $relational_Expression_LeftPart.start, $relational_Expression_LeftPart.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "'||', '&&', '=', '!='", null);}
        |   string_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'+'", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota null.", null, null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $relational_Expression_LeftPart.start, $relational_Expression_LeftPart.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        ))*
    ;

relational_Expression_LeftPart
	:   unary_Boolean_Expression
	|   (	numericOrString_Expression
        |	numeric_Expression
        |	unknownTypeValue
        ) op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
                (numericOrString_Expression | numeric_Expression | unknownTypeValue)
            |   {
                    String msg = "Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.";
                    if ($op.getType() == LT && _input.LA(1) == LT || $op.getType() == GT && _input.LA(1) == GT) {
                        msg+= "\nPS: v PS Diagramu nelze přímo provádět bitové operace";
                    }
                    notifyErrorListenersOnPointAfter(msg, "číselná hodnota", $op);
                }
            // wrong combinations follow
            |   unary_Boolean_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
            |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);}
            |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci '" + operation + "' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
            |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
            )
    |   op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";

            String message = "Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.";
            if (_input.LT(-2) != null && ($op.getType() == LT || $op.getType() == GT) && _input.LA(-2) == EQUAL && _input.LT(-2).getText().equals("=")) { // valid only for single '=' equal symbol (not valid for '==')
                // check again if there is not any whitespace between the EQUAL and the BANG symbol
                int idx = $op.getStartIndex() - $op.text.length();
                if (idx > -1 && _input.getTokenSource().getInputStream().getText(new Interval(idx, idx)).equals("=")) {
                    message += "\nPS: nemáte na mysli operátor '" + $op.text + _input.LT(-2).getText() + "'?";
                }
            }
            notifyErrorListenersOnPointBefore(message, "číselná hodnota", $op);
        } (
            (numericOrString_Expression | numeric_Expression | unknownTypeValue)
        |   {
                String msg = "Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.";
                if ($op.getType() == LT && _input.LA(1) == LT || $op.getType() == GT && _input.LA(1) == GT) {
                    msg+= "\nPS: v PS Diagramu nelze přímo provádět bitové operace";
                }
                notifyErrorListenersOnPointAfter(msg, "číselná hodnota", $op);
            }
        // wrong combinations follow
        |   unary_Boolean_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
        |   string_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci '" + operation + "' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
        )
    |   string_Expression op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            (numericOrString_Expression | numeric_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\nPokud chcete k řetězcové hodnotě připojit výsledek porovnání operátoru '" + $op.text + "', vložte tuto operaci do závorky.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "'+'", null);}
        |   string_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $string_Expression.start, $string_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        )
    |   array_Expression op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            (numericOrString_Expression | numeric_Expression | unknownTypeValue) {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci '" + operation + "' provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   string_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", null, null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete operaci '" + operation + "' provést pro jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        )
    |   NULL_LITERAL op=(LE | GE | LT | GT) {
            String operation = "chyba!";
            if ($op.getType() == LE) {
                operation = "menší nebo rovno";
            } else if ($op.getType() == GE) {
                operation = "větší nebo rovno";
            } else if ($op.getType() == LT) {
                operation = "menší než";
            } else if ($op.getType() == GT) {
                operation = "větší než";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            (numericOrString_Expression | numeric_Expression | unknownTypeValue) {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   string_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'+', '=', '!='", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", "'=', '!='", null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        )
    ;

additive_Expression returns [Double value]
	:
        additive_Expression_LeftPart {$value = $additive_Expression_LeftPart.value;} (
			MINUS multiplicative_Expression {$value = eval($value, $MINUS.type, $multiplicative_Expression.value);}
		|	MINUS (
            	numericOrString_Expression_WithParetheses // parenthesis are needed because of the ordering, which is on the same level: 1 - 2 + pom + 1 <- the part after 1 is NumberOrString otherwise
            |	unknownTypeValue
			|	{notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);}
            ) {$value = null;}
		|   PLUS multiplicative_Expression {$value = eval($value, $PLUS.type, $multiplicative_Expression.value);}
        |   PLUS (numericOrString_Expression_Without_Numeric_Prefix | unknownTypeValue) MINUS {
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            } (
                (multiplicative_Expression | numericOrString_Expression_WithParetheses | unknownTypeValue)
            |   {notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);}
            // wrong combinations follow
            |   unary_Boolean_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
            |   atomic_String_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.\nČíselnou hodnotu však lze s řetězcovou hodnotou spojit operátorem '+'.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);}
            |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete číselnou hodnotu odčítat od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
            |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
            ) {$value = null;}
        // wrong combinations follow
        |   PLUS string_Expression_Without_Numeric_And_NumericOrString_Prefix MINUS {
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            } (
                multiplicative_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\nPokud chcete k řetězcové hodnotě připojit výsledek odčítání, vložte tento výpočet do závorky.", "'+'", null);}
            |   unary_Boolean_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.\nŘetězcovou hodnotu však lze s logickou hodnotou spojit operátorem '+'.", "'+'", null);}
            |   atomic_String_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
            |   array_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
            |   unknownTypeValue {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
            |   numericOrString_Expression_WithParetheses {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
            |   NULL_LITERAL {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
            |   {
                    ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $additive_Expression_LeftPart.start, $string_Expression_Without_Numeric_And_NumericOrString_Prefix.stop);
                    notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
                }
            ) {$value = null;}
        |   MINUS unary_Boolean_Expression {
                $value = null;
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
                ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
            }
        |   MINUS atomic_String_Expression {
                $value = null;
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
                ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.\nČíselnou hodnotu však lze s řetězcovou hodnotou spojit operátorem '+'.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
            }
        |   MINUS array_Expression {
                $value = null;
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
                ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete číselnou hodnotu odčítat od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
            }
        |   MINUS NULL_LITERAL {
                $value = null;
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
            }
        )*
    ;

additive_Expression_LeftPart returns [Double value]
	:   multiplicative_Expression {$value = $multiplicative_Expression.value;}
	|   (   numericOrString_Expression_Without_Numeric_Prefix // mutual left recursion otherwise + ambiguity in left vs right part (solution could be held by either left or right side with numeric prefix here)
        |	unknownTypeValue) MINUS {
                String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            } (
                multiplicative_Expression
            |	numericOrString_Expression_WithParetheses
            |	unknownTypeValue
            |   {notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);}
            // wrong combinations follow
            |   unary_Boolean_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
            |   atomic_String_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);}
            |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete odčítat číselnou hodnotu od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
            |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
            ) {$value = null;}
    |   MINUS (multiplicative_Expression | numericOrString_Expression_WithParetheses | unknownTypeValue) {$value = null; notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);}
    |   MINUS {$value = null; notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS); notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);}
    // wrong combinations follow
    |   unary_Boolean_Expression MINUS {
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            multiplicative_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "'||', '&&', '=', '!='", null);}
        |   atomic_String_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.\nLogickou hodnotu však lze s řetězcovou hodnotou spojit operátorem '+'.", "'+'", null);}
        |   array_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "'||', '&&', '=', '!=', '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "'+', nebo číselná hodnota na levé straně", null);}
        |   NULL_LITERAL {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota null.", null, null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            }
        ) {$value = null;}
    |   MINUS unary_Boolean_Expression {
            $value = null;
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
        }

    |   string_Expression_Without_Numeric_And_NumericOrString_Prefix MINUS {
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            multiplicative_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\nPokud chcete k řetězcové hodnotě připojit výsledek odčítání, vložte tento výpočet do závorky.", "'+'", null);}
        |   unary_Boolean_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.\nŘetězcovou hodnotu však lze s logickou hodnotou spojit operátorem '+'.", "'+'", null);}
        |   atomic_String_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $string_Expression_Without_Numeric_And_NumericOrString_Prefix.start, $string_Expression_Without_Numeric_And_NumericOrString_Prefix.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            }
        ) {$value = null;}
    |   MINUS atomic_String_Expression {
            $value = null;
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
        }

    |   array_Expression MINUS {
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            multiplicative_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete číselnou hodnotu odčítat od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   atomic_String_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", null, null);}
        |   array_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud od sebe chcete odčítat jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete číselnou hodnotu odčítat od jednotlivých prvků pole, je nutné použít cyklus.", null, null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete číselnou hodnotu odčítat od jednotlivých prvků pole, je nutné použít cyklus.", null, null);}
        |   NULL_LITERAL {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete odčítat číselnou hodnotu od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            }
        ) {$value = null;}
    |   MINUS array_Expression // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            $value = null;
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete odčítat číselnou hodnotu od jednotlivých prvků pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
        }

    |   NULL_LITERAL MINUS {
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            multiplicative_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", null, null);}
        |   unary_Boolean_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   atomic_String_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", "'=', '!='", null);}
        |   unknownTypeValue {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "'=', '!=' '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($MINUS, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            }
        ) {$value = null;}
    |   MINUS NULL_LITERAL // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            $value = null;
            String errorHeader = "Operátor '" + $MINUS.text + "' (odčítání) lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $MINUS.text + "'.", "číselná hodnota", $MINUS);
            notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
        }
    ;

multiplicative_Expression returns [Double value]
    :
        multiplicative_Expression_LeftPart {$value = $multiplicative_Expression_LeftPart.value;} (multiplicative_Expression_Operator multiplicative_Expression_RightPart[$multiplicative_Expression_Operator.op] {$value = eval($value, $multiplicative_Expression_Operator.op.getType(), $multiplicative_Expression_RightPart.value);})*
    ;

multiplicative_Expression_Operator returns [Token op]
    :
        operator=(MUL | DIV | FLOORDIV | MOD) {$op = $operator;}
    ;

multiplicative_Expression_RightPart [Token op] returns [Double value]
    :
        unary_Numeric_Expression {$value = $unary_Numeric_Expression.value;}
    |   (
            numericOrString_Expression_WithParetheses
        |   unknownTypeValue
        |   {notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.getText() + "'.", "číselná hodnota", $op);}
        ) {$value = null;}
    // wrong combinations follow
    |   unary_Boolean_Expression {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.getText() + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
        }
    |   atomic_String_Expression {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.getText() + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.\nČíselnou hodnotu však lze s řetězcovou hodnotou spojit operátorem '+'.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
        }
    |   array_Expression {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.getText() + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
        }
    |   NULL_LITERAL {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.getText() + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
        }
    ;

multiplicative_Expression_LeftPart_ValidLeft
    :   
        numericOrString_Expression_WithParetheses 
    |   unknownTypeValue
    ;

multiplicative_Expression_LeftPart_ValidRight
    :   
        unary_Numeric_Expression
    |	numericOrString_Expression_WithParetheses
    |	unknownTypeValue
    ;

multiplicative_Expression_LeftPart returns [Double value]
    :	unary_Numeric_Expression {$value = $unary_Numeric_Expression.value;}
    |   (   multiplicative_Expression_LeftPart_ValidLeft) multiplicative_Expression_Operator {
                $value = null;
                String operation = "chyba!";
                if ($multiplicative_Expression_Operator.op.getType() == MUL) {
                    operation = "násobení";
                } else if ($multiplicative_Expression_Operator.op.getType() == DIV) {
                    operation = "dělení";
                } else if ($multiplicative_Expression_Operator.op.getType() == FLOORDIV) {
                    operation = "celočíselné dělení";
                } else if ($multiplicative_Expression_Operator.op.getType() == MOD) {
                    operation = "modulo (zbytek po celočíselném dělení)";
                }
                String errorHeader = "Operátor '" + $multiplicative_Expression_Operator.op.getText() + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            } (
                multiplicative_Expression_LeftPart_ValidRight
            |   {notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $multiplicative_Expression_Operator.op.getText() + "'.", "číselná hodnota", $multiplicative_Expression_Operator.op);}
            // wrong combinations follow
            |   unary_Boolean_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
            |   atomic_String_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);}
            |   array_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
            |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
        )
    |   op=(MUL | DIV | FLOORDIV | MOD) (unary_Numeric_Expression | numericOrString_Expression_WithParetheses | unknownTypeValue) {$value = null; notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);}
    |   op=(MUL | DIV | FLOORDIV | MOD) {$value = null; notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op); notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);}
    // wrong combinations follow
    |   unary_Boolean_Expression op=(MUL | DIV | FLOORDIV | MOD) {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            unary_Numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota logická.", "'||', '&&', '=', '!='", null);}
        |   atomic_String_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.\nLogickou hodnotu však lze s řetězcovou hodnotou spojit operátorem '+'.", "'+'", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "'||', '&&', '=', '!=', '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "'+', nebo číselná hodnota na levé straně", null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.\n- Na pravé straně operátoru byla nalezena hodnota null.", null, null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        )
    |   op=(MUL | DIV | FLOORDIV | MOD) unary_Boolean_Expression {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota logická.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
        }

    |   atomic_String_Expression op=(MUL | DIV | FLOORDIV | MOD) {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            unary_Numeric_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'+'", null);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota logická.\nŘetězcovou hodnotu však lze s logickou hodnotou spojit operátorem '+'.", "'+'", null);}
        |   atomic_String_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        ) {$value = null;}
    |   op=(MUL | DIV | FLOORDIV | MOD) atomic_String_Expression {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
        }

    |   array_Expression op=(MUL | DIV | FLOORDIV | MOD) {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            unary_Numeric_Expression {ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   atomic_String_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", null, null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky polí mezi sebou, je nutné použít cyklus.", null, null);}
        |   unknownTypeValue {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", null, null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", null, null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                ruleNotifyErrorListeners(errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        ) {$value = null;}
    |   op=(MUL | DIV | FLOORDIV | MOD) array_Expression // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            ruleNotifyErrorListeners(errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.\nPokud chcete " + operation + " provést pro jednotlivé prvky pole, je nutné použít cyklus.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
        }

    |   NULL_LITERAL op=(MUL | DIV | FLOORDIV | MOD) {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
        } (
            unary_Numeric_Expression {notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);}
        |   unary_Boolean_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota logická.", null, null);}
        |   atomic_String_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota řetězcová.", "'=', '!=', '+'", null);}
        |   array_Expression {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota v podobě pole.", "'=', '!='", null);}
        |   unknownTypeValue {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   numericOrString_Expression_WithParetheses {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "'=', '!=', '+'", null);}
        |   NULL_LITERAL {notifyErrorListeners($op, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.\n- Na pravé straně operátoru byla nalezena hodnota null.", "'=', '!='", null);}
        |   {
                notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na levé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
                notifyErrorListenersOnPointAfter("Chybí číselná hodnota na pravé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            }
        ) {$value = null;}
    |   op=(MUL | DIV | FLOORDIV | MOD) NULL_LITERAL // Will not be used because PLUS can't exist because of the UNARY_PLUS_OR_MINUS_OPERATOR token. Leaving it here anyway....
        {
            $value = null;
            String operation = "chyba!";
            if ($op.getType() == MUL) {
                operation = "násobení";
            } else if ($op.getType() == DIV) {
                operation = "dělení";
            } else if ($op.getType() == FLOORDIV) {
                operation = "celočíselné dělení";
            } else if ($op.getType() == MOD) {
                operation = "modulo (zbytek po celočíselném dělení)";
            }
            String errorHeader = "Operátor '" + $op.text + "' (" + operation + ") lze použít pouze v kombinaci s číselnými hodnotami.";
            notifyErrorListenersOnPointBefore("Chybí číselná hodnota na levé straně operátoru '" + $op.text + "'.", "číselná hodnota", $op);
            notifyErrorListeners($NULL_LITERAL, errorHeader + "\n- Na pravé straně operátoru byla nalezena hodnota null.", "číselná hodnota", null);
        }
    ;

unary_Numeric_Expression returns [Double value]
	:	atomic_Numeric_Expression {$value = $atomic_Numeric_Expression.value;}
	|   unaryPlusOrMinusOperator atomic_Numeric_Expression {
            $value = $atomic_Numeric_Expression.value;
            if ($value != null && $unaryPlusOrMinusOperator.text != null && $unaryPlusOrMinusOperator.text.contains("-")) {
                $value = - $value;
            }
        }
    |   unaryPlusOrMinusOperator (numericOrString_Expression_WithParetheses | unknownTypeValue) {$value = null;}
    |   unaryPlusOrMinusOperator {$value = null; notifyErrorListenersOnPointAfter("Za znaménkem chybí číselná hodnota.", "číselná hodnota", $unaryPlusOrMinusOperator.stop);}
    // wrong combinations follow
    |   unaryPlusOrMinusOperator unary_Boolean_Expression {
            $value = null;
            ruleNotifyErrorListeners("Logické hodnotě nemůže být určena kladnost/zápornost.", "číselná hodnota", $unary_Boolean_Expression.start, $unary_Boolean_Expression.stop);
        }
    |   unaryPlusOrMinusOperator atomic_String_Expression {
            $value = null;
            ruleNotifyErrorListeners("Řetězcové hodnotě nemůže být určena kladnost/zápornost.", "číselná hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);
        }
    |   unaryPlusOrMinusOperator array_Expression {
            $value = null;
            ruleNotifyErrorListeners("Hodnotě v podobě pole nemůže být určena kladnost/zápornost.", "číselná hodnota", $array_Expression.start, $array_Expression.stop);
        }
    |   unaryPlusOrMinusOperator NULL_LITERAL {
            $value = null;
            notifyErrorListeners($NULL_LITERAL, "Hodnotě null nemůže být určena kladnost/zápornost.", "číselná hodnota", null);
        }
    ;

atomic_Numeric_Expression returns [Double value]
	:	constant_Number {$value = $constant_Number.value;}
    |	LPAR numeric_Expression (
            RPAR {$value = $numeric_Expression.value;}
        |	EOF {
                $value = $numeric_Expression.value;
                notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);
            }
        )
    ;

unary_Boolean_Expression
	:   atomic_Boolean_Expression
    |	BANG {
            String addMsg = "";
            if (_input.LT(-2) != null && _input.LA(-2) == EQUAL && _input.LT(-2).getText().equals("=")) { // valid only for single '=' equal symbol (not valid for '==')
                // check again if there is not any whitespace between the EQUAL and the BANG symbol
                int idx = $BANG.getStartIndex() - $BANG.text.length();
                if (idx > -1 && _input.getTokenSource().getInputStream().getText(new Interval(idx, idx)).equals("=")) {
                    addMsg += "\nPS: nemáte na mysli operátor '" + $BANG.text + _input.LT(-2).getText() + "'?";
                }
            }
        } (
            (atomic_Boolean_Expression | unknownTypeValue)
        |	{notifyErrorListenersOnPointAfter("Chybí logická hodnota na pravé straně operátoru '" + $BANG.text + "'.", "logická hodnota", $BANG);}
        // wrong combinations follow
        |   unary_Numeric_Expression {ruleNotifyErrorListeners("Logickou negaci ('" + $BANG.text + "') lze použít pouze v kombinaci s logickou hodnotou.\n- Nalezena hodnota číselná." + addMsg, "logická hodnota", $unary_Numeric_Expression.start, $unary_Numeric_Expression.stop);}
        |   atomic_String_Expression {ruleNotifyErrorListeners("Logickou negaci ('" + $BANG.text + "') lze použít pouze v kombinaci s logickou hodnotou.\n- Nalezena hodnota řetězcová." + addMsg, "logická hodnota", $atomic_String_Expression.start, $atomic_String_Expression.stop);}
        |   array_Expression {ruleNotifyErrorListeners("Logickou negaci ('" + $BANG.text + "') lze použít pouze v kombinaci s logickou hodnotou.\n- Nalezena hodnota v podobě pole." + addMsg, "logická hodnota", $array_Expression.start, $array_Expression.stop);}
        |   numericOrString_Expression_WithParetheses {ruleNotifyErrorListeners("Logickou negaci ('" + $BANG.text + "') lze použít pouze v kombinaci s logickou hodnotou.\n- Nalezena hodnota číselná/řetězcová." + addMsg, "logická hodnota", $numericOrString_Expression_WithParetheses.start, $numericOrString_Expression_WithParetheses.stop);}
        |   NULL_LITERAL {notifyErrorListeners($NULL_LITERAL, "Logickou negaci ('" + $BANG.text + "') lze použít pouze v kombinaci s logickou hodnotou.\n- Nalezena hodnota null." + addMsg, "logická hodnota", null);}
        )
    ;

atomic_Boolean_Expression
	:	CONSTANT_BOOLEAN
    |	LPAR boolean_Expression (
            RPAR
        |	EOF {notifyErrorListeners($LPAR, "Chybí ukončovací kulatá závorka.", "')'", null);}
        )
    ;

//unaryPlusOrMinusOperator
//	:   (PLUS | MINUS)+ //{_input.LA(1) != PLUS && _input.LA(1) != MINUS}?
//	|   (PLUS | MINUS)* (incrementDecrementOperatorSyntaxError (PLUS | MINUS)*)+
//    ;

unaryPlusOrMinusOperator
	:   UNARY_PLUS_OR_MINUS_OPERATOR //{_input.LA(1) != PLUS && _input.LA(1) != MINUS}?
	|   UNARY_PLUS_OR_MINUS_OPERATOR? (incrementDecrementOperatorSyntaxError UNARY_PLUS_OR_MINUS_OPERATOR?)+
    ;

incrementDecrementOperatorSyntaxError
	:
	    doublesign=(INC | DEC) {notifyErrorListeners($doublesign, "Zdvojené znaménko není v PS Diagramu povoleno.", null, null);}
    ;
