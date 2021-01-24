package cz.miroslavbartyzal.psdiagram.app.parser.antlr;

import cz.miroslavbartyzal.psdiagram.app.parser.PSDGrammarParser;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDGrammarParserBaseVisitor;

public class PSDToJavaVisitor extends PSDGrammarParserBaseVisitor<String>{
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_ListOf_Constants(PSDGrammarParser.Solo_ListOf_ConstantsContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); 
	
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitListOf_Constants_RepeatingPart(PSDGrammarParser.ListOf_Constants_RepeatingPartContext ctx) { 
		System.out.println("visitListOfConstantsRepeatingPart");
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_solo(PSDGrammarParser.Constant_soloContext ctx) { 
		System.out.println("visitConstant_solo");
		return visitChildren(ctx); 	
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_ListOf_NumberConstants(PSDGrammarParser.Solo_ListOf_NumberConstantsContext ctx) { 
		System.out.println("visitSolo_ListOf_NumberConstants");
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitListOf_NumberConstants_RepeatingPart(PSDGrammarParser.ListOf_NumberConstants_RepeatingPartContext ctx) { 
		System.out.println("visitListOf_NumberConstants_RepeatingPart");
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_Number_solo(PSDGrammarParser.Constant_Number_soloContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_NoArrayVariableToAssignTo(PSDGrammarParser.Solo_NoArrayVariableToAssignToContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitArray_lookup_bracketsPart_multiple(PSDGrammarParser.Array_lookup_bracketsPart_multipleContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNoArrayVariableToAssignTo_solo_part(PSDGrammarParser.NoArrayVariableToAssignTo_solo_partContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_VariableToAssignTo(PSDGrammarParser.Solo_VariableToAssignToContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_NumericExpression(PSDGrammarParser.Solo_NumericExpressionContext ctx) { 
		
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_BooleanExpression(PSDGrammarParser.Solo_BooleanExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_StringExpression(PSDGrammarParser.Solo_StringExpressionContext ctx) {
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitSolo_Expression(PSDGrammarParser.Solo_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant(PSDGrammarParser.ConstantContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_Number(PSDGrammarParser.Constant_NumberContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_Number_Signed(PSDGrammarParser.Constant_Number_SignedContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_String(PSDGrammarParser.Constant_StringContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_Array(PSDGrammarParser.Constant_ArrayContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConstant_Array_RepeatingPart(PSDGrammarParser.Constant_Array_RepeatingPartContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitArrayLookup_BracketsPart(PSDGrammarParser.ArrayLookup_BracketsPartContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitArrayLookup_BracketsRepeatErrorPart(PSDGrammarParser.ArrayLookup_BracketsRepeatErrorPartContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitArrayIntegerIndex_Expression(PSDGrammarParser.ArrayIntegerIndex_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx);
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitVariableOrProperty(PSDGrammarParser.VariableOrPropertyContext ctx) {
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitVariableOrProperty_AfterDotPart(PSDGrammarParser.VariableOrProperty_AfterDotPartContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx);
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitVariableOrProperty_RightPart(PSDGrammarParser.VariableOrProperty_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitVariableOrProperty_WithoutConstants(PSDGrammarParser.VariableOrProperty_WithoutConstantsContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitVariable_Identifier(PSDGrammarParser.Variable_IdentifierContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitFunctionReturnValue(PSDGrammarParser.FunctionReturnValueContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitFunction_WithoutConstants(PSDGrammarParser.Function_WithoutConstantsContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitFunction_Parentheses_RepeatingPart(PSDGrammarParser.Function_Parentheses_RepeatingPartContext ctx) {
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitFunction_suffix(PSDGrammarParser.Function_suffixContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitUnknownTypeValue(PSDGrammarParser.UnknownTypeValueContext ctx) { 
		System.out.println(ctx.getStop().toString());

		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitExpression(PSDGrammarParser.ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitArray_Expression(PSDGrammarParser.Array_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitBoolean_Expression(PSDGrammarParser.Boolean_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNumeric_Expression(PSDGrammarParser.Numeric_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitString_Expression(PSDGrammarParser.String_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitString_Expression_Without_Numeric_And_NumericOrString_Prefix(PSDGrammarParser.String_Expression_Without_Numeric_And_NumericOrString_PrefixContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitString_Expression_InCommon(PSDGrammarParser.String_Expression_InCommonContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitString_Expression_RightPart(PSDGrammarParser.String_Expression_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitAtomic_String_Expression(PSDGrammarParser.Atomic_String_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNumericOrString_Expression(PSDGrammarParser.NumericOrString_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNumericOrString_Expression_Without_Numeric_Prefix(PSDGrammarParser.NumericOrString_Expression_Without_Numeric_PrefixContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNumericOrString_Expression_WithParetheses(PSDGrammarParser.NumericOrString_Expression_WithParethesesContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitNumericOrString_Expression_RightPart(PSDGrammarParser.NumericOrString_Expression_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalOr_Expression(PSDGrammarParser.ConditionalOr_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalOr_Expression_LeftPart(PSDGrammarParser.ConditionalOr_Expression_LeftPartContext ctx) {
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalOr_Expression_RightPart(PSDGrammarParser.ConditionalOr_Expression_RightPartContext ctx) {
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalAnd_Expression(PSDGrammarParser.ConditionalAnd_ExpressionContext ctx) {
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalAnd_Expression_LeftPart(PSDGrammarParser.ConditionalAnd_Expression_LeftPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitConditionalAnd_Expression_RightPart(PSDGrammarParser.ConditionalAnd_Expression_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitEquality_Expression(PSDGrammarParser.Equality_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitEquality_Expression_LeftPart(PSDGrammarParser.Equality_Expression_LeftPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitEquality_Expression_RightPart(PSDGrammarParser.Equality_Expression_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitRelational_Expression(PSDGrammarParser.Relational_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitRelational_Expression_LeftPart(PSDGrammarParser.Relational_Expression_LeftPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitAdditive_Expression(PSDGrammarParser.Additive_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitAdditive_Expression_LeftPart(PSDGrammarParser.Additive_Expression_LeftPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression(PSDGrammarParser.Multiplicative_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression_Operator(PSDGrammarParser.Multiplicative_Expression_OperatorContext ctx) {
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression_RightPart(PSDGrammarParser.Multiplicative_Expression_RightPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression_LeftPart_ValidLeft(PSDGrammarParser.Multiplicative_Expression_LeftPart_ValidLeftContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression_LeftPart_ValidRight(PSDGrammarParser.Multiplicative_Expression_LeftPart_ValidRightContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitMultiplicative_Expression_LeftPart(PSDGrammarParser.Multiplicative_Expression_LeftPartContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitUnary_Numeric_Expression(PSDGrammarParser.Unary_Numeric_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitAtomic_Numeric_Expression(PSDGrammarParser.Atomic_Numeric_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitUnary_Boolean_Expression(PSDGrammarParser.Unary_Boolean_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitAtomic_Boolean_Expression(PSDGrammarParser.Atomic_Boolean_ExpressionContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); 
		
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitUnaryPlusOrMinusOperator(PSDGrammarParser.UnaryPlusOrMinusOperatorContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public String visitIncrementDecrementOperatorSyntaxError(PSDGrammarParser.IncrementDecrementOperatorSyntaxErrorContext ctx) { 
		System.out.println(ctx.getStop().toString());
		return visitChildren(ctx); }
}
