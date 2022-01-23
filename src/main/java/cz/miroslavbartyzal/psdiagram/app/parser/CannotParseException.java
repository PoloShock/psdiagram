package cz.miroslavbartyzal.psdiagram.app.parser;

import org.antlr.v4.runtime.ParserRuleContext;

public class CannotParseException extends RuntimeException
{
    
    public CannotParseException(String message)
    {
        super(message);
    }
    
    public CannotParseException(ParserRuleContext parserRuleContext, String input)
    {
        super(createMessage(parserRuleContext, input));
    }
    
    public CannotParseException(ParserRuleContext parserRuleContext, String input, Throwable cause)
    {
        super(createMessage(parserRuleContext, input), cause);
    }
    
    private static String createMessage(ParserRuleContext parserRuleContext, String input)
    {
        try {
            int startIdx = parserRuleContext.getStart().getStartIndex();
            int stopIdx = parserRuleContext.getStop().getStopIndex() + 1;
            
            String thePart = input.substring(startIdx, stopIdx);
            return String.format("Could not parse '%s'", thePart);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    
}
