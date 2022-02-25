package cz.miroslavbartyzal.psdiagram.app.codeImportExport;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Decision;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.For;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Goto;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.GotoLabel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.IO;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopStart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.SubRoutine;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Switch;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.dialog.MyJOptionPane;
import cz.miroslavbartyzal.psdiagram.app.parser.ConversionResult;
import cz.miroslavbartyzal.psdiagram.app.parser.SourceCodeGenerator;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.AntlrPsdParser;
import cz.miroslavbartyzal.psdiagram.app.parser.psd.PSDGrammarParser;

import java.awt.HeadlessException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

public final class Java implements SourceCodeGenerator
{

    private static final String LINE_SEP = System.lineSeparator();
    private final AntlrPsdParser psdToJavaParser = new AntlrPsdParser();
    private boolean errored;
    private boolean gotoUsage;
    private boolean missingCommandWarning;
    private Boolean scannerNeeded = false;
    
    public static Flowchart<LayoutSegment, LayoutElement> getFlowchart(String code)
    {
        JavaToFlowchartConvertor javaToPsdConvertor = new JavaToFlowchartConvertor(5);
        ConversionResult conversionResult = javaToPsdConvertor.convertToPsd(code);
        if (conversionResult.isInputValid()) {
            return conversionResult.getFlowchart();
        }
    
        MyJOptionPane.showMessageDialog(null, conversionResult.getErrorMessage(), "Chyba při generování diagramu",
                JOptionPane.ERROR_MESSAGE);
        return null;
    }
    
    public static String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name,
            boolean blockScopeVariables)
    {
        Java instance = new Java();
        return instance.generateSourceCode(flowchart, name, blockScopeVariables);
    }
    
    private String generateSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name,
            boolean blockScopeVariables)
    {
        errored = false;
        missingCommandWarning = false;
        scannerNeeded = false;
        gotoUsage = false;
        // generovani hlavicky
        String sourceCode = "public class " + normalizeAsVariable(name) + LINE_SEP + "{" + LINE_SEP;
        sourceCode += "\t" + "public static void main(String[] args)" + LINE_SEP + "\t" + "{";
        
        Set<String> alreadyDeclaredVariables = new HashSet<>();
        if (!blockScopeVariables) {
            // vyhledani vsech pouzitych identifikatoru promennych
            Set<String> vars = new TreeSet<>();
            Set<String> arrayVars = new TreeSet<>();
            findAndSetVariables(flowchart, vars, arrayVars);
            sourceCode = addVariableDeclarations(sourceCode, vars, arrayVars, "\t\t", alreadyDeclaredVariables);
        }
        
        String mainSourceCode = generateSourceCode(flowchart.getMainSegment(), "\t\t", alreadyDeclaredVariables);
        
        if (scannerNeeded) {
            sourceCode = "import java.util.Scanner;" + LINE_SEP + LINE_SEP + sourceCode;
            sourceCode += LINE_SEP + "\t\t" + "Scanner scanner = new Scanner(System.in);" + LINE_SEP + "\t\t";
        }
        
        sourceCode += mainSourceCode;

        if (errored) {
            sourceCode = null;
        } else if (missingCommandWarning || gotoUsage) {
            String warnings = "<ul>";
            if (missingCommandWarning) {
                warnings += "<li>Některý symbol nemá vyplněnu svou funkci!</li>";
            }
            if (gotoUsage) {
                warnings += "<li>Goto příkaz není v Javě podporován!</li>";
            }
            warnings += "</ul>";
            
            JOptionPane.showMessageDialog(null,
                    "<html>Zdrojový kód byl vygenerován s následujícím upozorněním:" + warnings + "</html>",
                    "Nevyplněná funkce symbolu", JOptionPane.WARNING_MESSAGE);
        }
        
        sourceCode += LINE_SEP + "}";
        
        return sourceCode;
    }

    private String addVariableDeclarations(String sourceCode, Set<String> vars, Set<String> arrayVars, String tabsDepth,
            Set<String> declaredVariables)
    {
        if (!vars.isEmpty() || !arrayVars.isEmpty()) {
            sourceCode += LINE_SEP + tabsDepth + "// zde je nutne doplnit typy promennych (diagramy jsou netypove)";
            if (!vars.isEmpty()) {
                for (String variable : vars) {
                    sourceCode += LINE_SEP + tabsDepth;
                    sourceCode += "{typ_promenne} " + variable + ";";
                    addToDeclared(variable, declaredVariables);
                }
            }

            if (!arrayVars.isEmpty()) {
                for (String arrayVar : arrayVars) {
                    sourceCode += LINE_SEP + tabsDepth;
                    sourceCode += "{typ_promenne_pole}[] " + arrayVar + ";";
                    addToDeclared(arrayVar, declaredVariables);
                }
            }
            sourceCode += LINE_SEP + tabsDepth;
        }
        
        return sourceCode;
    }

    private void findAndSetVariables(Flowchart<LayoutSegment, LayoutElement> flowchart, Set<String> vars,
            Set<String> arrayVars)
    {
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (FlowchartElement<?, ?> element : segment) {
                    Symbol symbol = element.getSymbol();
                    if (symbol.getCommands() != null) {
                        if (symbol instanceof Process 
                                || (symbol instanceof IO && symbol.getCommands().containsKey("var"))) {
                            if (symbol.getCommands().get("var").contains("[") 
                                    || (symbol.getCommands().containsKey("value") && symbol.getCommands().get(
                                    "value").startsWith("["))) {
                                String variable = symbol.getCommands().get("var");
                                addToDeclared(variable, arrayVars);
                            } else {
                                String variable = symbol.getCommands().get("var");
                                addToDeclared(variable, vars);
                            }
                        } else if (symbol instanceof For) {
                            addToDeclared(symbol.getCommands().get("var"), vars);
                            if (symbol.getCommands().containsKey("array")) {
                                addToDeclared(symbol.getCommands().get("array"), arrayVars);
                            }
                        }
                    }
                }
            }
        }
    }

    private String generateSourceCode(LayoutSegment segment, String tabsDepth, Set<String> alreadyDeclaredVariables)
    {
        Set<String> declaredVariables = new HashSet<>(alreadyDeclaredVariables);
        
        String pairedCommentText = null;
        boolean lastWasPairedComment = false;
        String sourceCode = "";

        int index = -1;
        for (LayoutElement element : segment) {
            index++;
            Symbol symbol = element.getSymbol();
            try {
                boolean isElseIf = isElseIf(segment);
                if (!isElseIf) {
                    // odsadit pro nasledujici symbol
                    sourceCode += LINE_SEP + tabsDepth;
                }
//#####################################PROCESS############################################################################

                if (symbol instanceof Process) {
                    if (symbol.getCommands() != null) {
                        String variable = symbol.getCommands().get("var");
                        variable = psdToJavaParser.translatePSDToJava(variable, PSDGrammarParser::solo_Expression);
                        String processValue = symbol.getCommands().get("value");
                        processValue =
                                psdToJavaParser.translatePSDToJava(processValue, PSDGrammarParser::solo_Expression);
                        if (isNotDeclaredYet(variable, declaredVariables)) {
                            sourceCode += "var ";
                            addToDeclared(variable, declaredVariables);
                        }
                        sourceCode += variable + " = " + processValue + ";";
                    } else {
                        sourceCode += "{symbol Zpracování bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
//################################INPUT##OUTPUT#############################################################################
                } else if (symbol instanceof IO) {
                    if (symbol.getCommands() != null) {
//####################################INPUT#################################################################################
                        if (symbol.getCommands().containsKey("var")) {
                            scannerNeeded = true;
                            String variable = symbol.getCommands().get("var");
                            variable = psdToJavaParser.translatePSDToJava(variable, PSDGrammarParser::solo_Expression);
                            if (isNotDeclaredYet(variable, declaredVariables)) {
                                sourceCode += "var ";
                                addToDeclared(variable, declaredVariables);
                            }
                            sourceCode += variable + " = scanner.next/*{data type}*/();";
//##################################OUTPUT#################################################################################
                        } else {
                            String value = symbol.getCommands().get("value");
                            value = psdToJavaParser.translatePSDToJava(value, PSDGrammarParser::solo_Expression);
                            sourceCode += "System.out.println(" + value + ");";
                        }
                    } else {
                        sourceCode += "{symbol Vstup/Vystup bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
//##################################SWITCH####################################################################################
                } else if (symbol instanceof Switch) {
                    if (symbol.getCommands() != null) {
                        String conditionVar = symbol.getCommands().get("conditionVar");
                        conditionVar =
                                psdToJavaParser.translatePSDToJava(conditionVar, PSDGrammarParser::solo_Expression);
                        sourceCode += "switch (" + conditionVar + ") {";
                    } else {
                        sourceCode += "switch ({nevyplnena funkce!}) {";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    for (int i = 1; i < element.getInnerSegmentsCount(); i++) {
                        if (symbol.getCommands() != null) {
                            String switchCase = symbol.getCommands().get(String.valueOf(i));
                            switchCase = psdToJavaParser.translatePSDToJava(switchCase,
                                    PSDGrammarParser::solo_ListOf_Constants);
                            switchCase = switchCase.replaceAll(LINE_SEP, LINE_SEP + tabsDepth + "\t");
                            sourceCode += LINE_SEP + tabsDepth + "\t" + switchCase;
                        } else {
                            sourceCode +=
                                    LINE_SEP + tabsDepth + "\t" + "case {nevyplnena funkce vetve Switch symbolu!}:";
                        }

                        sourceCode +=
                                generateSourceCode(element.getInnerSegment(i), tabsDepth + "\t\t", declaredVariables);
                        sourceCode += LINE_SEP + tabsDepth + "\t\t" + "break;";
                    }
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "default:";
                        sourceCode +=
                                generateSourceCode(element.getInnerSegment(0), tabsDepth + "\t\t", declaredVariables);
                        sourceCode += LINE_SEP + tabsDepth + "\t\t" + "break;";
                    }
                    sourceCode += LINE_SEP + tabsDepth + "}";

//####################################DECISION########################################################################
                } else if (symbol instanceof Decision) {
                    if (symbol.getCommands() != null) {
                        String condition = symbol.getCommands().get("condition");
                        condition = psdToJavaParser.translatePSDToJava(condition, PSDGrammarParser::solo_Expression);
                        sourceCode += "if (" + condition + ") {";
                    } else {
                        sourceCode += "if ({nevyplnena funkce!}) {";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t", declaredVariables);
                    sourceCode += LINE_SEP + tabsDepth + "}";
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {
                        sourceCode += " else ";
                        if (isElseIf(element.getInnerSegment(0))) {
                            sourceCode += generateSourceCode(element.getInnerSegment(0), tabsDepth, declaredVariables);
                        } else {
                            sourceCode += "{" + generateSourceCode(element.getInnerSegment(0), tabsDepth + "\t",
                                    declaredVariables);
                            sourceCode += LINE_SEP + tabsDepth + "}";
                        }
                    }

//############################################FOR#######################################################################################
                } else if (symbol instanceof For) {
                    if (symbol.getCommands() != null) {
                        String variable = symbol.getCommands().get("var");
                        variable = psdToJavaParser.translatePSDToJava(variable, PSDGrammarParser::solo_Expression);
                        if (symbol.getCommands().containsKey("inc")) {
                            String from = symbol.getCommands().get("from");
                            from = psdToJavaParser.translatePSDToJava(from, PSDGrammarParser::solo_Expression);
                            String to = symbol.getCommands().get("to");
                            to = psdToJavaParser.translatePSDToJava(to, PSDGrammarParser::solo_Expression);
                            String inc = symbol.getCommands().get("inc");
                            inc = psdToJavaParser.translatePSDToJava(inc, PSDGrammarParser::solo_Expression);
                        
                            if (isNumeric(inc)) {
                                if (Double.parseDouble(inc) < 0) {
                                    sourceCode += makeForHeader(variable, from, to, inc, ">=", declaredVariables);
                                } else {
                                    sourceCode += makeForHeader(variable, from, to, inc, "<=", declaredVariables);
                                }
                            } else if (isNumeric(from) && isNumeric(to)) {
                                if (Double.parseDouble(from) > Double.parseDouble(to)) {
                                    sourceCode += makeForHeader(variable, from, to, inc, ">=", declaredVariables);
                                } else {
                                    sourceCode += makeForHeader(variable, from, to, inc, "<=", declaredVariables);
                                }
                            } else {
                                // everything is set via variables, so we know nothing. Let's just make it as `<=`
                                sourceCode += makeForHeader(variable, from, to, inc, "<=", declaredVariables);
                            }
                        } else {
                            String array = symbol.getCommands().get("array");
                            array = psdToJavaParser.translatePSDToJava(array, PSDGrammarParser::solo_Expression);
                            sourceCode += String.format("for (var %s : %s) {", variable, array);
                        }
                        addToDeclared(variable, declaredVariables);
                    } else {
                        sourceCode += "for ({nevyplnena funkce!}) {";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t", declaredVariables);
                    sourceCode += LINE_SEP + tabsDepth + "}";

//#########################################WHILE################################################################################# 
                } else if (symbol instanceof LoopStart) {
                    if (symbol.isOverHang()) {
                        // while
                        if (symbol.getCommands() != null) {
                            String condition = symbol.getCommands().get("condition");
                            condition =
                                    psdToJavaParser.translatePSDToJava(condition, PSDGrammarParser::solo_Expression);
                            sourceCode += "while (" + condition + ") { ";
                        } else {
                            sourceCode += "while ({nevyplnena funkce!}) {";
                            missingCommandWarning = true;
                        }
                        if (pairedCommentText != null) {
                            sourceCode += pairedCommentText; // prirazeni paroveho komentare
                            pairedCommentText = null;
                        }

                        sourceCode +=
                                generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t ", declaredVariables);
                        sourceCode += LINE_SEP + tabsDepth + "} ";
                    } else {
                        // repeat
                        String condition = null;
                        for (int i = index + 1; i < segment.size(); i++) {
                            if (segment.getElement(i).getSymbol() instanceof LoopEnd) {
                                if (segment.getElement(i).getSymbol().getCommands() != null) {
                                    condition = segment.getElement(i).getSymbol().getCommands().get("condition");
                                    condition = psdToJavaParser.translatePSDToJava(condition,
                                            PSDGrammarParser::solo_Expression);
                                } else {
                                    condition = "{nevyplnena funkce!}";
                                    missingCommandWarning = true;
                                }
                                break;
                            }
                        }
                        if (condition != null) {
                            sourceCode += "do {";
                            if (pairedCommentText != null) {
                                sourceCode += pairedCommentText; // prirazeni paroveho komentare
                                pairedCommentText = null;
                            }
                            sourceCode +=
                                    generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t", declaredVariables);
                            sourceCode += LINE_SEP + tabsDepth + "} while ( " + condition + ");";
                        }
                    }
                } else if (symbol instanceof Comment) {
                    if (symbol.hasPairSymbol() && !(segment.getElement(index + 1).getSymbol() instanceof StartEnd)) {
                        pairedCommentText = " " + getSourceCommentText(symbol.getValue(), tabsDepth, true);
                        lastWasPairedComment = true;
                        if (!isElseIf) {
                            sourceCode = removeLastNewLine(tabsDepth, sourceCode);
                        }
                    } else {
                        sourceCode += getSourceCommentText(symbol.getValue(), tabsDepth, false);
                    }
                } else if (symbol instanceof Goto) {
                    if (symbol.getCommands() != null) {
                        switch (symbol.getCommands().get("mode")) {
                            case "break":
                                sourceCode += "break;";
                                break;
                            case "continue":
                                sourceCode += "continue;";
                                break;
                            case "goto":
                                sourceCode += "// {goto " + symbol.getValue() + "}";
                                gotoUsage = true;
                                break;
                        }
                    } else {
                        sourceCode += "{symbol Spojka-break/continue/goto bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof GotoLabel) {
                    sourceCode += "// {label " + symbol.getValue() + "}";
                    gotoUsage = true;
                } else if (symbol instanceof SubRoutine) {
                    if (symbol.getValue() != null && !symbol.getValue().isEmpty()) {
                        String subroutine = symbol.getValue();
                        subroutine = psdToJavaParser.translatePSDToJava(subroutine, PSDGrammarParser::solo_Expression);
                        sourceCode += subroutine + ";";
                    } else {
                        sourceCode += "{symbol Předdefinované zpracování bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof StartEnd) {
                    if (segment.getParentElement() == null && (index == 0 || (index == 1 && segment.getElement(
                            0).getSymbol() instanceof Comment))) {
                        // prvni begin
                        sourceCode = removeLastNewLine(tabsDepth, sourceCode);
                    } else if (segment.getParentElement() == null && index == segment.size() - 1) {
                        //posledni symbol - end.
                        if (scannerNeeded) {
                            sourceCode += LINE_SEP + tabsDepth + "scanner.close();" + LINE_SEP + tabsDepth;
                        }
                        sourceCode = sourceCode.substring(0, sourceCode.length() - 1) + "}";
                    } else if (segment.getParentElement() != null || index > 1 || index == 1 && !(segment.getElement(0)
                            .getSymbol() instanceof Comment)) {
                        sourceCode += "System.exit(0);";
                    }
                } else {
                    sourceCode = removeLastNewLine(tabsDepth, sourceCode);
                }

                if (pairedCommentText != null) {
                    if (!lastWasPairedComment) {
                        // prirazeni paroveho komentare
                        sourceCode += pairedCommentText;
                        pairedCommentText = null;
                    } else {
                        lastWasPairedComment = false;
                    }
                }
            } catch (NumberFormatException | HeadlessException e) {
                JOptionPane.showMessageDialog(null,
                        "<html>Zdrojový kód se nepodařilo vytvořit!<br />problémový symbol vlastní popisek: \""
                                + symbol.getValue() + "\".</html>",
                        "Chyba při generování zdrojového kódu", JOptionPane.ERROR_MESSAGE);
                errored = true;
            }
            if (errored) {
                break;
            }
        }

        return sourceCode;
    }

    private boolean isNotDeclaredYet(String variable, Set<String> declaredVariables) {
        String variableWithoutArrayIndexing = stripVariableOfArrayIndexing(variable);
        return !declaredVariables.contains(variableWithoutArrayIndexing);
    }
    
    private void addToDeclared(String variable, Set<String> declaredVariables) {
        String variableWithoutArrayIndexing = stripVariableOfArrayIndexing(variable);
        declaredVariables.add(stripVariableOfArrayIndexing(variableWithoutArrayIndexing));
    }
    
    private String stripVariableOfArrayIndexing(String variable) {
        if (variable.contains("[")) {
            return variable.substring(0, variable.indexOf('['));
        } else {
            return variable;
        }
    }

    private String removeLastNewLine(String tabsDepth, String sourceCode)
    {
        return sourceCode.replaceFirst("\\s*$", "");
    }
    
    private String makeForHeader(String variable, String from, String to, String inc, String conditionOperator, Set<String> alreadyDeclaredVariables) {
        String result;
        if (isNotDeclaredYet(variable, alreadyDeclaredVariables)) {
            result = String.format("for (%s = %s; %s %s %s; %s += %s) {", variable, from, variable, conditionOperator,
                    to, variable, inc);
        } else {
            String initVariableType;
            if (isNumeric(from) && isNumeric(inc)) {
                if (isInteger(from) && isInteger(inc)) {
                    initVariableType = "int";
                } else {
                    initVariableType = "double";
                }
            } else {
                initVariableType = "var";
            }
            result = String.format("for (%s %s = %s; %s %s %s; %s += %s) {", initVariableType, variable, from, variable,
                    conditionOperator, to, variable, inc);
        }
        return result;
    }

    private boolean isElseIf(LayoutSegment segment)
    {
        if (segment.getParentElement() != null
                && isDecisionAndNotSwitch(segment.getParentElement())
                && segment.getParentElement().indexOfInnerSegment(segment) == 0
                && ((segment.size() == 1 && isDecisionAndNotSwitch(segment.getElement(0)))
                || (segment.size() == 2 && isPairedComment(segment.getElement(0)) && isDecisionAndNotSwitch(
                segment.getElement(1))))) {
            return true;
        }
        return false;
    }

    private boolean isDecisionAndNotSwitch(LayoutElement element) {
        return element.getSymbol() instanceof Decision && !(element.getSymbol() instanceof Switch);
    }
    
    private boolean isPairedComment(LayoutElement element) {
        return element.getSymbol() instanceof Comment && element.getSymbol().hasPairSymbol();
    }

    private boolean containsFunctionalSymbols(LayoutSegment segment, int actualElementIndex)
    {
        for (int i = actualElementIndex + 1; i < segment.size(); i++) {
            Symbol symbol = segment.getElement(i).getSymbol();
            if (symbol instanceof Process || symbol instanceof IO || symbol instanceof Decision || symbol instanceof For || symbol instanceof LoopStart || symbol instanceof Comment || symbol instanceof SubRoutine || symbol instanceof Goto || symbol instanceof GotoLabel
                    || (symbol instanceof StartEnd && (segment.getParentElement() != null || i < segment.size() - 1))) {
                return true;
            }
        }
        return false;
    }

    private String getSourceCommentText(String commentText, String tabsDepth, boolean isPaired)
    {
        String additionalTabs = "";
        if (isPaired) {
            additionalTabs = "\t";
        }
        commentText = commentText.replaceAll("\r?\n", LINE_SEP + tabsDepth + additionalTabs);
        if (commentText.contains("\n") || commentText.contains("\r")) {
            if (isPaired) {
                return "/*" + commentText + "*/";
            } else {
                return "/*" + LINE_SEP + tabsDepth + commentText + LINE_SEP + tabsDepth + "*/";
            }
        } else {
            return "//" + commentText;
        }
    }
    
    private boolean isNumeric(String str)
    {
        return isDouble(str);
    }
    
    private boolean isDouble(String str)
    {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isInteger(String str)
    {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
