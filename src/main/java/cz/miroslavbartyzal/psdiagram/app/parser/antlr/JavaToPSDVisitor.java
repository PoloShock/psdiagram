package cz.miroslavbartyzal.psdiagram.app.parser.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8BaseVisitor;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.ExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.StatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.StatementNoShortIfContext;

public class JavaToPSDVisitor extends Java8BaseVisitor<Flowchart<LayoutSegment, LayoutElement>> {
	
	private static Flowchart<LayoutSegment, LayoutElement> flowchart;
	private static LayoutSegment actualSegment;
	private static LayoutElement lastElement;

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLiteral(Java8Parser.LiteralContext ctx) {
		////System.out.println("visit literal");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitType(Java8Parser.TypeContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimitiveType(Java8Parser.PrimitiveTypeContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitNumericType(Java8Parser.NumericTypeContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitIntegralType(Java8Parser.IntegralTypeContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}


	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFloatingPointType(Java8Parser.FloatingPointTypeContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitReferenceType(Java8Parser.ReferenceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassType(Java8Parser.ClassTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceType(Java8Parser.InterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceType_lf_classOrInterfaceType(
			Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceType_lfno_classOrInterfaceType(
			Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeVariable(Java8Parser.TypeVariableContext ctx) {
		//////System.out.println("type variable");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayType(Java8Parser.ArrayTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitDims(Java8Parser.DimsContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeParameter(Java8Parser.TypeParameterContext ctx) {
		////System.out.println("type parameter");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeBound(Java8Parser.TypeBoundContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAdditionalBound(Java8Parser.AdditionalBoundContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeArguments(Java8Parser.TypeArgumentsContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeArgumentList(Java8Parser.TypeArgumentListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeArgument(Java8Parser.TypeArgumentContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitWildcard(Java8Parser.WildcardContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitWildcardBounds(Java8Parser.WildcardBoundsContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPackageName(Java8Parser.PackageNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeName(Java8Parser.TypeNameContext ctx) {
		////System.out.println("type name context");
		////System.out.println(ctx.getText());
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodName(Java8Parser.MethodNameContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAmbiguousName(Java8Parser.AmbiguousNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
		
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPackageModifier(Java8Parser.PackageModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx) {
		////System.out.println("type declaration");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassModifier(Java8Parser.ClassModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeParameters(Java8Parser.TypeParametersContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeParameterList(Java8Parser.TypeParameterListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSuperclass(Java8Parser.SuperclassContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSuperinterfaces(Java8Parser.SuperinterfacesContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassBody(Java8Parser.ClassBodyContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFieldModifier(Java8Parser.FieldModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableInitializer(Java8Parser.VariableInitializerContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannType(Java8Parser.UnannTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannClassType(Java8Parser.UnannClassTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannClassType_lf_unannClassOrInterfaceType(
			Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannClassType_lfno_unannClassOrInterfaceType(
			Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannInterfaceType_lf_unannClassOrInterfaceType(
			Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannInterfaceType_lfno_unannClassOrInterfaceType(
			Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodModifier(Java8Parser.MethodModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodHeader(Java8Parser.MethodHeaderContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitResult(Java8Parser.ResultContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFormalParameterList(Java8Parser.FormalParameterListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFormalParameters(Java8Parser.FormalParametersContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFormalParameter(Java8Parser.FormalParameterContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableModifier(Java8Parser.VariableModifierContext ctx) {
		////System.out.println("variable modifier");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLastFormalParameter(Java8Parser.LastFormalParameterContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitReceiverParameter(Java8Parser.ReceiverParameterContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitThrows_(Java8Parser.Throws_Context ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExceptionType(Java8Parser.ExceptionTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodBody(Java8Parser.MethodBodyContext ctx) {
		actualSegment = new LayoutSegment(null);
        flowchart = new Flowchart<>(actualSegment);
        lastElement = actualSegment.addSymbol(null, EnumSymbol.STARTEND.getInstance(
                "Start"));
        actualSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("End"));
        visitChildren(ctx);
		return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInstanceInitializer(Java8Parser.InstanceInitializerContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStaticInitializer(Java8Parser.StaticInitializerContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstructorModifier(Java8Parser.ConstructorModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstructorBody(Java8Parser.ConstructorBodyContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumBody(Java8Parser.EnumBodyContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumConstantList(Java8Parser.EnumConstantListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumConstant(Java8Parser.EnumConstantContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceModifier(Java8Parser.InterfaceModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceBody(Java8Parser.InterfaceBodyContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstantModifier(Java8Parser.ConstantModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitDefaultValue(Java8Parser.DefaultValueContext ctx) {
		////System.out.println("visit default value");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAnnotation(Java8Parser.AnnotationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitNormalAnnotation(Java8Parser.NormalAnnotationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitElementValuePairList(Java8Parser.ElementValuePairListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitElementValuePair(Java8Parser.ElementValuePairContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitElementValue(Java8Parser.ElementValueContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitElementValueList(Java8Parser.ElementValueListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayInitializer(Java8Parser.ArrayInitializerContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitVariableInitializerList(Java8Parser.VariableInitializerListContext ctx) {
//		System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBlock(Java8Parser.BlockContext ctx) {
		
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBlockStatements(Java8Parser.BlockStatementsContext ctx) {
		////System.out.println("block statements");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBlockStatement(Java8Parser.BlockStatementContext ctx) {
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				for (int i=0; i<child.getChildCount(); i++) {
					//System.out.println(child.getChild(i).getClass().getSimpleName());
					//System.out.println(child.getChild(i).getText());
				}
				//System.out.println(child.getClass().getSimpleName());
				//System.out.println( child.getText());
			}
		} else {
			//System.out.println(ctx.getClass().getSimpleName());
			//System.out.println( ctx.getText());
		}
		visitChildren(ctx);
		return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
		////System.out.println("local variable declaration statement");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
		////System.out.println("local variable declaration");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStatement(Java8Parser.StatementContext ctx) {
		if (ctx.getText() != null && ctx.getText().contains("System.out.print")) {
			String toPrint = ctx.getText();
			int beginIndex = toPrint.indexOf('"');
			int endIndex = toPrint.lastIndexOf('"');
			toPrint = toPrint.substring(beginIndex+1, endIndex);
			Symbol symbol = EnumSymbol.IO.getInstance("");
			cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateOValues(symbol, '"'+toPrint+'"');
			symbol.setValueAndSize(symbol.getDefaultValue());
			lastElement = actualSegment.addSymbol(lastElement, symbol);
			
		}
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx) {
		String condition = "true"; 
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				visitChildren((RuleNode) child);
				if (child instanceof StatementContext) {
					visitChildren(((StatementContext)child));
				}
				if (child instanceof StatementNoShortIfContext) {
					visitChildren(((StatementNoShortIfContext)child));
				}
				if (child instanceof ExpressionContext) {
					condition = child.getText();
					visitChildren(((ExpressionContext)child));
				}
			}
		}
		lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.DECISION.getInstance(condition));
		visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStatementWithoutTrailingSubstatement(
			Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEmptyStatement(Java8Parser.EmptyStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLabeledStatement(Java8Parser.LabeledStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExpressionStatement(Java8Parser.ExpressionStatementContext ctx) {
		////System.out.println("visit expression");
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				////System.out.println(child.getClass().getName());
				////System.out.println(child.getText());
			}
		}
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStatementExpression(Java8Parser.StatementExpressionContext ctx) {
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				////System.out.println(child.getClass().getSimpleName());
				////System.out.println(child.getText());
			}
		}
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
		String condition = "true"; 
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				if (child instanceof ExpressionContext) {
					condition = child.getText();
				}
				System.out.println("if child: "+ child.getClass().getSimpleName() + " : " + child.getText());
			}
		}
		lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.DECISION.getInstance(condition));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
		String condition = "true"; 
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				if (child instanceof StatementContext) {
					visitChildren(((StatementContext)child));
				}
				if (child instanceof StatementNoShortIfContext) {
					visitChildren(((StatementNoShortIfContext)child));
				}
				if (child instanceof ExpressionContext) {
					condition = child.getText();
					visitChildren(((ExpressionContext)child));
				}
			}
		}
		lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.DECISION.getInstance(condition));
		visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAssertStatement(Java8Parser.AssertStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
		////System.out.println("visit switch statement");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSwitchBlock(Java8Parser.SwitchBlockContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSwitchLabels(Java8Parser.SwitchLabelsContext ctx) {
		////System.out.println("visit swithc labels");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSwitchLabel(Java8Parser.SwitchLabelContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnumConstantName(Java8Parser.EnumConstantNameContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitWhileStatement(Java8Parser.WhileStatementContext ctx) {
		////System.out.println("visit while statement");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitDoStatement(Java8Parser.DoStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitForStatement(Java8Parser.ForStatementContext ctx) {
		
		visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx) {
		////System.out.println("visit for statement no short if");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
		////System.out.println("basic for");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx) {
		////System.out.println("visit basic for statement no short if");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitForInit(Java8Parser.ForInitContext ctx) {
		////System.out.println("visit for statement init");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitForUpdate(Java8Parser.ForUpdateContext ctx) {
		////System.out.println("visit for statement update");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitStatementExpressionList(Java8Parser.StatementExpressionListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
		////System.out.println("visit for statement enchanced");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
		////System.out.println("visit for statement enchanced no short if");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitBreakStatement(Java8Parser.BreakStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitContinueStatement(Java8Parser.ContinueStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * { 
@inheritDoc
 }
	 *
	 * <p>The default implementation 
 returns the result of calling
	 * { 
@link #visitChildren
 } on { 
@code ctx
 }.</p>
	 */
	@Override 
    public Flowchart<LayoutSegment, LayoutElement> visitReturnStatement(Java8Parser.ReturnStatementContext ctx) { 
			////////System.out.println("visitChildren(ctx): "+visitChildren(ctx));
			System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart; 	
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitThrowStatement(Java8Parser.ThrowStatementContext ctx) {
		////////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTryStatement(Java8Parser.TryStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCatches(Java8Parser.CatchesContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCatchClause(Java8Parser.CatchClauseContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCatchType(Java8Parser.CatchTypeContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFinally_(Java8Parser.Finally_Context ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitResourceSpecification(Java8Parser.ResourceSpecificationContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitResourceList(Java8Parser.ResourceListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitResource(Java8Parser.ResourceContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimary(Java8Parser.PrimaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(
			Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(
			Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(
			Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(
			Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassInstanceCreationExpression_lf_primary(
			Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitClassInstanceCreationExpression_lfno_primary(
			Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx) {
		
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFieldAccess(Java8Parser.FieldAccessContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayAccess(Java8Parser.ArrayAccessContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
		if (ctx.getText() == "////System.out.println") {
			////System.out.println("output");
		}
		if (ctx.getChildCount() > 0) {
			for (ParseTree child : ctx.children) {
				////System.out.println(child.getClass().getSimpleName());
				////System.out.println(child.getText());
			}
		}
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArgumentList(Java8Parser.ArgumentListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodReference(Java8Parser.MethodReferenceContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitDimExprs(Java8Parser.DimExprsContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitDimExpr(Java8Parser.DimExprContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConstantExpression(Java8Parser.ConstantExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExpression(Java8Parser.ExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLambdaExpression(Java8Parser.LambdaExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLambdaParameters(Java8Parser.LambdaParametersContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLambdaBody(Java8Parser.LambdaBodyContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx) {
		////System.out.println("visit assigment");
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}
	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAssignment(Java8Parser.AssignmentContext ctx) {
		String value = "";
		for (ParseTree child : ctx.children) {
			System.out.println("assignment child:  " + child.getText());
			value += child.getText();
			}
		actualSegment.addSymbol(lastElement, EnumSymbol.PROCESS.getInstance(value));
		visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitLeftHandSide(Java8Parser.LeftHandSideContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConditionalExpression(Java8Parser.ConditionalExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAndExpression(Java8Parser.AndExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitEqualityExpression(Java8Parser.EqualityExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitRelationalExpression(Java8Parser.RelationalExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitShiftExpression(Java8Parser.ShiftExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnaryExpression(Java8Parser.UnaryExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPostfixExpression(Java8Parser.PostfixExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPostIncrementExpression_lf_postfixExpression(
			Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx) {
		//////System.out.println("visitChildren(ctx): " + visitChildren(ctx));
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitPostDecrementExpression_lf_postfixExpression(
			Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

	/**
	 * {
	 * 
	 * @inheritDoc }
	 *
	 *             <p>
	 *             The default implementation returns the result of calling {
	 * @link #visitChildren } on {
	 * @code ctx }.
	 *       </p>
	 */
	@Override
	public Flowchart<LayoutSegment, LayoutElement> visitCastExpression(Java8Parser.CastExpressionContext ctx) {
		System.out.println(ctx.getClass().getSimpleName()+" : "+ctx.getText());visitChildren(ctx);return flowchart;
	}

}
