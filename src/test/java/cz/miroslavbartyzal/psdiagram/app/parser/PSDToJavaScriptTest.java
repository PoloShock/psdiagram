/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.parser.antlr.ANTLRParser;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDToJavaScriptTest
{
    
    private final ANTLRParser parser = new ANTLRParser();
    
    @Test
    public void visitorTest1()
    {
        String input = "19 // 1 // ((21 // 2) // 5)";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.floor(Math.floor(19 / 1) / (Math.floor((Math.floor(21 / 2)) / 5)))", result);
    }
    
    @Test
    public void visitorTest2()
    {
        String input = "5 * 5 // 2 * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.floor(5 * 5 / 2) * 2", result);
    }
    
    @Test
    public void visitorTest3()
    {
        String input = "2 + -5 * 5 // 2 * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + Math.floor(-5 * 5 / 2) * 2", result);
    }
    
    @Test
    public void visitorTest4()
    {
        String input = "2 + -5 * func(5 // 2) * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + -5 * func(Math.floor(5 / 2)) * 2", result);
    }
    
    @Test
    public void visitorTest5()
    {
        String input = "2 + -5 * func(5 // 2) // 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + Math.floor(-5 * func(Math.floor(5 / 2)) / 2)", result);
    }
    
    @Test
    public void visitorTest6()
    {
        String input = "Math.random() + 1 = 1";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.random() + 1 == 1", result);
    }
    
    @Test
    public void visitorSpacesTest()
    {
        String input = " 2 + -5 * 5 //  2   *     2\t";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals(" 2 + Math.floor(-5 * 5 /  2)   *     2\t", result);
    }
    
    @Test
    public void visitorSyntaxErrorTest() {
        String input = "š3čřž č65 0š";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals(input, result);
    }
    
}
