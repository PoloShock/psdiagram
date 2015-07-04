/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ValueFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ParsingSpeedTest
{

    @Test
    public void bracketsTest()
    {
        long time = System.currentTimeMillis();
        Assert.assertTrue(ValueFilter.isValid("((!(true)))"));
        time = System.currentTimeMillis() - time;
        Assert.assertTrue("Parser is too slow (" + time + " milliseconds)", time < 1000);
    }

    @Test
    public void bracketsTest2()
    {
        long time = System.currentTimeMillis();
        Assert.assertTrue(ValueFilter.isValid("([true,false])[1]"));
        time = System.currentTimeMillis() - time;
        Assert.assertTrue("Parser is too slow (" + time + " milliseconds)", time < 1000);
    }

}
