/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ConstantFilter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ConstantFilterTest
{

//    public static List<String> validExamplesEditConv = Arrays.asList(
//            // for editing convenience purposes:
//            "\"rvglkíčřá51../.59", // = "rvglkíčřá51../.59
//            "\"-*/čš\",\"00,,,", // = "-*/čš","00,,,
//            "\"ab\",",
//            ",\"ab\"", // if one is inserting another constant
//            "\"f\",,\"12,,,\"" // if one is inserting another constant
//    );
    public static List<String> validExamples = new ArrayList<String>()
    {
        {
            addAll(ConstantNumberFilterTest.validExamples);
//            addAll(validExamplesEditConv);

            add("\"rvglkíčřá51../.59\""); // = "rvglkíčřá51../.59"
            add("\"-*/čš\",\"00\""); // = "-*/čš","00"
            add("\"\""); // ""
            add("98,\"1\""); // = 98,"1" -> I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
            add("\"a\",98"); // = "a",98 -> I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
            add("true,false,9"); // -> I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
            add("false");
            add("\"r'59\""); // = "r'59"
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
//            add("\"r'59\""); is valid now // = "r'59"
//            add("1, 1"); <- this should be actually valid for syntax checker.. So let's get rid of multiple spaces somewhere else
            add("1,1 1");
//            add("98,\"1\""); = 98,"1" -> I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
//            add("\"a\",98"); = "a",98 -> I'm allowing mixed strings and numbers after all since JavaScipt is tolerant too
//            add("98, 5"); <- this should be actually valid for syntax checker.. So let's get rid of spaces somewhere else
            add("\"abc\",\"a\"b\""); // = "abc","a"b"
            add("\"a\"c\",\"ab\""); // = "a"c","ab"
            add("\"-*/čš\" \"00\""); // = "-*/čš" "00"
        }
    };

    @Test
    public void validInputsTest()
    {
        for (String validExample : validExamples) {
            Assert.assertTrue("This input should be valid: " + validExample,
                    ConstantFilter.isValid(validExample));
//            if (validExamplesEditConv.contains(validExample) || ConstantNumberFilterTest.validExamplesEditConv.contains(
//                    validExample)) {
//                Assert.assertFalse(
//                        "This input should not be valid for leaving as is: " + validExample,
//                        ConstantFilter.isValid(validExample).canBeLeftAsIs);
//            } else {
//                Assert.assertTrue("This input should be valid for leaving as is: " + validExample,
//                        ConstantFilter.isValid(validExample).canBeLeftAsIs);
//            }
//
//            for (int i = 1; i < validExample.length(); i++) {
//                String partOfValidExample = validExample.substring(0, i);
//                Assert.assertTrue("This input should be valid: " + partOfValidExample,
//                        ConstantFilter.isValid(partOfValidExample).isValid);
//            }
        }
    }

    @Test
    public void invalidInputsTest()
    {
        for (String invalidExample : invalidExamples) {
            Assert.assertFalse("This input should be invalid: " + invalidExample,
                    ConstantFilter.isValid(invalidExample));
        }
    }

}
