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
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.SubRoutine;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Switch;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.gui.dialog.MyJOptionPane;
import cz.miroslavbartyzal.psdiagram.app.parser.ConversionResult;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

public final class Java
{

    private static final String LINE_SEP = System.lineSeparator();
    private boolean errored = false;
    private boolean gotoUsage = false;
    private boolean missingCommandWarning = false;
    private Set<String> variableNames = new HashSet<>();
    private HashMap<String, String> dataTypes = new HashMap<>();
    private Boolean scannerNeeded = false;
    private String sourceCode;
    
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
    
    public static String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name) {
        Java instance = new Java();
        return instance.generateSourceCode(flowchart, name);
    }

    private String generateSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name)
    {
        errored = false;
        missingCommandWarning = false;
        variableNames = new HashSet<>();
        dataTypes = new HashMap<>();
        scannerNeeded = false;
        gotoUsage = false;
        // generovani hlavicky
        sourceCode = "public void " + name + "() {" + LINE_SEP + LINE_SEP;

        // vyhledani vsech pouzitych identifikatoru promennych
        HashMap<String, String> vars = new HashMap<>();
        HashMap<String, String> arrayVars = new HashMap<>();
        findAndSetVariables(flowchart, vars, arrayVars);
        sourceCode += generateSourceCode(flowchart.getMainSegment(), "    ");
        if (scannerNeeded) {
            sourceCode = "// tento import je potrebne pridat pre fungovanie Scanneru"
                    + "//import java.util.Scanner;" + System.lineSeparator() + sourceCode;
        }
        if (gotoUsage) {
            JOptionPane.showMessageDialog(null,
                    "<html>Zdrojový kód byl vygenerován s následujícím upozorněním:<br />Použitý goto příkaz v Jave není definován!</html>",
                    "Goto v jazyce Java!", JOptionPane.WARNING_MESSAGE);
        }

        if (errored) {
            sourceCode = null;
        } else if (missingCommandWarning) {
            JOptionPane.showMessageDialog(null,
                    "<html>Zdrojový kód byl vygenerován s následujícím upozorněním:<br />Některý symbol nemá vyplněnu svou funkci!</html>",
                    "Nevyplněná funkce symbolu", JOptionPane.WARNING_MESSAGE);
        }
        return sourceCode;
    }

    private HashMap<String, String> setVariableTypes(HashMap<String, String> vars)
    {
        HashMap<String, String> modifiedVars = new HashMap<>();

        for (String key : vars.keySet()) {
            String[] variableDeclaration = key.split(" ");
            String variableName = variableDeclaration[variableDeclaration.length - 1];
            if (variableNames.contains(variableName)) {
                continue;
            }
            variableNames.add(variableName);
            String value = vars.get(key);
            if (value == null) {
                continue;
            }
            value = value.replaceAll("=", "==");

            if (value.contains(".")) {
                String[] valueParts = value.split(".");

                for (String valuePart : valueParts) {
                    key = setVariableType(key, valuePart, true);
                }
            }

            key = setVariableType(key, value, false);
            if (!(modifiedVars.containsKey(key))) {
                modifiedVars.put(key, value);
            }

        }
        return modifiedVars;
    }

    private String setVariableType(String key, String value, boolean iterating)
    {
        if (!iterating) {
            if (value.startsWith("[") && value.endsWith("]")) {
                key = "[] " + key;
            }
            if (value.contains("\"")) {
                key = "String " + key;
                return key;
            }
            if (!value.matches("^[^\\n0-9]*$")) {
                if (value.contains(".")) {
                    key = "Double " + key;
                } else {
                    key = "Integer " + key;
                }
            } else if (value.matches("^[^\\n0-9]*$")) {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")
                        || value.contains("==") || value.contains("!=") || value.contains(">")
                        || value.contains("<")) {
                    key = "Boolean " + key;
                } else {
                    key = "String " + key;
                }
            }
        }
        if (key.contains("[]")) {
            dataTypes.put(key.split(" ")[2], key.split(" ")[0] + " []");
        } else {
            dataTypes.put(key.split(" ")[1], key.split(" ")[0]);
        }
        sourceCode += key + ";" + LINE_SEP;
        return key;
    }

    private void findAndSetVariables(Flowchart<LayoutSegment, LayoutElement> flowchart,
            HashMap<String, String> vars, HashMap<String, String> arrayVars)
    {
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (FlowchartElement<LayoutSegment, LayoutElement> element : segment) {
                    Symbol symbol = element.getSymbol();

                    if (symbol.getCommands() != null) {

                        if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process
                                || (symbol instanceof IO && symbol.getCommands().containsKey("var"))) {
                            if (symbol.getCommands().get("var").contains("[")) {
                                String var = symbol.getCommands().get("var");
                                arrayVars.put(var.substring(0, var.indexOf("[")), symbol.getCommands().get("value"));
                            } else if (symbol.getCommands().containsKey("value") && symbol.getCommands().get(
                                    "value").startsWith("[")) {
                                if (!(arrayVars.containsKey(symbol.getCommands().get("var")))) {
                                    arrayVars.put(symbol.getCommands().get("var"), symbol.getCommands().get("value"));
                                }
                            } else {
                                if (symbol.getCommands().get("value") != null) {
                                    vars.put(symbol.getCommands().get("var"), symbol.getCommands().get("value"));
                                }

                            }
                        } else if (symbol instanceof For) {
                            vars.put(symbol.getCommands().get("var"), symbol.getCommands().get("value"));
                            if (symbol.getCommands().containsKey("array")) {
                                arrayVars.put(symbol.getCommands().get("array"), symbol.getCommands().get("value"));
                            }
                        } else if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Decision) {
//	                        	extractDecisionVariable(symbol, vars);
                        }
                    }
                    vars = setVariableTypes(vars);
                    arrayVars = setVariableTypes(arrayVars);
                }
            }
        }

    }

    private String generateSourceCode(LayoutSegment segment, String tabsDepth)
    {
        String pairedCommentText = null;
        boolean lastWasPairedComment = false;
        String sourceCode = "";

        int index = -1;
        for (Iterator<LayoutElement> it = segment.iterator(); it.hasNext();) {
            LayoutElement element = it.next();
            index++;
            Symbol symbol = element.getSymbol();
            try {
                boolean isElseIf = isElseIf(segment);
                if (!isElseIf) {
                    // odsadit na tento symbol
                    sourceCode += LINE_SEP + tabsDepth;
                }
//#####################################PROCESS############################################################################

                if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process) {
                    if (symbol.getCommands() != null) {
                        String processValue = symbol.getCommands().get("value");
                        if (processValue.contains("//")) {
                            processValue = processValue.replaceAll("//", "/");
                        }
                        sourceCode += symbol.getCommands().get("var") + " = " + processValue + insertSemicolon();
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
                            sourceCode += "//Scanner has to be imported from java.util" + System.lineSeparator();
                            sourceCode += "Scanner scanner = new Scanner(System.in);" + System.lineSeparator();

                            sourceCode += readLineToVariable(symbol.getCommands().get("var"));
//##################################OUTPUT#################################################################################
                        } else {
                            sourceCode += "System.out.println(" + symbol.getCommands().get(
                                    "value") + ")" + insertSemicolon();
                        }
                    } else {
                        sourceCode += "{symbol Vstup/Vystup bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
//##################################SWITCH####################################################################################
                } else if (symbol instanceof Switch) {
                    if (symbol.getCommands() != null) {
                        sourceCode += "switch( " + symbol.getCommands().get(
                                "conditionVar") + "){";
                    } else {
                        sourceCode += "switch {nevyplnena funkce!} of";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    for (int i = 1; i < element.getInnerSegmentsCount(); i++) {
                        if (symbol.getCommands() != null) {
                            sourceCode += tabsDepth + "case "
                                    + symbol.getCommands().get(String.valueOf(i)) + ":" + System.lineSeparator();
                        } else {
                            sourceCode += tabsDepth + "\t{nevyplnena funkce vetve Switch symbolu!}:";
                        }

                        sourceCode += generateSourceCode(element.getInnerSegment(i), insertSemicolon());
                        sourceCode += "break" + insertSemicolon();
                    }
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {

                        sourceCode += "default: " + generateSourceCode(element.getInnerSegment(0),
                                tabsDepth + "\t\t");
                    }
                    sourceCode += tabsDepth + "}";

//####################################DECISION########################################################################
                } else if (symbol instanceof Decision) {
                    if (symbol.getCommands() != null) {
                        sourceCode += "if (" + symbol.getCommands().get(
                                "condition") + ") {";
                    } else {
                        sourceCode += "if {nevyplnena funkce!} ";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    sourceCode += LINE_SEP + tabsDepth;
                    sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t");
                    sourceCode += LINE_SEP + tabsDepth + "}";
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {
                        sourceCode += " else {";
                        if (isElseIf(element.getInnerSegment(0))) {
                            sourceCode += " " + generateSourceCode(element.getInnerSegment(0),
                                    tabsDepth);
                        } else {
                            sourceCode += LINE_SEP + tabsDepth;
                            sourceCode += generateSourceCode(element.getInnerSegment(0),
                                    tabsDepth + "\t");
                            sourceCode += LINE_SEP + tabsDepth + "}";
                        }
                    }

//############################################FOR#######################################################################################
                } else if (symbol instanceof For) {
                    if (symbol.getCommands() != null) {
                        if (symbol.getCommands().containsKey("inc")) {
                            int increment = Integer.valueOf(symbol.getCommands().get("inc"));
                            //TODO allow also another types of increment (/ * etc)
                            if (increment > 0) {
                                sourceCode += "for (int " + symbol.getCommands().get("var") + " = " + symbol.getCommands().get(
                                        "from") + ";" + symbol.getCommands().get(
                                                "var") + "<=" + symbol.getCommands().get(
                                                "to") + ";" + symbol.getCommands().get("var") + "++) {";
                            } else {
                                sourceCode += "for (int " + symbol.getCommands().get("var") + " = " + symbol.getCommands().get(
                                        "from") + ";" + symbol.getCommands().get(
                                                "var") + ">=" + symbol.getCommands().get(
                                                "to") + ";" + symbol.getCommands().get("var") + "--) {";
                            }
                        } else {
                            String typeOfArray = dataTypes.get(symbol.getCommands().get("array"));
                            sourceCode += "for (" + typeOfArray.replaceAll("\\[\\]", "") + " " + symbol.getCommands().get(
                                    "var") + " : " + symbol.getCommands().get("array") + ") {";
                        }
                    } else {
                        sourceCode += "for {nevyplnena funkce!} {";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    if (element.getInnerSegment(1) != null) {
                        sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t");
                    }
                    sourceCode += LINE_SEP + tabsDepth + "}" + insertSemicolon();

//#########################################WHILE################################################################################# 
                } else if (symbol instanceof LoopStart) {
                    if (symbol.isOverHang()) {
                        // while
                        if (symbol.getCommands() != null) {
                            sourceCode += "while(" + symbol.getCommands().get(
                                    "condition") + ") { ";
                            sourceCode += System.lineSeparator();
                        } else {
                            sourceCode += "while {nevyplnena funkce!} do";
                            missingCommandWarning = true;
                        }
                        if (pairedCommentText != null) {
                            sourceCode += pairedCommentText; // prirazeni paroveho komentare
                            pairedCommentText = null;
                        }

                        sourceCode += generateSourceCode(element.getInnerSegment(1),
                                tabsDepth + "\t ");
                        sourceCode += tabsDepth + "} ";
                    } else {
                        // repeat
                        String condition = null;
                        for (int i = index + 1; i < segment.size(); i++) {
                            if (segment.getElement(i).getSymbol() instanceof LoopEnd) {
                                if (segment.getElement(i).getSymbol().getCommands() != null) {
                                    condition = segment.getElement(i).getSymbol().getCommands().get(
                                            "condition");
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
                            sourceCode += generateSourceCode(element.getInnerSegment(1),
                                    tabsDepth + "\t");
                            sourceCode += LINE_SEP + tabsDepth
                                    + "} while ( " + condition + ") " + insertSemicolon();
                        }
                    }
                } else if (symbol instanceof Comment) {
                    if (symbol.hasPairSymbol()) {
                        pairedCommentText = " " + getSourceCommentText(symbol.getValue(), tabsDepth);
                        lastWasPairedComment = true;
                        if (!isElseIf) {
                            sourceCode = sourceCode.substring(0,
                                    sourceCode.length() - (LINE_SEP + tabsDepth).length());
                        }
                    } else {
                        sourceCode += getSourceCommentText(symbol.getValue(), tabsDepth);
                    }
                } else if (symbol instanceof Goto) {
                    if (symbol.getCommands() != null) {
                        switch (symbol.getCommands().get("mode")) {
                            case "break":
                                sourceCode += "break" + insertSemicolon();
                                break;
                            case "continue":
                                sourceCode += "continue" + insertSemicolon();
                                break;
                            case "goto":
                                gotoUsage = true;
                                break;
                        }
                    } else {
                        sourceCode += "{symbol Spojka-break/continue/goto bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof GotoLabel) {
                    gotoUsage = true;
                } else if (symbol instanceof SubRoutine) {
                    if (symbol.getValue() != null && !symbol.getValue().equals("")) {
                        sourceCode += symbol.getValue() + insertSemicolon();
                    } else {
                        sourceCode += "// {symbol Předdefinované zpracování bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof StartEnd) {
                    if (segment.getParentElement() == null && (index == 0 || (index == 1 && segment.getElement(
                            0).getSymbol() instanceof Comment))) {
                        // prvni begin
                        sourceCode += "";
                        tabsDepth = "\t";
                    } else if (segment.getParentElement() == null && index == segment.size() - 1) {
                        //posledni symbol - end.
                        if (scannerNeeded) {
                            sourceCode += tabsDepth + "scanner.close();" + System.lineSeparator();
                        }
                        sourceCode += System.lineSeparator();
                        sourceCode = sourceCode.substring(0,
                                sourceCode.length() - tabsDepth.length()) + "}";
                    } else {
                        sourceCode += "System.exit(0);" + insertSemicolon();
                    }
                } else {
                    sourceCode = sourceCode.substring(0,
                            sourceCode.length() - (LINE_SEP + tabsDepth).length());
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
                        "<html>Zdrojový kód se nepodařilo vytvořit!<br />problémový symbol vlastní popisek: \"" + symbol.getValue() + "\".</html>",
                        "Chyba při generování zdrojového kódu", JOptionPane.ERROR_MESSAGE);
                errored = true;
            }
            if (errored) {
                break;
            }
        }

        return sourceCode;
    }

    private String readLineToVariable(String variableName)
    {
        if (variableName == null || variableName.equals("")) {
            return "{data type of variable} {variable name} = scanner.next{variable type}();";
        }
        if (dataTypes.get(variableName) == null) {
            return "{data type of variable} " + variableName + " = scanner.next{data type}();";
        }
        if (dataTypes.get(variableName).equals("Double")) {
            return variableName + " = scanner.nextDouble();";
        }
        if (dataTypes.get(variableName).equals("Integer")) {
            return variableName + " = scanner.nextInt();";
        }
        return variableName;
    }

    private boolean isElseIf(LayoutSegment segment)
    {
        if (segment.getParentElement() != null
                && segment.getParentElement().getSymbol() instanceof Decision && !(segment.getParentElement().getSymbol() instanceof Switch)
                && segment.getParentElement().indexOfInnerSegment(segment) == 0
                && ((segment.size() == 1 && segment.getElement(0).getSymbol() instanceof Decision && !(segment.getElement(
                0).getSymbol() instanceof Switch))
                || (segment.size() == 2 && segment.getElement(0).getSymbol() instanceof Comment && segment.getElement(
                0).getSymbol().hasPairSymbol() && segment.getElement(1).getSymbol() instanceof Decision && !(segment.getElement(
                1).getSymbol() instanceof Switch)))) {
            return true;
        }
        return false;
    }

    private String insertSemicolon()
    {
        return ";" + LINE_SEP;
    }

    private boolean containsFunctionalSymbols(LayoutSegment segment, int actualElementIndex)
    {
        for (int i = actualElementIndex + 1; i < segment.size(); i++) {
            Symbol symbol = segment.getElement(i).getSymbol();
            if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process || symbol instanceof IO || symbol instanceof Decision || symbol instanceof For || symbol instanceof LoopStart || symbol instanceof Comment || symbol instanceof SubRoutine || symbol instanceof Goto || symbol instanceof GotoLabel
                    || (symbol instanceof StartEnd && (segment.getParentElement() != null || i < segment.size() - 1))) {
                return true;
            }
        }
        return false;
    }

    private String getSourceCommentText(String commentText, String tabsDepth)
    {
        commentText = commentText.replaceAll("\r|[^\r]\n", LINE_SEP + tabsDepth + "\t");
        if (commentText.contains("\n") || commentText.contains("\r")) {
            return "/*" + commentText + "*/";
        } else {
            return "//" + commentText;
        }
    }

}
