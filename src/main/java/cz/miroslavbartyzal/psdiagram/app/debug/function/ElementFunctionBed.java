/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.debug.function;

import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.StartEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.For;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Decision;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopEnd;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.IO;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.GotoLabel;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Goto;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Switch;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Symbol;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.LoopStart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.global.RegexFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.EnhancedJOptionPane;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

/**
 * <p>
 * Tato třída představuje jakýsi "blackbox", či "postýlku", ve které jsou
 * prováděny veškeré funkce symbolů. K tomuto účelu je používán Javascript
 * (Rhino), který je součástí JDK Javy.</p>
 *
 * <p>
 * Funkce symbolu je provedena a následně jsou všechny proměnné
 * překontrolovány, přičemž se hledají jejich případné modifikace či proměnné,
 * které právě vznikly.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class ElementFunctionBed
{

    private static final String scriptStart = "importPackage(javax.swing);"
            + "var updateVariables = \"{"
            + "var _locals = (function(){}).__parent__;"
            + "for (var _i in _locals) {"
            + "if (/_locals/(_i) || /_i/(_i) || _i.equals('arguments') || _i.equals('_variables_') || _i.equals('_updatedVars_') || _i.equals('_extraRet_') || myUneval(_locals[_i])=== 'undefined'"
            + "|| (_variables_.containsKey(_i) && _variables_.get(_i).equals(myUneval(_locals[_i])))) continue;"
            + "_updatedVars_.put(_i, myUneval(_locals[_i]));"
            + "}"
            + "}\";"
            + "function script(_variables_, _updatedVars_, _extraRet_) {" // ve fci vyuzivam pro vedlejsi vypocty jen globalni promenne, lokalni promenne jdou totiz na vystup
            + "entries = _variables_.entrySet().toArray();"
            + "for (entry in entries) {"
            + "eval(\"var \" + entries[entry].getKey() + \" = \" + entries[entry].getValue() + \";\");"
            + "}";
    private static final String scriptEnd = ""
            + "eval(updateVariables);"
            + "}"
            + "function myUneval(s) {" // solution found on http://stackoverflow.com/questions/7885096/how-do-i-decode-a-string-with-escaped-unicode
            + "return uneval(s)"
            + ".replace(/\\\\u([\\d\\w]{4})/gi, function (match, grp) {"
            + "return String.fromCharCode(parseInt(grp, 16));"
            + "}"
            + ").replace("
            + "/\\\\x([\\d\\w]{2})/gi, function (match, grp) {return String.fromCharCode(parseInt(grp, 16));"
            + "}"
            + ")"
            + ";"
            + "}";

// ***vyrazeno kvuli antiviru***
//    /**
//     * Metoda zajišťuje provedení jakéhokoliv Javascriptu.
//     *
//     * @param script skript, který má být proveden
//     * @return výsledek poslední provedené operace v rámci spuštěného skriptu
//     */
//    public static Object executeCustomJavaScript(String script) {
//        try {
//            return getJavaScriptEngine().eval(script);
//
//            //System.out.println(JSENGINE.getFactory().getEngineVersion());
//        } catch (ScriptException ex) {
//            ex.printStackTrace(System.err);
//            return null;
//        }
//    }
    /**
     * Provede zpracování funkce symbolu a vrátí instaci FunctionResult,
     * reprezentující její výsledek.
     *
     * @param actualElement element, jehož symbol, jehož funkce má být
     * zpracována
     * @param variables aktuálně platné proměnné, které mohou být použity funkcí
     * symbolu
     * @return instace FunctionResult, reprezentující výsledek zpracování funkce
     * symbolu
     */
    public static FunctionResult getResult(LayoutElement actualElement,
            HashMap<String, String> variables)
    {
        String err = "<html>Nelze pokračovat v procházení, protože není nastavena funkce aktuálně zpracovávaného symbolu.<br />Funkci symbolu lze nastavit v editačním režimu, po kliknutí na symbol v záložce \"Funkce\" vlevo.<html>";
        FunctionResult result = new FunctionResult();
        LayoutSegment actualSegment = actualElement.getParentSegment();
        Symbol symbol = actualElement.getSymbol();
        if (symbol instanceof StartEnd && (actualSegment.getParentElement() != null
                || actualSegment.indexOfElement(actualElement) > 1 || (actualSegment.indexOfElement(
                        actualElement) == 1 && !(actualSegment.getElement(0).getSymbol() instanceof Comment)))) { // > 1 protoze 0ty muze byt komentar
            return result;
        }
        HashMap<String, String> commandsCopy = null; // budu nahrazovat randomy, tak abych nenahrazoval i zdroj..
        if (symbol.getCommands() != null) {
            commandsCopy = new HashMap<>(symbol.getCommands());
            setRandoms(commandsCopy);
        }
        if ((commandsCopy == null && (symbol instanceof Decision || symbol instanceof For || symbol instanceof IO || symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process))) {
            JOptionPane.showMessageDialog(null, err, "Chybí funkce symbolu",
                    JOptionPane.ERROR_MESSAGE);
            return result;
        } else if ((symbol instanceof Goto || symbol instanceof GotoLabel) && (symbol.getValue() == null || symbol.getValue().equals(
                ""))) {
            JOptionPane.showMessageDialog(null,
                    "<html>Nelze pokračovat v procházení, protože symbol Spojky musí mít vyplněnou svou textovou hodnotu.<br />Textovou hodnotu symbolu lze nastavit v editačním režimu, po kliknutí na symbol v záložce \"Text\" vlevo.<html>",
                    "Chybí textová hodnota symbolu", JOptionPane.ERROR_MESSAGE);
            return result;
        }
        int innerSegment = -1;

        if (symbol instanceof Switch) {
            innerSegment = caseSetProgAndUpVars(result, commandsCopy, variables);
        } else if (symbol instanceof Decision) {
            innerSegment = decisionSetProgAndUpVars(result, commandsCopy, variables);
        } else if (symbol instanceof For) {
            innerSegment = forSetProgAndUpVars(result, commandsCopy, variables);
        } else if (symbol instanceof IO) {
            innerSegment = ioSetProgAndUpVars(result, commandsCopy, variables);
        } else if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process) {
            innerSegment = processSetProgAndUpVars(result, commandsCopy, variables);
        } else if (symbol instanceof LoopEnd) {
            innerSegment = 1;
            LayoutElement pairElement = null;
            for (int i = actualSegment.indexOfElement(actualElement) - 1; i >= 0; i--) {
                pairElement = actualSegment.getElement(i);
                if (pairElement.getSymbol() instanceof LoopStart) {
                    if (!pairElement.getSymbol().isOverHang()) {
                        if (commandsCopy == null) {
                            JOptionPane.showMessageDialog(null, err, "Chybí funkce symbolu",
                                    JOptionPane.ERROR_MESSAGE);
                            return result;
                        }
                        innerSegment = decisionSetProgAndUpVars(result, commandsCopy, variables);
                        if (innerSegment == 0) {
                            actualElement = pairElement;
                            innerSegment = -1;
                        }
                    }
                    break;
                }
            }
            if (innerSegment == 1) { // pujdu klasicky k horni casti loopu
                result.nextElement = pairElement;
                result.paths = new Path2D[]{actualElement.getPathToNextSymbol()};
                return result;
            }
        } else if (symbol instanceof LoopStart) {
            if (symbol.isOverHang()) {
                if (commandsCopy == null) {
                    JOptionPane.showMessageDialog(null, err, "Chybí funkce symbolu",
                            JOptionPane.ERROR_MESSAGE);
                    return result;
                }
                innerSegment = decisionSetProgAndUpVars(result, commandsCopy, variables);
                if (innerSegment == 0) {
                    innerSegment = -1;
                }
            } else {
                innerSegment = 1;
            }
        } else if (symbol instanceof Goto) {
            if (commandsCopy.get("mode").equals("break") || commandsCopy.get("mode").equals(
                    "continue")) {
                // nalezeni rodicovskeho elementu cyklu
                LayoutElement parentLoop = actualElement;
                do {
                    parentLoop = parentLoop.getParentSegment().getParentElement();
                    if (parentLoop == null) {
                        JOptionPane.showMessageDialog(null,
                                "<html>Symbol spojky ve funkci příkazu " + commandsCopy.get("mode") + " musí být umístěn<br />přímo uvnitř těla cyklu, který má být přerušen!</html>",
                                "Rodičovský cyklus nenalezen", JOptionPane.ERROR_MESSAGE);
                        return result;
                    }
                } while (!(parentLoop.getSymbol() instanceof For) && !(parentLoop.getSymbol() instanceof LoopStart));

                if (commandsCopy.get("mode").equals("continue")) {
                    if (!parentLoop.getSymbol().isOverHang()) {
                        do {
                            parentLoop = parentLoop.getParentSegment().getElement(
                                    parentLoop.getParentSegment().indexOfElement(parentLoop) + 1);
                        } while (!(parentLoop.getSymbol() instanceof LoopEnd));
                    }
                    result.nextElement = parentLoop;
                    return result;
                } else {
                    innerSegment = -1;
                    actualElement = parentLoop;
                    actualSegment = actualElement.getParentSegment();
                }
            } else {
                // Goto
                // nalezeni korenoveho segmentu
                while (actualSegment.getParentElement() != null) {
                    actualSegment = actualSegment.getParentElement().getParentSegment();
                }

                ArrayList<LayoutSegment> segments = new ArrayList<>();
                segments.add(actualSegment);
                LayoutElement myGotoLabel = null;
                while (!segments.isEmpty()) {
                    LayoutSegment segment = segments.remove(0);
                    for (LayoutElement element : segment) {
                        if (element.getInnerSegmentsCount() > 0) {
                            for (LayoutSegment sgmnt : element.getInnerSegments()) {
                                if (sgmnt != null) {
                                    segments.add(sgmnt);
                                }
                            }
                        } else if (element.getSymbol() instanceof GotoLabel && element.getSymbol().getValue().equals(
                                actualElement.getSymbol().getValue())) {
                            if (myGotoLabel != null) {
                                JOptionPane.showMessageDialog(null,
                                        "<html>Nelze určit, kam symbol Spojky odkazuje.<br />Korespondujících Spojek-návěští je více než jedna.</html>",
                                        "Duplicita hodnot Spojky", JOptionPane.ERROR_MESSAGE);
                                return result;
                            }
                            myGotoLabel = element;
//                            actualSegment = segment;
                        }
                    }
                }
                if (myGotoLabel == null) {
                    JOptionPane.showMessageDialog(null,
                            "<html>Nelze určit, kam symbol Spojky odkazuje.<br />Nebyla nalezena žádná Spojka-návěští s korespondující hodnotou .</html>",
                            "Duplicita hodnot Spojky", JOptionPane.ERROR_MESSAGE);
                    return result;
                }
                result.nextElement = myGotoLabel;
                //result.updateSegment = actualSegment;
                return result;
            }
        }

        ArrayList<Path2D> paths = new ArrayList<>();
        if (innerSegment > -1) {
            actualSegment = actualElement.getInnerSegment(innerSegment);
            if (actualSegment.getDescriptionLayout() != null) {
                result.segmentDesc = actualSegment.getDescriptionLayout();
            }
            if (!actualSegment.getPathToThisSegment().getPathIterator(null).isDone()) {
                paths.add(actualSegment.getPathToThisSegment());
            }
        }

        setNextElSegAndPaths(result, paths, actualElement, actualSegment,
                actualSegment.indexOfElement(actualElement));

        return result;
    }

    private static ScriptEngine getJavaScriptEngine()
    {
        return new ScriptEngineManager().getEngineByName("JavaScript");
    }

    /**
     *
     * @param variables
     * @param symbolScript
     * @param extraReturn
     * @param silently
     * @return null if there was an error and the debug process should be stopped
     */
    private static HashMap<String, String> doSymbolScript(HashMap<String, String> variables,
            String symbolScript, String[] extraReturn, boolean silently) throws ScriptException
    {
        HashMap<String, String> updatedVariables = new HashMap<>();
        ScriptEngine jsEngine = getJavaScriptEngine();
        try {
            jsEngine.eval(scriptStart + symbolScript + scriptEnd);
            Invocable invocableEngine = (Invocable) jsEngine;
            invocableEngine.invokeFunction("script", variables, updatedVariables, extraReturn);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace(System.err);
        } catch (ScriptException ex) {
            if (!silently) {
                JOptionPane.showMessageDialog(null, ex.getCause().getMessage(), "Chyba!",
                        JOptionPane.ERROR_MESSAGE);
                throw ex;
            }
        }
        /*
         * for (String key: updatedVariables.keySet()) {
         * System.out.println(key + " = " + updatedVariables.get(key));
         * }
         */
        return updatedVariables;
    }

    private static void setNextElSegAndPaths(FunctionResult result, ArrayList<Path2D> paths,
            LayoutElement actualElement, LayoutSegment actualSegment, int actualElIndex)
    {
        if (!paths.isEmpty() && actualElIndex > -1 && actualElement.getSymbol() instanceof For) { // jestli jsme do for prisli z vnoreneho
            // u For je treba se navracet primo do nej, ne do nasledujiciho
            result.nextElement = actualElement;
            result.paths = paths.toArray(new Path2D[0]);
            //result.updateSegment = actualSegment;
            return;
        }
        for (int i = actualElIndex + 1; i < actualSegment.size(); i++) {
            LayoutElement element = actualSegment.getElement(i);
            if (!(element.getSymbol() instanceof Comment) && (!paths.isEmpty() || !(actualElement.getSymbol() instanceof LoopStart) || !(element.getSymbol() instanceof LoopEnd))) {
                result.nextElement = element;
                if (actualElIndex > -1 && !(element.getSymbol() instanceof LoopEnd)) {
                    paths.add(actualElement.getPathToNextSymbol());
                }
                result.paths = paths.toArray(new Path2D[0]);
                //if (paths.size() > 1 || actualElIndex == -1) { // je-li cesta jen jedna, jsme porad ve stejnem segmentu.. teda pokud nejsme na zacatku vnitrniho segmentu
                //result.updateSegment = actualSegment;
                //}
                return;
            }
        }
        // nyni jsme dosahli konce segmentu, musime o uroven vys...
        paths.add(actualSegment.getPathFromThisSegment());
        actualElement = actualSegment.getParentElement();
        actualSegment = actualElement.getParentSegment();
        actualElIndex = actualSegment.indexOfElement(actualElement);
        setNextElSegAndPaths(result, paths, actualElement, actualSegment, actualElIndex);
    }

    private static void setRandoms(HashMap<String, String> commands)
    {
        for (String key : commands.keySet()) {
            if (commands.get(key).matches(".*Math.random\\([^\\)]*\\).*")) { // jestli je pritomen random
                String command = commands.get(key);
                String[] commandSplit = RegexFunctions.splitString(command, "\"[^\"]*\"?"); // jsou-li pritomny uvozovky, musim se jich nejdrive zbavit, take hrozi ze je random jen v nich

                for (int i = 0; i < commandSplit.length; i += 2) {
                    while (commandSplit[i].matches(".*Math.random\\([^\\)]*\\).*")) {
                        try {
                            commandSplit[i] = commandSplit[i].replaceFirst(
                                    "Math.random\\([^\\)]*\\)", getJavaScriptEngine().eval(
                                            "Math.random();").toString());
                        } catch (ScriptException ex) {
                            ex.printStackTrace(System.err);
                            commandSplit[i] = commandSplit[i].replaceFirst(
                                    "Math.random\\([^\\)]*\\)", Double.toString(Math.random()));
                        }
                    }
                }
                command = "";
                for (String commandPart : commandSplit) {
                    command += commandPart;
                }
                commands.put(key, command);
            }
        }
    }

    //*************************************************************************
    //*******************SYMBOLS PROGRESS AND UPDATEVARS SET*******************
    //*************************************************************************
    private static String getCompiledProgressDesc(HashMap<String, String> variables, String command)
    {
//        String functionRegex = "([a-zA-Z\\_\\$][a-zA-Z0-9\\_\\$]*\\.)*[a-zA-Z\\_\\$][a-zA-Z0-9\\_\\$]*\\([^\\(\\)]*\\)";

        // sude indexy jsou prikazy, liche uvozovky
        String[] commandsWithoutQ = RegexFunctions.splitString(command, "\"[^\"]*\"?");
        for (int i = 0; i < commandsWithoutQ.length; i += 2) {
//            Matcher matcher = Pattern.compile(functionRegex).matcher(commandsWithoutQ[i]);
//            while (matcher.find()) { // jestli prikaz obsahuje volani funkci(x.x(x)), je treba je prvne zpracovat
//                String[] extraRet = new String[1];
//                doSymbolScript(variables, "_extraRet_[0] = " + matcher.group() + ";", extraRet, true);
//                commandsWithoutQ[i] = commandsWithoutQ[i].replaceFirst(functionRegex, extraRet[0]);
//                matcher = Pattern.compile(functionRegex).matcher(commandsWithoutQ[i]); // osetrim i vnorene fce
//            }
            String[] methodsSplit = RegexFunctions.splitByMethods(commandsWithoutQ[i]);
            commandsWithoutQ[i] = "";
            for (int j = 0; j < methodsSplit.length; j++) {
                if (j % 2 == 1) {
                    String[] extraRet = new String[1];
                    try {
                        doSymbolScript(variables,
                                "_extraRet_[0] = myUneval(" + methodsSplit[j] + ");",
                                extraRet, true);
                    } catch (ScriptException ex) {
                        // won't happen since silent is on true as doSymbolScript call parameter
                        ex.printStackTrace(System.err);
                    }
                    methodsSplit[j] = extraRet[0];
                }
                commandsWithoutQ[i] += methodsSplit[j];
            }
        }
        command = "";
        for (String commandPart : commandsWithoutQ) {
            command += commandPart;
        }

        // sude indexy jsou promenne
        String[] commandSplit = RegexFunctions.splitStringIgnoreQuotesInsides(command,
                "[^a-zA-Z0-9\\_\\.]+|[a-zA-Z\\_\\$][\\w\\$]*\\[");
        for (int i = 0; i < commandSplit.length; i++) {
            if (i % 2 == 1) {
                if (SettingsHolder.settings.isFunctionFilters()) {
                    commandSplit[i] = commandSplit[i].replace("==", "=").replace("&&", "&").replace(
                            "||", "|").replace("!=", "≠").replace("!", "¬").replace(">=", "≥").replace(
                                    "<=", "≤");
                }
            } else {
                if (!commandSplit[i].equals("") && !commandSplit[i].matches("^\\s+$")) {
                    // nahradim promennou jeji hodnotou
                    String[] extraRet = new String[1];
                    try {
                        //doSymbolScript(variables, "_extraRet_[0] = " + commandSplit[i] + ";", extraRet);
                        //doSymbolScript(variables, "_extraRet_[0] = " + commandSplit[i] + ".toSource();", extraRet);
                        doSymbolScript(variables,
                                "_extraRet_[0] = myUneval(" + commandSplit[i] + ");", extraRet, true);
                    } catch (ScriptException ex) {
                        // won't happen since silent is on true as doSymbolScript call parameter
                        ex.printStackTrace(System.err);
                    }

                    if (extraRet[0] != null) {
                        commandSplit[i] = extraRet[0];
                    }
                }
            }
        }
        command = "";
        for (String commandPart : commandSplit) {
            command += commandPart;
        }

        return command;
    }

    private static int caseSetProgAndUpVars(FunctionResult result, HashMap<String, String> commands,
            HashMap<String, String> variables)
    {
        String[] extraRet = new String[]{"-1", ""};
        String script = ""
                + "_extraRet_[1] = myUneval(" + commands.get("conditionVar") + ");"
                + "switch (" + commands.get("conditionVar") + ") {";
        for (String key : commands.keySet()) {
            if (!key.equals("conditionVar")) {
                for (String cs : commands.get(key).split("\\,\\s*")) {
                    script += "case " + cs + ":";
                }
                script += "_extraRet_[0] = " + key + ";";
                script += "break;";
            }
        }
        script += ""
                + "default:"
                + "_extraRet_[0] = 0;"
                + "break;"
                + "}";

        // bohuzel switch nema za stejne hodnoty new Number(1) a 1; new String("ahoj") a "ahoj"
        // musel bych kazdou "key" hodnotu protahnout scriptovou toSource() metodou - to uz bude lepsi nahradit switch if else..
            /*
         * String script = ""
         * + "_extraRet_[0] = " + commands.get("conditionVar") + ";"
         * + "if (false){}";
         * for (String key: commands.keySet()) {
         * if (!key.equals("conditionVar")) {
         * String[] splitted = commands.get(key).split("\\,\\s*");
         * script += "else if (" + commands.get("conditionVar") + " == " +
         * splitted[0];
         * for (int i = 1; i < splitted.length; i++) {
         * script += " || " + commands.get("conditionVar") + " == " +
         * splitted[i];
         * }
         * script += ") {"
         * + "_extraRet_[1] = " + key + ";"
         * + "}";
         * }
         * }
         * script += "else {"
         * + "_extraRet_[1] = 0;"
         * + "}";
         */
        try {
            result.updatedVariables = doSymbolScript(variables, script, extraRet, false);
        } catch (ScriptException ex) {
            result.haltDebug = true;
        }
        result.progressDesc = extraRet[1];
        return Integer.parseInt(extraRet[0]);
    }

    private static int decisionSetProgAndUpVars(FunctionResult result,
            HashMap<String, String> commands, HashMap<String, String> variables)
    {
        String[] extraRet = new String[]{"-1"};
        String script = ""
                + "if (" + commands.get("condition") + ") {"
                + "_extraRet_[0] = 1;"
                + "} else {"
                + "_extraRet_[0] = 0;"
                + "}";

        try {
            result.updatedVariables = doSymbolScript(variables, script, extraRet, false);
        } catch (ScriptException ex) {
            result.haltDebug = true;
        }
        result.progressDesc = getCompiledProgressDesc(variables, commands.get("condition"));
        return Integer.parseInt(extraRet[0]);
    }

    private static int forSetProgAndUpVars(FunctionResult result, HashMap<String, String> commands,
            HashMap<String, String> variables)
    {
        String[] extraRet = new String[]{null, "-1"};
        String script = "if ((" + commands.get("inc") + " > 0 && " + commands.get("var") + " > " + commands.get(
                "to") + ") || (" + commands.get("inc") + " < 0 && " + commands.get("var") + " < " + commands.get(
                        "to") + ")) {";
        if (variables.containsKey(commands.get("var"))) {
            // inicializace jiz probehla
            if (commands.containsKey("array")) {
                script = ""
                        + "idx = " + commands.get("array") + ".indexOf(" + commands.get("var") + ") + 1;"
                        + "if (idx < " + commands.get("array") + ".length) {"
                        + "var " + commands.get("var") + " = " + commands.get("array") + "[idx];"
                        + "_extraRet_[1] = 1;"
                        + "_extraRet_[0] = myUneval(" + commands.get("var") + ");"
                        + "} else {"
                        + "_extraRet_[1] = -1;"
                        + "}";
            } else {
                script = ""
                        + "var " + commands.get("var") + " = " + commands.get("var") + " + " + commands.get(
                                "inc") + ";"
                        + script
                        + "_extraRet_[1] = -1;"
                        + "} else {"
                        + "_extraRet_[1] = 1;"
                        + "}"
                        + "_extraRet_[0] = myUneval(" + commands.get("var") + ");";
            }
        } // nutna inicializace
        else {
            if (variables.containsKey("0" + commands.get("var"))) {
                /*
                 * smluveny signal pro inicializaci cyklu (drive provadeno smazanim promenne cyklu, ale zjistil
                 * jsem, ze ji vlastne potrebuji, bude-li pomoci ni nekdo inicializovat)
                 */
                variables.put(commands.get("var"), variables.remove("0" + commands.get("var")));
            }

            if (commands.containsKey("array")) {
                script = ""
                        + "if (" + commands.get("array") + ".length > 0) {"
                        + "var " + commands.get("var") + " = " + commands.get("array") + "[0];"
                        + "_extraRet_[1] = 1;"
                        + "_extraRet_[0] = myUneval(" + commands.get("var") + ");"
                        + "} else {"
                        + "_extraRet_[1] = -1;"
                        + "}";
            } else {
                script = ""
                        + "var " + commands.get("var") + " = " + commands.get("from") + ";"
                        + script
                        + "_extraRet_[1] = -1;"
                        + "} else {"
                        + "_extraRet_[1] = 1;"
                        + "}"
                        + "_extraRet_[0] = myUneval(" + commands.get("var") + ");";
            }
        }

        try {
            result.updatedVariables = doSymbolScript(variables, script, extraRet, false);
        } catch (ScriptException ex) {
            result.haltDebug = true;
        }
        if (!commands.containsKey("array") || !extraRet[1].equals("-1")) {
            // v pripade inicializace je treba upozornit (zvyrazneni ve vypisu promennych) na inicializacni promennou
            addVarToResultIfNotPresent(commands.get("var").split("\\[.*")[0], variables,
                    result.updatedVariables);
        }
        if (extraRet[0] != null) {
            result.progressDesc = commands.get("var") + " ← " + extraRet[0];
        }
        return Integer.parseInt(extraRet[1]);
    }

    private static int ioSetProgAndUpVars(FunctionResult result, HashMap<String, String> commands,
            HashMap<String, String> variables)
    {
        String script;
        if (commands.containsKey("var")) { // input
            String var = "";
            String[] splitBrackets = RegexFunctions.varBracketsInsides(commands.get("var"));
            for (int i = 0; i < splitBrackets.length; i++) {
                if (i % 2 == 0) {
                    var += splitBrackets[i];
                } else {
                    String[] extraRet = new String[1];
                    try {
                        doSymbolScript(variables,
                                "_extraRet_[0] = myUneval(" + splitBrackets[i] + ");",
                                extraRet, true);
                    } catch (ScriptException ex) {
                        // won't happen since silent is on true as doSymbolScript call parameter
                        ex.printStackTrace(System.err);
                    }
                    var += extraRet[0];
                }
            }

            String input = EnhancedJOptionPane.showInputDialog(null,
                    "Zadej hodnotu pro proměnnou " + var + ":", "Vstup",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"OK", "Přerušit"}, "OK");
            if (input == null) {
                input = "null";
                result.haltDebug = true;
            } else if (!input.matches("^(\\-|\\+)?([1-9]|0(?=\\.|$))[0-9]*(\\.[0-9]+)?$")) {
                // input is not a number
                input = "\"" + input.replaceAll("\"", "").replaceAll("\'", "") + "\"";
            }

            if (!commands.get("var").contains("[")) {
                script = "var " + commands.get("var") + " = " + input + ";";
            } else {
                script = commands.get("var") + " = " + input + ";";
            }

            try {
                result.updatedVariables = doSymbolScript(variables, script, null, false);
            } catch (ScriptException ex) {
                result.haltDebug = true;
            }
            addVarToResultIfNotPresent(commands.get("var").split("\\[.*")[0], variables,
                    result.updatedVariables);
            result.progressDesc = var + " ← " + input;
        } else { // output
            String[] extraRet = new String[2];
            script = "_extraRet_[0] = myUneval(" + commands.get("value") + ");"
                    + "_extraRet_[1] = JOptionPane.showOptionDialog(null, _extraRet_[0].replaceAll(\"\\\"\", \"\"), \"Výstup\", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, [\"OK\", \"Přerušit\"], \"OK\");";
//                    + "_extraRet_[1] = JOptionPane.showConfirmDialog(null, _extraRet_[0].replaceAll(\"\\\"\", \"\"), \"Výstup\", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);";
            //+ "JOptionPane.showMessageDialog(null, _extraRet_[0], \"Výstup\", JOptionPane.INFORMATION_MESSAGE);";

            try {
                result.updatedVariables = doSymbolScript(variables, script, extraRet, false);
            } catch (ScriptException ex) {
                result.haltDebug = true;
            }
            if (!result.haltDebug && extraRet[1] != null && Integer.valueOf(extraRet[1]) != JOptionPane.CLOSED_OPTION && Integer.valueOf(
                    extraRet[1]) != JOptionPane.OK_OPTION) {
                result.haltDebug = true;
            }
            result.progressDesc = extraRet[0] + " →";
        }

        return -1;
    }

    private static int processSetProgAndUpVars(FunctionResult result,
            HashMap<String, String> commands, HashMap<String, String> variables)
    {
        String var = "";
        String[] splitBrackets = RegexFunctions.varBracketsInsides(commands.get("var"));
        for (int i = 0; i < splitBrackets.length; i++) {
            if (i % 2 == 0) {
                var += splitBrackets[i];
            } else {
                String[] extraRet = new String[1];
                try {
                    doSymbolScript(variables, "_extraRet_[0] = myUneval(" + splitBrackets[i] + ");",
                            extraRet, true);
                } catch (ScriptException ex) {
                    // won't happen since silent is on true as doSymbolScript call parameter
                    ex.printStackTrace(System.err);
                }
                var += extraRet[0];
            }
        }

        String script;
        if (!commands.get("var").contains("[")) {
            script = "var " + commands.get("var") + " = " + commands.get("value") + ";";
        } else {
            script = commands.get("var") + " = " + commands.get("value") + ";";
        }

        try {
            result.updatedVariables = doSymbolScript(variables, script, null, false);
        } catch (ScriptException ex) {
            result.haltDebug = true;
        }
        addVarToResultIfNotPresent(commands.get("var").split("\\[.*")[0], variables,
                result.updatedVariables);
        result.progressDesc = var + " ← " + getCompiledProgressDesc(variables, commands.get("value"));
        return -1;
    }

    /**
     * Metoda slouží k dodatečnému přiřazení proměnné do mapy změněných proměnných.
     * To proto, že i když se vlastní hodnota po přiřazení stejné hodnoty do
     * proměnné nezmění, měli bychom tuto proměnnou ztučnit, aby bylo zřejmé,
     * že se programově hodnota i tak přepsala. Kdybych proměnnou neztučnil, tedy
     * nepřidal ji do změněných, mohlo by se zdát, že počítač proměnné nepřepisuje.
     * <p/>
     * @param variable název proměnné, která se má dodatečně přidat
     * @param variables mapa stávajících proměnných
     * @param updatedVariables mapa změněných proměnných
     * @return byla-li či nebyla-li proměnná do mapy změněných proměnných dodána
     */
    private static boolean addVarToResultIfNotPresent(String variable,
            HashMap<String, String> variables, HashMap<String, String> updatedVariables)
    {
        if (updatedVariables.containsKey(variable) || !variables.containsKey(variable)) {
            return false;
        }
        updatedVariables.put(variable, variables.get(variable));
        return true;
    }

}
