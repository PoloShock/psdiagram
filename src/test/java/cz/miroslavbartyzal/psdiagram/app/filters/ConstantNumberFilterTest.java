/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ConstantNumberFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ConstantNumberFilterTest
{

//    public static List<String> validExamplesEditConv = Arrays.asList(
//            // for editing convenience purposes:
//            "",
//            "-",
//            "+",
//            "-+",
//            "+-",
//            "-+-",
//            "+-+-",
//            "+-+-1.",
//            "98.",
//            "98,",
//            "98.0,",
//            // if one is inserting another constant
//            "98.,51",
//            "0.,",
//            "-,51",
//            ",",
//            ",12",
//            "98,,45"
//    );
    public static List<String> validExamples = new ArrayList<String>()
    {
        {
//            addAll(validExamplesEditConv);

            add("0");
            add("985");
            add("+985");
            add("-985");
            add("-+985");
            add("-+-985");
            add("-+-+985");
            add("+-+- + - + -0");
            add("+-+ + +1");
            add("1.589");
            add("21.58900");
            add("0.0014");
            add("0.0100");
            add("98,51,91.01,0.10,0");
            add("98,+-+-51,+91.01,-+-0.10,-0");

//            add("-(-1)"); // although this is possible in java, I am not going to support brackets here
//            add("-(-1),-(-+1)"); // although this is possible in java, I am not going to support brackets here
//            add("+((-1)+-(-2%+2))-1"); // although this is possible in java, I am not going to support mathematic operations here
        }
    };
    public List<String> invalidExamples = new ArrayList<String>()
    {
        {
            addAll(VariableFilterTest.validExamples);
            remove("");

            add(",,");
            add("a,,");
            add(",,b");
            add("a,,,b");
            add("00");
            add("002");
            add("1 2");
            add("21.589.00");
            add("00.12");
            add("--5");
            add("-5-");
            add("-55.-");
            add("+.");
            add("++1");
            add("-++1");
            add("++-1");
            add("-+--1");
            add("--+-1");
            add("--++");
            add("---");
            add("\"r'59\""); // = "r'59"
            add("1,1 1");
            add("98,\"1\""); // = 98,"1"
            add("\"a\",98"); // = "a",98
//            add("98, 5"); <- this should be actually valid for syntax checker.. So let's get rid of spaces somewhere else
            add("\"\""); // ""
            add("\"rvglkíčřá51../.59\""); // = "rvglkíčřá51../.59"
            add("\"-*/čš\",\"00\""); // = "-*/čš","00"
            add("\"abc\",\"a\"b\""); // = "abc","a"b"
            add("\"a\"c\",\"ab\""); // = "a"c","ab",
            add("\"");
            add("\"\",");
        }
    };

    @Test
    public void validInputsTest()
    {
        for (String validExample : validExamples) {
            Assert.assertTrue("This input should be valid: " + validExample,
                    ConstantNumberFilter.isValid(validExample));
//            if (validExamplesEditConv.contains(validExample)) {
//                Assert.assertFalse(
//                        "This input should not be valid for leaving as is: " + validExample,
//                        ConstantNumberFilter.isValid(validExample).canBeLeftAsIs);
//            } else {
//                Assert.assertTrue("This input should be valid for leaving as is: " + validExample,
//                        ConstantNumberFilter.isValid(validExample).canBeLeftAsIs);
//            }
//
//            for (int i = 1; i < validExample.length(); i++) {
//                String partOfValidExample = validExample.substring(0, i);
//                Assert.assertTrue("This input should be valid: " + partOfValidExample,
//                        ConstantNumberFilter.isValid(partOfValidExample).isValid);
//            }
        }
    }

    @Test
    public void invalidInputsTest()
    {
        for (String invalidExample : invalidExamples) {
            Assert.assertFalse("This input should be invalid: " + invalidExample,
                    ConstantNumberFilter.isValid(invalidExample));
        }
    }

}
