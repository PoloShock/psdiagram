/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NumericValueFilter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class NumericValueFilterTest
{

    public static List<String> validExamples = new ArrayList<String>()
    {
        {
            addAll(NoArrayVariableFilterTest.validExamples);

            // from ConstantFilterTest:
            add("0");
            add("985");
            add("+985");
            add("-985");
            add("1.589");
            add("21.58900");
            add("0.0014");
            add("0.0100");

            add("pole.length");
            add("func(a)");
            add("func(a,b)");
            add("Func.func.func(a)");
            add("Ahoj.cau.func(a > b)");
            add("Func.func.func(a>b)");
            add("a / -1");
            add("a // -1");
            add("a + -1");
            add("a - -1");
            add("a / +1");
            add("a + +1");
            add("a - +1");
            add("a -+-+1.02");
            add("a / -pom");
            add("a + -pom");
            add("a - -pom");
            add("a / +pom");
            add("a // +pom");
            add("a // +pom // Math.floor(a // 2 // 3)");
            add("19 // ((21 // 2) // 5)");
            add("a + +pom");
            add("a - +pom");
            add("a -+-+pom");
            add("Math.floor(Math.random()*11)");
            add("A % 2");
            add("A(true) % B[2]");
            add("\"ahoj\".length");
            add("-(-1)");
            add("1-(-1)");
            add("[1,2][1]");
            add("func()[1]");
            add("func([1,2])[1]");
            add("-pom");
            add("1 - -pom");
            add("1 - -+-+pom");
            add("pole. length");
            add("pole .length");
            add("pole . length");
            add("func (a)");
            add("Math.func (a)");
            add("func( 1)");
            add("func ( 1 ) ");
            add("[1,2][0] + [5,1][1]");
            add("1 + [0][0]");
            add("(1)");
            add("hege.concat(stale).length");
            add("hege.concat(stale).concat(stale)");
            add("[1,2].concat(stale).concat(stale)");
            add("[1,[2,3]][1].concat(stale).concat(stale)");
            add("1 + a * 1");
            add("[1, [a]][1].something[0].length");
            add("(\"ahoj\" + a).length");
            add("a[1][2].length");
            add("[a,b,c][1].length");
            add("\"ahoj\".par1[1].par2[0].length");
            add("prom + 1 - 1");
            add("1 + 1 + prom");
            add("1 + 1 - prom");
            add("po[prom + 1 - 1]");
            add("1 - 2 + pom + 1");
            add("1 - (pom + 1)");
            add("1 + (pom - 1)");
            add("(pom+1)");
            add("1 - -(pom+1)");
            add("1 - -(pom-1)");
            add("1 * (pom+1)");
            add("1 * (pom+1) * (pom+1)");
            add("1 + p + (pom+1)");
            add("-(p+1)");
            add("1 * -+-(pom+1)");
            add("1 + 1 - p * 2");
            add("a - (a + 1) * 2");
            add("(a + 1) + 2");
            add("(a + 1) + 2 - 1");
            add("1 + (a + 2)");
            add("1 + (2 + a)");
            add("1 + (a+1).gg()");
            add("((\"\").getBytes()).prop");
            add("([1].g()).prop");
            add("([1].g()).prop()");
            add("([1].g).prop");
            add("([1]).prop");
            add("([1]).f()");
            add("([true,false])[1]");
            add("1 + +-+1");
            add("1*1+a-1");
            add("a+1-1");
            add("1+a-1");
            add("1+a+a-1");
            add("1+a+a+1-1");
            add("1+a+1+a+1-1");
            add("1+a+1+a*1+1-1");
        }
    };
    public List<String> invalidExamples = new ArrayList<String>()
    {
        {
            add("\"rvglkíčřá51../.59\""); // = "rvglkíčřá51../.59"
            add("\"\""); // ""
            add("a = \"ahoj\"");
            add("!a");
            add("! a"); // I don't like it but Java permits it
            add("a = b");
            add("a < 0");
            add("a < b");
            add("a > b");
            add("a > (b)");
            add("(a) > b");
            add("(a) > (b)");
            add("a >= b");
            add("a <= b");
            add("a != b");
            add("a && b");
            add("a > 0 && b");
            add("a || func(b)");
            add("a > 0 || !func(b-1)");
            add("a > 0 || (!func(b*-1) && c)");
            add("Func.func.func(a>b) != !b");
            add("!(a > 1)");
            add("a = b+\"ahoj\"");
            add("true");
            add("false");
            add("\"ahoj\" + 1");
            add("1 + \"ahoj\""); // javascript is tolerant
            add("[1,b, c[1]]");
            add("[1, b, c[1], \"\"][2][1][0] > 5");
            add("!true"); // javascript is tolerant
            add("(true)");
            add("!(true)");
            add("(!false)");
            add("(!(true))");
            add("((!(false)))");
            add("\"ahoj\"[1]");
            add("\"ahoj\" [1]");
            add("1 = 2 = true = false");
            add("(true && true) = true");
            add("(-(1) = (1)) != (!(!true) && !false)");
            add("false || true = false"); // interesting example of precedence
            add("(-(1) + (1)) != (-(-1) * -2) && true");
            add("1 - 2 + \"\"");
            add("1 - 2 + \"\" + 1 / 2");
            add("1 - 2 + \"\" + 1 // 2");
            add("!true + \"_\"");
            add("1 - 2 + \"\" + 1 / 2 + -1");
            add("((\"ahoj\") + (\"b\")) + (\"c\") = \"\""); // (("ahoj") + ("b")) + ("c") = ""
            add("a + \"\" + 1");
            add("true && ((b))");
            add("a + a = \"\"");
            add("\"\" != a+a+a");
            add("a != a+a+a");
            add("a+a > 1");
            add("a+a >= a");
            add("\"r'59\""); // = "r'59"
            add("(\"ahoj\")[1]");
            add("\"Acquired result after: \" + (start - System.currentTimeMillis()) + \" milliseconds.\"");
            add("true + (pom + 1) + \"\"");
            add("true + (pom + 1) + 1");
            add("true + (pom + 1) + (pom + 1)");
            add("true + 1 + p");
            add("p true");
            add("(true && true) + \"\"");
            add("p + true + 1");
            add("1 + - > 2");
            add("1 + -+- > 2");
            add("1 + -1 > 2");
            add("1 + -+-1 > 2");
            add("1*1+a-");
            add("a+1-");
            add("1+a-");
            add("1+a-true");
            add("1+a-\"\"");
            add("1+a-[1,2]");
            add("1+\"\"-");
            add("1+\"\"-1");
            add("1+a+\"\"-");
            add("1+a+\"\"-1");
            add("1+a+true-");
            add("1+a+true-1");
            add("a+a+[1]-");
            add("a+a+[1]-1");
            add("1+a+[1]-1");
            add("1+a+[1]-");
            add("\"\" + null");
            add("a + null");
            add("a + a + 1 + null");
            add("\"\" = null");
            add("\"\" != null");
            add("[1] = null");
            add("null + \"\"");
            add("null + a");
            add("null = \"\"");
            add("null != \"\"");
            add("null != [1,2]");
            add("null = null");
            add("null = 1");
            add("null = a - 1");
            add("null != true");
            add("1 != null");
            add("a - 1 != null");
            add("true = null");
            add("1 && null");
            add("null || null");
            add("null || true");
            add("null > null");
            add("null >= 1");

            add("--");
            add("++");
            add("--pom");
            add("-- pom");
            add("1--");
            add("1--1");
            add("1--pom");
            add("1 --");
            add("1 -- 1");
            add("1 -- pom");
            add("a -+-+01");
            add("a -+-+01.22");
            add("= b");
            add(".length");
            add("11.ahoj");
            add("ahoj.11");
            add("\"ahoj\".11");
            add("1/*2");
            add("a == b"); // we are comparing using single equal sign
            add("a > = b");
            add("a < = b");
            add("a ! = b");
            add("a && b");
            add("a > 0 | b");
            add("1 ++ 2");
//            add("1  + 2"); // allow only single spaces <- this should be actually valid for syntax checker.. So let's get rid of multiple spaces somewhere else
//            add("a  "); <- this should be actually valid for syntax checker.. So let's get rid of multiple spaces somewhere else
            add("a,b");
            add(",1");
            add("(a,b)");
            add("6+*5");
            add("1(b)");
            add("(b)1");
            add("(1)b");
            add("\"\"()");
            add("(1)[0]");
            add("this(a)");
            add("a.this(a)");
            add("a.this");
            add("func(1)(2)");
            add("func(1)(");
            add("func(1)()");
            add("dfspog*-- -rt +9 + 1");
            add("!1"); // javascript is tolerant to alow it but I don't want it
            add("!(1)");
            add("(!1)");
            add("(!(1))");
            add("((!(1)))");
            add("!(((1)))");
            add("true > false");
            add("1 + 2 > 3 > 5");
            add("1 = 2 = 3");
            add("true = 2 = 3");
            add("true + 1 + \"\"");
            add("1 - 2 + \"\" + 1 - 2"); // ""1 is already a string and we can't subtract 2 from it
            add("true + \"\" + true | true");
            add("true | true + \"\"");
            add("[1,2].concat(stale).\"\".concat(stale)");
            add("Class.func(a > b)"); // class is a keyword
            add("class.func(a > b)"); // class is a keyword
            add("12. 0");
            add("12 .0");
            add("\"Acquired result after: \" + start - System.currentTimeMillis() + \" milliseconds.\"");
            add("áý + 1");

            // from constants
            add(",");
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
//            add("-5-"); commenting out after all in the name of editing convenience
//            add("-55.-"); commenting out after all in the name of editing convenience
            add("+.");
            add("++1");
            add("-++1");
            add("++-1");
            add("-+--1");
            add("--+-1");
            add("--++");
            add("---");
//            add("\"r'59\""); is valid now // = "r'59"
            add("1, 1");
            add("1,1 1");
            add("98,\"1\""); // = 98,"1"
            add("\"a\",98"); // = "a",98
            add("98, 5");
            add("\"-*/čš\", \"00\""); // = "-*/čš" "00"
            add("\"abc\",\"a\"b\""); // = "abc","a"b"
            add("\"a\"c\",\"ab\""); // = "a"c","ab"

            add("98.,51");
            add("0.,");
            add("-,51");
            add(",");
            add(",12");
            add("98,,45");
            add("\"-*/čš\",\"00,,,"); // = "-*/čš","00,,,
            add(",\"ab\"");
            add("\"f\",,\"12,,,\"");
            add("98,51,91.01,0.10,0");
            add("\"-*/čš\",\"00\""); // = "-*/čš","00"
            add("98,\"1\""); // = 98,"1"
            add("\"a\",98"); // = "a",98
            add("true,false,9");

            // from variables
            add("pom]");
            add("]pom[");
            add("pom][[]");
            add("pom][1]");
            add("pom[1]]");
            add("pom[[1]]");
            add("pom[a]a");
            add("pom[0,1]");
            add("pom[0.1]");
            add("pom[0 1]");
            add("1pom");
            add("proměnná");
//            add("dlouhynazev23456789012345678901"); <- 31 chars let's allow unlimited length variable names
            add("po,m");
            add("true[a]");
            add("true[");
            add("this[a]");
            add("this[");
            add("1[a]");
            // key words:
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
                    NumericValueFilter.isValid(validExample));
        }
    }

    @Test
    public void invalidInputsTest()
    {
        for (String invalidExample : invalidExamples) {
            Assert.assertFalse("This input should be invalid: " + invalidExample,
                    NumericValueFilter.isValid(invalidExample));
        }
    }

}
