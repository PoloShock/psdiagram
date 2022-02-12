/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

import java.text.Normalizer;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public interface SourceCodeGenerator
{
    
    default String normalizeAsVariable(String name)
    {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll(" ", "_");
    }
    
}