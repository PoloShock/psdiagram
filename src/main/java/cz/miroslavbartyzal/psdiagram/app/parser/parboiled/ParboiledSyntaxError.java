/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.parboiled;

import cz.miroslavbartyzal.psdiagram.app.parser.PSDParseError;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ParboiledSyntaxError extends PSDParseError implements ParseError
{

    private final InputBuffer inputBuffer;

    public ParboiledSyntaxError(String errorMessage, int startIndex, int endIndex,
            InputBuffer inputBuffer)
    {
        super(errorMessage, startIndex, endIndex);
        this.inputBuffer = inputBuffer;
    }

    @Override
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

}
