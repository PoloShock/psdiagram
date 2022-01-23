package cz.miroslavbartyzal.psdiagram.app.parser;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ParseError
{

    private final String errorMessage;
    private final int startIndex;
    private final int endIndex;

    public ParseError(String errorMessage, int startIndex, int endIndex)
    {
        this.errorMessage = errorMessage;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}
