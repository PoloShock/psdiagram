/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import javax.swing.JTextField;

/**
 * <p>
 * Tato třída představuje filtr obecné hodnoty.</p>
 *
 * <p>
 * Může se jednat o hodnotu dosazovanou do proměnné, či o hodnotu použitou
 * jako vyhodnocovací podmíněný výraz. Tento filtr je v porovnání s ostatními
 * nejkomplexnější.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ValueFilter extends AbstractFilter
{

    private static final EnumRule RULE = EnumRule.EXPRESSION;

//    // TODO scroller: http://stackoverflow.com/questions/17218661/java-string-replaceold-new-count-how-many-replaced
//    private static final String NUMBER_WITHOUT_SIGN_REGEX = "(([1-9]|0(?=\\.|$))[0-9]{0,100}(\\.[0-9]{1,100})?)"; // needs to be limited in lookbehind
////    private static final String NUMBER_REGEX = "(((\\+(\\-\\+){0,20}\\-?)|(\\-(\\+\\-){0,20}\\+?))?([1-9]|0(?=\\.|$))[0-9]{0,100}(\\.[0-9]{1,100})?)"; // needs to be limited in lookbehind
//    private static final String VARIABLE_REGEX = "([a-zA-Z\\_\\$][\\w\\$]{0,29})";
//    private static final Pattern SEPARATOR_PATTERN = Pattern.compile(""
//            //            + "(?<!^" + NUMBER_REGEX + "\\s?)\\s?(?=\\()|"
//            //            + "(?<!^" + NUMBER_REGEX + "\\s?)\\s?(?=\\[)|"
//            + "\\s?(?=\\()|"
//            + "\\s?(?=\\[)|"
//            + "\\s?\\!\\=\\s?|"
//            + "\\s?\\!(?!\\=)\\s?|" // uncompleted != operator
//            + "\\s?\\>\\=\\s?|"
//            + "\\s?\\<\\=\\s?|"
//            + "\\s?[\\=\\>\\<\\*\\/\\%\\,\\&\\|]\\s?|" // '=' '>' '<' '*' '/' '%' ',' '&' '|'
//            + "(?<!^|[\\-\\+])\\s?[\\-\\+](?![\\-\\+]|\\d|[a-zA-Z\\_\\$]|$)\\s?|" // vyber (-|+) kdyz pred nim neni dalsi znamenko nebo zacatek retezce a za nim nenasleduje dalsi znamenko nebo cislo nebo promenna nebo konec retezce
//            //            + "(?<=^\\s?[\\-\\+]{1,21}" + NUMBER_WITHOUT_SIGN_REGEX + ")\\s?|" // kdyz po (serii) + nebo - nasleduje cislo, nevybirej (-|+) jako operator (priradi se k cislu)
//            + "(?<!^)\\s?(?<![\\-\\+])(?=[\\-\\+]{1,21}(" + NUMBER_WITHOUT_SIGN_REGEX + "|" + VARIABLE_REGEX + "|$))|" // kdyz po (serii) + nebo - nasleduje cislo nebo promenna, nevybirej (-|+) jako operator (priradi se k cislu)
//            + "(?<=^(" + VARIABLE_REGEX + "|\"\"))\\s?\\.\\s?(?=" + VARIABLE_REGEX + "|$)"); // dot is considered as separator only if it is a subfunction call
    public ValueFilter(JTextField parentJTextField, ValidationListener validationListener,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        super(parentJTextField, validationListener, maxBalloonSizeCallback);

        if (!parentJTextField.getText().isEmpty()) {
            super.parseInputAndUpdateGUI(parentJTextField.getText());
        }
    }

    @Override
    EnumRule getRule()
    {
        return RULE;
    }

    /**
     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
     *
     * @param input text, který chceme prověřit
     * @return
     */
    public static boolean isValid(String input)
    {
        return AbstractFilter.parseInput(input, RULE);
    }

//    /**
//     * Pokusí se daný textový řeťezec porovnat vůči tomuto filtru.
//     *
//     * @param input text, který chceme prověřit
//     * @param isCommaAsSeparatorValid (, jako separator validni jen kdyz jsem v hranate zavorce nebo byl-li predtim validni promenna)
//     * @return
//     */
//    static ValidityCheckResult isValid(String input, boolean isCommaAsSeparatorValid,
//            boolean isEmptyValid)
//    {
//        if (input.length() == 0) {
//            return ValidityCheckResult.createValidRes(isEmptyValid);
//        }
//        if (input.contains("'") || input.matches("^\\s.*") || input.matches(".*\\s{2,}.*")) { // ' char is not allowed as well as a space char at the beginning of the text and double spaces within the text
//            return ValidityCheckResult.createInvalidRes(null); // TODO
//        }
//
//        String textToProcess = input.replaceAll("\"([^\"\\\\]|\\\\.)*\"?", "\"\"");
//        // let's get rid of any unnecessary spaces
////        textToProcess = textToProcess.replaceAll("\\s\\)", ")");
////        textToProcess = textToProcess.replaceAll("\\s\\]", "]");
////        textToProcess = textToProcess.replaceFirst("(?<![\\[\\(])\\s$", ""); // single space at the end is allowed
//        textToProcess = textToProcess.replaceFirst("(?<!\\d\\.)\\s$", ""); // single space at the end is allowed
//
//        boolean prevWasVar = false;
//        boolean prevWasNonStringConstant = false;
//        boolean expectedAnotherOperand = false;
//        boolean canBeLeftAsIs = (input.length() - input.replaceAll("\"", "").length()) % 2 == 0
//                && textToProcess.replaceAll("\\[", "").length() == textToProcess.replaceAll("\\]",
//                        "").length()
//                && textToProcess.replaceAll("\\(", "").length() == textToProcess.replaceAll("\\)",
//                        "").length(); // odd count of quotes is not allowed, brackets count has to be equal
//        while (!textToProcess.isEmpty()) {
//            ValidityCheckResult externalFilterRes = null;
//
//            if (textToProcess.matches("^(\\[|[\\+\\-]*\\().*")) {
//                // brackets (), []
//                textToProcess = textToProcess.replaceFirst(
//                        "^((\\+(\\-\\+){0,20}\\-?)|(\\-(\\+\\-)*\\+?))", "");
//                if (textToProcess.matches("^[\\+\\-].*")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO double signes
//                }
//
////                if (expectedAnotherOperand && !textToProcess.startsWith("(")) {
////                    // there should be an operand
////                    canBeLeftAsIs = false;
////                }
//                String[] split;
//                boolean shouldCommaBeValid;
//                boolean isSquareBracket = false;
//                if (textToProcess.startsWith("(")) {
//                    split = RegexFunctions.varBracketsInsides(textToProcess, "(", ")");
//                    shouldCommaBeValid = prevWasVar;
//                    if (split.length >= 2) {
//                        externalFilterRes = isValid(split[1].trim(), shouldCommaBeValid,
//                                shouldCommaBeValid);
//                        if (!externalFilterRes.isValid) {
//                            return externalFilterRes;
//                        }
//                    }
//                } else {
//                    isSquareBracket = true;
//                    split = RegexFunctions.varBracketsInsides(textToProcess);
//                    shouldCommaBeValid = !prevWasVar;
//                    if (prevWasNonStringConstant) {
//                        return ValidityCheckResult.createInvalidRes("Nevalidní název proměnné pole.");
//                    }
//                    if (split.length >= 2) {
//                        if (prevWasVar) {
//                            // array variable indexing (example: pom[1])
//                            externalFilterRes = VariableFilter.isValid("a[" + split[1].trim() + "]");
//                            if (!externalFilterRes.isValid) {
//                                return externalFilterRes;
//                            }
//                        } else {
//                            // just array initialization (example: [1,3]; "ahoj"[2])
//                            externalFilterRes = isValid(split[1].trim(), shouldCommaBeValid, false);
//                            if (!externalFilterRes.isValid) {
//                                return externalFilterRes;
//                            }
//                        }
//                    }
//                }
//
//                textToProcess = "";
//                if (split.length >= 3 && split[2].length() > 0) {
//                    textToProcess += split[2].substring(1);
//                }
//                for (int i = 3; i < split.length; i++) {
//                    textToProcess += split[i];
//                }
//                if (!textToProcess.isEmpty()) {
//                    if (!isSquareBracket && textToProcess.matches("^\\s?\\(.*")) {
//                        // we are missing operator; example: func(1)(2)
//                        return ValidityCheckResult.createInvalidRes(null); // TODO missing operator
//                    } else if (!prevWasVar && !isSquareBracket && textToProcess.matches("^\\s?\\[.*")) {
//                        // can't be refferencing index on brackets without function output; example: (1)[0]
//                        return ValidityCheckResult.createInvalidRes(null); // TODO double signes
//                    }
//
//                    if (textToProcess.matches("^\\s.*") || (textToProcess.matches("^\\[.*") && (prevWasVar || isSquareBracket))) {
//                        textToProcess = "a" + textToProcess; // replace brackets with variable so it does not ruin itself; example: (1) *2, pom[1][2]
//                    } else {
//                        textToProcess = "a " + textToProcess; // same as above, but include space; example: func(1)a
//                    }
//                }
//                prevWasVar = false;
//                prevWasNonStringConstant = false;
//                expectedAnotherOperand = false;
//            } else if (textToProcess.matches(
//                    "^([\\d\"]|[\\+\\-]+(?![\\+\\-a-zA-Z\\_\\$])|(true|false)(?![\\w\\$])).*")) {
//                // constant
//                prevWasVar = false;
//                prevWasNonStringConstant = !textToProcess.startsWith("\"");
//
//                String[] split = getNextSeparationIteration(textToProcess);
//                if (split == null) {
//                    split = new String[]{textToProcess, "", ""};
//                }
//                if (split[0].matches(".*\\s.*")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO missing operator
//                }
//
//                if (!isCommaAsSeparatorValid && split[1].matches("^\\s?,\\s?$")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO
//                }
//                externalFilterRes = ConstantFilter.isValid(split[0]);
//                if (!externalFilterRes.isValid) {
//                    return externalFilterRes;
//                }
//                textToProcess = split[2];
//
//                prevWasNonStringConstant = prevWasNonStringConstant && split[1].matches("^\\s*$") && !textToProcess.matches(
//                        "^[\\+\\-].*"); // if there is an operator, this variable doesn't matter to the next operand so it should be false now
//
//                expectedAnotherOperand = !split[1].matches("^\\s*$"); // there is bracket or end of input -> no operand will be expected
//                if (!expectedAnotherOperand && textToProcess.matches("^[\\(\\[].*") && (!split[0].startsWith(
//                        "\"") || textToProcess.startsWith("("))) {
//                    // there should have been an operator; example: 1(), 1[], true(), true[], ""()
//                    return ValidityCheckResult.createInvalidRes(null); // TODO missing operator
//                }
//                if (canBeLeftAsIs && split[1].matches("^\\s?\\!\\s?$")) {
//                    canBeLeftAsIs = false; // uncompleted != operator
//                }
//            } else if (textToProcess.matches("^[\\+\\-]*[a-zA-Z\\_\\$].*")) {
//                // variable
//                prevWasNonStringConstant = false;
//                textToProcess = textToProcess.replaceFirst(
//                        "^((\\+(\\-\\+){0,20}\\-?)|(\\-(\\+\\-)*\\+?))", "");
//                if (textToProcess.matches("^[\\+\\-].*")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO double signes
//                }
//
//                String[] split = getNextSeparationIteration(textToProcess);
//                if (split == null) {
//                    split = new String[]{textToProcess, "", ""};
//                }
//                if (split[0].matches(".*\\s.*")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO missing operator
//                }
//
//                if (!isCommaAsSeparatorValid && split[1].matches("^\\s?,\\s?$")) {
//                    return ValidityCheckResult.createInvalidRes(null); // TODO
//                }
//                externalFilterRes = NoArrayVariableFilter.isValid(split[0]);
//                if (!externalFilterRes.isValid) {
//                    return externalFilterRes;
//                }
//                textToProcess = split[2];
//
//                prevWasVar = split[1].matches("^\\s*$") && !textToProcess.matches("^[\\+\\-].*"); // if there is an operator, this variable doesn't matter to the next operand so it should be false now
//
//                expectedAnotherOperand = !split[1].matches("^\\s*$"); // there is bracket or end of input -> no operand will be expected
//                if (canBeLeftAsIs && split[1].matches("^\\s?\\!\\s?$")) {
//                    canBeLeftAsIs = false; // uncompleted != operator
//                }
//            } else if (textToProcess.matches("^\\!\\s?([\\(\\w\\$].*|$)")) {
//                // negation
//                prevWasVar = false;
//                prevWasNonStringConstant = false;
//                textToProcess = textToProcess.substring(1).replaceFirst("^\\s", "");
//                if (canBeLeftAsIs && textToProcess.isEmpty()) {
//                    canBeLeftAsIs = false;
//                }
//            } else if (isCommaAsSeparatorValid && textToProcess.startsWith(",")) {
//                // comma after uncompleted input -> example: [,2]
//                canBeLeftAsIs = false;
//                prevWasVar = false;
//                prevWasNonStringConstant = false;
//                textToProcess = textToProcess.substring(1).replaceFirst("^\\s", "");
//            } else {
//                return ValidityCheckResult.createInvalidRes(null); // TODO
//            }
//
//            if (canBeLeftAsIs && externalFilterRes != null && !externalFilterRes.canBeLeftAsIs) {
//                canBeLeftAsIs = false;
//            }
//        }
//        if (expectedAnotherOperand) {
//            // there should have been an operand
//            canBeLeftAsIs = false;
//        }
//
//        return ValidityCheckResult.createValidRes(canBeLeftAsIs);
//    }
//
//    private static String[] getNextSeparationIteration(String textToProcess)
//    {
//        String[] output = null;
//
//        Matcher m = SEPARATOR_PATTERN.matcher(textToProcess);
//        if (m.find()) {
//            output = new String[3];
//            output[0] = textToProcess.substring(0, m.start());
//            output[1] = textToProcess.substring(m.start(), m.end());
//            output[2] = textToProcess.substring(m.end(), textToProcess.length());
//        }
//
//        return output;
//    }
}
