/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDParseError
{

    private final String errorMessage;
    private final int startIndex;
    private final int endIndex;

    public PSDParseError(String errorMessage, int startIndex, int endIndex)
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
