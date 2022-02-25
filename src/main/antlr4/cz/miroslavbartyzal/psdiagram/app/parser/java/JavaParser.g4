/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr, Sam Harwell
 Copyright (c) 2017 Ivan Kochurkin (upgrade to Java 8)
 Copyright (c) 2021 Michał Lorek (upgrade to Java 11)
 Copyright (c) 2022 Michał Lorek (upgrade to Java 17)
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

parser grammar JavaParser;

options { tokenVocab=JavaLexer; }

@parser::header {
	import java.util.Set;
	import java.util.HashSet;
}

@parser::members {
	private final Set<String> integerIdentifiers = new HashSet<>();
}

compilationUnit
    : packageDeclaration? importDeclaration* typeDeclaration*
    | moduleDeclaration EOF
    ;

packageDeclaration
    : annotation* PACKAGE qualifiedName ';'
    ;

importDeclaration
    : IMPORT STATIC? qualifiedName ('.' '*')? ';'
    ;

typeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | enumDeclaration | interfaceDeclaration | annotationTypeDeclaration | recordDeclaration)
    | ';'
    ;

modifier
    : classOrInterfaceModifier
    | NATIVE
    | SYNCHRONIZED
    | TRANSIENT
    | VOLATILE
    ;

classOrInterfaceModifier
    : annotation
    | PUBLIC
    | PROTECTED
    | PRIVATE
    | STATIC
    | ABSTRACT
    | FINAL    // FINAL for class only -- does not apply to interfaces
    | STRICTFP
    | SEALED // Java17
    | NON_SEALED // Java17
    ;

variableModifier
    : FINAL
    | annotation
    ;

classDeclaration
    : CLASS identifier typeParameters?
      (EXTENDS typeType)?
      (IMPLEMENTS typeList)?
      (PERMITS typeList)? // Java17
      classBody
    ;

typeParameters
    : '<' typeParameter (',' typeParameter)* '>'
    ;

typeParameter
    : annotation* identifier (EXTENDS annotation* typeBound)?
    ;

typeBound
    : typeType ('&' typeType)*
    ;

enumDeclaration
    : ENUM identifier (IMPLEMENTS typeList)? '{' enumConstants? ','? enumBodyDeclarations? '}'
    ;

enumConstants
    : enumConstant (',' enumConstant)*
    ;

enumConstant
    : annotation* identifier arguments? classBody?
    ;

enumBodyDeclarations
    : ';' classBodyDeclaration*
    ;

interfaceDeclaration
    : INTERFACE identifier typeParameters? (EXTENDS typeList)? interfaceBody
    ;

classBody
    : '{' classBodyDeclaration* '}'
    ;

interfaceBody
    : '{' interfaceBodyDeclaration* '}'
    ;

classBodyDeclaration
    : ';'
    | STATIC? block
    | modifier* memberDeclaration
    ;

memberDeclaration
    : methodDeclaration
    | genericMethodDeclaration
    | fieldDeclaration
    | constructorDeclaration
    | genericConstructorDeclaration
    | interfaceDeclaration
    | annotationTypeDeclaration
    | classDeclaration
    | enumDeclaration
    | recordDeclaration //Java17
    ;

/* We use rule this even for void methods which cannot have [] after parameters.
   This simplifies grammar and we can consider void to be a type, which
   renders the [] matching as a context-sensitive issue or a semantic check
   for invalid return type after parsing.
 */
methodDeclaration
    : typeTypeOrVoid identifier formalParameters dims?
      (THROWS qualifiedNameList)?
      methodBody
    ;

methodBody
    : block
    | ';'
    ;

typeTypeOrVoid
    : typeType
    | VOID
    ;

genericMethodDeclaration
    : typeParameters methodDeclaration
    ;

genericConstructorDeclaration
    : typeParameters constructorDeclaration
    ;

constructorDeclaration
    : identifier formalParameters (THROWS qualifiedNameList)? constructorBody=block
    ;

fieldDeclaration
    : typeType variableDeclarators ';'
    ;

interfaceBodyDeclaration
    : modifier* interfaceMemberDeclaration
    | ';'
    ;

interfaceMemberDeclaration
    : constDeclaration
    | interfaceMethodDeclaration
    | genericInterfaceMethodDeclaration
    | interfaceDeclaration
    | annotationTypeDeclaration
    | classDeclaration
    | enumDeclaration
    | recordDeclaration // Java17
    ;

constDeclaration
    : typeType constantDeclarator (',' constantDeclarator)* ';'
    ;

constantDeclarator
    : identifier dims? '=' variableInitializer
    ;

// Early versions of Java allows brackets after the method name, eg.
// public int[] return2DArray() [] { ... }
// is the same as
// public int[][] return2DArray() { ... }
interfaceMethodDeclaration
    : interfaceMethodModifier* interfaceCommonBodyDeclaration
    ;

// Java8
interfaceMethodModifier
    : annotation
    | PUBLIC
    | ABSTRACT
    | DEFAULT
    | STATIC
    | STRICTFP
    ;

genericInterfaceMethodDeclaration
    : interfaceMethodModifier* typeParameters interfaceCommonBodyDeclaration
    ;

interfaceCommonBodyDeclaration
    : annotation* typeTypeOrVoid identifier formalParameters dims? (THROWS qualifiedNameList)? methodBody
    ;

variableDeclarators
    : variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    : variableDeclaratorId ('=' variableInitializer)?
    ;

variableDeclaratorId
    : identifier dims?
    ;

variableInitializer
    : arrayInitializer
    | expression
    ;

arrayInitializer
    : '{' variableInitializerList? '}'
    ;

variableInitializerList
	: variableInitializer (',' variableInitializer)* (',')?
	;

classOrInterfaceType returns [boolean isInteger]
    : a=identifier typeArguments? ('.' identifier typeArguments?)* {$isInteger = $a.ctx.getText().matches("^(Integer|Long|Short|Byte|Character)$");}
    ;

typeArgument
    : typeType
    | annotation* '?' ((EXTENDS | SUPER) typeType)?
    ;

qualifiedNameList
    : qualifiedName (',' qualifiedName)*
    ;

formalParameters
    : '(' ( receiverParameter?
          | receiverParameter (',' formalParameterList)?
          | formalParameterList?
          ) ')'
    ;

receiverParameter
    : typeType (identifier '.')* THIS
    ;

formalParameterList
    : formalParameter (',' formalParameter)* (',' lastFormalParameter)?
    | lastFormalParameter
    ;

formalParameter
    : variableModifier* typeType variableDeclaratorId
    ;

lastFormalParameter
    : variableModifier* typeType annotation* '...' variableDeclaratorId
    ;

// local variable type inference
lambdaLVTIList
    : lambdaLVTIParameter (',' lambdaLVTIParameter)*
    ;

lambdaLVTIParameter
    : variableModifier* VAR identifier
    ;

qualifiedName
    : identifier ('.' identifier)*
    ;

literal returns [boolean isInteger]
    : integerLiteral {$isInteger = true;}
    | floatLiteral
    | CHAR_LITERAL {$isInteger = true;}
    | STRING_LITERAL
    | BOOL_LITERAL
    | NULL_LITERAL
    | TEXT_BLOCK // Java17
    ;

integerLiteral
    : DECIMAL_LITERAL
    | HEX_LITERAL
    | OCT_LITERAL
    | BINARY_LITERAL
    ;

floatLiteral
    : FLOAT_LITERAL
    | HEX_FLOAT_LITERAL
    ;

// ANNOTATIONS
altAnnotationQualifiedName
    : (identifier DOT)* '@' identifier
    ;

annotation
    : ('@' qualifiedName | altAnnotationQualifiedName) ('(' ( elementValuePairs | elementValue )? ')')?
    ;

elementValuePairs
    : elementValuePair (',' elementValuePair)*
    ;

elementValuePair
    : identifier '=' elementValue
    ;

elementValue
    : expression
    | annotation
    | elementValueArrayInitializer
    ;

elementValueArrayInitializer
    : '{' (elementValue (',' elementValue)*)? (',')? '}'
    ;

annotationTypeDeclaration
    : '@' INTERFACE identifier annotationTypeBody
    ;

annotationTypeBody
    : '{' (annotationTypeElementDeclaration)* '}'
    ;

annotationTypeElementDeclaration
    : modifier* annotationTypeElementRest
    | ';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;

annotationTypeElementRest
    : typeType annotationMethodOrConstantRest ';'
    | classDeclaration ';'?
    | interfaceDeclaration ';'?
    | enumDeclaration ';'?
    | annotationTypeDeclaration ';'?
    | recordDeclaration ';'? // Java17
    ;

annotationMethodOrConstantRest
    : annotationMethodRest
    | annotationConstantRest
    ;

annotationMethodRest
    : identifier '(' ')' defaultValue?
    ;

annotationConstantRest
    : variableDeclarators
    ;

defaultValue
    : DEFAULT elementValue
    ;

// MODULES - Java9

moduleDeclaration
    : OPEN? MODULE qualifiedName moduleBody
    ;

moduleBody
    : '{' moduleDirective* '}'
    ;

moduleDirective
	: REQUIRES requiresModifier* qualifiedName ';'
	| EXPORTS qualifiedName (TO qualifiedName)? ';'
	| OPENS qualifiedName (TO qualifiedName)? ';'
	| USES qualifiedName ';'
	| PROVIDES qualifiedName WITH qualifiedName ';'
	;

requiresModifier
	: TRANSITIVE
	| STATIC
	;

// RECORDS - Java 17

recordDeclaration
    : RECORD identifier typeParameters? recordHeader
      (IMPLEMENTS typeList)?
      recordBody
    ;

recordHeader
    : '(' recordComponentList? ')'
    ;

recordComponentList
    : recordComponent (',' recordComponent)*
    ;

recordComponent
    : typeType identifier
    ;

recordBody
    : '{' classBodyDeclaration* '}'
    ;

// STATEMENTS / BLOCKS

block
    : '{' blockStatementsOptional '}'
    ;

blockStatementsOptional
	:	blockStatement*
	;

blockStatements
	: blockStatement+
	;

blockStatement
    : localVariableDeclaration ';'
    | statement
    | localTypeDeclaration
    | comment
    ;

comment
    : COMMENT
    | LINE_COMMENT
    ;
    
commentAfterStatement
    : COMMENT
    | LINE_COMMENT
    ;

localVariableDeclaration
    : variableModifier* (
			typeType variableDeclarators {
				if ($typeType.isInteger) {
					for (var _variableDeclarator : $variableDeclarators.ctx.variableDeclarator()) {
						String _identifier = _variableDeclarator.variableDeclaratorId().identifier().getText();
						integerIdentifiers.add(_identifier);
					}
				}
			}
		|	VAR identifier '=' expression
    	)
    ;

identifier returns [boolean isInteger]
    : IDENTIFIER {$isInteger = integerIdentifiers.contains($IDENTIFIER.text);}
    | MODULE
    | OPEN
    | REQUIRES
    | EXPORTS
    | OPENS
    | TO
    | USES
    | PROVIDES
    | WITH
    | TRANSITIVE
    | YIELD
    | SEALED
    | PERMITS
    | RECORD
    | VAR
    ;

localTypeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | interfaceDeclaration | recordDeclaration)
    | ';'
    ;

statement
    : blockLabel=block														# compoundStatement
    | ASSERT expression (':' expression)? ';'								# assertStatement
    | IF parExpression commentAfterStatement? statement (ELSE statement)?	# ifStatement
    | FOR '(' forControl ')' commentAfterStatement? statement				# forStatement
    | WHILE parExpression commentAfterStatement? statement					# whileStatement
    | DO commentAfterStatement? statement WHILE parExpression ';'			# doStatement
    | TRY block (catchClause+ finallyBlock? | finallyBlock)					# tryStatement
    | TRY resourceSpecification block catchClause* finallyBlock?			# tryStatementWithResources
    | SWITCH parExpression commentAfterStatement? '{' switchBlockStatementGroup* switchLabel* '}'		# switchStatement
    | SYNCHRONIZED parExpression block										# synchronizedStatement
    | RETURN expression? ';'												# returnStatement
    | THROW expression ';'													# throwStatement
    | BREAK identifier? ';'													# breakStatement
    | CONTINUE identifier? ';'												# continueStatement
    | YIELD expression ';'													# yieldStatement // Java17
    | SEMI																	# emptyStatement
    | switchExpression ';'?													# switchStatementJava17 // Java17
    | statementExpression=expression ';'									# expressionStatement
    | identifierLabel=identifier ':' statement								# labeledStatement
    ;

catchClause
    : CATCH '(' variableModifier* catchType identifier ')' block
    ;

catchType
    : qualifiedName ('|' qualifiedName)*
    ;

finallyBlock
    : FINALLY block
    ;

resourceSpecification
    : '(' resources ';'? ')'
    ;

resources
    : resource (';' resource)*
    ;

resource
    : variableModifier* ( classOrInterfaceType variableDeclaratorId | VAR identifier ) '=' expression
    | identifier
    ;

/** Matches cases then statements, both of which are mandatory.
 *  To handle empty cases at the end, we add switchLabel* to statement.
 */
switchBlockStatementGroup
    : switchLabel+ blockStatements
    ;

switchLabel
    : CASE switchLabelCase ':'
    | DEFAULT ':'
    ;

switchLabelCase
	: constantExpression=expression
	| enumConstantName=IDENTIFIER
	| typeType varName=identifier
	;

forControl
    : enhancedForControl
    | basicForControl
    ;

basicForControl
	: forInit? ';' expression? ';' forUpdate?
	;

forInit
    : localVariableDeclaration
    | expressionList
    ;

enhancedForControl
    : variableModifier* (typeType | VAR) variableDeclaratorId ':' expression
    ;

// EXPRESSIONS

parExpression
    : '(' expression ')'
    ;

forUpdate
	: expressionList
	;

expressionList
    : expression (',' expression)*
    ;

methodCall
    : identifier '(' expressionList? ')'
    | THIS '(' expressionList? ')'
    | SUPER '(' expressionList? ')'
    ;

assignmentOperator
	:	'='
	|	'*='
	|	'/='
	|	'%='
	|	'+='
	|	'-='
	|	'<<='
	|	'>>='
	|	'>>>='
	|	'&='
	|	'^='
	|	'|='
	;

relationalOperator
	:	'<'
	|	'>'
	|	'<='
	|	'>='
	;

multiplicativeOperator
	:	'*'
	|	'/'
	|	'%'
	;

additiveOperator
	:	'+'
	|	'-'
	;

expression returns [boolean isInteger]
    : primary {$isInteger = $primary.isInteger;}							# exp1
    | expression bop='.'
      (
         identifier
       | methodCall
       | THIS
       | NEW nonWildcardTypeArguments? innerCreator
       | SUPER superSuffix
       | explicitGenericInvocation
      )																		# invocation
    | ar=expression '[' expression ']' {$isInteger = $ar.isInteger;}		# array
    | methodCall															# methodInvocation
    | NEW creator {$isInteger = $creator.isInteger;}						# new
    | '(' annotation* typeType ('&' typeType)* ')' expression {
    	for (var _typeType : ((CastContext)$ctx).typeType()) {
			if (_typeType.isInteger) {
				$isInteger = true;
				break;
			}
		}
    }																		# cast
    | e=expression postfix=(INC | DEC) {$isInteger = $e.isInteger;}			# post
    | prefix=(INC|DEC) expression {$isInteger = $expression.isInteger;}		# pre
    | prefix=('+'|'-') expression {$isInteger = $expression.isInteger;}		# unary1
    | prefix=('~'|'!') expression {$isInteger = $prefix.type == TILDE;}		# unary2
    | a=expression multiplicativeOperator b=expression {
    	$isInteger = $a.isInteger && $b.isInteger;
    }																		# multiplicative
    | a=expression additiveOperator b=expression {
    	$isInteger = $a.isInteger && $b.isInteger;
    }																		# additive
    | a=expression ('<' '<' | '>' '>' '>' | '>' '>') b=expression {
    	$isInteger = true;
    }																		# shift
    | expression relationalOperator expression								# relational
    | expression bop=INSTANCEOF (typeType | pattern)						# instanceOf
    | expression bop=('==' | '!=') expression								# equality
    | expression bop='&' expression	{$isInteger = true;}					# and
    | expression bop='^' expression	{$isInteger = true;}					# exclusiveOr
    | expression bop='|' expression	{$isInteger = true;}					# inclusiveOr
    | expression bop='&&' expression										# conditionalAnd
    | expression bop='||' expression										# conditionalOr
    | <assoc=right> expression bop='?' expression ':' expression			# ternaryConditional
    | <assoc=right> a=expression assignmentOperator expression {
    	$isInteger = $a.isInteger;
    }																		# assignment
    | lambdaExpression														# lambda // Java8
    | switchExpression														# switchExpressionJava17 // Java17

    // Java 8 methodReference
    | expression '::' typeArguments? identifier								# methodReference1
    | typeType '::' (typeArguments? identifier | NEW)						# methodReference2
    | classType '::' typeArguments? NEW										# methodReference3
    ;

// Java17
pattern
    : variableModifier* typeType annotation* identifier
    ;

// Java8
lambdaExpression
    : lambdaParameters '->' lambdaBody
    ;

// Java8
lambdaParameters
    : identifier
    | '(' formalParameterList? ')'
    | '(' identifier (',' identifier)* ')'
    | '(' lambdaLVTIList? ')'
    ;

// Java8
lambdaBody
    : expression
    | block
    ;

primary returns [boolean isInteger]
    : '(' expression ')' {$isInteger = $expression.isInteger;}
    | THIS
    | SUPER
    | literal {$isInteger = $literal.isInteger;}
    | identifier {$isInteger = $identifier.isInteger;}
    | typeTypeOrVoid '.' CLASS
    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
    ;

// Java17
switchExpression
    : SWITCH parExpression '{' switchLabeledRule* '}'
    ;

// Java17
switchLabeledRule
    : CASE (expressionList | NULL_LITERAL | guardedPattern) (ARROW | COLON) switchRuleOutcome
    | DEFAULT (ARROW | COLON) switchRuleOutcome
    ;

// Java17
guardedPattern
    : '(' guardedPattern ')'
    | variableModifier* typeType annotation* identifier ('&&' expression)*
    | guardedPattern '&&' expression
    ;

// Java17
switchRuleOutcome
    : block
    | blockStatement*
    ;

classType
    : (classOrInterfaceType '.')? annotation* identifier typeArguments?
    ;

creator returns [boolean isInteger]
    : nonWildcardTypeArguments createdName classCreatorRest
    | createdName (arrayCreatorRest | classCreatorRest) {$isInteger = $createdName.isInteger;}
    ;

createdName returns [boolean isInteger]
    : identifier typeArgumentsOrDiamond? ('.' identifier typeArgumentsOrDiamond?)*
    | primitiveType {$isInteger = $primitiveType.isInteger;}
    ;

innerCreator
    : identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

//arrayCreatorRest
//    : '[' (']' dim* arrayInitializer | expression ']' ('[' expression ']')* dim*)
//    ;
    
arrayCreatorRest
    : dims arrayInitializer
    | dimExprs dims?
    ;

dimExprs
	:	dimExpr dimExpr*
	;

dimExpr
	:	'[' expression ']'
	;

dims
	:	'[' ']' ('[' ']')*
	;

classCreatorRest
    : arguments classBody?
    ;

explicitGenericInvocation
    : nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

typeArgumentsOrDiamond
    : '<' '>'
    | typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    : '<' '>'
    | nonWildcardTypeArguments
    ;

nonWildcardTypeArguments
    : '<' typeList '>'
    ;

typeList
    : typeType (',' typeType)*
    ;

typeType returns [boolean isInteger]
    : annotation* (
    		classOrInterfaceType {
    			$isInteger = $classOrInterfaceType.isInteger;
    		}
    	|	primitiveType {
    			$isInteger = $primitiveType.isInteger;
    		}
    	) (annotation* '[' ']')*
    ;

primitiveType returns [boolean isInteger]
    : BOOLEAN
    | numericType {$isInteger = $numericType.isInteger;}
    ;

numericType returns [boolean isInteger]
	:	integralType {$isInteger = true;}
	|	floatingPointType
	;

integralType
	: CHAR
    | BYTE
    | SHORT
    | INT
    | LONG
    ;

floatingPointType
    : FLOAT
    | DOUBLE
    ;

typeArguments
    : '<' typeArgument (',' typeArgument)* '>'
    ;

superSuffix
    : arguments
    | '.' typeArguments? identifier arguments?
    ;

explicitGenericInvocationSuffix
    : SUPER superSuffix
    | identifier arguments
    ;

arguments
    : '(' expressionList? ')'
    ;