package cz.miroslavbartyzal.psdiagram.app.codeImportExport;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.parser.CodeToFlowchartConvertor;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaLexer;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaParser;
import cz.miroslavbartyzal.psdiagram.app.parser.java.JavaToFlowchartVisitor;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class JavaToFlowchartConvertor extends CodeToFlowchartConvertor<JavaParser, JavaLexer>
{
    
    public JavaToFlowchartConvertor(int maxParseErrorsAllowed)
    {
        super(maxParseErrorsAllowed);
    }
    
    @Override
    protected JavaLexer getLexer(CharStream charStream)
    {
        return new JavaLexer(charStream);
    }
    
    @Override
    protected JavaParser getParser(CommonTokenStream tokenStream)
    {
        return new JavaParser(tokenStream);
    }
    
    @Override
    protected ParseTree createParseTree(JavaParser parser)
    {
        return parser.blockStatementsOptional();
    }
    
    @Override
    protected Flowchart<LayoutSegment, LayoutElement> generateFlowchartFromParseTree(ParseTree parseTree, String code)
    {
        JavaToFlowchartVisitor visitor = new JavaToFlowchartVisitor(code);
        visitor.visit(parseTree);
        return visitor.getFlowchart();
    }
    
}
