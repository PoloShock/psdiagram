/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NoArrayVariableFilter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class NoArrayVariableFilterTest
{

//    public static List<String> validExamplesEditConv = Arrays.asList(
//            // for editing convenience purposes:
//            ""
//    );
    public static List<String> validExamples = new ArrayList<String>()
    {
        {
//            addAll(validExamplesEditConv);

            add("a");
            add("A");
            add("pom");
            add("POM");
            add("_");
            add("_xz1");
            add("$");
            add("$a1_");
            add("a51");
            add("$_$df");
            add("dlouhynAzev2345678901234567890"); // 30 chars
            add("dlouhy_nA$ev12_htzu13GFDGsdf0$");
        }
    };
    public List<String> invalidExamples = new ArrayList<String>()
    {
        {
            addAll(ConstantFilterTest.validExamples);
            remove("");

            // from VariableFilterTest's valid examples
            add("pom[1]");
            add("pom[a]");
            add("pom[a[1]]");
            add("pom[pom.length]");
            add("pom[pom.length - 1]");
            add("pom[pom.length-1]");
            add("pom[func(a > 1.2)]");
            add("pom[func(a) - 1.1]");
            add("pom[0][1]");
            add("pom[a[1]][]");
            add("pom[]");
            add("pom[");
            add("pom[a[");

            add("pom]");
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
            add("áý");
//            add("dlouhynazev23456789012345678901"); <- 31 chars let's allow unlimited length variable names
            add("po,m");
            add("po.m");
            // key words:
            add("true");
            add("false");
            add("arguments");
            add("this");
            add("This");
            add("thIs");
            add("THIS");
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
            add("int");
        }
    };

    @Test
    public void validInputsTest()
    {
        for (String validExample : validExamples) {
            Assert.assertTrue("This input should be valid: " + validExample,
                    NoArrayVariableFilter.isValid(validExample));
//            if (validExamplesEditConv.contains(validExample)) {
//                Assert.assertFalse(
//                        "This input should not be valid for leaving as is: " + validExample,
//                        NoArrayVariableFilter.isValid(validExample).canBeLeftAsIs);
//            } else {
//                Assert.assertTrue("This input should be valid for leaving as is: " + validExample,
//                        NoArrayVariableFilter.isValid(validExample).canBeLeftAsIs);
//            }
//
//            for (int i = 1; i < validExample.length(); i++) {
//                String partOfValidExample = validExample.substring(0, i);
//                Assert.assertTrue("This input should be valid: " + partOfValidExample,
//                        NoArrayVariableFilter.isValid(partOfValidExample).isValid);
//            }
        }
    }

    @Test
    public void invalidInputsTest()
    {
        for (String invalidExample : invalidExamples) {
            Assert.assertFalse("This input should be invalid: " + invalidExample,
                    NoArrayVariableFilter.isValid(invalidExample));
        }
    }

}
