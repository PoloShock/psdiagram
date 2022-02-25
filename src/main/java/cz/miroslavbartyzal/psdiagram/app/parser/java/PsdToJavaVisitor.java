package cz.miroslavbartyzal.psdiagram.app.parser.java;

import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser.Constant_ArrayContext;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser.Constant_Array_RepeatingPartContext;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser.ExpressionContext;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser.Solo_ListOf_ConstantsContext;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParserBaseVisitor;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PsdToJavaVisitor extends PSDGrammarParserBaseVisitor<String>
{
    
    private static final String LINE_SEP = System.lineSeparator();
    
    private final String input;
    
    public PsdToJavaVisitor(String input)
    {
        this.input = input;
    }
    
    @Override
    public String visitSolo_ListOf_Constants(Solo_ListOf_ConstantsContext ctx)
    {
        String result = "";
        List<PSDGrammarParser.ListOf_Constants_RepeatingPartContext> repeatingPartContexts =
                ctx.listOf_Constants_RepeatingPart();
        for (PSDGrammarParser.ListOf_Constants_RepeatingPartContext repeatingPartContext : repeatingPartContexts) {
            result += "case " + visit(repeatingPartContext.constant_solo()) + ":" + LINE_SEP;
        }
        PSDGrammarParser.Constant_soloContext constantSoloContext = ctx.constant_solo();
        if (constantSoloContext != null) {
            result += "case " + visit(constantSoloContext) + ":";
        }
        
        return result;
    }
    
    @Override
    public String visitConstant_Array(Constant_ArrayContext ctx)
    {
        String result = "";
        if (!parentsContain(ctx, Constant_ArrayContext.class)) {
            result += "new {typ_promenne_pole}" + getBrackets(ctx) + " ";
        }
        
        result += "{";
        List<Constant_Array_RepeatingPartContext> repeatingPartContexts =
                ctx.constant_Array_RepeatingPart();
        for (Constant_Array_RepeatingPartContext repeatingPartContext : repeatingPartContexts) {
            result += visit(repeatingPartContext);
        }
        if (ctx.expression() != null) {
            result += visit(ctx.expression());
        }
        result += "}";
        
        return result;
    }
    
    private String getBrackets(Constant_ArrayContext ctx)
    {
        List<ExpressionContext> expressionContexts = new ArrayList<>();
        List<Constant_Array_RepeatingPartContext> repeatingPartContexts =
                ctx.constant_Array_RepeatingPart();
        for (Constant_Array_RepeatingPartContext repeatingPartContext : repeatingPartContexts) {
            expressionContexts.add(repeatingPartContext.expression());
        }
        if (ctx.expression() != null) {
            expressionContexts.add(ctx.expression());
        }
        
        int maxInnerConstantArrayContextCount = 0;
        for (ExpressionContext expressionContext : expressionContexts) {
            int childrenCount = childrenCount(expressionContext, Constant_ArrayContext.class);
            if (childrenCount > maxInnerConstantArrayContextCount) {
                maxInnerConstantArrayContextCount = childrenCount;
            }
        }
        maxInnerConstantArrayContextCount++;
        
        String result = "";
        for (int i = 0; i < maxInnerConstantArrayContextCount; i++) {
            result += "[]";
        }
        return result;
    }
    
    @Override
    public String visitTerminal(TerminalNode node)
    {
        if (node.getSymbol().getType() == Recognizer.EOF) {
            return "";
        }
        
        String spaceLeft = getTerminalSpaceLeft(node.getSymbol().getStartIndex());
        String spaceRight = getTerminalSpaceRight(node.getSymbol().getStopIndex());
        
        String nodeText;
        switch (node.getSymbol().getType()) {
            case PSDGrammarParser.EQUAL:
                nodeText = "==";
                break;
            case PSDGrammarParser.FLOORDIV:
                nodeText = "/";
                break;
            default:
                nodeText = node.getText();
                break;
        }
        
        return spaceLeft + nodeText + spaceRight;
    }
    
    private String getTerminalSpaceLeft(int startIdx)
    {
        int leftSpaceIdx = startIdx;
        while (leftSpaceIdx > 0 && input.substring(leftSpaceIdx - 1, leftSpaceIdx).matches("\\s")) {
            leftSpaceIdx--;
        }
        if (leftSpaceIdx != 0) {
            // do left space-exploring only on the beginning of the input
            leftSpaceIdx = startIdx;
        }
        return input.substring(leftSpaceIdx, startIdx);
    }
    
    private String getTerminalSpaceRight(int stopIdx)
    {
        int rightSpaceIdx = stopIdx + 1;
        while (rightSpaceIdx < input.length() && input.substring(rightSpaceIdx, rightSpaceIdx + 1).matches("\\s")) {
            rightSpaceIdx++;
        }
        return input.substring(stopIdx + 1, rightSpaceIdx);
    }
    
    @Override
    protected String aggregateResult(String aggregate, String nextResult)
    {
        if (aggregate == null && nextResult == null) {
            return "";
        }
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }
        
        return aggregate + nextResult;
    }
    
    private <T extends ParseTree> boolean parentsContain(ParserRuleContext root, Class<T> wantedParent)
    {
        if (root == null) {
            return false;
        }
        
        ParserRuleContext parent = root.getParent();
        if (parent == null) {
            return false;
        }
        if (wantedParent.isInstance(parent)) {
            return true;
        }
        
        return parentsContain(parent, wantedParent);
    }
    
    private <T extends ParseTree> int childrenCount(ParserRuleContext root, Class<T> childToCount)
    {
        if (root == null || root.children == null) {
            return 0;
        }
        
        int count = 0;
        for (ParseTree child : root.children) {
            if (child instanceof ParserRuleContext) {
                if (childToCount.isInstance(child)) {
                    count++;
                }
                count += childrenCount((ParserRuleContext) child, childToCount);
            }
        }
        return count;
    }
    
}
