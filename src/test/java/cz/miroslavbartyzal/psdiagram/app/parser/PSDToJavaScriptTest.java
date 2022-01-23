/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.parser.psd.AntlrPsdParser;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDToJavaScriptTest
{
    
    private final AntlrPsdParser parser = new AntlrPsdParser();
    
    @Test
    public void visitorTest1()
    {
        String input = "19 // 1 // ((21 // 2) // 5)";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.trunc(Math.trunc(19 / 1) / (Math.trunc((Math.trunc(21 / 2)) / 5)))", result);
    }
    
    @Test
    public void visitorTest1b()
    {
        String input = "a // b // ((c // d) // e)";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.trunc(Math.trunc(a / b) / (Math.trunc((Math.trunc(c / d)) / e)))", result);
    }
    
    @Test
    public void visitorTest2()
    {
        String input = "5 * 5 // 2 * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("Math.trunc(5 * 5 / 2) * 2", result);
    }
    
    @Test
    public void visitorTest3()
    {
        String input = "2 + -5 * 5 // 2 * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + Math.trunc(-5 * 5 / 2) * 2", result);
    }
    
    @Test
    public void visitorTest3b()
    {
        String input = "2 + -b * c // d * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + Math.trunc(-b * c / d) * 2", result);
    }
    
    @Test
    public void visitorTest4()
    {
        String input = "2 + -5 * func(5 // 2) * 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + -5 * func(Math.trunc(5 / 2)) * 2", result);
    }
    
    @Test
    public void visitorTest5()
    {
        String input = "2 + -5 * func(5 // 2) // 2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals("2 + Math.trunc(-5 * func(Math.trunc(5 / 2)) / 2)", result);
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
        Assert.assertEquals(" 2 + Math.trunc(-5 * 5 /  2)   *     2\t", result);
    }
    
    @Test
    public void visitorSyntaxErrorTest() {
        String input = "š3čřž č65 0š";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals(input, result);
    }
    
    @Test
    public void visitorSyntaxErrorTest2() {
        String input = "1,2";
        String result = parser.translatePSDToJavaScript(input);
        Assert.assertEquals(input, result);
    }
    
}
