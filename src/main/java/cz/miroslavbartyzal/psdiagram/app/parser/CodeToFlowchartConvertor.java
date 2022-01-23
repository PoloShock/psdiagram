package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.global.StringFunctions;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class CodeToFlowchartConvertor<P extends Parser, L extends Lexer>
{
    
    private final int maxParseErrorsAllowed;
    private ParseResult parseResult;
    
    protected CodeToFlowchartConvertor(int maxParseErrorsAllowed)
    {
        this.maxParseErrorsAllowed = maxParseErrorsAllowed;
    }
    
    public ConversionResult convertToPsd(String code)
    {
        parseResult = new ParseResult(code);
        Flowchart<LayoutSegment, LayoutElement> flowchart = generateFlowchart(code);
        
        ConversionResult conversionResult = new ConversionResult();
        conversionResult.setFlowchart(flowchart);
        if (!parseResult.isInputValid()) {
            String errorMessage = createErrorMessage(code);
            conversionResult.setErrorMessage(errorMessage);
        }
        
        if (flowchart == null) {
            if (conversionResult.getErrorMessage() == null) {
                // unexpected exception occurred and there is no error message to show -> let's make some
                String message = "Při vytváření diagramu došlo k neočekávané chybě";
                conversionResult.setErrorMessage(message);
            }
        }
        
        return conversionResult;
    }
    
    protected abstract L getLexer(CharStream charStream);
    
    protected abstract P getParser(CommonTokenStream tokenStream);
    
    protected abstract ParseTree createParseTree(P parser);
    
    protected abstract Flowchart<LayoutSegment, LayoutElement> generateFlowchartFromParseTree(ParseTree parseTree, String code);
    
    private Flowchart<LayoutSegment, LayoutElement> generateFlowchart(String code)
    {
        try {
            L lexer = getLexer(CharStreams.fromString(code));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MyAntlrSyntaxErrorListener(false)
            {
                @Override
                public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
                {
                    if (parseResult.getParseErrors().size() >= maxParseErrorsAllowed) {
                        parseResult.setWholeInputParsed(false);
                        throw new ParseCancellationException(
                                String.format("Maximum parser errors reached (%s)", maxParseErrorsAllowed));
                    }
                    parseResult.addErrorInfo(errorMessage, beginIndex, endIndex);
                }
            });
            P parser = getParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MyAntlrSyntaxErrorListener(false)
            {
                @Override
                public void onSyntaxError(String errorMessage, int beginIndex, int endIndex)
                {
                    if (parseResult.getParseErrors().size() >= maxParseErrorsAllowed) {
                        parseResult.setWholeInputParsed(false);
                        throw new ParseCancellationException(
                                String.format("Maximum parser errors reached (%s)", maxParseErrorsAllowed));
                    }
                    parseResult.addErrorInfo(errorMessage, beginIndex, endIndex);
                }
            });
            parser.setErrorHandler(new AntlrErrorStrategyTranslated(false));
            
            if (!parseResult.isInputValid()) {
                return null;
            }
            
            ParseTree tree = createParseTree(parser);

//            <dependency>
//                <groupId>org.antlr</groupId>
//                <artifactId>antlr4</artifactId>
//                <version>4.9.3</version>
//            </dependency>
//    
//            //show AST in GUI
//            JFrame frame = new JFrame("Antlr AST");
//            JPanel panel = new JPanel();
//            TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
//            viewer.setScale(1.5); // Scale a little
//            panel.add(viewer);
//            frame.add(panel);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.pack();
//            frame.setVisible(true);
            
            return generateFlowchartFromParseTree(tree, code);
        } catch (RuntimeException ignored) {
            return null;
        }
    }
    
    private String createErrorMessage(String code)
    {
        StringBuilder message = new StringBuilder("<html>Diagram se nepodařilo vytvořit!");
        List<ParseError> parseErrors = parseResult.getParseErrors();
        if (!parseErrors.isEmpty()) {
            message.append("<br /><br />Problémová místa:<ul>");
            
            for (ParseError parseError : parseErrors) {
                if (parseError.getStartIndex() == parseError.getEndIndex()) {
                    message.append(String.format("<li>(místo na pozici %s)", parseError.getStartIndex()));
                } else {
                    String thePart = code.substring(parseError.getStartIndex(), parseError.getEndIndex());
                    message.append(
                            String.format("<li>`%s` (řetězec na pozici %s)", thePart, parseError.getStartIndex()));
                }
                
                String errorMessage = parseError.getErrorMessage();
                if (errorMessage != null) {
                    errorMessage = errorMessage.replaceAll(" *\\n *", " ");
                    errorMessage = StringFunctions.escapeHTML(errorMessage);
                    message.append(String.format("<ul><li>%s</li></ul>", errorMessage));
                }
                message.append("</li>");
            }
            if (!parseResult.isWholeInputParsed()) {
                message.append("<li>a tak dále ...</li>");
            }
            
            message.append("</ul>");
        }
        message.append("</html>");
        
        return message.toString();
    }
    
}
