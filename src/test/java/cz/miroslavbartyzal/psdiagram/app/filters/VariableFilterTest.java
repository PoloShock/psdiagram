/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class VariableFilterTest
{

//    public static List<String> validExamplesEditConv = Arrays.asList(
//            // for editing convenience purposes:
//            "pom[a[1]][]",
//            "pom[]",
//            "pom[",
//            "pom[0",
//            "pom[func(a > 1.)]",
//            "pom[func(a > )]",
//            "pom[func(\"ah)]",
//            "pom[func(]"
//    );
    public static List<String> validExamples = new ArrayList<String>()
    {
        {
            addAll(NoArrayVariableFilterTest.validExamples);
//            addAll(validExamplesEditConv);

            add("pom[1]");
            add("pom[a]");
            add("pom[a[1]]");
            add("pom[pom.length]");
            add("pom[pom.length - 1]");
            add("pom[pom.length-1]");
            add("pom[func(a > 1.2)]");
            add("pom[func(a) - 1.1]");
            add("pom[func(\"ah]oj\")]");
//            add("pom[[1][0]]"); <- lets not allow these kind of stuff
            add("pom[0][1]");
        }
    };
    public List<String> invalidExamples = new ArrayList<String>()
    {
        {
            addAll(ConstantFilterTest.validExamples);
            remove("");

            add("[");
            add("]");
            add("[]");
            add("pom]");
            add("]pom[");
            add("pom][[]");
            add("pom][1]");
            add("pom[1]]");
            add("pom[[1]]");
            add("pom[[]]");
            add("pom[a]-1");
            add("pom[a] / 1");
            add("pom[a] // 1");
            add("pom[a]>1");
            add("pom[a] != 1");
            add("pom[a] = 1");
            add("func(pom[a])");
            add("func(pom)");
            add("func()");
            add("pom[a]a");
            add("pom[0,1]");
            add("pom[0.1]");
            add("pom[0 1]");
            add("1pom");
            add("proměnná");
//            add("dlouhynazev23456789012345678901"); <- 31 chars let's allow unlimited length variable names
            add("po,m");
            add("po.m");
            add("true[a]");
            add("this[a]");
            add("1[a]");
            // key words:
            add("true");
            add("false");
            add("arguments");
            add("this");
            add("break");
            add("case");
            add("catch");
            add("continue");
            add("debugger");
            add("default");
            add("delete");
            add("do");
            add("else");
            add("finally");
            add("for");
            add("function");
            add("if");
            add("in");
            add("instanceof");
            add("new");
            add("return");
            add("switch");
            add("this");
            add("throw");
            add("try");
            add("typeof");
            add("var");
            add("void");
            add("while");
            add("with");
        }
    };

    @Test
    public void validInputsTest()
    {
        for (String validExample : validExamples) {
            Assert.assertTrue("This input should be valid: " + validExample,
                    VariableFilter.isValid(validExample));
//            if (validExamplesEditConv.contains(validExample) || NoArrayVariableFilterTest.validExamplesEditConv.contains(
//                    validExample)) {
//                Assert.assertFalse(
//                        "This input should not be valid for leaving as is: " + validExample,
//                        VariableFilter.isValid(validExample).canBeLeftAsIs);
//            } else {
//                Assert.assertTrue("This input should be valid for leaving as is: " + validExample,
//                        VariableFilter.isValid(validExample).canBeLeftAsIs);
//            }
//
//            for (int i = 1; i < validExample.length(); i++) {
//                String partOfValidExample = validExample.substring(0, i);
//                Assert.assertTrue("This input should be valid: " + partOfValidExample,
//                        VariableFilter.isValid(partOfValidExample).isValid);
//            }
        }
    }

    @Test
    public void invalidInputsTest()
    {
        for (String invalidExample : invalidExamples) {
            Assert.assertFalse("This input should be invalid: " + invalidExample,
                    VariableFilter.isValid(invalidExample));
        }
    }

}
