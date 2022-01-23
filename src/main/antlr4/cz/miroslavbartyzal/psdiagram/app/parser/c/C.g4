/*
 [The "BSD licence"]
 Copyright (c) 2013 Sam Harwell
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/** C 2011 grammar built from the C11 Spec */
grammar C;

/*
Práce na koverzi jazyka C pozastavena. Důvody viz následující zpráva:

2022-01-23
Hezké odpoledne, chci Vám dát vědět jak se věci ohledně jazyka C vyvíjejí.
Někdy během čtvrtka jsem dokončil převod z Javy do PS Diagramu a tak jsem se pustil do převodu C -> PS Diagram. Během prací jsem ale zjistil několik nepříjemných faktů, které brání bezproblémovému převodu:
1) práce s řetězci (textem) je v C velmi odlišné oproti PSD (konec řetězce označen číslem 0 resp. znakem '\0', s textem se pracuje výhradně jako s polem bytů a non-ASCII znaky (ž,č,ř,ň, ...) zabírají více než jeden element v poli)
2) podmínky (if else) v C pracují s čísly (0 = false, 1+ = true), což do PS Diagramu nelze přímo převést
3) C často využívá práci s pointery a adresami proměnných, což jsou témata jimiž se PS Diagram záměrně nezabývá.
Je proto velmi obtížné převést C algoritmus, využívající tyto funkcionality, do PSD tak, aby zachoval své vlastnosti. Čím více se nořím do problematiky převodu, tím více docházím k závěru, že jazyk C nebudu moci podporovat tak dobře, jak bych si přál.
Došel jsem proto k poznání, že ne každý jazyk se pro převod do/z PSD hodí, a jazyk C je, žel, jeden z nich. Kéž bych si to uvědomil dříve. Převod jazyka C proto vzdávám a omlouvám se.
*/

@parser::header {
	import java.util.Set;
	import java.util.HashSet;
}

@parser::members {
	private final Set<String> integerIdentifiers = new HashSet<>();
}

primaryExpression returns [boolean isInteger]
    :   identifier {$isInteger = $identifier.isInteger;}
    |   constant {$isInteger = $constant.isInteger;}
    |   StringLiteral+
    |   '(' expression ')' {$isInteger = $expression.isInteger;}
    |   genericSelection
    |   '__extension__'? '(' compoundStatement ')' // Blocks (GCC extension)
    |   '__builtin_va_arg' '(' unaryExpression ',' typeName ')'
    |   '__builtin_offsetof' '(' typeName ',' unaryExpression ')'
    ;

identifier returns [boolean isInteger]
    :   Identifier {$isInteger = integerIdentifiers.contains($Identifier.text);}
    ;

constant returns [boolean isInteger]
    :   IntegerConstant {$isInteger = true;}
    |   FloatingConstant
    //|   EnumerationConstant
    |   CharacterConstant {$isInteger = true;}
    ;

genericSelection
    :   '_Generic' '(' assignmentExpression ',' genericAssocList ')'
    ;

genericAssocList
    :   genericAssociation (',' genericAssociation)*
    ;

genericAssociation
    :   (typeName | 'default') ':' assignmentExpression
    ;

postfixExpression returns [boolean isInteger]
    :
    (   
    	    primaryExpression {$isInteger = $primaryExpression.isInteger;}
    	|   '__extension__'? '(' typeName ')' '{' initializerList ','? '}'
    ) (
		  '[' expression ']'
		| '(' argumentExpressionList? ')'
		| ('.' | '->') identifier
		| ('++' | '--')
    )*
    ;

argumentExpressionList
    :   expression
    ;

unaryExpression returns [boolean isInteger]
    :	('++' |	'--')* unaryExpressionSecondPart {$isInteger = $unaryExpressionSecondPart.isInteger;}
    |	'sizeof'* unaryExpressionSecondPart {$isInteger = true;}
    ;
    
unaryExpressionSecondPart returns [boolean isInteger]
	:	postfixExpression {$isInteger = $postfixExpression.isInteger;}
	|   unaryOperator castExpression {$isInteger = $castExpression.isInteger;}
	|   ('sizeof' | '_Alignof') '(' typeName ')' {$isInteger = true;}
	|   '&&' identifier {$isInteger = true;} // GCC extension address of label
	;

unaryOperator
    :   '&' | '*' | '+' | '-' | '~' | '!'
    ;

castExpression returns [boolean isInteger]
    :   '__extension__'? '(' typeName ')' castExpression {$isInteger = $typeName.isInteger;}
    |   unaryExpression {$isInteger = $unaryExpression.isInteger;}
    |   DigitSequence // for
    ;

multiplicativeExpression returns [boolean isInteger]
    :   castExpression (('*'|'/'|'%') castExpression)* {$isInteger = $ctx.castExpression().stream().allMatch(m -> m.isInteger);}
    ;

additiveExpression returns [boolean isInteger]
    :   multiplicativeExpression (('+'|'-') multiplicativeExpression)* {$isInteger = $ctx.multiplicativeExpression().stream().allMatch(m -> m.isInteger);}
    ;

shiftExpression returns [boolean isInteger]
    :   a=additiveExpression (('<<'|'>>') last_b=additiveExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

relationalExpression returns [boolean isInteger]
    :   a=shiftExpression (('<'|'>'|'<='|'>=') last_b=shiftExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

equalityExpression returns [boolean isInteger]
    :   a=relationalExpression (('=='| '!=') last_b=relationalExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

andExpression returns [boolean isInteger]
    :   a=equalityExpression ( '&' last_b=equalityExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

exclusiveOrExpression returns [boolean isInteger]
    :   a=andExpression ('^' last_b=andExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

inclusiveOrExpression returns [boolean isInteger]
    :   a=exclusiveOrExpression ('|' last_b=exclusiveOrExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

logicalAndExpression returns [boolean isInteger]
    :   a=inclusiveOrExpression ('&&' last_b=inclusiveOrExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

logicalOrExpression returns [boolean isInteger]
    :   a=logicalAndExpression ( '||' last_b=logicalAndExpression)* {$isInteger = $last_b.ctx != null || $a.isInteger;}
    ;

conditionalExpression returns [boolean isInteger]
    :   logicalOrExpression ('?' expression ':' conditionalExpression)? {$isInteger = $logicalOrExpression.isInteger && $conditionalExpression.ctx == null;}
    ;

assignmentExpression returns [boolean isInteger]
    :   conditionalExpression {$isInteger = $conditionalExpression.isInteger;}
    |   unaryExpression assignmentOperator assignmentExpression {$isInteger = $unaryExpression.isInteger;}
    |   DigitSequence // for
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

expression returns [boolean isInteger]
    :   a=assignmentExpression (',' last_b=assignmentExpression)* {$isInteger = $a.isInteger && $last_b.ctx == null;}
    ;

constantExpression
    :   conditionalExpression
    ;

declaration
    :   declarationWithoutStaticAssert ';'
    |   staticAssertDeclaration
    ;

declarationWithoutStaticAssert
	:	declarationSpecifiers initDeclaratorList? {
			if ($declarationSpecifiers.isInteger && $initDeclaratorList.ctx != null) {
				for (var _initDeclarator : $initDeclaratorList.ctx.initDeclarator()) {
					String _identifier = _initDeclarator.declarator().identifierText;
					integerIdentifiers.add(_identifier);
				}
			}
		}
	;

declarationSpecifiers returns [boolean isInteger]
    :   declarationSpecifier+ {$isInteger = $ctx.declarationSpecifier().stream().anyMatch(d -> d.isInteger);}
    ;

declarationSpecifiers2
    :   declarationSpecifier+
    ;

declarationSpecifier returns [boolean isInteger]
    :   storageClassSpecifier
    |   typeSpecifier {$isInteger = $typeSpecifier.isInteger;}
    |   typeQualifier
    |   functionSpecifier
    |   alignmentSpecifier
    ;

initDeclaratorList
    :   initDeclarator (',' initDeclarator)*
    ;

initDeclarator
    :   declarator ('=' initializer)?
    ;

storageClassSpecifier
    :   'typedef'
    |   'extern'
    |   'static'
    |   '_Thread_local'
    |   'auto'
    |   'register'
    ;

typeSpecifier returns [boolean isInteger]
    :   ('void'
    |   'char' {$isInteger = true;}
    |   'short' {$isInteger = true;}
    |   'int' {$isInteger = true;}
    |   'long' {$isInteger = true;}
    |   'float'
    |   'double'
    |   'signed'
    |   'unsigned'
    |   '_Bool'
    |   '_Complex'
    |   '__m128'
    |   '__m128d'
    |   '__m128i')
    |   '__extension__' '(' ('__m128' | '__m128d' | '__m128i') ')'
    |   atomicTypeSpecifier
    |   structOrUnionSpecifier
    |   enumSpecifier
    |   typedefName
    |   '__typeof__' '(' constantExpression ')' // GCC extension
    |   typeSpecifier pointer
    ;

structOrUnionSpecifier
    :   structOrUnion identifier? '{' structDeclarationList '}'
    |   structOrUnion identifier
    ;

structOrUnion
    :   'struct'
    |   'union'
    ;

structDeclarationList
    :   structDeclaration+
    ;

structDeclaration
    :   specifierQualifierList structDeclaratorList? ';'
    |   staticAssertDeclaration
    ;

specifierQualifierList returns [boolean isInteger]
    :   (typeSpecifier | typeQualifier) specifierQualifierList? {
			$isInteger = $typeSpecifier.isInteger || ($specifierQualifierList.ctx != null && $specifierQualifierList.isInteger);
		}
    ;

structDeclaratorList
    :   structDeclarator (',' structDeclarator)*
    ;

structDeclarator
    :   declarator
    |   declarator? ':' constantExpression
    ;

enumSpecifier
    :   'enum' identifier? '{' enumeratorList ','? '}'
    |   'enum' identifier
    ;

enumeratorList
    :   enumerator (',' enumerator)*
    ;

enumerator
    :   enumerationConstant ('=' constantExpression)?
    ;

enumerationConstant
    :   identifier
    ;

atomicTypeSpecifier
    :   '_Atomic' '(' typeName ')'
    ;

typeQualifier
    :   'const'
    |   'restrict'
    |   'volatile'
    |   '_Atomic'
    ;

functionSpecifier
    :   ('inline'
    |   '_Noreturn'
    |   '__inline__' // GCC extension
    |   '__stdcall')
    |   gccAttributeSpecifier
    |   '__declspec' '(' identifier ')'
    ;

alignmentSpecifier
    :   '_Alignas' '(' (typeName | constantExpression) ')'
    ;

declarator returns [String identifierText]
    :   pointer? directDeclarator gccDeclaratorExtension* {$identifierText = $directDeclarator.identifierText;}
    ;

directDeclarator returns [String identifierText]
    :   identifier {$identifierText = $identifier.text;}
    |   '(' declarator ')' {$identifierText = $declarator.identifierText;}
    |   d=directDeclarator '[' typeQualifierList? assignmentExpression? ']' {$identifierText = $d.identifierText;}
    |   d=directDeclarator '[' 'static' typeQualifierList? assignmentExpression ']' {$identifierText = $d.identifierText;}
    |   d=directDeclarator '[' typeQualifierList 'static' assignmentExpression ']' {$identifierText = $d.identifierText;}
    |   d=directDeclarator '[' typeQualifierList? '*' ']' {$identifierText = $d.identifierText;}
    |   d=directDeclarator '(' parameterTypeList ')' {$identifierText = $d.identifierText;}
    |   d=directDeclarator '(' identifierList? ')' {$identifierText = $d.identifierText;}
    |   identifier ':' DigitSequence {$identifierText = $identifier.text;}  // bit field
    |   '(' typeSpecifier? pointer d=directDeclarator ')' {$identifierText = $d.identifierText;} // function pointer like: (__cdecl *f)
    ;

gccDeclaratorExtension
    :   '__asm' '(' StringLiteral+ ')'
    |   gccAttributeSpecifier
    ;

gccAttributeSpecifier
    :   '__attribute__' '(' '(' gccAttributeList ')' ')'
    ;

gccAttributeList
    :   gccAttribute? (',' gccAttribute?)*
    ;

gccAttribute
    :   ~(',' | '(' | ')') // relaxed def for "identifier or reserved word"
        ('(' argumentExpressionList? ')')?
    ;

nestedParenthesesBlock
    :   (   ~('(' | ')')
        |   '(' nestedParenthesesBlock ')'
        )*
    ;

pointer
    :  (('*'|'^') typeQualifierList?)+ // ^ - Blocks language extension
    ;

typeQualifierList
    :   typeQualifier+
    ;

parameterTypeList
    :   parameterList (',' '...')?
    ;

parameterList
    :   parameterDeclaration (',' parameterDeclaration)*
    ;

parameterDeclaration
    :   declarationSpecifiers declarator
    |   declarationSpecifiers2 abstractDeclarator?
    ;

identifierList
    :   identifier (',' identifier)*
    ;

typeName returns [boolean isInteger]
    :   specifierQualifierList abstractDeclarator? {$isInteger = $specifierQualifierList.isInteger;}
    ;

abstractDeclarator
    :   pointer
    |   pointer? directAbstractDeclarator gccDeclaratorExtension*
    ;

directAbstractDeclarator
    :   '(' abstractDeclarator ')' gccDeclaratorExtension*
    |   '[' typeQualifierList? assignmentExpression? ']'
    |   '[' 'static' typeQualifierList? assignmentExpression ']'
    |   '[' typeQualifierList 'static' assignmentExpression ']'
    |   '[' '*' ']'
    |   '(' parameterTypeList? ')' gccDeclaratorExtension*
    |   directAbstractDeclarator '[' typeQualifierList? assignmentExpression? ']'
    |   directAbstractDeclarator '[' 'static' typeQualifierList? assignmentExpression ']'
    |   directAbstractDeclarator '[' typeQualifierList 'static' assignmentExpression ']'
    |   directAbstractDeclarator '[' '*' ']'
    |   directAbstractDeclarator '(' parameterTypeList? ')' gccDeclaratorExtension*
    ;

typedefName
    :   identifier
    ;

initializer
    :   assignmentExpression
    |   '{' initializerList ','? '}'
    ;

initializerList
    :   designation? initializer (',' designation? initializer)*
    ;

designation
    :   designatorList '='
    ;

designatorList
    :   designator+
    ;

designator
    :   '[' constantExpression ']'
    |   '.' identifier
    ;

staticAssertDeclaration
    :   '_Static_assert' '(' constantExpression ',' StringLiteral+ ')' ';'
    ;

statement
    :   labeledStatement
    |   compoundStatement
    |   expressionStatement
    |   ifStatement
    |   switchStatement
    |   forStatement
    |   whileStatement
    |   doStatement
    |   returnStatement
    |   breakStatement
    |   continueStatement
    |   gotoStatement
    |   ('__asm' | '__asm__') ('volatile' | '__volatile__') '(' (logicalOrExpression (',' logicalOrExpression)*)? (':' (logicalOrExpression (',' logicalOrExpression)*)?)* ')' ';'
    |   ';'
    ;

labeledStatement
    :   identifier ':' statement
    |   'case' constantExpression ':' statement
    |   'default' ':' statement
    ;

compoundStatement
    :   '{' blockItemListOptional '}'
    ;

blockItemListOptional
	:	blockItem*
	;

blockItem
    :   statement
    |   declaration
    |   comment
    ;

comment
    :   BlockComment
    |   LineComment
    ;
    
commentAfterStatement
    :   BlockComment
    |   LineComment
    ;

expressionStatement
    :   expression ';'
    ;

ifStatement
    :   'if' '(' expression ')' commentAfterStatement? statement ('else' statement)?
    ;

switchStatement
    :   'switch' '(' expression ')' commentAfterStatement? statement
    ;

forStatement
    :   For '(' forCondition ')' commentAfterStatement? statement
    ;

whileStatement
    :   While '(' expression ')' commentAfterStatement? statement
    ;

doStatement
    :   Do commentAfterStatement? statement While '(' expression ')' ';'
    ;

//    |   'for' '(' expression? ';' expression?  ';' forUpdate? ')' statement
//    |   For '(' declaration  expression? ';' expression? ')' statement

forCondition
	:   forInit? ';' expression? ';' forUpdate?
	;

forInit
	:	declarationWithoutStaticAssert
	|	expression
	;

forUpdate
	: expression
	;

returnStatement
    :   'return' expression? ';'
    ;

breakStatement
    :   'break' ';'
    ;

continueStatement
    :   'continue' ';'
    ;

gotoStatement
    :   'goto' (
    	    identifier 
    	|   unaryExpression // GCC extension
    	) ';'
    ;

compilationUnit
    :   translationUnit? EOF
    ;

translationUnit
    :   externalDeclaration+
    ;

externalDeclaration
    :   functionDefinition
    |   declaration
    |   ';' // stray ;
    ;

functionDefinition
    :   declarationSpecifiers? declarator declarationList? compoundStatement
    ;

declarationList
    :   declaration+
    ;

Auto : 'auto';
Break : 'break';
Case : 'case';
Char : 'char';
Const : 'const';
Continue : 'continue';
Default : 'default';
Do : 'do';
Double : 'double';
Else : 'else';
Enum : 'enum';
Extern : 'extern';
Float : 'float';
For : 'for';
Goto : 'goto';
If : 'if';
Inline : 'inline';
Int : 'int';
Long : 'long';
Register : 'register';
Restrict : 'restrict';
Return : 'return';
Short : 'short';
Signed : 'signed';
Sizeof : 'sizeof';
Static : 'static';
Struct : 'struct';
Switch : 'switch';
Typedef : 'typedef';
Union : 'union';
Unsigned : 'unsigned';
Void : 'void';
Volatile : 'volatile';
While : 'while';

Alignas : '_Alignas';
Alignof : '_Alignof';
Atomic : '_Atomic';
Bool : '_Bool';
Complex : '_Complex';
Generic : '_Generic';
Imaginary : '_Imaginary';
Noreturn : '_Noreturn';
StaticAssert : '_Static_assert';
ThreadLocal : '_Thread_local';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
PlusPlus : '++';
Minus : '-';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
// '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
StarAssign : '*=';
DivAssign : '/=';
ModAssign : '%=';
PlusAssign : '+=';
MinusAssign : '-=';
LeftShiftAssign : '<<=';
RightShiftAssign : '>>=';
AndAssign : '&=';
XorAssign : '^=';
OrAssign : '|=';

Equal : '==';
NotEqual : '!=';

Arrow : '->';
Dot : '.';
Ellipsis : '...';

Identifier
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;

fragment
IdentifierNondigit
    :   Nondigit
    |   UniversalCharacterName
    //|   // other implementation-defined characters...
    ;

fragment
Nondigit
    :   [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;

fragment
UniversalCharacterName
    :   '\\u' HexQuad
    |   '\\U' HexQuad HexQuad
    ;

fragment
HexQuad
    :   HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
    ;

IntegerConstant
    :   DecimalConstant IntegerSuffix?
    |   OctalConstant IntegerSuffix?
    |   HexadecimalConstant IntegerSuffix?
    |	BinaryConstant
    ;

fragment
BinaryConstant
	:	'0' [bB] [0-1]+
	;

fragment
DecimalConstant
    :   NonzeroDigit Digit*
    ;

fragment
OctalConstant
    :   '0' OctalDigit*
    ;

fragment
HexadecimalConstant
    :   HexadecimalPrefix HexadecimalDigit+
    ;

fragment
HexadecimalPrefix
    :   '0' [xX]
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
HexadecimalDigit
    :   [0-9a-fA-F]
    ;

fragment
IntegerSuffix
    :   UnsignedSuffix LongSuffix?
    |   UnsignedSuffix LongLongSuffix
    |   LongSuffix UnsignedSuffix?
    |   LongLongSuffix UnsignedSuffix?
    ;

fragment
UnsignedSuffix
    :   [uU]
    ;

fragment
LongSuffix
    :   [lL]
    ;

fragment
LongLongSuffix
    :   'll' | 'LL'
    ;

FloatingConstant
    :   DecimalFloatingConstant
    |   HexadecimalFloatingConstant
    ;

fragment
DecimalFloatingConstant
    :   FractionalConstant ExponentPart? FloatingSuffix?
    |   DigitSequence ExponentPart FloatingSuffix?
    ;

fragment
HexadecimalFloatingConstant
    :   HexadecimalPrefix (HexadecimalFractionalConstant | HexadecimalDigitSequence) BinaryExponentPart FloatingSuffix?
    ;

fragment
FractionalConstant
    :   DigitSequence? '.' DigitSequence
    |   DigitSequence '.'
    ;

fragment
ExponentPart
    :   [eE] Sign? DigitSequence
    ;

fragment
Sign
    :   [+-]
    ;

DigitSequence
    :   Digit+
    ;

fragment
HexadecimalFractionalConstant
    :   HexadecimalDigitSequence? '.' HexadecimalDigitSequence
    |   HexadecimalDigitSequence '.'
    ;

fragment
BinaryExponentPart
    :   [pP] Sign? DigitSequence
    ;

fragment
HexadecimalDigitSequence
    :   HexadecimalDigit+
    ;

fragment
FloatingSuffix
    :   [flFL]
    ;

CharacterConstant
    :   '\'' CCharSequence '\''
    |   'L\'' CCharSequence '\''
    |   'u\'' CCharSequence '\''
    |   'U\'' CCharSequence '\''
    ;

fragment
CCharSequence
    :   CChar+
    ;

fragment
CChar
    :   ~['\\\r\n]
    |   EscapeSequence
    ;

fragment
EscapeSequence
    :   SimpleEscapeSequence
    |   OctalEscapeSequence
    |   HexadecimalEscapeSequence
    |   UniversalCharacterName
    ;

fragment
SimpleEscapeSequence
    :   '\\' ['"?abfnrtv\\]
    ;

fragment
OctalEscapeSequence
    :   '\\' OctalDigit OctalDigit? OctalDigit?
    ;

fragment
HexadecimalEscapeSequence
    :   '\\x' HexadecimalDigit+
    ;

StringLiteral
    :   EncodingPrefix? '"' SCharSequence? '"'
    ;

fragment
EncodingPrefix
    :   'u8'
    |   'u'
    |   'U'
    |   'L'
    ;

fragment
SCharSequence
    :   SChar+
    ;

fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
    ;

ComplexDefine
    :   '#' Whitespace? 'define'  ~[#\r\n]*
        -> skip
    ;

IncludeDirective
    :   '#' Whitespace? 'include' Whitespace? (('"' ~[\r\n]* '"') | ('<' ~[\r\n]* '>' )) Whitespace? Newline
        -> skip
    ;

// ignore the following asm blocks:
/*
    asm
    {
        mfspr x, 286;
    }
 */
AsmBlock
    :   'asm' ~'{'* '{' ~'}'* '}'
	-> skip
    ;

// ignore the lines generated by c preprocessor
// sample line : '#line 1 "/home/dm/files/dk1.h" 1'
LineAfterPreprocessing
    :   '#line' Whitespace* ~[\r\n]*
        -> skip
    ;

LineDirective
    :   '#' Whitespace? DecimalConstant Whitespace? StringLiteral ~[\r\n]*
        -> skip
    ;

PragmaDirective
    :   '#' Whitespace? 'pragma' Whitespace ~[\r\n]*
        -> skip
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
    ;

LineComment
    :   '//' ~[\r\n]*
    ;