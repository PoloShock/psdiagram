package cz.miroslavbartyzal.psdiagram.app.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ParseResult
{
    
    private final String input;
    private final List<ParseError> parseErrors = new ArrayList<>();
    private boolean wholeInputParsed = true;
    
    public ParseResult(String input)
    {
        this.input = input;
    }
    
    public void addErrorInfo(String errorMessage, int errorSubstringStart, int errorSubstringEnd)
    {
        parseErrors.add(new ParseError(errorMessage, errorSubstringStart, errorSubstringEnd));
    }
    
    public void addErrorInfo(ParseError errorInfo)
    {
        parseErrors.add(errorInfo);
    }
    
    public boolean isInputValid()
    {
        return parseErrors.isEmpty();
    }
    
    public List<ParseError> getParseErrors()
    {
        return parseErrors;
    }
    
    public String getInput()
    {
        return input;
    }
    
    public boolean isWholeInputParsed()
    {
        return wholeInputParsed;
    }
    
    public void setWholeInputParsed(boolean wholeInputParsed)
    {
        this.wholeInputParsed = wholeInputParsed;
    }
    
}
