/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Switch;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface FlowchartGenerator
{
    String LINE_SEP = System.lineSeparator();
    String INVALID_COMMAND = String.format("Příkaz se%snepodařilo%spřevést", LINE_SEP, LINE_SEP);
    
    default LayoutElement addComment(LayoutSegment segment, LayoutElement element, String commentText, boolean pair)
    {
        LayoutElement beforeElement = null;
        if (pair && !(element.getSymbol() instanceof Comment)) {
            int index = segment.indexOfElement(element);
            if (index == -1) {
                if (segment.getParentElement().indexOfInnerSegment(segment) == 1 
                        && !(segment.getParentElement().getSymbol() instanceof Switch)) { // jen pri prvnim innersegmentu v neswitch.. protoze podle kodu se komentar ocekava v jeho vetvi, ne v symbolu
                    // jedna se o parovy komentar, ktery by mel nalezet rodici actualsegmentu
                    int indx = element.getParentSegment().indexOfElement(element);
                    if (indx != -1 && (indx == 0 || !(element.getParentSegment().getElement(
                            indx - 1).getSymbol() instanceof Comment) || !element.getParentSegment().getElement(
                            indx - 1).getSymbol().hasPairSymbol())) {
                        // jestli rodic uz nema parovy komentar, pridelim mu ho ted, protoze tim nezmenim actual element
                        segment = element.getParentSegment();
                        if (indx == 0) {
                            beforeElement = segment.getParentElement();
                        } else {
                            beforeElement = segment.getElement(indx - 1);
                        }
                    }
                }
            } else if (index == 0) {
                beforeElement = segment.getParentElement();
            } else if (!(segment.getElement(index - 1).getSymbol() instanceof Comment) || !segment.getElement(
                    index - 1).getSymbol().hasPairSymbol()) {
                beforeElement = segment.getElement(index - 1);
            }
        }
        
        Symbol comment = EnumSymbol.COMMENT.getInstance(commentText);
        if (beforeElement != null) {
            comment.setHasPairSymbol(true);
            segment.addSymbol(beforeElement, comment);
            return element;
        } else {
            return segment.addSymbol(element, comment);
        }
    }
    
    default <T extends ParseTree> boolean parentsContain(ParserRuleContext root, Class<T> wantedParent)
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
    
    default <T extends ParseTree> boolean directChildrenContain(ParserRuleContext root, Class<T> wantedChild)
    {
        if (root == null || root.children == null) {
            return false;
        }
        
        for (ParseTree child : root.children) {
            if (child instanceof ParserRuleContext) {
                if (wantedChild.isInstance(child)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    default <T extends ParseTree> boolean childrenContain(ParserRuleContext root, Class<T> wantedChild)
    {
        if (root == null || root.children == null) {
            return false;
        }
        
        for (ParseTree child : root.children) {
            if (child instanceof ParserRuleContext) {
                if (wantedChild.isInstance(child)) {
                    return true;
                }
                if (childrenContain((ParserRuleContext) child, wantedChild)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    default <T extends ParseTree> boolean childrenContain(ParserRuleContext root, int tokenType)
    {
        if (root == null || root.children == null) {
            return false;
        }
        
        for (ParseTree child : root.children) {
            if (child instanceof TerminalNode) {
                TerminalNode node = (TerminalNode) child;
                Token symbol = node.getSymbol();
                if (symbol.getType() == tokenType) {
                    return true;
                }
            }
            if (child instanceof ParserRuleContext) {
                if (childrenContain((ParserRuleContext) child, tokenType)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    default <T extends ParseTree> boolean ancestorExists(ParserRuleContext root, Class<T> ancestorType, int targetDepth)
    {
        ParserRuleContext currentParent = root;
        for (int i = 0; i < targetDepth && currentParent != null; i++) {
            currentParent = currentParent.getParent();
        }
        
        return ancestorType.isInstance(currentParent);
    }
    
    default <T extends ParseTree> T getParent(ParserRuleContext root, Class<T> wantedParent, int targetDepth)
    {
        ParserRuleContext currentParent = root;
        for (int i = 0; i < targetDepth && currentParent != null; i++) {
            currentParent = currentParent.getParent();
        }
        
        if (!wantedParent.isInstance(currentParent)) {
            throw new CannotParseException("Unexpected parent context");
        }
        
        return (T) currentParent;
    }
    
    default <T extends ParseTree> T getTheOnlyChildFromSingleChildrenTree(ParserRuleContext root,
            Class<T> wantedChildClass, int targetDepth)
    {
        ParseTree child = root;
        for (int i = 0; i < targetDepth; i++) {
            if (child.getChildCount() != 1) {
                throw new CannotParseException("Unexpected count of children");
            }
            child = child.getChild(0);
        }
        
        if (!wantedChildClass.isInstance(child)) {
            throw new CannotParseException(
                    String.format("Unexpected child context. Expected '%s', was '%s'", wantedChildClass.getSimpleName(),
                            child.getClass().getSimpleName()));
        }
        
        return (T) child;
    }
    
    default <T extends ParseTree> List<T> getChildren(ParserRuleContext root, Class<T> wantedChildClass,
            int expectedCount)
    {
        List<T> resultList = getChildren(root, wantedChildClass);
        if (resultList.size() != expectedCount) {
            throw new CannotParseException(
                    String.format("Unexpected rule count present in '%s'", root.getClass().getSimpleName()));
        }
        return resultList;
    }
    
    default <T extends ParseTree> List<T> getChildren(ParserRuleContext root, Class<T> wantedChildClass)
    {
        List<T> resultList = new ArrayList<>();
        
        if (root == null || root.children == null) {
            return resultList;
        }
        
        for (ParseTree child : root.children) {
            if (wantedChildClass.isInstance(child)) {
                resultList.add((T) child);
            }
            if (child instanceof ParserRuleContext) {
                resultList.addAll(getChildren((ParserRuleContext) child, wantedChildClass));
            }
        }
        return resultList;
    }
    
}
