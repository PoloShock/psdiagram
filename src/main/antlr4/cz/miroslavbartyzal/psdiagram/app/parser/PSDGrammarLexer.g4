lexer grammar PSDGrammarLexer;

@lexer::members {
    public boolean testAssertion(int assertionMode, int passingRule) {
        int index = _input.index();
        int mark = _input.mark();
        try {
            LexerATNSimulator interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
            interp.copyState(_interp);
            int result = interp.match(_input, assertionMode);
            return result == passingRule;
        } finally {
            _input.seek(index);
            _input.release(mark);
        }
    }

    boolean lookBehindForMinusOrPlus()
    {
        int i = -1;
        int character = _input.LA(i);
        if (character == IntStream.EOF) {
            return true;
        }
        String infrontOfSymbol = "" + (char)_input.LA(1);

        while (character != IntStream.EOF && (character == '+' || character == '-' || character == ' ' || character == '\t' || character == '\r' || character == '\f' || character == '\n')) {
            infrontOfSymbol = (char)character + infrontOfSymbol;
            i--;
            character = _input.LA(i);
        }
        if (character == IntStream.EOF || character == '*' || character == '/' || character == '%' || character == '>' || character == '<' || character == '!' || character == '=' || character == '&' || character == '|' || character == '(' || character == '[' || character == '{' || character == ',' || character == ';' || character == '^') {
            return true;
        }
        infrontOfSymbol = infrontOfSymbol.trim();

        i = 0;
        while (i < infrontOfSymbol.length()-1) {
            if (infrontOfSymbol.charAt(i) != infrontOfSymbol.charAt(i+1)) {
                return true;
            } else {
                i++;
            }
            i++;
        }

        return false;
    }

    private void myNotifyErrorListeners(String msg, String expecting) {
        CommonToken token = new CommonToken(-1);
        token.setStartIndex(_tokenStartCharIndex);
        token.setStopIndex(_input.index()-1);

        ANTLRErrorListener listener = getErrorListenerDispatch();
        listener.syntaxError(this, token, _tokenStartLine, _tokenStartCharPositionInLine, mixMsgWithExpected(msg, expecting), null);
    }

    private String mixMsgWithExpected(String msg, String expecting)
    {
        if (expecting == null || expecting.isEmpty()) {
            return msg;
        }
        return msg + "\nOčekáváné možnosti: {" + expecting + "}.";
    }
}

// Reserved Words (forbidden to use)
/*
 AWK code for auto-generation: (usage: awk -f awk.awk awk.in > awk.out)

{
  for (i = 1; i <= length($0); i++) {
    c = substr($0, i, 1);
    if (toupper(c) != tolower(c))
      printf("('%s'|'%s')", toupper(c), tolower(c));
	else if (i == length($0)) # new line
	  printf(" // %s|\t", $0);
    else
      printf("('%s')", c);
  }
}
*/
RESERVED_WORD // (must appear as topmost)
    :   ('A'|'a')('R'|'r')('G'|'g')('U'|'u')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('S'|'s') // arguments
	|	('A'|'a')('S'|'s')('S'|'s')('E'|'e')('R'|'r')('T'|'t') // assert
	|	('B'|'b')('O'|'o')('O'|'o')('L'|'l')('E'|'e')('A'|'a')('N'|'n') // boolean
	|	('B'|'b')('R'|'r')('E'|'e')('A'|'a')('K'|'k') // break
	|	('B'|'b')('Y'|'y')('T'|'t')('E'|'e') // byte
	|	('C'|'c')('A'|'a')('S'|'s')('E'|'e') // case
	|	('C'|'c')('A'|'a')('T'|'t')('C'|'c')('H'|'h') // catch
	|	('C'|'c')('H'|'h')('A'|'a')('R'|'r') // char
	|	('C'|'c')('L'|'l')('A'|'a')('S'|'s')('S'|'s') // class
	|	('C'|'c')('O'|'o')('N'|'n')('S'|'s')('T'|'t') // const
	|	('C'|'c')('O'|'o')('N'|'n')('T'|'t')('I'|'i')('N'|'n')('U'|'u')('E'|'e') // continue
	|	('D'|'d')('E'|'e')('B'|'b')('U'|'u')('G'|'g')('G'|'g')('E'|'e')('R'|'r') // debugger
	|	('D'|'d')('E'|'e')('F'|'f')('A'|'a')('U'|'u')('L'|'l')('T'|'t') // default
	|	('D'|'d')('E'|'e')('L'|'l')('E'|'e')('T'|'t')('E'|'e') // delete
	|	('D'|'d')('O'|'o') // do
	|	('D'|'d')('O'|'o')('U'|'u')('B'|'b')('L'|'l')('E'|'e') // double
	|	('E'|'e')('L'|'l')('S'|'s')('E'|'e') // else
	|	('E'|'e')('N'|'n')('U'|'u')('M'|'m') // enum
	|	('E'|'e')('X'|'x')('P'|'p')('O'|'o')('R'|'r')('T'|'t') // export
	|	('E'|'e')('X'|'x')('T'|'t')('E'|'e')('N'|'n')('D'|'d')('S'|'s') // extends
	|	('F'|'f')('I'|'i')('N'|'n')('A'|'a')('L'|'l')('L'|'l')('Y'|'y') // finally
	|	('F'|'f')('I'|'i')('N'|'n')('A'|'a')('L'|'l') // final
	|	('F'|'f')('L'|'l')('O'|'o')('A'|'a')('T'|'t') // float
	|	('F'|'f')('O'|'o')('R'|'r') // for
	|	('F'|'f')('U'|'u')('N'|'n')('C'|'c')('T'|'t')('I'|'i')('O'|'o')('N'|'n') // function
	|	('G'|'g')('O'|'o')('T'|'t')('O'|'o') // goto
	|	('I'|'i')('F'|'f') // if
	|	('I'|'i')('M'|'m')('P'|'p')('L'|'l')('E'|'e')('M'|'m')('E'|'e')('N'|'n')('T'|'t')('S'|'s') // implements
	|	('I'|'i')('M'|'m')('P'|'p')('O'|'o')('R'|'r')('T'|'t') // import
	|	('I'|'i')('N'|'n')('T'|'t')('E'|'e')('R'|'r')('F'|'f')('A'|'a')('C'|'c')('E'|'e') // interface
	|	('I'|'i')('N'|'n')('T'|'t') // int
	|	('I'|'i')('N'|'n')('S'|'s')('T'|'t')('A'|'a')('N'|'n')('C'|'c')('E'|'e')('O'|'o')('F'|'f') // instanceof
	|	('I'|'i')('N'|'n') // in
	|	('L'|'l')('E'|'e')('T'|'t') // let
	|	('L'|'l')('O'|'o')('N'|'n')('G'|'g') // long
	|	('N'|'n')('E'|'e')('W'|'w') // new
	|	('P'|'p')('A'|'a')('C'|'c')('K'|'k')('A'|'a')('G'|'g')('E'|'e') // package
	|	('P'|'p')('R'|'r')('I'|'i')('V'|'v')('A'|'a')('T'|'t')('E'|'e') // private
	|	('P'|'p')('R'|'r')('O'|'o')('T'|'t')('E'|'e')('C'|'c')('T'|'t')('E'|'e')('D'|'d') // protected
	|	('P'|'p')('U'|'u')('B'|'b')('L'|'l')('I'|'i')('C'|'c') // public
	|	('R'|'r')('E'|'e')('T'|'t')('U'|'u')('R'|'r')('N'|'n') // return
	|	('S'|'s')('H'|'h')('O'|'o')('R'|'r')('T'|'t') // short
	|	('S'|'s')('T'|'t')('A'|'a')('T'|'t')('I'|'i')('C'|'c') // static
	|	('S'|'s')('U'|'u')('P'|'p')('E'|'e')('R'|'r') // super
	|	('S'|'s')('W'|'w')('I'|'i')('T'|'t')('C'|'c')('H'|'h') // switch
	|	('S'|'s')('Y'|'y')('N'|'n')('C'|'c')('H'|'h')('R'|'r')('O'|'o')('N'|'n')('I'|'i')('Z'|'z')('E'|'e')('D'|'d') // synchronized
	|	('T'|'t')('H'|'h')('I'|'i')('S'|'s') // this
	|	('T'|'t')('H'|'h')('R'|'r')('O'|'o')('W'|'w')('S'|'s') // throws
	|	('T'|'t')('H'|'h')('R'|'r')('O'|'o')('W'|'w') // throw
	|	('T'|'t')('R'|'r')('Y'|'y') // try
	|	('T'|'t')('Y'|'y')('P'|'p')('E'|'e')('O'|'o')('F'|'f') // typeof
	|	('V'|'v')('A'|'a')('R'|'r') // var
	|	('V'|'v')('O'|'o')('I'|'i')('D'|'d') // void
	|	('W'|'w')('H'|'h')('I'|'i')('L'|'l')('E'|'e') // while
	|	('W'|'w')('I'|'i')('T'|'t')('H'|'h') // with
	|	('Y'|'y')('I'|'i')('E'|'e')('L'|'l')('D'|'d') // yield
    ;

// The Null Literal
NULL_LITERAL
    :	'null'
    ;

// Constants
CONSTANT_INTEGER // celé číslo
    :
        CONSTANT_DECIMAL_INTEGER
        // here could be also HexCONSTANT_INTEGER, OctalCONSTANT_INTEGER, or BinaryCONSTANT_INTEGER in future
    ;

fragment
CONSTANT_DECIMAL_INTEGER
    :
    '0'
    |   NON_ZERO_DIGIT DIGITS?
    ;

CONSTANT_FLOATING_POINT // desetinné číslo
    :
        CONSTANT_DECIMAL_FLOATING_POINT
        // here could be also HexadecimalFloatingPointLiteral in future
    ;

ERROR_CONSTANT_FLOATING_POINT_WITHOUT_FLOATING_PART
    :
        ERROR_CONSTANT_DECIMAL_FLOATING_POINT_WITHOUT_FLOATING_PART
        // here could be also HexadecimalFloatingPointLiteral in future
    ;

fragment
CONSTANT_DECIMAL_FLOATING_POINT
    :
        CONSTANT_DECIMAL_INTEGER '.' DIGITS
    ;

fragment
ERROR_CONSTANT_DECIMAL_FLOATING_POINT_WITHOUT_FLOATING_PART
    :
        CONSTANT_DECIMAL_INTEGER '.'
    ;

fragment
DIGITS
    :
        DIGIT+
    ;

fragment
DIGIT
    :
        [0-9]
    ;

fragment
NON_ZERO_DIGIT
    :
        [1-9]
    ;

CONSTANT_BOOLEAN // true, false
    :
        'true' | 'false'
    ;

CONSTANT_STRING // řetězcová hodnota
    :
        '"' STRING_CHARACTER* '"'
    ;

ERROR_CONSTANT_STRING_EOF
    :
        '"' STRING_CHARACTER* EOF
    ;

ERROR_CONSTANT_STRING_BAD_ESCAPE
    :
        '"' STRING_CHARACTER* (ERROR_ESCAPE_SEQUENCE STRING_CHARACTER*)+ '"'
    ;

fragment
STRING_CHARACTER
    :   ~["\\]
    |   ESCAPE_SEQUENCE
    ;

//      \t Insert a tab in the text at this point.
//      \b Insert a backspace in the text at this point.
//      \n Insert a newline in the text at this point.
//      \r Insert a carriage return in the text at this point.
//      \f Insert a formfeed in the text at this point.
//      \' Insert a single quote character in the text at this point.
//      \" Insert a double quote character in the text at this point.
//      \\ Insert a backslash character in the text at this point.
fragment
ESCAPE_SEQUENCE
    :
        '\\' [tbnrf"'\\]
    ;
fragment
ERROR_ESCAPE_SEQUENCE
    :
        '\\' ~[tbnrf"'\\]
    ;

// Separators
LPAR : '(';
RPAR : ')';
LBRK : '[';
RBRK : ']';
COMMA : ',';
DOT : '.';

// Operators
UNARY_PLUS_OR_MINUS_OPERATOR
    :
        {lookBehindForMinusOrPlus()}? (
            '+' (WS? '-' WS? '+' | WS '+')* {_input.LA(1) != '+'}?
        |   '+' (WS? '-' WS? '+' | WS '+')* WS? '-' {_input.LA(1) != '-'}?
        |   '-' (WS? '+' WS? '-' | WS '-')* {_input.LA(1) != '-'}?
        |   '-' (WS? '+' WS? '-' | WS '-')* WS? '+' {_input.LA(1) != '+'}?
        ) {_input.LA(1) == IntStream.EOF || testAssertion(MODE_UPOMO_ASSERTION, UPOMO_ASSERTION_PASS)}? // end of stream, digit or identifier must follow
    ;
INC : '++';
DEC : '--';
GT : '>';
LT : '<';
BANG : '!';
EQUAL
    :   '='
    //|    '==' {myNotifyErrorListeners("Pro operátor rovnosti použijte jediný znak '='.", "'='");}
    ;
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
OR
    :   '||'
    |   '|' {myNotifyErrorListeners("Pro logický operátor OR použijte dvojici znaků '|'.\nPS: pokud hledáte bitový operátor OR, v PS Diagramu jej přímo použít nelze", "'||'");}
    ;
AND
    :   '&&'
    |   '&' {myNotifyErrorListeners("Pro logický operátor AND použijte dvojici znaků '&'.\nPS: pokud hledáte bitový operátor AND, v PS Diagramu jej přímo použít nelze", "'&&'");}
    ;
PLUS : '+';
MINUS : '-';
MUL : '*';
FLOORDIV : '//';
DIV : '/';
MOD : '%';

// Identifiers (must appear after all keywords in the grammar)
INDENTIFIER // proměnná(identifikátor)
    :
        IDENTIFIER_FIRST_CHAR IDENTIFIER_AFTER_FIRST_CHAR*
    ;
ERROR_INDENTIFIER_INSIDE
    :
        IDENTIFIER_FIRST_CHAR NONSEPARATOR+
    ;
ERROR_INDENTIFIER_BEGINNING
    :
        NONSEPARATOR+
    ;

fragment
IDENTIFIER_FIRST_CHAR
    :
        [a-zA-Z$_]
    ;

fragment
IDENTIFIER_AFTER_FIRST_CHAR
    :
        IDENTIFIER_FIRST_CHAR | [0-9]
    ;

fragment
NONSEPARATOR
    :
        ~[ \t\r\f\n%/*\-+|&!=><.,()[\]{}"'^;]
    ;

// Whitespace
WS
    :
        [ \t\r\f\n]+ -> skip
        //[ \t\r\f\n]+ -> channel(HIDDEN)
    ;

mode MODE_UPOMO_ASSERTION;
    UPOMO_ASSERTION_PASS : .;
    UPOMO_ASSERTION_FAIL : WS? (CONSTANT_BOOLEAN | RESERVED_WORD | NULL_LITERAL | CONSTANT_STRING | ERROR_CONSTANT_STRING_EOF | ERROR_CONSTANT_STRING_BAD_ESCAPE); // it is not allowed to put unary plus or minus in front of boolean, keyword, and null
