package cz.miroslavbartyzal.psdiagram.app.parser.java;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch;
import cz.miroslavbartyzal.psdiagram.app.parser.CannotParseException;
import cz.miroslavbartyzal.psdiagram.app.parser.FlowchartGenerator;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AdditiveContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AdditiveOperatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AnnotationContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ArrayCreatorRestContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ArrayInitializerContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AssertStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AssignmentContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.AssignmentOperatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.BasicForControlContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.BlockContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.BlockStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.BlockStatementsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.BreakStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.CatchClauseContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.CommentAfterStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.CommentContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ContinueStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.CreatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.DimExprsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.DimsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.DoStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.EnhancedForControlContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ExpressionListContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ExpressionStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.FinallyBlockContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ForControlContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ForInitContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ForStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ForUpdateContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.IdentifierContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.IfStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.InvocationContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.LabeledStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.LiteralContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.LocalVariableDeclarationContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.MethodCallContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.MethodInvocationContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.MultiplicativeContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.MultiplicativeOperatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.NewContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.NumericTypeContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ParExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.PostContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.PreContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.PrimitiveTypeContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.RelationalContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.RelationalOperatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ResourceContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ReturnStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.StatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SwitchBlockStatementGroupContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SwitchExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SwitchLabelContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SwitchStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SwitchStatementJava17Context;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.SynchronizedStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.ThrowStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.TryStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.TryStatementWithResourcesContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.TypeTypeContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.VariableDeclaratorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.VariableDeclaratorIdContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.VariableInitializerContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.VariableInitializerListContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.WhileStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser.YieldStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.java.pojo.AssignmentFields;
import cz.miroslavbartyzal.psdiagram.app.parser.java.pojo.BasicForIncField;
import cz.miroslavbartyzal.psdiagram.app.parser.java.pojo.BasicForToField;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaToFlowchartVisitor extends JavaParserBaseVisitor<String> implements FlowchartGenerator
{
    
    private final List<Class<?>> loopStatementClasses =
            List.of(ForStatementContext.class, WhileStatementContext.class, DoStatementContext.class);
    
    private final String input;
    private final Flowchart<LayoutSegment, LayoutElement> flowchart;
    private LayoutSegment currentSegment;
    private LayoutElement lastElement;
    
    private int namedSwitchesCount;
    private final Deque<String> switchNamesStack = new ArrayDeque<>();
    
    private final Set<String> scannerVariables = new HashSet<>();
    private final Set<String> consoleVariables = new HashSet<>();
    
    public JavaToFlowchartVisitor(String input)
    {
        this.input = input;
        
        currentSegment = new LayoutSegment(null);
        flowchart = new Flowchart<>(currentSegment);
        lastElement = currentSegment.addSymbol(null, EnumSymbol.STARTEND.getInstance(
                "Začátek"));
        currentSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("Konec"));
    }
    
    public Flowchart<LayoutSegment, LayoutElement> getFlowchart()
    {
        return flowchart;
    }
    
    @Override
    public String visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx)
    {
        if (ctx.identifier() != null) {
            // a VAR declaration
            return visitLocalVarVariableDeclaration(ctx);
        }
        
        try {
            for (VariableDeclaratorContext variableDeclaratorContext : ctx.variableDeclarators().variableDeclarator()) {
                VariableDeclaratorIdContext variableDeclaratorIdContext =
                        variableDeclaratorContext.variableDeclaratorId();
                VariableInitializerContext variableInitializerContext = variableDeclaratorContext.variableInitializer();
                if (variableInitializerContext == null) {
                    continue;
                }
                
                String variable = visit(variableDeclaratorIdContext).trim();
                
                if (isNewScanner(variableInitializerContext.expression())) {
                    scannerVariables.add(variable);
                    return "--VARIABLE-DECLARATION-(SCANNER)--";
                }
                if (isConsole(variableInitializerContext.expression())) {
                    consoleVariables.add(variable);
                    return "--VARIABLE-DECLARATION-(CONSOLE)--";
                }
                if (isInput(variableInitializerContext.expression())) {
                    addInput(variable, ctx);
                    return "--IO-(SCANNER)--";
                }
                
                String value = visit(variableInitializerContext).trim();
                if (!Process.areValuesValid(variable, value)) {
                    throw new CannotParseException("Values are not PSD valid");
                }
                
                Symbol symbol = EnumSymbol.PROCESS.getInstance("");
                Process.generateValues(symbol, variable, value);
                symbol.setValueAndSize(symbol.getDefaultValue());
                lastElement = currentSegment.addSymbol(lastElement, symbol);
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            resolveParseException(EnumSymbol.PROCESS, ctx);
        }
        
        return "--VARIABLE-DECLARATION--";
    }
    
    private String visitLocalVarVariableDeclaration(LocalVariableDeclarationContext ctx)
    {
        try {
            String variable = visit(ctx.identifier()).trim();
            
            if (isNewScanner(ctx.expression())) {
                scannerVariables.add(variable);
                return "--VAR-VARIABLE-DECLARATION-(SCANNER)--";
            }
            if (isConsole(ctx.expression())) {
                consoleVariables.add(variable);
                return "--VAR-VARIABLE-DECLARATION-(CONSOLE)--";
            }
            if (isInput(ctx.expression())) {
                addInput(variable, ctx);
                return "--IO-(SCANNER)--";
            }
            
            String value = visit(ctx.expression()).trim();
            
            Symbol symbol = EnumSymbol.PROCESS.getInstance("");
            Process.generateValues(symbol, variable, value);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            resolveParseException(EnumSymbol.PROCESS, ctx);
        }
        
        return "--VAR-VARIABLE-DECLARATION--";
    }
    
    @Override
    public String visitAssignment(AssignmentContext ctx)
    {
        return visitAssignment(ctx, true);
    }
    
    private String visitAssignment(AssignmentContext ctx, boolean performAncestorCheck)
    {
        if (performAncestorCheck) {
            ParserRuleContext parent = ctx.getParent();
            while (parent instanceof AssignmentContext) {
                parent = parent.getParent();
            }
            if (!ancestorExists(parent, ExpressionStatementContext.class, 0)) {
                return visitChildren(ctx);
            }
        }
        
        try {
            AssignmentFields assignmentFields = extractAssignmentFields(ctx);
            
            String variable = assignmentFields.getVariable().trim();
            
            if (isNewScanner(ctx.expression(1))) {
                scannerVariables.add(variable);
                return variable;
            }
            if (isConsole(ctx.expression(1))) {
                consoleVariables.add(variable);
                return variable;
            }
            if (isInput(ctx.expression(1))) {
                addInput(variable, ctx);
                return "--IO-(SCANNER)--";
            }
            
            String valueProcessed = assignmentFields.getValue().trim();
            
            if (!Process.areValuesValid(variable, valueProcessed)) {
                throw new CannotParseException("Values are not PSD valid");
            }
            
            Symbol symbol = EnumSymbol.PROCESS.getInstance("");
            Process.generateValues(symbol, variable, valueProcessed);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            return variable;
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            resolveParseException(EnumSymbol.PROCESS, ctx);
            return "";
        }
    }
    
    private void addInput(String variable, ParserRuleContext ctx)
    {
        if (!IO.isIVarValid(variable)) {
            resolveParseException(EnumSymbol.IO, ctx);
            return;
        }
        
        Symbol symbol = EnumSymbol.IO.getInstance("");
        IO.generateIValues(symbol, variable);
        symbol.setValueAndSize(symbol.getDefaultValue());
        lastElement = currentSegment.addSymbol(lastElement, symbol);
    }
    
    @Override
    public String visitPost(PostContext ctx)
    {
        return visitGeneralCrementExpression(ctx, ctx.expression(), ctx.postfix, true);
    }
    
    @Override
    public String visitPre(PreContext ctx)
    {
        return visitGeneralCrementExpression(ctx, ctx.expression(), ctx.prefix, true);
    }
    
    private String visitGeneralCrementExpression(ParserRuleContext originalContext, ExpressionContext expressionContext,
            Token operator, boolean performAncestorCheck)
    {
        if (performAncestorCheck && !ancestorExists(originalContext, ExpressionStatementContext.class, 1)) {
            return visitChildren(originalContext);
        }
        
        try {
            IdentifierContext identifierContext =
                    getTheOnlyChildFromSingleChildrenTree(expressionContext, IdentifierContext.class, 2);
            String variable = visit(identifierContext).trim();
            
            String value = variable;
            if (operator.getType() == JavaParser.INC) {
                value += " + 1";
            } else if (operator.getType() == JavaParser.DEC) {
                value += " - 1";
            } else {
                throw new CannotParseException(String.format("Unexpected operator ('%s')", operator.getText()));
            }
            
            Symbol symbol = EnumSymbol.PROCESS.getInstance("");
            Process.generateValues(symbol, variable, value);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            resolveParseException(EnumSymbol.PROCESS, originalContext);
        }
        
        return "--CREMENT--";
    }
    
    @Override
    public String visitMethodInvocation(MethodInvocationContext ctx)
    {
        return visitGeneralInvocation(ctx, true);
    }
    
    @Override
    public String visitInvocation(InvocationContext ctx)
    {
        return visitGeneralInvocation(ctx, true);
    }
    
    private String visitGeneralInvocation(ParserRuleContext ctx, boolean performAncestorCheck)
    {
        if (performAncestorCheck && !ancestorExists(ctx, ExpressionStatementContext.class, 1)) {
            return visitChildren(ctx);
        }
        
        try {
            if (ctx.getText().matches("^System\\.out\\.print(ln|f)?\\(.+\\)$")) {
                ExpressionContext firstExpressionContext = ctx
                        .getChild(MethodCallContext.class, 0)
                        .getChild(ExpressionListContext.class, 0)
                        .getChild(ExpressionContext.class, 0);
                
                String outputValue = visit(firstExpressionContext).trim();
                
                if (!IO.isOValueValid(outputValue)) {
                    throw new CannotParseException(ctx, input);
                }
                
                Symbol symbol = EnumSymbol.IO.getInstance("");
                IO.generateOValues(symbol, outputValue);
                symbol.setValueAndSize(symbol.getDefaultValue());
                lastElement = currentSegment.addSymbol(lastElement, symbol);
                
                return visitChildren(ctx);
            }
            if (ctx.getText().matches("^System\\.exit\\(.+\\)$")) {
                lastElement = currentSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("Konec"));
                return visitChildren(ctx);
            }
            
            String value = visitChildren(ctx);
            
            Symbol symbol = EnumSymbol.SUBROUTINE.getInstance(value);
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            return value;
            
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            resolveParseException(EnumSymbol.SUBROUTINE, ctx);
            return "";
        }
    }
    
    @Override
    public String visitIfStatement(IfStatementContext ctx)
    {
        ParExpressionContext parExpressionContext = ctx.parExpression();
        CommentAfterStatementContext commentContext = ctx.commentAfterStatement();
        StatementContext ifSegmentContext = ctx.statement(0);
        StatementContext elseSegmentContext = ctx.statement(1);
        try {
            ExpressionContext conditionExpressionContext = parExpressionContext.expression();
            
            String condition = visit(conditionExpressionContext).trim();
            
            if (!Decision.isConditionValid(condition)) {
                throw new CannotParseException(parExpressionContext, input);
            }
            
            Symbol symbol = EnumSymbol.DECISION.getInstance("");
            Decision.generateValues(symbol, condition);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            visitIfNotNull(commentContext);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            int startIdx = ctx.getStart().getStartIndex();
            int stopIdx = parExpressionContext.getStop().getStopIndex() + 1;
            resolveParseException(EnumSymbol.DECISION, startIdx, stopIdx);
            
            LayoutSegment originalSegment = currentSegment;
            LayoutElement originalElement = lastElement;
            currentSegment = lastElement.getInnerSegment(1);
            visitIfNotNull(commentContext);
            currentSegment = originalSegment;
            lastElement = originalElement;
        }
        
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        currentSegment = lastElement.getInnerSegment(1);
        visitIfNotNull(ifSegmentContext);
        currentSegment = originalSegment;
        lastElement = originalElement;
        
        if (elseSegmentContext != null) {
            currentSegment = lastElement.getInnerSegment(0);
            visitIfNotNull(elseSegmentContext);
            currentSegment = originalSegment;
            lastElement = originalElement;
        }
        
        return "--IF(+ELSE)--";
    }
    
    @Override
    public String visitSwitchStatementJava17(SwitchStatementJava17Context ctx)
    {
        resolveParseException(EnumSymbol.SWITCH, ctx, 1);
        return "--SWITCH-Java17--";
    }
    
    @Override
    public String visitSwitchExpression(SwitchExpressionContext ctx)
    {
        throw new CannotParseException("Java 17 Switch expression is not supported");
    }
    
    @Override
    public String visitSwitchStatement(SwitchStatementContext ctx)
    {
        String conditionVar;
        List<String> segmentCases;
        Map<String, BlockStatementsContext> segments = new LinkedHashMap<>();
        CommentAfterStatementContext commentContext = ctx.commentAfterStatement();
        try {
            ExpressionContext expression = ctx.parExpression().expression();
            conditionVar = visit(expression).trim();
            
            boolean defaultLabelVisited = false;
            List<SwitchBlockStatementGroupContext> blockStatementGroupContexts = ctx.switchBlockStatementGroup();
            for (SwitchBlockStatementGroupContext blockStatementGroupContext : blockStatementGroupContexts) {
                List<SwitchLabelContext> labelContexts = blockStatementGroupContext.switchLabel();
                String labelAggregated = getAggregatedLabel(labelContexts, defaultLabelVisited);
                if (labelAggregated == null) {
                    // default label present
                    defaultLabelVisited = true;
                }
                segments.put(labelAggregated, blockStatementGroupContext.blockStatements());
            }
            
            List<SwitchLabelContext> emptyLabelContexts = ctx.switchLabel();
            if (emptyLabelContexts != null && !emptyLabelContexts.isEmpty()) {
                String labelAggregated = getAggregatedLabel(emptyLabelContexts, defaultLabelVisited);
                segments.put(labelAggregated, null);
            }
            
            segmentCases = segments.keySet().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!Switch.areValuesValid(conditionVar, segmentCases)) {
                throw new CannotParseException("Switch values are not PSD valid");
            }
            
            Symbol symbol = EnumSymbol.SWITCH.getInstance("");
            lastElement = currentSegment.addSymbol(lastElement, symbol, 1);
            
            visitIfNotNull(commentContext);
            
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            visitIfNotNull(commentContext);
            resolveParseException(EnumSymbol.SWITCH, ctx, 1);
            return "--SWITCH--";
            // let's not do inner segments of Switch symbol if their condition of execution cannot be parsed
        }
        
        switchNamesStack.push(pickupSwitchName());
        boolean connectWithPreviousCase = false;
        boolean breakSituatedNotAtTheRootOfCasePresent = false;
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        for (Map.Entry<String, BlockStatementsContext> segmentEntry : segments.entrySet()) {
            String label = segmentEntry.getKey();
            BlockStatementsContext innerSegmentContext = segmentEntry.getValue();
            
            int innerSegmentIndex = prepareNextInnerSegment(originalElement, label == null);
            
            String gotoLabel = createGotoSymbolIfAppropriate(connectWithPreviousCase, innerSegmentIndex);
            
            currentSegment = originalElement.getInnerSegment(innerSegmentIndex);
            
            createGotoLabelIfAppropriate(connectWithPreviousCase, originalElement, gotoLabel);
            
            visitIfNotNull(innerSegmentContext);
            
            connectWithPreviousCase = !containsBreak(innerSegmentContext);
            
            if (!breakSituatedNotAtTheRootOfCasePresent) {
                breakSituatedNotAtTheRootOfCasePresent =
                        isAnyBreakStatementNotAtTheRootOfSwitchSegment(innerSegmentContext);
            }
        }
        lastElement = originalElement;
        currentSegment = originalSegment;
        
        generateSwitchValues(conditionVar, segmentCases);
        
        if (breakSituatedNotAtTheRootOfCasePresent) {
            Symbol symbol = EnumSymbol.GOTOLABEL.getInstance(switchNamesStack.peek() + LINE_SEP + "BR");
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        }
        
        switchNamesStack.pop();
        
        return "--SWITCH--";
    }
    
    private void createGotoLabelIfAppropriate(boolean connectWithPreviousCase, LayoutElement switchElement,
            String gotoLabel)
    {
        if (connectWithPreviousCase) {
            // add label to the current segment
            Symbol symbol = EnumSymbol.GOTOLABEL.getInstance(gotoLabel);
            lastElement = currentSegment.addSymbol(switchElement, symbol);
        }
    }
    
    private String createGotoSymbolIfAppropriate(boolean connectWithPreviousCase,
            int innerSegmentIndex)
    {
        String gotoLabel = null;
        if (connectWithPreviousCase) {
            // add goto symbol to the previous segment
            gotoLabel = switchNamesStack.peek() + LINE_SEP + innerSegmentIndex;
            
            Symbol symbol = EnumSymbol.GOTO.getInstance(gotoLabel);
            Goto.generateGotoValues(symbol);
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        }
        return gotoLabel;
    }
    
    private int prepareNextInnerSegment(LayoutElement switchElement, boolean defaultCase)
    {
        if (defaultCase) {
            // default corresponds to inner segment with index zero
            // the default segment is already present
            return 0;
        }
        
        // it is not a default case
        int innerSegmentIndex = switchElement.getInnerSegmentsCount();
        switchElement.addInnerSegment(new LayoutSegment(switchElement));
        return innerSegmentIndex;
    }
    
    private void generateSwitchValues(String conditionVar, List<String> segmentCases)
    {
        Switch.generateValues(lastElement, conditionVar, segmentCases.toArray(String[]::new));
        lastElement.getSymbol().setValueAndSize(lastElement.getSymbol().getDefaultValue());
        for (int i = 1; i < lastElement.getInnerSegmentsCount(); i++) {
            LayoutSegment innerSegment = lastElement.getInnerSegment(i);
            innerSegment.setDescription(innerSegment.getDefaultDescription());
        }
    }
    
    private String pickupSwitchName()
    {
        namedSwitchesCount++;
        
        String name = "switch";
        name += "_" + namedSwitchesCount;
        return name;
    }
    
    private boolean containsBreak(BlockStatementsContext innerSegmentContext)
    {
        List<BlockStatementContext> blockStatementContexts = innerSegmentContext.blockStatement();
        return blockStatementContexts.stream().anyMatch(b -> b.statement() instanceof BreakStatementContext);
    }
    
    private boolean isAnyBreakStatementNotAtTheRootOfSwitchSegment(ParserRuleContext root)
    {
        for (ParseTree child : root.children) {
            if (loopStatementClasses.stream().anyMatch(loopClass -> loopClass.isInstance(child))
                    || child instanceof SwitchStatementContext) {
                /*
                 - break statements inside loops belong to the loops, not switch
                 - also do not follow other switches
                */
                continue;
            }
            if (child instanceof BreakStatementContext) {
                BreakStatementContext breakStatementContext = (BreakStatementContext) child;
                if (breakStatementContext.identifier() != null) {
                    // break with identifier does not belong to a switch
                    continue;
                }
                
                if (!ancestorExists(breakStatementContext, SwitchBlockStatementGroupContext.class, 3)) {
                    return true;
                }
            } else if (child instanceof ParserRuleContext) {
                if (isAnyBreakStatementNotAtTheRootOfSwitchSegment((ParserRuleContext) child)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return null input consists of a default label
     */
    private String getAggregatedLabel(List<SwitchLabelContext> labelContexts,
            boolean throwExceptionIfDefaultLabelPresent)
    {
        StringBuilder labelAggregated = new StringBuilder();
        for (SwitchLabelContext labelContext : labelContexts) {
            if (labelContext.DEFAULT() == null) {
                if (labelAggregated.length() > 0) {
                    labelAggregated.append(",");
                }
                String label = visit(labelContext.switchLabelCase()).trim();
                labelAggregated.append(label);
            } else {
                if (throwExceptionIfDefaultLabelPresent) {
                    throw new CannotParseException("Duplicate default label");
                }
                // do not care about rest of the labels - PSD does not support it within "else" segment
                return null;
            }
        }
        
        return labelAggregated.toString();
    }
    
    @Override
    public String visitBlockStatements(BlockStatementsContext ctx)
    {
        if (ctx.getParent() instanceof SwitchBlockStatementGroupContext) {
            for (BlockStatementContext blockStatementContext : ctx.blockStatement()) {
                if (blockStatementContext.statement() == null
                        || !(blockStatementContext.statement() instanceof BreakStatementContext)) {
                    visit(blockStatementContext);
                } else {
                    // do not visit break statements directly in root of a switch statement cases
                    // iteration should not continue since we already met a break statement in a switch statement case
                    break;
                }
            }
            return "--SWITCH-BLOCK-STATEMENTS--";
        }
        
        return visitChildren(ctx);
    }
    
    @Override
    public String visitForStatement(ForStatementContext ctx)
    {
        ForControlContext forControlContext = ctx.forControl();
        CommentAfterStatementContext commentAfterStatementContext = ctx.commentAfterStatement();
        StatementContext statementContext = ctx.statement();
        
        if (directChildrenContain(forControlContext, BasicForControlContext.class)) {
            BasicForControlContext basicForControlContext = forControlContext.basicForControl();
            return visitBasicForStatement(ctx, basicForControlContext, commentAfterStatementContext, statementContext);
        } else {
            EnhancedForControlContext enhancedForControlContext = forControlContext.enhancedForControl();
            return visitEnhancedForStatement(ctx, enhancedForControlContext, commentAfterStatementContext,
                    statementContext);
        }
    }
    
    private String visitBasicForStatement(ForStatementContext originalCtx, BasicForControlContext ctx,
            CommentAfterStatementContext commentContext, StatementContext statementContext)
    {
        try {
            ForInitContext forInitContext = ctx.forInit();
            RelationalContext relationalContext = ctx.getChild(RelationalContext.class, 0);
            ForUpdateContext forUpdateContext = ctx.forUpdate();
            
            ParserRuleContext variableContext = extractVariableContext(forUpdateContext);
            String variableText = variableContext.getText();
            String variable = visit(variableContext).trim();
            
            String from = extractFromField(forInitContext, variable, variableText).trim();
            BasicForToField basicForToField = extractToField(relationalContext, variableText);
            String to = basicForToField.getTo().trim();
            BasicForIncField basicForIncField = extractIncField(forUpdateContext, variableText);
            String inc = basicForIncField.getInc().trim();
            
            if (basicForIncField.isSimpleFormWithoutVariables()) {
                boolean negativeInc = inc.startsWith("-");
                if ((negativeInc
                        && basicForToField.getOperatorType() == BasicForToField.RelationalOperatorType.LESSER_THAN)
                        || (!negativeInc
                        && basicForToField.getOperatorType() == BasicForToField.RelationalOperatorType.GREATER_THAN)) {
                /*
                    PS Diagram cannot do loops to infinity.
                    Example of infinity loops:
                        - for (int i = 6; i > 5; i++)
                        - for (int i = 0; i < 5; i--)
                 */
                    throw new CannotParseException(
                            String.format("Infinity loop detected '%s'", ctx.getText()));
                }
            }
            
            if (!For.areValuesValid(variable, from, to, inc)) {
                throw new CannotParseException("Values are not PSD valid");
            }
            
            Symbol symbol = EnumSymbol.FOR.getInstance("");
            For.generateForValues(symbol, variable, from, to, inc);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            visitIfNotNull(commentContext);
        } catch (RuntimeException e) {
            try {
                transformForLoopIntoWhileLoop(ctx, commentContext, statementContext);
                return "--FOR-TRANSFORMED--";
            } catch (RuntimeException ignored) {
            }
            
            System.err.println(e.getMessage());
            
            int startIdx = originalCtx.getStart().getStartIndex();
            int stopIdx;
            if (commentContext == null) {
                stopIdx = statementContext.getStart().getStartIndex();
            } else {
                stopIdx = commentContext.getStart().getStartIndex();
            }
            resolveParseException(EnumSymbol.FOR, startIdx, stopIdx);
            
            LayoutSegment originalSegment = currentSegment;
            LayoutElement originalElement = lastElement;
            currentSegment = lastElement.getInnerSegment(1);
            visitIfNotNull(commentContext);
            visitIfNotNull(statementContext);
            currentSegment = originalSegment;
            lastElement = originalElement;
            
            return "--FOR--";
        }
        
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        currentSegment = lastElement.getInnerSegment(1);
        visitIfNotNull(statementContext);
        currentSegment = originalSegment;
        lastElement = originalElement;
        
        return "--FOR--";
    }
    
    private String visitEnhancedForStatement(ForStatementContext originalCtx, EnhancedForControlContext ctx,
            CommentAfterStatementContext commentContext, StatementContext statementContext)
    {
        try {
            VariableDeclaratorIdContext variableDeclaratorIdContext = ctx.variableDeclaratorId();
            ExpressionContext expressionContext = ctx.expression();
            
            String variable = visit(variableDeclaratorIdContext).trim();
            String arrayVariable = visit(expressionContext).trim();
            
            if (!For.areValuesValid(variable, arrayVariable)) {
                throw new CannotParseException("Values are not PSD valid");
            }
            
            Symbol symbol = EnumSymbol.FOR.getInstance("");
            For.generateForeachValues(symbol, variable, arrayVariable);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            visitIfNotNull(commentContext);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            int startIdx = originalCtx.getStart().getStartIndex();
            int stopIdx;
            if (commentContext == null) {
                stopIdx = statementContext.getStart().getStartIndex();
            } else {
                stopIdx = commentContext.getStart().getStartIndex();
            }
            resolveParseException(EnumSymbol.FOR, startIdx, stopIdx);
            
            LayoutSegment originalSegment = currentSegment;
            LayoutElement originalElement = lastElement;
            currentSegment = lastElement.getInnerSegment(1);
            visitIfNotNull(commentContext);
            visitIfNotNull(statementContext);
            currentSegment = originalSegment;
            lastElement = originalElement;
            
            return "--FOR-EACH--";
        }
        
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        currentSegment = lastElement.getInnerSegment(1);
        visitIfNotNull(statementContext);
        currentSegment = originalSegment;
        lastElement = originalElement;
        
        return "--FOR-EACH--";
    }
    
    private ParserRuleContext extractVariableContext(ForUpdateContext forUpdateContext)
    {
        if (forUpdateContext == null) {
            throw new CannotParseException("No 'ForUpdateContext' present in for loop");
        }
        ExpressionContext expressionContext =
                getTheOnlyChildFromSingleChildrenTree(forUpdateContext, ExpressionContext.class, 2);
        
        if (expressionContext instanceof AssignmentContext
                || expressionContext instanceof PreContext
                || expressionContext instanceof PostContext) {
            ExpressionContext variableContext = getChildren(expressionContext, ExpressionContext.class).get(0);
            return variableContext;
        }
        
        throw new CannotParseException("No supported rule present in for loop 'ForUpdateContext'");
    }
    
    private String extractFromField(ForInitContext forInitContext, String variable, String variableText)
    {
        if (forInitContext == null) {
            return variable;
        }
        
        if (directChildrenContain(forInitContext, LocalVariableDeclarationContext.class)) {
            VariableDeclaratorContext declarator =
                    getChildren(forInitContext, VariableDeclaratorContext.class, 1).get(0);
            
            String forInitVariable = declarator.variableDeclaratorId().getText();
            if (!forInitVariable.equals(variableText)) {
                throw new CannotParseException(
                        String.format("Variable in 'ForInitContext' ('%s') does not equal to expected variable ('%s')",
                                forInitVariable, variableText));
            }
            
            return visit(declarator.variableInitializer());
        } else {
            AssignmentContext assignmentContext =
                    getTheOnlyChildFromSingleChildrenTree(forInitContext, AssignmentContext.class, 2);
            
            ExpressionContext leftHandSideContext = assignmentContext.expression(0);
            String forInitVariableText = leftHandSideContext.getText();
            if (!forInitVariableText.equals(variableText)) {
                throw new CannotParseException(
                        String.format("Variable in 'ForInitContext' ('%s') does not equal to expected variable ('%s')",
                                forInitVariableText, variableText));
            }
            
            AssignmentFields assignmentFields = extractAssignmentFields(assignmentContext);
            
            return assignmentFields.getValue();
        }
    }
    
    private BasicForToField extractToField(RelationalContext relationalExpression, String variableText)
    {
        if (relationalExpression == null) {
            throw new CannotParseException("No 'RelationalContext' present in for loop");
        }
        getChildren(relationalExpression, RelationalOperatorContext.class, 1);
        
        RelationalOperatorContext relationalExpressionOperator = relationalExpression.relationalOperator();
        
        ExpressionContext variableExpression = relationalExpression.expression(0);
        ExpressionContext toExpression = relationalExpression.expression(1);
        
        String expressionVariable = variableExpression.getText();
        if (!expressionVariable.equals(variableText)) {
            throw new CannotParseException(
                    String.format("Variable in 'ExpressionContext' ('%s') does not equal to expected variable ('%s')",
                            expressionVariable, variableText));
        }
        
        int increment;
        BasicForToField.RelationalOperatorType relationalOperatorType;
        String operator = relationalExpressionOperator.getText();
        switch (operator) {
            case ">":
                increment = 1;
                relationalOperatorType = BasicForToField.RelationalOperatorType.GREATER_THAN;
                break;
            case "<":
                relationalOperatorType = BasicForToField.RelationalOperatorType.LESSER_THAN;
                increment = -1;
                break;
            case ">=":
                relationalOperatorType = BasicForToField.RelationalOperatorType.GREATER_THAN;
                increment = 0;
                break;
            case "<=":
                relationalOperatorType = BasicForToField.RelationalOperatorType.LESSER_THAN;
                increment = 0;
                break;
            default:
                throw new CannotParseException(String.format("Unexpected relational operator value ('%s')", operator));
        }
        
        if (getChildren(toExpression, LiteralContext.class).size() == 1) {
            // let's make it nice and modify the number 
            String to = visit(toExpression).trim();
            if (to.contains(".")) {
                double toAsDouble = Double.parseDouble(to);
                toAsDouble += increment;
                to = String.valueOf(toAsDouble);
            } else {
                int toAsInt = Integer.parseInt(to);
                toAsInt += increment;
                to = String.valueOf(toAsInt);
            }
            return new BasicForToField(to, relationalOperatorType);
        } else {
            String to = visit(toExpression).trim();
            if (increment == 1) {
                to = "(" + to + ")+1";
            } else if (increment == -1) {
                to = "(" + to + ")-1";
            }
            return new BasicForToField(to, relationalOperatorType);
        }
    }
    
    private BasicForIncField extractIncField(ForUpdateContext forUpdateContext, String variableText)
    {
        if (forUpdateContext == null) {
            throw new CannotParseException("No 'ForUpdateContext' present in for loop");
        }
        ExpressionListContext expressionListContext = forUpdateContext.expressionList();
        if (expressionListContext.getChildCount() != 1) {
            throw new CannotParseException(forUpdateContext, input);
        }
        
        if (directChildrenContain(expressionListContext, AssignmentContext.class)) {
            AssignmentContext assignment = getChildren(expressionListContext, AssignmentContext.class, 1).get(0);
            AssignmentOperatorContext assignmentOperator =
                    getChildren(assignment, AssignmentOperatorContext.class, 1).get(0);
            ExpressionContext assignmentVariableContext = assignment.expression(0);
            ExpressionContext expressionContext = assignment.expression(1);
            
            String assignmentVariableText = assignmentVariableContext.getText();
            if (!assignmentVariableText.equals(variableText)) {
                throw new IllegalArgumentException(String.format(
                        "Supported parameter variableText ('%s') is different from expected value ('%s')",
                        variableText, assignmentVariableText));
            }
            
            boolean simpleFormWithoutVariables;
            String inc;
            String operator = assignmentOperator.getText();
            switch (operator) {
                case "=":
                    if (!(expressionContext instanceof AdditiveContext)) {
                        throw new CannotParseException("Expected AdditiveContext");
                    }
                    AdditiveContext additiveExpression = (AdditiveContext) expressionContext;
                    
                    ExpressionContext firstOperand = additiveExpression.expression(0);
                    String firstOperandText = firstOperand.getText();
                    if (!firstOperandText.equals(variableText)) {
                        /*
                          The only supported forms of increment assignment are:
                          > i = i + ....
                          > i = i - ....
                          
                          This is not the case.
                        */
                        throw new CannotParseException(
                                String.format("Unsupported form of assignment expression in 'ForUpdateContext' ('%s')",
                                        assignment.getText()));
                    }
                    
                    ExpressionContext incExpression = additiveExpression.expression(1);
                    inc = visit(incExpression).trim();
                    
                    simpleFormWithoutVariables = !childrenContain(incExpression, IdentifierContext.class);
                    
                    AdditiveOperatorContext additiveOperatorContext = additiveExpression.additiveOperator();
                    String additiveOperator = additiveOperatorContext.getText();
                    if (additiveOperator.equals("-")) {
                        inc = visit(additiveOperatorContext) + inc;
                    }
                    
                    return new BasicForIncField(inc, simpleFormWithoutVariables);
                case "+=":
                    simpleFormWithoutVariables = !childrenContain(expressionContext, IdentifierContext.class);
                    inc = visit(expressionContext).trim();
                    return new BasicForIncField(inc, simpleFormWithoutVariables);
                case "-=":
                    simpleFormWithoutVariables = !childrenContain(expressionContext, IdentifierContext.class);
                    inc = visit(expressionContext).trim();
                    String minusSignFormatted = visit(assignmentOperator).replace("=", "").trim();
                    inc = String.format("%s(%s)", minusSignFormatted, inc);
                    return new BasicForIncField(inc, simpleFormWithoutVariables);
                default:
                    throw new CannotParseException(
                            String.format("Unexpected assignment operator value ('%s')", operator));
            }
        }
        if (directChildrenContain(expressionListContext, PreContext.class)) {
            PreContext preContext = expressionListContext.getChild(PreContext.class, 0);
            ExpressionContext preExpressionContext = preContext.expression();
            String unaryExpressionText = preExpressionContext.getText();
            if (!unaryExpressionText.equals(variableText)) {
                throw new IllegalArgumentException(String.format(
                        "Supported parameter variableText ('%s') is different from expected value ('%s')",
                        variableText, unaryExpressionText));
            }
            
            if (preContext.prefix.getType() == JavaParser.INC) {
                return new BasicForIncField("1", true);
            } else if (preContext.prefix.getType() == JavaParser.DEC) {
                return new BasicForIncField("-1", true);
            } else {
                throw new CannotParseException(
                        String.format("Unexpected PreContext ('%s')", preContext.prefix.getText()));
            }
        }
        if (childrenContain(expressionListContext, PostContext.class)) {
            PostContext postContext = expressionListContext.getChild(PostContext.class, 0);
            ExpressionContext postExpressionContext = postContext.expression();
            String postfixExpressionText = postExpressionContext.getText();
            if (!postfixExpressionText.equals(variableText)) {
                throw new IllegalArgumentException(String.format(
                        "Supported parameter variableText ('%s') is different from expected value ('%s')",
                        variableText, postfixExpressionText));
            }
            
            if (postContext.postfix.getType() == JavaParser.INC) {
                return new BasicForIncField("1", true);
            } else if (postContext.postfix.getType() == JavaParser.DEC) {
                return new BasicForIncField("-1", true);
            } else {
                throw new CannotParseException(
                        String.format("Unexpected PreContext ('%s')", postContext.postfix.getText()));
            }
        }
        
        throw new CannotParseException("No supported rule present in for loop 'ForUpdateContext'");
    }
    
    private void transformForLoopIntoWhileLoop(BasicForControlContext ctx, CommentAfterStatementContext commentContext,
            StatementContext statementContext)
    {
        // exceptions have to be handled in caller method
        
        LayoutElement originalLastElement = lastElement;
        int preInitElementsCount = currentSegment.size();
        ForInitContext forInitContext = ctx.forInit();
        if (forInitContext != null) {
            LocalVariableDeclarationContext localVariableDeclarationContext = forInitContext.localVariableDeclaration();
            if (localVariableDeclarationContext == null) {
                processExpressionsPresentInForLoopWhichIsBeingTransformed(forInitContext.expressionList().expression());
            } else {
                visitLocalVariableDeclaration(localVariableDeclarationContext);
            }
        }
        
        String expressionContextVisited = null;
        ExpressionContext expressionContext = ctx.expression();
        if (expressionContext == null) {
            expressionContextVisited = "true";
        }
        try {
            visitWhileStatement(expressionContext, commentContext, statementContext, -1, false, true,
                    expressionContextVisited);
        } catch (RuntimeException e) {
            // rollback init stage
            int indexOfOriginalLastElement = currentSegment.indexOfElement(originalLastElement);
            int elementCountForRollback = currentSegment.size() - preInitElementsCount;
            for (int i = 0; i < elementCountForRollback; i++) {
                LayoutElement elementToRemove = currentSegment.getElement(indexOfOriginalLastElement + 1);
                currentSegment.removeElement(elementToRemove);
            }
            lastElement = originalLastElement;
            throw e;
        }
        
        ForUpdateContext forUpdateContext = ctx.forUpdate();
        if (forUpdateContext != null) {
            LayoutSegment originalSegment = currentSegment;
            LayoutElement originalElement = lastElement;
            currentSegment = lastElement.getInnerSegment(1);
            if (!currentSegment.isEmpty()) {
                lastElement = currentSegment.getElement(currentSegment.size() - 1);
            }
            
            processExpressionsPresentInForLoopWhichIsBeingTransformed(forUpdateContext.expressionList().expression());
            
            currentSegment = originalSegment;
            lastElement = originalElement;
        }
        
        lastElement = currentSegment.addSymbol(lastElement,
                new cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd());
    }
    
    private void processExpressionsPresentInForLoopWhichIsBeingTransformed(List<ExpressionContext> expressionContexts)
    {
        for (ExpressionContext expressionContext : expressionContexts) {
            if (expressionContext instanceof InvocationContext
                    || expressionContext instanceof MethodInvocationContext) {
                visitGeneralInvocation(expressionContext, false);
            } else if (expressionContext instanceof PostContext) {
                PostContext ctx = (PostContext) expressionContext;
                visitGeneralCrementExpression(ctx, ctx.expression(), ctx.postfix, false);
            } else if (expressionContext instanceof PreContext) {
                PreContext ctx = (PreContext) expressionContext;
                visitGeneralCrementExpression(ctx, ctx.expression(), ctx.prefix, false);
            } else if (expressionContext instanceof AssignmentContext) {
                AssignmentContext ctx = (AssignmentContext) expressionContext;
                visitAssignment(ctx, false);
            }
        }
    }
    
    @Override
    public String visitWhileStatement(WhileStatementContext ctx)
    {
        ExpressionContext expressionContext = ctx.parExpression().expression();
        CommentAfterStatementContext commentContext = ctx.commentAfterStatement();
        StatementContext statementContext = ctx.statement();
        return visitWhileStatement(expressionContext, commentContext, statementContext, ctx.getStart().getStartIndex(),
                true, false, null);
    }
    
    private String visitWhileStatement(ExpressionContext expressionContext, CommentAfterStatementContext commentContext,
            StatementContext statementContext, int startIdx, boolean includeLoopEnd, boolean rethrowExceptions,
            String expressionContextVisited)
    {
        try {
            String condition =
                    Objects.requireNonNullElseGet(expressionContextVisited, () -> visit(expressionContext).trim());
            
            if (!LoopStart.isConditionValid(condition)) {
                throw new CannotParseException("While condition expression is not PSD valid");
            }
            
            Symbol symbol = EnumSymbol.LOOPCONDITIONUP.getInstance("");
            LoopStart.generateValues(symbol, condition);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
            
            visitIfNotNull(commentContext);
        } catch (RuntimeException e) {
            if (rethrowExceptions) {
                throw e;
            }
            System.err.println(e.getMessage());
            
            int stopIdx;
            if (commentContext == null) {
                stopIdx = statementContext.getStart().getStartIndex();
            } else {
                stopIdx = commentContext.getStart().getStartIndex();
            }
            resolveParseException(EnumSymbol.LOOPCONDITIONUP, startIdx, stopIdx);
            
            LayoutSegment originalSegment = currentSegment;
            LayoutElement originalElement = lastElement;
            currentSegment = lastElement.getInnerSegment(1);
            visitIfNotNull(commentContext);
            currentSegment = originalSegment;
            lastElement = originalElement;
        }
        
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        currentSegment = lastElement.getInnerSegment(1);
        visitIfNotNull(statementContext);
        currentSegment = originalSegment;
        lastElement = originalElement;
        
        if (includeLoopEnd) {
            lastElement = currentSegment.addSymbol(lastElement,
                    new cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd());
        }
        
        return "--WHILE--";
    }
    
    @Override
    public String visitDoStatement(DoStatementContext ctx)
    {
        CommentAfterStatementContext commentContext = ctx.commentAfterStatement();
        StatementContext statementContext = ctx.statement();
        ParExpressionContext parExpressionContext = ctx.parExpression();
        
        Symbol symbol = EnumSymbol.LOOPCONDITIONDOWN.getInstance("");
        lastElement = currentSegment.addSymbol(lastElement, symbol);
        LayoutElement loopElement = lastElement;
        
        visitIfNotNull(commentContext);
        
        LayoutSegment originalSegment = currentSegment;
        LayoutElement originalElement = lastElement;
        currentSegment = loopElement.getInnerSegment(1);
        visitIfNotNull(statementContext);
        currentSegment = originalSegment;
        lastElement = originalElement;
        
        try {
            ExpressionContext conditionExpressionContext = parExpressionContext.expression();
            
            String condition = visit(conditionExpressionContext).trim();
            
            if (!LoopEnd.isConditionValid(condition)) {
                throw new CannotParseException(parExpressionContext, input);
            }
            
            lastElement = currentSegment.addSymbol(lastElement,
                    new cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd());
            
            LoopEnd.generateValues(lastElement.getSymbol(), condition);
            lastElement.getSymbol().setValueAndSize(lastElement.getSymbol().getDefaultValue());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            
            int startIdx = statementContext.getStop().getStopIndex() + 1;
            int stopIdx = parExpressionContext.getStop().getStopIndex() + 1;
            String thePart = input.substring(startIdx, stopIdx);
            
            lastElement = currentSegment.addSymbol(lastElement,
                    new cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd(INVALID_COMMAND));
            lastElement = FlowchartGenerator.super.addComment(currentSegment, lastElement, thePart, true);
        }
        
        return "--DO-WHILE--";
    }
    
    @Override
    public String visitLabeledStatement(LabeledStatementContext ctx)
    {
        String labelValue = visit(ctx.identifier()).trim();
        StatementContext statement = ctx.statement();
        
        boolean breakPresent = false;
        boolean continuePresent = false;
        
        List<BreakStatementContext> breakStatementContexts = getChildren(statement, BreakStatementContext.class);
        for (BreakStatementContext breakStatementContext : breakStatementContexts) {
            IdentifierContext identifier = breakStatementContext.identifier();
            if (identifier != null) {
                String breakLabelValue = visit(identifier);
                if (breakLabelValue != null && breakLabelValue.trim().equals(labelValue)) {
                    breakPresent = true;
                    break;
                }
            }
            
        }
        
        // labeled continue is valid only for loops
        if (loopStatementClasses.stream().anyMatch(loopClass -> loopClass.isInstance(statement))) {
            List<ContinueStatementContext> continueStatementContexts =
                    getChildren(statement, ContinueStatementContext.class);
            for (ContinueStatementContext continueStatementContext : continueStatementContexts) {
                IdentifierContext identifier = continueStatementContext.identifier();
                if (identifier != null) {
                    String continueLabelValue = visit(identifier);
                    if (continueLabelValue != null && continueLabelValue.trim().equals(labelValue)) {
                        continuePresent = true;
                        break;
                    }
                }
            }
        }
        
        visitIfNotNull(statement);
        
        if (continuePresent) {
            LayoutElement loopElement = null;
            for (int i = currentSegment.indexOfElement(lastElement); i >= 0; i--) {
                loopElement = currentSegment.getElement(i);
                if (loopElement.getInnerSegmentsCount() > 0) {
                    break;
                }
            }
            if (loopElement == null) {
                throw new IllegalStateException("Expected a loop element");
            }
            LayoutSegment innerSegment = loopElement.getInnerSegment(1);
            Symbol symbol = EnumSymbol.GOTOLABEL.getInstance(labelValue + LINE_SEP + "CT");
            innerSegment.addSymbol(innerSegment.getElement(innerSegment.size() - 1), symbol);
        }
        if (breakPresent) {
            Symbol symbol = EnumSymbol.GOTOLABEL.getInstance(labelValue + LINE_SEP + "BR");
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        }
        
        return "--LABELED-STATEMENT--";
    }
    
    @Override
    public String visitBreakStatement(BreakStatementContext ctx)
    {
        IdentifierContext identifier = ctx.identifier();
        if (identifier == null) {
            ParserRuleContext parent = ctx.getParent();
            int depth = 1;
            while (parent != null) {
                ParserRuleContext finalParent = parent;
                if (loopStatementClasses.stream().anyMatch(loopClass -> loopClass.isInstance(finalParent))) {
                    // break belongs to a loop
                    createBreakSymbol();
                    return "--BREAK--";
                }
                
                if (parent instanceof SwitchBlockStatementGroupContext) {
                    // break belongs to a switch
                    if (depth > 3) {
                        /*
                         this break statement is not directly on root of the switch case -> it is somewhere 
                         deeper (e.x. inside an if statement) so goto needs to be created
                        */
                        Symbol symbol = EnumSymbol.GOTO.getInstance(switchNamesStack.peek() + LINE_SEP + "BR");
                        Goto.generateGotoValues(symbol);
                        lastElement = currentSegment.addSymbol(lastElement, symbol);
                    }
                    // no creation of a break symbol is needed for a switch statement
                    return "--BREAK--";
                }
                
                depth++;
                parent = parent.getParent();
            }
            
            // break statement does not make sense here but let's create it anyway
            createBreakSymbol();
        } else {
            String gotoLabel = visit(identifier).trim();
            Symbol symbol = EnumSymbol.GOTO.getInstance(gotoLabel + LINE_SEP + "BR");
            Goto.generateGotoValues(symbol);
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        }
        
        return "--BREAK--";
    }
    
    private void createBreakSymbol()
    {
        Symbol symbol = EnumSymbol.GOTO.getInstance("");
        Goto.generateBreakValues(symbol);
        symbol.setValueAndSize(symbol.getDefaultValue());
        lastElement = currentSegment.addSymbol(lastElement, symbol);
    }
    
    @Override
    public String visitContinueStatement(ContinueStatementContext ctx)
    {
        IdentifierContext identifier = ctx.identifier();
        String gotoLabel = visit(identifier).trim();
        Symbol symbol = EnumSymbol.GOTO.getInstance(gotoLabel + LINE_SEP + "CT");
        Goto.generateGotoValues(symbol);
        lastElement = currentSegment.addSymbol(lastElement, symbol);
        
        return "--CONTINUE--";
    }
    
    @Override
    public String visitComment(CommentContext ctx)
    {
        int startIdx = ctx.getStart().getStartIndex();
        for (int i = startIdx - 1; i >= 0; i--) {
            String charBefore = input.substring(i, i + 1);
            if (charBefore.matches("[\\r\\n]")) {
                break;
            } else if (!charBefore.matches("[ \\t]")) {
                return visitGeneralComment(ctx, true); // the comment is not on a new line
            }
        }
        
        return visitGeneralComment(ctx, false);
    }
    
    @Override
    public String visitCommentAfterStatement(CommentAfterStatementContext ctx)
    {
        return visitGeneralComment(ctx, true);
    }
    
    private String visitGeneralComment(ParserRuleContext ctx, boolean pair)
    {
        String value = visitChildren(ctx);
        lastElement = FlowchartGenerator.super.addComment(currentSegment, lastElement, value, pair);
        
        return "";
    }
    
    @Override
    public String visitReturnStatement(ReturnStatementContext ctx)
    {
        ExpressionContext expressionContext = ctx.expression();
        if (expressionContext != null) {
            try {
                String outputValue = visit(expressionContext).trim();
                
                if (!IO.isOValueValid(outputValue)) {
                    throw new CannotParseException(ctx, input);
                }
                
                Symbol symbol = EnumSymbol.IO.getInstance("");
                IO.generateOValues(symbol, outputValue);
                symbol.setValueAndSize(symbol.getDefaultValue());
                lastElement = currentSegment.addSymbol(lastElement, symbol);
                
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                
                resolveParseException(EnumSymbol.IO, expressionContext);
            }
        }
        
        lastElement = currentSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("Konec"));
        
        return "--RETURN--";
    }
    
    @Override
    public String visitAssertStatement(AssertStatementContext ctx)
    {
        resolveParseException(EnumSymbol.PROCESS, ctx);
        return "--ASSERT--";
    }
    
    @Override
    public String visitTryStatement(TryStatementContext ctx)
    {
        visitGeneralTryStatement(ctx.block(), ctx.catchClause(), ctx.finallyBlock());
        
        return "--TRY--";
    }
    
    @Override
    public String visitTryStatementWithResources(TryStatementWithResourcesContext ctx)
    {
        Set<String> scannerVariablesToAdd = new HashSet<>();
        boolean resourcesOnlyContainScanners = true;
        for (ResourceContext resourceContext : ctx.resourceSpecification().resources().resource()) {
            if (isNewScanner(resourceContext.expression())) {
                String scannerVariable;
                if (resourceContext.variableDeclaratorId() == null) {
                    scannerVariable = visit(resourceContext.identifier()).trim(); // VAR variant
                } else {
                    scannerVariable = visit(resourceContext.variableDeclaratorId()).trim();
                }
                scannerVariablesToAdd.add(scannerVariable);
                continue;
            }
            resourcesOnlyContainScanners = false;
            break;
        }
        
        if (!resourcesOnlyContainScanners) {
            int startIdx = ctx.getStart().getStartIndex();
            int stopIdx = ctx.block().getStart().getStartIndex();
            resolveParseException(EnumSymbol.PROCESS, startIdx, stopIdx);
        }
        
        scannerVariables.addAll(scannerVariablesToAdd);
        visitGeneralTryStatement(ctx.block(), ctx.catchClause(), ctx.finallyBlock());
        scannerVariables.removeAll(scannerVariablesToAdd);
        
        return "--TRY-WITH-RESOURCES--";
    }
    
    private boolean isNewScanner(ExpressionContext expressionContext)
    {
        if (!(expressionContext instanceof NewContext)) {
            return false;
        }
        
        NewContext newContext = (NewContext) expressionContext;
        
        if (newContext.creator().nonWildcardTypeArguments() != null) {
            return false;
        }
        
        String createdNameText = newContext.creator().createdName().getText();
        if (!createdNameText.matches("^(java\\.util\\.)?Scanner$")) {
            return false;
        }
        
        List<ExpressionContext> argumentExpressionContexts =
                newContext.creator().classCreatorRest().arguments().expressionList().expression();
        if (argumentExpressionContexts.isEmpty() || argumentExpressionContexts.size() > 2) {
            return false;
        }
        return argumentExpressionContexts.get(0).getText().equals("System.in");
    }
    
    private boolean isConsole(ExpressionContext expressionContext)
    {
        if (!(expressionContext instanceof InvocationContext)) {
            return false;
        }
        
        InvocationContext invocationContext = (InvocationContext) expressionContext;
        String invocationContextText = invocationContext.getText();
        return invocationContextText.equals("System.console()");
    }
    
    private boolean isInput(ExpressionContext expressionContext)
    {
        return isScannerInput(expressionContext) || isConsoleInput(expressionContext);
    }
    
    private boolean isScannerInput(ExpressionContext expressionContext)
    {
        if (!(expressionContext instanceof InvocationContext)) {
            return false;
        }
        
        InvocationContext invocationContext = (InvocationContext) expressionContext;
        
        String variableText;
        try {
            variableText =
                    getTheOnlyChildFromSingleChildrenTree(invocationContext.expression(), IdentifierContext.class,
                            2).getText().trim();
        } catch (RuntimeException e) {
            return false;
        }
        
        if (invocationContext.methodCall() == null || invocationContext.methodCall().identifier() == null) {
            return false;
        }
        String methodNameText = invocationContext.methodCall().identifier().getText();
        
        return scannerVariables.contains(variableText) && methodNameText.startsWith("next");
    }
    
    private boolean isConsoleInput(ExpressionContext expressionContext)
    {
        if (!(expressionContext instanceof InvocationContext)) {
            return false;
        }
        
        InvocationContext invocationContext = (InvocationContext) expressionContext;
        if (invocationContext.getText().startsWith("System.console().readLine(")) {
            // it is not nice to add symbol from here - I am sorry for it
            addOutputSymbolIfAppropriate(invocationContext);
            return true;
        }
        
        String variableText;
        try {
            variableText =
                    getTheOnlyChildFromSingleChildrenTree(invocationContext.expression(), IdentifierContext.class,
                            2).getText().trim();
        } catch (RuntimeException e) {
            return false;
        }
        
        if (invocationContext.methodCall() == null || invocationContext.methodCall().identifier() == null) {
            return false;
        }
        String methodNameText = invocationContext.methodCall().identifier().getText();
        
        if (consoleVariables.contains(variableText) && methodNameText.equals("readLine")) {
            // it is not nice to add symbol from here - I am sorry for it
            addOutputSymbolIfAppropriate(invocationContext);
            return true;
        }
        
        return false;
    }
    
    private void addOutputSymbolIfAppropriate(InvocationContext consoleInvocationContext)
    {
        if (consoleInvocationContext.methodCall() == null
                || consoleInvocationContext.methodCall().expressionList() == null) {
            return;
        }
        
        ExpressionContext firstArgumentContext = consoleInvocationContext.methodCall().expressionList().expression(0);
        String outputValue = visit(firstArgumentContext);
        
        if (IO.isOValueValid(outputValue)) {
            Symbol symbol = EnumSymbol.IO.getInstance("");
            IO.generateOValues(symbol, outputValue);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = currentSegment.addSymbol(lastElement, symbol);
        } else {
            resolveParseException(EnumSymbol.IO, firstArgumentContext);
        }
    }
    
    private void visitGeneralTryStatement(BlockContext blockContext, List<CatchClauseContext> catchClauseContexts,
            FinallyBlockContext finallyBlockContext)
    {
        visitIfNotNull(blockContext);
        
        for (CatchClauseContext catchClauseContext : catchClauseContexts) {
            resolveParseException(EnumSymbol.PROCESS, catchClauseContext);
        }
        
        if (finallyBlockContext != null) {
            visitIfNotNull(finallyBlockContext);
        }
    }
    
    @Override
    public String visitSynchronizedStatement(SynchronizedStatementContext ctx)
    {
        int startIdx = ctx.getStart().getStartIndex();
        int stopIdx = ctx.block().getStart().getStartIndex();
        resolveParseException(EnumSymbol.PROCESS, startIdx, stopIdx);
        
        visitIfNotNull(ctx.block());
        
        return "--SYNCHRONIZED--";
    }
    
    @Override
    public String visitThrowStatement(ThrowStatementContext ctx)
    {
        resolveParseException(EnumSymbol.PROCESS, ctx);
        
        return "--THROW--";
    }
    
    @Override
    public String visitYieldStatement(YieldStatementContext ctx)
    {
        resolveParseException(EnumSymbol.PROCESS, ctx);
        
        return "--YIELD--";
    }
    
    @Override
    public String visitLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext ctx)
    {
        resolveParseException(EnumSymbol.PROCESS, ctx);
        
        return "--LOCAL-TYPE-DECLARATION--";
    }
    
    @Override
    public String visitNew(NewContext ctx)
    {
        if (isNewScanner(ctx)) {
            return visitChildren(ctx);
        }
        
        CreatorContext creatorContext = ctx.creator();
        if (!directChildrenContain(creatorContext, ArrayCreatorRestContext.class)) {
            throw new CannotParseException(ctx, input);
        }
        
        ArrayCreatorRestContext arrayCreatorRestContext = creatorContext.arrayCreatorRest();
        ArrayInitializerContext arrayInitializerContext = arrayCreatorRestContext.arrayInitializer();
        if (arrayInitializerContext != null) {
            return visit(arrayInitializerContext);
        }
        
        PrimitiveTypeContext primitiveTypeContext = creatorContext.createdName().primitiveType();
        DimExprsContext dimExprsContext = arrayCreatorRestContext.dimExprs();
        DimsContext dimsContext = arrayCreatorRestContext.dims();
        
        String dimExprs = visit(dimExprsContext);
        String dimExprsText = dimExprsContext.getText();
        if (!dimExprsText.matches("(\\[(0|([1-9][0-9]*))\\])+")) {
            throw new CannotParseException(creatorContext, input);
        }
        
        String fillingContent = "null";
        if (primitiveTypeContext != null && dimsContext == null) {
            if (childrenContain(primitiveTypeContext, NumericTypeContext.class)) {
                fillingContent = "0";
            } else {
                fillingContent = "false";
            }
        }
        String arrayInitValue = explodeArrayDimensions(dimExprs, fillingContent);
        
        return arrayInitValue;
    }
    
    private String explodeArrayDimensions(String dimExprs, String fillingContent)
    {
        if (dimExprs.isEmpty()) {
            return fillingContent;
        }
        
        int stopIdx = dimExprs.indexOf(']');
        int dimension = Integer.parseInt(dimExprs.substring(1, stopIdx));
        
        String restDimExprs = dimExprs.substring(stopIdx + 1);
        String elementFill = explodeArrayDimensions(restDimExprs, fillingContent);
        
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < dimension; i++) {
            result.append(elementFill).append(", ");
        }
        if (dimension > 0) {
            result.replace(result.length() - 2, result.length(), "]");
        } else {
            result.append("]");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitArrayInitializer(ArrayInitializerContext ctx)
    {
        VariableInitializerListContext variableInitializerListContext = ctx.variableInitializerList();
        
        String arrayInitValue = "";
        if (variableInitializerListContext != null) {
            arrayInitValue = visit(variableInitializerListContext);
        }
        
        return "[" + arrayInitValue + "]";
    }
    
    @Override
    public String visitMultiplicativeOperator(MultiplicativeOperatorContext ctx)
    {
        MultiplicativeContext multiplicativeContext = getParent(ctx, MultiplicativeContext.class, 1);
        if (!multiplicativeContext.isInteger || ctx.DIV() == null) {
            return visitChildren(ctx);
        }
        
        return visitChildren(ctx).replace("/", "//");
    }
    
    @Override
    public String visitAnnotation(AnnotationContext ctx)
    {
        return "";
    }
    
    @Override
    public String visitDims(DimsContext ctx)
    {
        return "";
    }
    
    @Override
    public String visitTypeType(TypeTypeContext ctx)
    {
        return "";
    }
    
    @Override
    public String visitTerminal(TerminalNode node)
    {
        if (node.getSymbol().getType() == Recognizer.EOF) {
            return "";
        }
        
        String spaceRight = getTerminalSpaceRight(node.getSymbol().getStopIndex());
        
        String nodeText;
        switch (node.getSymbol().getType()) {
            case JavaParser.DECIMAL_LITERAL:
                nodeText = node.getText()
                        .replaceAll("_", "")
                        .replaceAll("[lL]$", "");
                break;
            case JavaParser.FLOAT_LITERAL:
                nodeText = node.getText()
                        .replaceAll("[fFdD]$", "");
                break;
            case JavaParser.EQUAL:
                nodeText = "=";
                break;
            case JavaParser.COMMENT:
                nodeText = node.getText()
                        .replaceFirst("^/\\*\\s*", "")
                        .replaceFirst("\\s*\\*/$", "");
                spaceRight = "";
                break;
            case JavaParser.LINE_COMMENT:
                nodeText = node.getText()
                        .replaceFirst("^//", "")
                        .trim();
                break;
            default:
                nodeText = node.getText();
                break;
        }
        
        return nodeText + spaceRight;
    }
    
    @Override
    protected String aggregateResult(String aggregate, String nextResult)
    {
        if (aggregate == null) {
            return nextResult;
        }
        
        return aggregate + nextResult;
    }
    
    private AssignmentFields extractAssignmentFields(AssignmentContext assignmentContext)
    {
        ExpressionContext leftHandExpressionContext = assignmentContext.expression(0);
        AssignmentOperatorContext assignmentOperatorContext = assignmentContext.assignmentOperator();
        ExpressionContext expressionContext = assignmentContext.expression(1);
        
        String variable = visit(leftHandExpressionContext);
        String operator = visit(assignmentOperatorContext);
        String value = visit(expressionContext);
        
        String valueProcessed = extractAssignmentValue(variable, operator, value, assignmentContext.isInteger);
        
        return new AssignmentFields(variable, operator, valueProcessed);
    }
    
    private String extractAssignmentValue(String variable, String operator, String value,
            boolean isInteger)
    {
        switch (operator.trim()) {
            case "=":
                return value;
            case "/=":
                if (isInteger) {
                    return variable + operator.replace("=", "/") + value;
                }
                // fall-through
            case "+=":
            case "-=":
            case "*=":
            case "%=":
                return variable + operator.replace("=", "") + value;
            default:
                throw new CannotParseException(String.format("Unsupported assignment operator '%s'", operator.trim()));
        }
    }
    
    private String getTerminalSpaceRight(int stopIdx)
    {
        int rightSpaceIdx = stopIdx + 1;
        while (rightSpaceIdx < input.length() && input.substring(rightSpaceIdx, rightSpaceIdx + 1).matches("[ \t]")) {
            rightSpaceIdx++;
        }
        return input.substring(stopIdx + 1, rightSpaceIdx);
    }
    
    private void resolveParseException(EnumSymbol enumSymbol, ParserRuleContext ctx)
    {
        resolveParseException(enumSymbol, ctx, 0);
    }
    
    private void resolveParseException(EnumSymbol enumSymbol, ParserRuleContext ctx, int innerOutCount)
    {
        int startIdx = ctx.getStart().getStartIndex();
        int stopIdx = ctx.getStop().getStopIndex() + 1;
        resolveParseException(enumSymbol, startIdx, stopIdx, innerOutCount);
    }
    
    private void resolveParseException(EnumSymbol enumSymbol, int startIdx, int stopIdx)
    {
        resolveParseException(enumSymbol, startIdx, stopIdx, 0);
    }
    
    private void resolveParseException(EnumSymbol enumSymbol, int startIdx, int stopIdx, int innerOutCount)
    {
        String thePart = input.substring(startIdx, stopIdx);
        
        Symbol symbol = enumSymbol.getInstance(INVALID_COMMAND);
        lastElement = currentSegment.addSymbol(lastElement, symbol, innerOutCount);
        lastElement = FlowchartGenerator.super.addComment(currentSegment, lastElement, thePart, true);
    }
    
    private void visitIfNotNull(ParserRuleContext ctx)
    {
        if (ctx != null) {
            visit(ctx);
        }
    }
    
}
