package cz.miroslavbartyzal.psdiagram.app.parser.antlr;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Switch;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8BaseVisitor;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.AssignmentOperatorContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.BlockStatementsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.EnhancedForStatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.ExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.ForInitContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.ForUpdateContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.LeftHandSideContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.PostfixExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.StatementContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.StatementNoShortIfContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.SwitchBlockContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.SwitchBlockStatementGroupContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.SwitchLabelContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.SwitchLabelsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.Java8Parser.VariableDeclaratorIdContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class JavaToPSDVisitor extends Java8BaseVisitor<Flowchart<LayoutSegment, LayoutElement>>
{

    private static Flowchart<LayoutSegment, LayoutElement> flowchart;
    private static LayoutSegment actualSegment;
    private static LayoutElement lastElement;
    private Set<String> variableNames = new HashSet<>();
    private String lastSwitchBranch = "";

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx)
    {
        if (ctx.getParent() instanceof EnhancedForStatementContext || ctx.getParent() instanceof ForInitContext) {
            return flowchart;
        }
        String var = ctx.getText().replace("[", "");
        var = var.replace("]", "");
        lastElement = actualSegment.addSymbol(lastElement,
                EnumSymbol.PROCESS.getInstance(""));
        System.out.println("var: " + var);
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(lastElement.getSymbol(),
                var, "");
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitComment(Java8Parser.CommentContext ctx)
    {
        lastElement = AddComment(actualSegment, lastElement, getCommentCommand(
                ctx.getText()), false);
        return flowchart;
    }

    private static String getCommentCommand(String command)
    {
        command = command.trim();
        if (command.startsWith("/*")) {
            return command.substring(2, command.length() - 2);
        } else if (command.startsWith("//")) {
            return command.substring(2, command.length());
        }
        return null;
    }

    private static LayoutElement AddComment(LayoutSegment actualSegment, LayoutElement actualElement,
            String commentText, boolean pair)
    {
        LayoutElement beforeElement = null;
        if (pair && !(actualElement.getSymbol() instanceof Comment)) {
            int index = actualSegment.indexOfElement(actualElement);
            if (index == -1) {
                if (actualSegment.getParentElement().indexOfInnerSegment(actualSegment) == 1 && !(actualSegment.getParentElement().getSymbol() instanceof Switch)) { // jen pri prvnim innersegmentu v neswitch.. protoze podle kodu se komentar ocekava v jeho vetvi, ne v symbolu
                    // jedna se o parovy komentar, ktery by mel nalezet rodici actualsegmentu
                    int indx = actualElement.getParentSegment().indexOfElement(actualElement);
                    if (indx != -1 && (indx == 0 || !(actualElement.getParentSegment().getElement(
                            indx - 1).getSymbol() instanceof Comment) || !actualElement.getParentSegment().getElement(
                                    indx - 1).getSymbol().hasPairSymbol())) {
                        // jestli rodic uz nema parovy komentar, pridelim mu ho ted, protoze tim nezmenim actual element
                        actualSegment = actualElement.getParentSegment();
                        if (indx == 0) {
                            beforeElement = actualSegment.getParentElement();
                        } else {
                            beforeElement = actualSegment.getElement(indx - 1);
                        }
                    }
                }
            } else if (index == 0) {
                beforeElement = actualSegment.getParentElement();
            } else if (!(actualSegment.getElement(index - 1).getSymbol() instanceof Comment) || !actualSegment.getElement(
                    index - 1).getSymbol().hasPairSymbol()) {
                beforeElement = actualSegment.getElement(index - 1);
            }
        }

        Symbol comment = EnumSymbol.COMMENT.getInstance(commentText);
        if (beforeElement != null) {
            comment.setHasPairSymbol(true);
            actualSegment.addSymbol(beforeElement, comment);
            return actualElement;
        } else {
            return actualSegment.addSymbol(actualElement, comment);
        }
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitVariableInitializer(Java8Parser.VariableInitializerContext ctx)
    {
        String initializer = ctx.getText();
        if (initializer.startsWith("{") && initializer.endsWith("}")) {
            initializer = initializer.replace("{", "[");
            initializer = initializer.replace("}", "]");
        }
        if (initializer.startsWith("new") && initializer.contains("[") && initializer.contains("]")) {
            if (!variableNames.contains(initializer.substring(0, initializer.indexOf("[")))) {
                initializer = initializer.substring(initializer.indexOf("["));
            }
        }
        if (initializer.contains("==")) {
            initializer = initializer.replaceAll("==", "=");
        }
        variableNames.add(lastElement.getSymbol().getCommands().get("var"));
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(lastElement.getSymbol(),
                lastElement.getSymbol().getCommands().get("var"), initializer);
        lastElement.getSymbol().setValueAndSize(
                lastElement.getSymbol().getDefaultValue());
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitMethodBody(Java8Parser.MethodBodyContext ctx)
    {
        actualSegment = new LayoutSegment(null);
        flowchart = new Flowchart<>(actualSegment);
        lastElement = actualSegment.addSymbol(null, EnumSymbol.STARTEND.getInstance(
                "Start"));
        actualSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("End"));
        visitChildren(ctx);
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitStatement(Java8Parser.StatementContext ctx)
    {
        if (ctx.getText() != null && ctx.getText().startsWith("System.out.print")) {
            String toPrint = ctx.getText();
            int beginIndex = toPrint.indexOf('(');
            int endIndex = toPrint.lastIndexOf(')');
            toPrint = toPrint.substring(beginIndex + 1, endIndex);
            Symbol symbol = EnumSymbol.IO.getInstance("");
            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateOValues(symbol, toPrint);
            symbol.setValueAndSize(symbol.getDefaultValue());
            lastElement = actualSegment.addSymbol(lastElement, symbol);

        }
        visitChildren(ctx);
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitIfThenStatement(Java8Parser.IfThenStatementContext ctx)
    {
        LayoutElement tmpDecision = null;
        LayoutSegment tmpSegment = actualSegment;
        String condition = "true";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof StatementContext) {
                    visitStatement((StatementContext) child).getMainSegment();
                }
                if (child instanceof StatementNoShortIfContext) {
                    visitStatementNoShortIf((StatementNoShortIfContext) child).getMainSegment();
                }
                if (child instanceof ExpressionContext) {
                    condition = child.getText();
                    lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.DECISION.getInstance(condition));
                    tmpDecision = lastElement;
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision.generateValues(
                            lastElement.getSymbol(), condition);
                    actualSegment = tmpDecision.getInnerSegment(1);
                }
            }
        }
        lastElement = tmpDecision;
        actualSegment = tmpSegment;
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx)
    {
        LayoutElement tmpDecision = null;
        LayoutSegment tmpSegment = actualSegment;
        String condition = "true";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (tmpDecision != null) {
                }
                if (child instanceof StatementContext && child.getText() != null && !child.getText().equals("{}")) {
                    visitStatement((StatementContext) child);
                }
                if (child instanceof StatementNoShortIfContext && child.getText() != null && !child.getText().equals(
                        "{}")) {
                    visitStatementNoShortIf((StatementNoShortIfContext) child);
                }
                if (child instanceof ExpressionContext) {
                    condition = child.getText();
                    lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.DECISION.getInstance(condition));
                    tmpDecision = lastElement;
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision.generateValues(
                            lastElement.getSymbol(), condition);
                    actualSegment = tmpDecision.getInnerSegment(1);
                }
                if (child instanceof TerminalNodeImpl && child.getText().contains("else")) {
                    actualSegment = tmpDecision.getInnerSegment(0);
                }
            }
        }
        lastElement = tmpDecision;
        actualSegment = tmpSegment;
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitSwitchStatement(Java8Parser.SwitchStatementContext ctx)
    {
        LayoutElement tmpSwitch = null;
        LayoutSegment tmpSegment = actualSegment;
        String text = "";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof ExpressionContext) {
                    text = child.getText();

                }
                if (child instanceof SwitchBlockContext) {
                    int innerSegmentCount = extractInnerSegmentCount(child);
                    lastElement = actualSegment.addSymbol(lastElement, EnumSymbol.SWITCH.getInstance(text),
                            innerSegmentCount);
                    cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch.generateValues(lastElement, text,
                            new String[0]);
                    tmpSwitch = lastElement;
                    visitSwitchBlock((SwitchBlockContext) child);
                }
            }
        }
        lastElement = tmpSwitch;
        actualSegment = tmpSegment;
        return flowchart;
    }

    private int extractInnerSegmentCount(ParseTree child)
    {
        int count = child.getChildCount();
        for (int i = 0; i < child.getChildCount(); i++) {
            if (child.getChild(i).getText().contains("default")) {
                count -= 1;
            }
        }
        return count - 1;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitSwitchBlock(Java8Parser.SwitchBlockContext ctx)
    {
        LayoutSegment tmpSegment = actualSegment;
        LayoutElement tmpElement = lastElement;
        int indexOfInnerSegment = 1;
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof SwitchBlockStatementGroupContext) {
                    lastElement = tmpElement;
                    if (indexOfInnerSegment == tmpElement.getInnerSegmentsCount()) {
                        indexOfInnerSegment = 0;
                    }
                    actualSegment = tmpElement.getInnerSegment(indexOfInnerSegment++);
                    visitSwitchBlockStatementGroup((SwitchBlockStatementGroupContext) child);
                    actualSegment.setDescription(lastSwitchBranch);
                }
            }
        }
        lastElement = tmpElement;
        actualSegment = tmpSegment;
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitSwitchBlockStatementGroup(
            Java8Parser.SwitchBlockStatementGroupContext ctx)
    {
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof SwitchLabelsContext) {
                    visitSwitchLabels((SwitchLabelsContext) child);
                }
                if (child instanceof BlockStatementsContext) {
                    visitBlockStatements((BlockStatementsContext) child);
                }
            }
        }
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitSwitchLabels(Java8Parser.SwitchLabelsContext ctx)
    {
        visitSwitchLabel((SwitchLabelContext) ctx.getChild(0));
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitSwitchLabel(Java8Parser.SwitchLabelContext ctx)
    {
        String conditionVar = ctx.getText().replace("case", "");
        conditionVar = conditionVar.replace(":", "");
        if (conditionVar.contains("default")) {
            return flowchart;
        }
        List<String> constants = new ArrayList<>();
        constants.add(conditionVar);
        extractConstants(constants, 1);
        lastSwitchBranch = conditionVar;
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch.generateValues(lastElement,
                lastElement.getSymbol().getCommands().get("conditionVar"),
                constants.toArray(new String[0]));
        return flowchart;
    }

    private void extractConstants(List<String> constants, Integer key)
    {
        if (lastElement.getSymbol().getCommands().containsKey(key.toString())) {
            constants.add(key - 1, lastElement.getSymbol().getCommands().get(key.toString()));
            extractConstants(constants, key + 1);
        }
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitWhileStatement(Java8Parser.WhileStatementContext ctx)
    {

        LayoutSegment tmpSegment = actualSegment;
        String condition = "";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof ExpressionContext) {
                    condition = child.getText();
                }
            }
        }
        lastElement = actualSegment.addSymbol(lastElement,
                EnumSymbol.LOOPCONDITIONUP.getInstance(""));
        LayoutElement tmpElement = lastElement;
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart.generateValues(
                lastElement.getSymbol(), condition);
        lastElement.getSymbol().setValueAndSize(
                lastElement.getSymbol().getDefaultValue());
        actualSegment = lastElement.getInnerSegment(1);
        visitChildren(ctx);
        actualSegment = tmpSegment;
        lastElement = tmpElement;
        lastElement = actualSegment.addSymbol(lastElement, new LoopEnd());
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitDoStatement(Java8Parser.DoStatementContext ctx)
    {
        LayoutSegment tmpSegment = actualSegment;
        String condition = "";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof ExpressionContext) {
                    condition = child.getText();
                }
            }
        }
        lastElement = actualSegment.addSymbol(lastElement,
                EnumSymbol.LOOPCONDITIONDOWN.getInstance(""));
        LayoutElement tmpElement = lastElement;
        lastElement = actualSegment.addSymbol(lastElement, new LoopEnd());

        actualSegment = tmpElement.getInnerSegment(1);
        tmpElement = lastElement;
        visitChildren(ctx);
        actualSegment = tmpSegment;
        lastElement = tmpElement;
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd.generateValues(
                lastElement.getSymbol(), condition);
        lastElement.getSymbol().setValueAndSize(
                lastElement.getSymbol().getDefaultValue());
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitBasicForStatement(Java8Parser.BasicForStatementContext ctx)
    {
        LayoutSegment tmpSegment = actualSegment;
        LayoutElement tmpElement = actualSegment.addSymbol(lastElement,
                EnumSymbol.FOR.getInstance(""));
        String var = "";
        String inc = "";
        String from = "";
        String to = "";
        List<ParseTree> toDelete = new ArrayList<>();
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof ForInitContext) {
                    toDelete.add(child);
                    String initText = child.getText();
                    if (initText.startsWith("int")) {
                        var = initText.substring(3, initText.indexOf("="));
                        from = initText.substring(initText.indexOf("=") + 1);
                    }
                }
                if (child instanceof ExpressionContext) {
                    String expressionText = child.getText();
                    boolean varAfterOperand = expressionText.indexOf(var.charAt(0)) > expressionText.indexOf("<")
                            && expressionText.indexOf(var.charAt(0)) > expressionText.indexOf(">");
                    if (expressionText.contains("<")) {
                        to = varAfterOperand ? expressionText.substring(0, expressionText.indexOf("<")) : expressionText.substring(
                                expressionText.indexOf("<") + 1);
                        if (expressionText.contains("=")) {
                            to = varAfterOperand ? expressionText.substring(0, expressionText.indexOf("<")) : expressionText.substring(
                                    expressionText.indexOf("=") + 1);
                            String incremented = to + "+1";
                            to = varAfterOperand ? incremented.toString() : to;
                            toDelete.add(child);
                        }
                    }
                    if (expressionText.contains(">")) {
                        to = varAfterOperand ? expressionText.substring(0, expressionText.indexOf(">")) : expressionText.substring(
                                expressionText.indexOf(">") + 1);
                        if (expressionText.contains("=")) {
                            to = varAfterOperand ? expressionText.substring(0, expressionText.indexOf(">")) : expressionText.substring(
                                    expressionText.indexOf("=") + 1);
                            String decremented = to + "-1";
                            to = varAfterOperand ? decremented.toString() : to;
                        }
                    }

                }
                if (child instanceof ForUpdateContext) {
                    toDelete.add(child);
                    String forUpdateText = child.getText();
                    inc = forUpdateText.substring(var.length());
                    if (inc.equals("++")) {
                        inc = "1";
                    }
                    if (inc.equals("--")) {
                        inc = "-1";
                    }
                    inc = inc.replace("=", "");
                    if (inc.startsWith(var)) {
                        inc = inc.substring(var.length());
                    }
                }
            }

            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForValues(
                    tmpElement.getSymbol(), var, from, to, inc);
            tmpElement.getSymbol().setValueAndSize(
                    tmpElement.getSymbol().getDefaultValue());
        }
        actualSegment = tmpElement.getInnerSegment(1);
        ctx.children.removeAll(toDelete);
        visitChildren(ctx);
        actualSegment = tmpSegment;
        lastElement = tmpElement;
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx)
    {
        LayoutSegment tmpSegment = actualSegment;
        LayoutElement tmpElement = actualSegment.addSymbol(lastElement,
                EnumSymbol.FOR.getInstance(""));
        String var = "";
        String array = "";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof VariableDeclaratorIdContext) {
                    var = child.getText();

                }
                if (child instanceof ExpressionContext) {
                    array = child.getText();
                }
            }
            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForeachValues(
                    tmpElement.getSymbol(), var, array);
            tmpElement.getSymbol().setValueAndSize(
                    tmpElement.getSymbol().getDefaultValue());
        }
        actualSegment = tmpElement.getInnerSegment(1);
        visitChildren(ctx);
        actualSegment = tmpSegment;
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitAssignment(Java8Parser.AssignmentContext ctx)
    {
        Symbol symbol = extractAssignmentSymbol(ctx);
        lastElement = actualSegment.addSymbol(lastElement, symbol);

        visitChildren(ctx);
        return flowchart;
    }

    private Symbol extractAssignmentSymbol(Java8Parser.AssignmentContext ctx)
    {

        String exp = "";
        String var = "";
        String value = "";
        String operator = "";
        for (ParseTree child : ctx.children) {
            if (child instanceof LeftHandSideContext) {
                var = child.getText();
                var = var.replace("++", "");
                var = var.replace("--", "");
            }
            if (child instanceof ExpressionContext) {
                value = child.getText();
                value = value.replace("++", "");
                value = value.replace("--", "");
            }
            if (child instanceof AssignmentOperatorContext) {
                operator = child.getText();
            }
            exp += child.getText();
        }
        Symbol symbol = EnumSymbol.PROCESS.getInstance(exp);
        variableNames.add(var);
        value = extractAssignmentValue(operator, value, var);
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(symbol,
                var, value);
        System.out.println("exp: " + exp);
        System.out.println("var: " + var);
        System.out.println("value: " + value);
        System.out.println("operator: " + operator);
        return symbol;
    }

    private String extractAssignmentValue(String operator, String value, String var)
    {
        switch (operator) {
            case "++":
                return var + "+1";
            case "--":
                return var + "-1";
            case "+=":
                return var + " + " + value;
            case "-=":
                return var + " - " + value;
            case "*=":
                return var + " * " + value;
            case "/=":
                return var + " / " + value;
            case "%=":
                return var + " % " + value;
            case "==":
                return var + " = " + value;
            default:
                return value;
        }
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitPreIncrementExpression(
            Java8Parser.PreIncrementExpressionContext ctx)
    {
        int index = actualSegment.indexOfElement(lastElement);
        LayoutElement tmpElement = null;
        if (index > 0) {
            tmpElement = actualSegment.getElement(index - 1);
        }
        tmpElement = actualSegment.addSymbol(tmpElement,
                EnumSymbol.PROCESS.getInstance(ctx.getText()));
        String var = "";
        var = ctx.getText();
        var = var.replace("++", "");
        var = var.replace("--", "");
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(tmpElement.getSymbol(),
                var, var + "+" + 1);
        visitChildren(ctx);
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitPreDecrementExpression(
            Java8Parser.PreDecrementExpressionContext ctx)
    {
        int index = actualSegment.indexOfElement(lastElement);
        LayoutElement tmpElement = null;
        if (index > 0) {
            tmpElement = actualSegment.getElement(index - 1);
        }
        tmpElement = actualSegment.addSymbol(tmpElement,
                EnumSymbol.PROCESS.getInstance(ctx.getText()));
        String var = "";
        var = ctx.getText();
        var = var.replace("++", "");
        var = var.replace("--", "");
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(tmpElement.getSymbol(),
                var, var + "-" + 1);
        visitChildren(ctx);
        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitPostfixExpression(Java8Parser.PostfixExpressionContext ctx)
    {

        String var = "";
        var = ctx.getText();

        if (var.endsWith("--")) {
            var = var.replace("--", "");
            lastElement = actualSegment.addSymbol(lastElement,
                    EnumSymbol.PROCESS.getInstance(ctx.getText()));

            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(lastElement.getSymbol(),
                    var, var + "-" + 1);
            visitChildren(ctx);
        } else {
            visitChildren(ctx);
        }

        return flowchart;
    }

    @Override
    public Flowchart<LayoutSegment, LayoutElement> visitPostIncrementExpression(
            Java8Parser.PostIncrementExpressionContext ctx)
    {
        String var = "";
        if (ctx.getChildCount() > 0) {
            for (ParseTree child : ctx.children) {
                if (child instanceof PostfixExpressionContext) {
                    var = child.getText();
                    lastElement = actualSegment.addSymbol(lastElement,
                            EnumSymbol.PROCESS.getInstance(var));
                    var = var.replace("++", "");
                }
            }
        }
        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(lastElement.getSymbol(),
                var, var + "+" + 1);
        return flowchart;
    }

}
