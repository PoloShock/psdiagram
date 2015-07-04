/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDParseResult
{

    private final String input;
    private final List<PSDParseError> parseErrors = new ArrayList<>();

    public PSDParseResult(String input)
    {
        this.input = input;
    }

    public void addErrorInfo(String errorMessage, int errorSubstringStart, int errorSubstringEnd)
    {
        parseErrors.add(new PSDParseError(errorMessage, errorSubstringStart, errorSubstringEnd));
    }

    public void addErrorInfo(PSDParseError errorInfo)
    {
        parseErrors.add(errorInfo);
    }

    public boolean isInputValid()
    {
        return parseErrors.isEmpty();
    }

    public List<PSDParseError> getParseErrors()
    {
        return parseErrors;
    }

    public String getInput()
    {
        return input;
    }

}
