/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.codeImportExport;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.FlowchartElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Comment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Decision;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
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
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.BooleanValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ConstantFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NoArrayVariableFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.NumericValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.ValueFilter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters.VariableFilter;
import cz.miroslavbartyzal.psdiagram.app.parser.FlowchartGenerator;
import cz.miroslavbartyzal.psdiagram.app.parser.SourceCodeGenerator;

import java.awt.HeadlessException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * Tato třída představuje podporu programovacího jazyka Pascal pro import/export
 * z/do zdrojového kódu/vývojového diagramu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Pascal implements FlowchartGenerator, SourceCodeGenerator
{

    // TODO nepozadovat jen cast mezi begin a end.
    // TODO více try, catch
    private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;
    private static final String COMMENT_MARKER = "--komentář--";
    private boolean errored = false;
    private boolean missingCommandWarning = false;

    /**
     * Metoda pro získání instance Flowchart, tedy vývojového diagramu, ze
     * zdrojového kódu na vstupu.
     *
     * @param code zdrojový kód, ze kterého má být vývojový diagram vygenerován
     * @return vygenerovaný vývojový diagram
     */
    public static Flowchart<LayoutSegment, LayoutElement> getFlowchart(String code)
    {
        Pascal instance = new Pascal();
        return instance.generateFlowchart(code);
    }
    
    private Flowchart<LayoutSegment, LayoutElement> generateFlowchart(String code)
    {
        errored = false;
        LayoutSegment actualSegment = new LayoutSegment(null);
        Flowchart<LayoutSegment, LayoutElement> flowchart = new Flowchart<>(actualSegment);

        LayoutElement lastElement = actualSegment.addSymbol(null, EnumSymbol.STARTEND.getInstance(
                "Začátek"));
        actualSegment.addSymbol(lastElement, EnumSymbol.STARTEND.getInstance("Konec"));

        generateFlowchart(parseCommands(code), actualSegment, actualSegment.getElement(0), false);
        if (errored) {
            return null;
        }
        return flowchart;
    }

    private void generateFlowchart(ArrayList<String> commands, LayoutSegment actualSegment,
            LayoutElement actualElement, boolean onlyOneCommand)
    {
        while (!commands.isEmpty() && !errored) {
            if (actualSegment.getParentElement() != null && commands.get(0).toLowerCase().startsWith(
                    "until") && !(actualElement.getSymbol() instanceof LoopEnd)) {
                return;
            }
            String command = commands.remove(0);
            try {
                LayoutElement actualNoCommentElement = actualElement; // BACHA NA ACTUALSYMBOL - MUZE TO BYT NECEKANE KOMENTAR! - ELSE a SWITCH
                while (actualNoCommentElement.getSymbol() instanceof Comment) {
                    int index = actualSegment.indexOfElement(actualNoCommentElement);
                    if (index == 0) {
                        actualNoCommentElement = actualSegment.getParentElement();
                    } else {
                        actualNoCommentElement = actualSegment.getElement(index - 1);
                    }
                }

                if (command.matches("^\\s*[^\\'\\:\\+\\-\\*\\/ ]+ *[\\:\\+\\-\\*\\/]\\=[\\S\\s]+")) {
                    // PROCESS
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.PROCESS.getInstance(""));
                    command = command.trim();

                    String var;
                    if (!command.matches("^[^\\:\\+\\-\\*\\/ ]+ *[\\:]\\=[\\S\\s]+")) {
                        // je treba pretransformovat += -= *= /=
                        Matcher matcher = Pattern.compile("^[^\\+\\-\\*\\/ ]+", FLAGS).matcher(
                                command);
                        matcher.find();
                        var = matcher.group();

                        if (command.matches("^[^\\:\\+\\-\\*\\/ ]+ *[\\+]\\=[\\S\\s]+")) {
                            command = command.replaceFirst("\\+\\=\\s*", ":=" + var + "+");
                        } else if (command.matches("^[^\\:\\+\\-\\*\\/ ]+ *[\\-]\\=[\\S\\s]+")) {
                            command = command.replaceFirst("\\-\\=\\s*", ":=" + var + "-");
                        } else if (command.matches("^[^\\:\\+\\-\\*\\/ ]+ *[\\*]\\=[\\S\\s]+")) {
                            command = command.replaceFirst("\\*\\=\\s*", ":=" + var + "*");
                        } else {
                            command = command.replaceFirst("\\/\\=\\s*", ":=" + var + "/");
                        }
                    } else {
                        var = command.substring(0, command.indexOf(":=")).trim();
                    }

                    String value = command.substring(command.indexOf(":=") + 2, command.length()).trim();

                    if (!VariableFilter.isValid(var) || !ValueFilter.isValid(value)) {
                        // udaje neprosli filtrem
                        actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                        actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement, command, 
                                true);
                        var = "";
                    }

                    if (!var.equals("")) {
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Process.generateValues(
                                actualElement.getSymbol(), var, value);
                        actualElement.getSymbol().setValueAndSize(
                                actualElement.getSymbol().getDefaultValue());
                    }
                } else if (Pattern.compile("^\\s*(read(ln)?|write(ln)?)\\s*\\([\\S\\s]+", FLAGS).matcher(
                        command).matches()) {
                    // IO
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.IO.getInstance(""));
                    command = command.trim();

                    if (command.toLowerCase().startsWith("read")) {
                        // Input
                        command = command.trim();
                        String var = command.substring(command.indexOf("(") + 1,
                                command.lastIndexOf(")")).trim();

                        if (!VariableFilter.isValid(var)) {
                            // udaje neprosli filtrem
                            actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement, command,
                                    true);
                            command = "";
                        }
                        if (!command.equals("")) {
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateIValues(
                                    actualElement.getSymbol(), var);
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                        }
                    } else {
                        // Output
                        command = command.replaceAll("\"\\s*\\,", "\"+").replaceAll("\\,\\s*\"",
                                "+\"").trim();
                        String value = command.substring(command.indexOf("(") + 1,
                                command.lastIndexOf(")")).trim();

                        if (!ValueFilter.isValid(value)) {
                            // udaje neprosli filtrem
                            actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement, command,
                                    true);
                            command = "";
                        }
                        if (!command.equals("")) {
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.IO.generateOValues(
                                    actualElement.getSymbol(), value);
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                        }
                    }
                } else if (command.toLowerCase().startsWith("for") && command.toLowerCase().endsWith(
                        "do")) {
                    // TODO myCommand = command.toLowerCase() je spatne, prichazim o case
                    // FOR
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.FOR.getInstance(""));
                    String myCommand = command.toLowerCase().replaceFirst("for\\s*", "").replaceFirst(
                            "\\s*do$", "");

                    if (myCommand.contains(":=")) {
                        String var = myCommand.substring(0, myCommand.indexOf(":=")).trim();
                        myCommand = myCommand.substring(myCommand.indexOf(":=") + 2).trim();
                        String to = myCommand.substring(myCommand.lastIndexOf("to ") + 3).trim();
                        myCommand = myCommand.substring(0, myCommand.lastIndexOf(to)).trim();
                        String from;
                        String inc;
                        if (myCommand.endsWith("downto")) {
                            inc = "-1";
                            from = myCommand.substring(0, myCommand.lastIndexOf("downto")).trim();
                        } else {
                            inc = "1";
                            from = myCommand.substring(0, myCommand.lastIndexOf("to")).trim();
                        }

                        if (!NoArrayVariableFilter.isValid(var) || !NumericValueFilter.isValid(
                                from) || !NumericValueFilter.isValid(to) || !NumericValueFilter.isValid(
                                        inc)) {
                            // udaje neprosli filtrem
                            actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            FlowchartGenerator.super.addComment(actualSegment, actualElement, command, true); // neni sance ze by bylo obsazeno, proto ani neupravuji actual element
                            command = "";
                        }

                        if (!command.equals("")) {
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForValues(
                                    actualElement.getSymbol(), var, from, to, inc);
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                        }
                    } else if (myCommand.contains("in ")) {
                        //for-each
                        String var = myCommand.substring(0, myCommand.indexOf("in ")).trim();
                        String array = myCommand.substring(myCommand.indexOf("in ") + 3).trim();

                        if (!NoArrayVariableFilter.isValid(var) || !VariableFilter.isValid(
                                array)) {
                            // udaje neprosli filtrem
                            actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            FlowchartGenerator.super.addComment(actualSegment, actualElement, command, true); // neni sance ze by bylo obsazeno, proto ani neupravuji actual element
                            command = "";
                        }

                        if (!command.equals("")) {
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.For.generateForeachValues(
                                    actualElement.getSymbol(), var, array);
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                        }
                    }

                    generateFlowchart(commands, actualElement.getInnerSegment(1), actualElement,
                            oneCommandOnly(commands));
                } else if (command.toLowerCase().equals("repeat") || (command.toLowerCase().startsWith(
                        "while") && command.toLowerCase().endsWith("do"))) {
                    // LOOP
                    String condition = "";
                    boolean oneCommandOnly = false; // repeat nemusi mit telo z begin - end;! ?:/

                    if (!command.toLowerCase().equals("repeat")) {
                        oneCommandOnly = oneCommandOnly(commands);
                        actualElement = actualSegment.addSymbol(actualElement,
                                EnumSymbol.LOOPCONDITIONUP.getInstance(""));
                        condition = command.toLowerCase().replaceFirst("while\\s*", "").replaceFirst(
                                "\\s*do$", "");
                        if (!BooleanValueFilter.isValid(condition)) {
                            // udaje neprosli filtrem
                            actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            FlowchartGenerator.super.addComment(actualSegment, actualElement, command, true); // neni sance ze by bylo obsazeno, proto ani neupravuji actual element
                            condition = "";
                        }
                    } else {
                        actualElement = actualSegment.addSymbol(actualElement,
                                EnumSymbol.LOOPCONDITIONDOWN.getInstance(""));
                    }

                    if (!condition.equals("")) {
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopStart.generateValues(
                                actualElement.getSymbol(), condition);
                        actualElement.getSymbol().setValueAndSize(
                                actualElement.getSymbol().getDefaultValue());
                    }

                    generateFlowchart(commands, actualElement.getInnerSegment(1), actualElement,
                            oneCommandOnly);

                    actualElement = actualSegment.addSymbol(actualElement, new LoopEnd());
                } else if (command.toLowerCase().startsWith("until") && actualNoCommentElement.getSymbol() instanceof LoopEnd) {
                    // LOOPDOWN
                    //command = command.substring(0, command.indexOf("until")+5) + " !(" + command.substring(command.indexOf("until")+5, command.length()).trim() + ")";
                    String condition = "!(" + command.toLowerCase().replaceFirst("until\\s*", "").trim() + ")";

                    if (!BooleanValueFilter.isValid(condition)) {
                        // udaje neprosli filtrem
                        actualNoCommentElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                        if (actualElement.equals(actualNoCommentElement)) {
                            actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement, condition,
                                    true);
                        } else {
                            // neni-li actualNoCommentElement actualElement, musim zachovat aktualni element aktualnim elementem (tento komentar ma byt co nejblize parent symbolu)
                            FlowchartGenerator.super.addComment(actualSegment, actualNoCommentElement, condition, true);
                        }
                        condition = "";
                    }

                    if (!condition.equals("")) {
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.LoopEnd.generateValues(
                                actualNoCommentElement.getSymbol(), condition);
                        actualNoCommentElement.getSymbol().setValueAndSize(
                                actualNoCommentElement.getSymbol().getDefaultValue());
                    }
                } else if (command.toLowerCase().startsWith("case") && command.toLowerCase().endsWith(
                        "of")) {
                    // SWITCH
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.SWITCH.getInstance(""), 1);
                    String conditionVar = command.toLowerCase().replaceFirst("case\\s*", "").replaceFirst(
                            "\\s*of$", "");

                    if (!VariableFilter.isValid(conditionVar)) {
                        // udaje neprosli filtrem
                        actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                        actualElement = addComment(actualSegment, actualElement, command, true);
                        conditionVar = "";
                    }

                    if (!conditionVar.equals("")) {
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch.generateValues(
                                actualElement,
                                conditionVar, new String[0]);
                        actualElement.getSymbol().setValueAndSize(
                                actualElement.getSymbol().getDefaultValue());
                    }
                } else if (command.toLowerCase().startsWith("if") && command.toLowerCase().endsWith(
                        "then")) {
                    // DECISION
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.DECISION.getInstance(""));
                    String condition = command.toLowerCase().replaceFirst("if\\s*", "").replaceFirst(
                            "\\s*then$", "");

                    if (!BooleanValueFilter.isValid(condition)) {
                        // udaje neprosli filtrem
                        actualElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                        FlowchartGenerator.super.addComment(actualSegment, actualElement, command, true); // neni sance ze by bylo obsazeno, proto ani neupravuji actual element
                        condition = "";
                    }

                    if (!condition.equals("")) {
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Decision.generateValues(
                                actualElement.getSymbol(), condition);
                        actualElement.getSymbol().setValueAndSize(
                                actualElement.getSymbol().getDefaultValue());
                    }

                    if (!commands.isEmpty() && !commands.get(0).toLowerCase().equals("else")) {
                        generateFlowchart(commands, actualElement.getInnerSegment(1), actualElement,
                                oneCommandOnly(commands));
                    }
                } else if (isComment(command)) {
                    // COMMENT
                    boolean paired = command.matches("[ \\t]*[^\\s][\\s\\S]+");

                    //                for (int i = 1; i < command.length(); i++) {
                    //                    if (command.substring(i-1, i).matches("\\s")) {
                    //                        if (command.substring(i-1, i).matches("\\n") || command.substring(i-1, i).matches("\\r")) {
                    //                            break;
                    //                        }
                    //                    } else {
                    //                        // komentar je na stejne radce jako prikaz - jedna se o parovy komentar
                    //                        paired = true;
                    //                        break;
                    //                    }
                    //                }
                    actualElement = addComment(actualSegment, actualElement, getCommentCommand(
                            command), paired);
                } else if (Pattern.compile("^\\s*goto [\\S\\s]+", FLAGS).matcher(command).matches() || Pattern.compile(
                        "^\\s*break[\\S\\s]*", FLAGS).matcher(command).matches() || Pattern.compile(
                                "^\\s*continue[\\S\\s]*", FLAGS).matcher(command).matches()) {
                    // GOTO, BREAK, CONTINUE
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.GOTO.getInstance(""));
                    command = command.trim();
                    switch (command.toLowerCase()) {
                        case "break":
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateBreakValues(
                                    actualElement.getSymbol());
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                            break;
                        case "continue":
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateContinueValues(
                                    actualElement.getSymbol());
                            actualElement.getSymbol().setValueAndSize(
                                    actualElement.getSymbol().getDefaultValue());
                            break;
                        default:
                            cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Goto.generateGotoValues(
                                    actualElement.getSymbol());
                            actualElement.getSymbol().setValueAndSize(command.substring(5));
                            break;
                    }
                } else if (command.matches("\\s*[^0-9\\W]\\w+\\s*\\:\\s*")) {
                    // GOTOLABEL
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.GOTOLABEL.getInstance(command.trim().replaceFirst(
                                            "\\s*\\:\\s*", "")));
                } else if (command.toLowerCase().matches("exit\\s*")) {
                    // STARTEND
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.STARTEND.getInstance("Konec"));
                } else if (command.toLowerCase().equals("else") && actualNoCommentElement.getSymbol().hasElseSegment()) {
                    // else vetev
                    generateFlowchart(commands, actualNoCommentElement.getInnerSegment(0),
                            actualNoCommentElement, oneCommandOnly(commands));
                } else if (!command.matches("\\s*[^0-9\\W]\\w+\\s*\\:\\s*") && command.matches(
                        "[\\s\\S]+\\:\\s*") && actualNoCommentElement.getSymbol() instanceof Switch) {
                    // switch vetev

                    actualNoCommentElement.addInnerSegment(new LayoutSegment(actualNoCommentElement));
                    command = command.trim();

                    String constant = checkConstantForRanges(command.replaceFirst("\\s*\\:$", ""));

                    if (!actualNoCommentElement.getSymbol().getValue().equals(INVALID_COMMAND)) {
                        if (!ConstantFilter.isValid(constant)) {
                            // udaje neprosli filtrem
                            actualNoCommentElement.getSymbol().setValueAndSize(INVALID_COMMAND);
                            if (actualElement.equals(actualNoCommentElement)) {
                                actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement, 
                                        command, true);
                            } else {
                                // neni-li actualNoCommentElement actualElement, musim zachovat aktualni element aktulanim elementem (tento komentar ma byt co nejblize parent symbolu)
                                FlowchartGenerator.super.addComment(actualSegment, actualNoCommentElement, command, 
                                        true);
                            }
                            constant = "";
                        }

                        String[] segmentConstants = new String[actualNoCommentElement.getInnerSegmentsCount() - 1]; // prvni segment nema default, je to else vetev
                        for (int i = 0; i < segmentConstants.length - 1; i++) {
                            segmentConstants[i] = actualNoCommentElement.getInnerSegment(i + 1).getDefaultDescription();
                            if (segmentConstants[i].equals("")) {
                                break;
                            }
                        }
                        segmentConstants[segmentConstants.length - 1] = constant;

                        // je treba generovat v kazdem pripade - kdyz prikaz neprosel, prirozene se timto smazou default udaje
                        cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.Switch.generateValues(
                                actualNoCommentElement,
                                actualNoCommentElement.getSymbol().getCommands().get("conditionVar"),
                                segmentConstants);
                        if (!constant.equals("")) {
                            // value napsat jen do aktualniho segmentu, v ostatnich uz je
                            LayoutSegment lastInnerSegment = actualNoCommentElement.getInnerSegment(
                                    actualNoCommentElement.getInnerSegmentsCount() - 1);
                            lastInnerSegment.setDescription(lastInnerSegment.getDefaultDescription());
                        } else {
                            for (int i = 0; i < segmentConstants.length - 1; i++) {
                                actualNoCommentElement.getInnerSegment(i + 1).setDescription(null);
                            }
                        }
                    }

                    generateFlowchart(commands, actualNoCommentElement.getInnerSegment(
                            actualNoCommentElement.getInnerSegmentsCount() - 1),
                            actualNoCommentElement, oneCommandOnly(commands));
                } else if (command.toLowerCase().matches("end\\s*") && actualSegment.getParentElement() != null) {
                    // konec vetve
                    if (!(actualNoCommentElement.getSymbol() instanceof Switch)
                            || actualSegment.getParentElement().equals(actualNoCommentElement)
                            || onlyOneCommand) {
                        return;
                    } else if (!commands.isEmpty()) {
                        // je to switch, nerovna se s parentem a onlyOneCommand je vypnuty
                        // jestli nasleduje nejaky end; za timto switchem, na nej uz musim reagovat..
                        for (int i = 0; i < commands.size(); i++) {
                            if (!isComment(commands.get(i))) {
                                if (commands.get(i).toLowerCase().matches("end\\s*")) {
                                    commands.remove(i);
                                    return;
                                } else {
                                    break;
                                }
                            } else {
                                actualElement = FlowchartGenerator.super.addComment(actualSegment, actualElement,
                                        getCommentCommand(commands.get(i)), commands.get(i).matches(
                                                "[ \\t]*[^\\s][\\s\\S]+"));
                                commands.remove(i);
                                i--;
                            }
                        }
                    }
                } else if (!command.toLowerCase().equals("begin") && !command.toLowerCase().matches(
                        "end\\s*")) {
                    // SUBROUTINE
                    actualElement = actualSegment.addSymbol(actualElement,
                            EnumSymbol.SUBROUTINE.getInstance(command.trim()));
                }
                if (onlyOneCommand
                        && (!(actualElement.getSymbol() instanceof LoopEnd) || commands.isEmpty() || !commands.get(
                                0).startsWith("until"))
                        && (!(actualElement.getSymbol() instanceof Decision) || actualElement.getSymbol() instanceof Switch || commands.isEmpty() || !commands.get(
                                0).equals("else"))
                        && ((!isComment(command) || !actualSegment.getParentElement().equals(
                                actualNoCommentElement)) && (commands.isEmpty() || !isComment(
                                commands.get(0))))
                        && !(actualElement.getSymbol() instanceof Switch) && (!(actualNoCommentElement.getSymbol() instanceof Switch) || actualSegment.getParentElement().equals(
                                actualNoCommentElement))) { // switch je jako jeden prikaz, nemuzu predcasne opustit
                    return;
                }
            } catch (Exception e) {
                if (!errored) {
                    MyJOptionPane.showMessageDialog(null,
                            "<html>Diagram se nepodařilo vytvořit!<br />Problémový prvek:<br />" + command + "</html>",
                            "Chyba při generování diagramu", JOptionPane.ERROR_MESSAGE);
                    errored = true;
                }
            }
        }
    }

//    // tuto metodu je treba pouzit pred volanim oneCommandOnly(..), protoze pred begin muzou byt komentare a ja bych tak nedostal spravny report
//    private static LayoutElement makeComentsRow(ArrayList<String> commands, LayoutElement onElement, LayoutSegment toSegment) {
//
//        return onElement;
//    }
    private boolean isComment(String command)
    {
        if (command.matches("^\\s*\\{[\\S\\s]+") || command.matches("^\\s*\\(\\*[\\S\\s]+") || command.matches(
                "^\\s*\\/\\/[\\S\\s]+")) {
            return true;
        }
        return false;
    }

    private String getCommentCommand(String command)
    {
        command = command.trim();
        if (command.startsWith("{")) {
            return command.substring(1, command.length() - 1);
        } else if (command.startsWith("(*")) {
            return command.substring(2, command.length() - 2);
        } else if (command.startsWith("//")) {
            return command.substring(2, command.length());
        }
        return null;
    }

    private boolean oneCommandOnly(ArrayList<String> commands)
    {
        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);
            if (!isComment(command)) {
                if (command.toLowerCase().equals("begin")) {
                    commands.remove(i);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    private String checkConstantForRanges(String constant)
    {
        String[] split = constant.split(",");
        String retConst = "";

        for (String cnst : split) {
            cnst = cnst.trim();
            if (cnst.matches("^[0-9]+\\.\\.[0-9]+$")) {
                int first = Integer.valueOf(cnst.substring(0, cnst.indexOf(".")));
                int second = Integer.valueOf(cnst.substring(cnst.indexOf(".") + 2, cnst.length()));
                if (first > second) {
                    // swap
                    int h = first;
                    first = second;
                    second = h;
                }
                cnst = "";
                for (int i = first; i <= second; i++) {
                    cnst += "," + i;
                }
                cnst = cnst.substring(1);
            } else if (cnst.matches("^\"[a-z]\"\\.\\.\"[a-z]\"$") || cnst.matches(
                    "^\"[A-Z]\"\\.\\.\"[A-Z]\"$") || cnst.matches("^\"[0-9]\"\\.\\.\"[0-9]\"$")) {
                char first = cnst.charAt(cnst.indexOf(".") - 2);
                char second = cnst.charAt(cnst.indexOf(".") + 3);
                if (first > second) {
                    // swap
                    char h = first;
                    first = second;
                    second = h;
                }
                cnst = "";
                for (char i = first; i <= second; i++) {
                    cnst += ",\"" + Character.toString(i) + "\"";
                }
                cnst = cnst.substring(1);
            }

            retConst += "," + cnst;
        }

        return retConst.substring(1);
    }

    private String checkForMultidimArray(String sourceCode)
    {
//        return var.replaceAll("\\,", "]["); - nemohu pouzit, zkonvertovali by se i carky oddelujici parametry metod

//        String[] bracketElements = RegexFunctions.getBracketElements(var);
//        if (bracketElements.length > 1) {
//            var = var.substring(0, var.indexOf("["));
//            for (int i = 0; i < bracketElements.length; i++) {
//                if (bracketElements[i].contains(",") && bracketElements[i].contains("[")) {
//                    bracketElements[i] = checkForMultidimArray(bracketElements[i]); // zpracujeme to i rekurzivne
//                }
//                var += "[" + bracketElements[i] + "]";
//            }
//        }
//        return var;
        // method(kol, arr[met((2),1),1]) -> method(kol, arr[met((2),1)][1])
        // arr[1,me(zu[0,1],5)] := 5; -> arr[1][me(zu[0][1],5)]
        ArrayDeque<Boolean> method = new ArrayDeque<>();
        int noMethodBracket = 0;

        for (int i = 1; i <= sourceCode.length(); i++) {
            switch (sourceCode.substring(i - 1, i)) {
                case "(": {
                    if (i > 1 && sourceCode.substring(i - 2, i - 1).matches("\\w|\\$")) {
                        // zacatek metody
                        method.add(true);
                    } else {
                        noMethodBracket++;
                    }
                    break;
                }
                case ")": {
                    if (noMethodBracket > 0) {
                        noMethodBracket--;
                    } else {
                        method.pollLast();
                    }
                    break;
                }
                case "[": {
                    method.add(false);
                    break;
                }
                case "]": {
                    method.pollLast();
                    break;
                }
                case ",": {
                    if (!method.isEmpty() && !method.peekLast()) {
                        sourceCode = sourceCode.substring(0, i - 1) + "][" + sourceCode.substring(i,
                                sourceCode.length());
                        i++;
                    }
                    break;
                }
            }
        }
        return sourceCode;
    }

    private ArrayList<String> parseCommands(String code)
    {
        ArrayDeque<String> quotes = new ArrayDeque<>();
        ArrayDeque<String> comments = new ArrayDeque<>();

        // zbaveni se uvozovek (klicova slova muzou byt i v nich)
        code = parseQuotes(code, quotes);

        // zbaveni se komentaru (klicova slova muzou byt i v nich)
        code = parseComments(code, comments);

        // odstraneni multimezer a tabulatoru
        code = code.replaceAll("\\t", " ");
        code = code.replaceAll(" {2,}", " ");

        // Pascal je case-insensitive, diagramy nikoliv
        // TODO pridano dodatecne, odstranit zbytecne volani toLowerCase() v nasledujicim parsovani
        code = code.toLowerCase();

        // prizpusobeni odlisnosti pascalu
        code = Pattern.compile("(?<![\\w])mod(?![\\w])", FLAGS).matcher(code).replaceAll("%");
        code = Pattern.compile("(?<![\\w])div(?![\\w])", FLAGS).matcher(code).replaceAll("//");
        code = Pattern.compile("(?<![\\w])not\\s+", FLAGS).matcher(code).replaceAll("!");
        code = code.replaceAll("\\<\\>", "!=");

        code = Pattern.compile("(?<![\\w])and(?![\\w])", FLAGS).matcher(code).replaceAll("&&");
        code = Pattern.compile("(?<![\\w])or(?![\\w])", FLAGS).matcher(code).replaceAll("||");
        code = checkForMultidimArray(code);

        ArrayList<String> commands = new ArrayList<>();

        // zalamovat pred a za "repeat" "else" "begin" a komentari
        Matcher matcher = Pattern.compile(
                "(?<![\\w])repeat(?![\\w])|(?<![\\w])begin(?![\\w])|(?<![\\w])else(?![\\w])|\\s*" + COMMENT_MARKER,
                FLAGS).matcher(code);
        int lastEndIndex = 0;
        if (matcher.find()) {
            do {
                commands.add(code.substring(lastEndIndex, matcher.start()));
                lastEndIndex = matcher.end();
                commands.add(code.substring(matcher.start(), lastEndIndex));
            } while (matcher.find());
            if (lastEndIndex < code.length()) {
                commands.add(code.substring(lastEndIndex, code.length()));
            }
        } else {
            commands.add(code);
        }
        String[] split = commands.toArray(new String[0]);
        commands.clear();

        // zalamovat pred "if" "end" "case" "for" "while" "until" "exit"
        for (int i = 0; i < split.length; i++) {
            matcher = Pattern.compile(
                    "(?<![\\w])if(?![\\w])|(?<![\\w])end(?![\\w])|(?<![\\w])case(?![\\w])|(?<![\\w])for(?![\\w])|(?<![\\w])while(?![\\w])|(?<![\\w])until(?![\\w])|(?<![\\w])exit(?![\\w])",
                    FLAGS).matcher(split[i]);
            lastEndIndex = 0;
            if (matcher.find()) {
                do {
                    commands.add(split[i].substring(lastEndIndex, matcher.start()));
                    lastEndIndex = matcher.start();
                } while (matcher.find());
                if (lastEndIndex < split[i].length()) {
                    commands.add(split[i].substring(lastEndIndex, split[i].length()));
                }
            } else {
                commands.add(split[i]);
            }
        }
        split = commands.toArray(new String[0]);
        commands.clear();

        // zalamovat za ";" "then" "of" "do" ": "(bez :=)(label:, case:)
        for (int i = 0; i < split.length; i++) {
            matcher = Pattern.compile(
                    "\\;|(?<![\\w])then(?![\\w])|(?<![\\w])of(?![\\w])|(?<![\\w])do(?![\\w])|\\:(?![\\=0-9])",
                    FLAGS).matcher(split[i]);
            lastEndIndex = 0;
            if (matcher.find()) {
                do {
                    if (matcher.group().equals(";")) {
                        commands.add(split[i].substring(lastEndIndex, matcher.end() - 1)); // odstraneni stredniku - uz nebude potreba
                    } else {
                        commands.add(split[i].substring(lastEndIndex, matcher.end()));
                    }
                    lastEndIndex = matcher.end();
                } while (matcher.find());
                if (lastEndIndex < split[i].length()) {
                    commands.add(split[i].substring(lastEndIndex, split[i].length()));
                }
            } else {
                commands.add(split[i]);
            }
        }

        // navraceni komentaru
        for (int i = 0; i < commands.size(); i++) {
            matcher = Pattern.compile(COMMENT_MARKER, FLAGS).matcher(commands.get(i));
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, comments.poll());
            }
            matcher.appendTail(sb);
            commands.set(i, sb.toString());
        }

        // navraceni uvozovek
        for (int i = 0; i < commands.size(); i++) {
            String cmd = commands.get(i);
            if (cmd.matches("\\s*")) {
                // odstraneni nepotrebnych prazdnych prikazu
                commands.remove(i);
                i--;
                continue;
            }
            matcher = Pattern.compile("\\'\\'", FLAGS).matcher(cmd);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, quotes.poll());
            }
            matcher.appendTail(sb);
            commands.set(i, sb.toString());
        }

        return commands;
    }

    private String parseQuotes(String code, ArrayDeque<String> arrayToSaveQuotes)
    {
//        Matcher matcher = Pattern.compile("\'[^\']*\'", FLAGS).matcher(code);
//        StringBuffer sb = new StringBuffer();
//        while (matcher.find()) {
//            arrayToSaveQuotes.add("\"" + matcher.group().substring(1, matcher.group().length() - 1) + "\"");
//            matcher.appendReplacement(sb, "''");
//        }
//        matcher.appendTail(sb);
//        return sb.toString();

        // nested jobs are too much for regex :(there is problem with quotes in comments)
        boolean lineStart = false;
        boolean bracketStarStart = false;
        boolean bracketStarEnd = false;

        boolean lineC = false;
        int bracketStarC = 0;
        int bracketC = 0;

        boolean quoteEnabled = false;
        boolean escapedQuoteAhead = false;

        String newCode = "";
        int lastEndIndex = 0;
        for (int i = 1; i <= code.length(); i++) {
            if (quoteEnabled && !code.substring(i - 1, i).equals("'")) {
                continue;
            }
            switch (code.substring(i - 1, i)) {
                case "'": {
                    if (!lineC && bracketStarC == 0 && bracketC == 0) {
                        if (quoteEnabled) {
                            if (!escapedQuoteAhead) {
                                if (i + 1 <= code.length() && code.substring(i, i + 1).equals("'")) {
                                    // nasledujici znak je take ', coz znamena ze je to escape sekvence ''
                                    escapedQuoteAhead = true;
                                    continue;
                                }
                            } else {
                                // ignoruji tento znak protoze je to druha cast escape sekvence
                                escapedQuoteAhead = false;
                                continue;
                            }

                            quoteEnabled = false;
                            String stringInsides = code.substring(lastEndIndex, i - 1);
                            // nahradit dvojite uvozovky v pascalovskych stringach za escapovane dvojite uvozovky; napr: 'slovo "ahoj" je pozdrav' -> 'slovo \"ahoj\" je pozdrav'
                            stringInsides = stringInsides.replaceAll("\"", "\\\\\\\\\\\"");
                            // nahradit dvojite jednouvozovky za jedno jednouvozovky - escape v PSD neni potreba
                            stringInsides = stringInsides.replaceAll("''", "'");
                            arrayToSaveQuotes.add("\"" + stringInsides + "\"");
                            lastEndIndex = i - 1;
                        } else {
                            quoteEnabled = true;
                            newCode += code.substring(lastEndIndex, i);
                            lastEndIndex = i;
                        }
                        lineStart = false;
                        bracketStarStart = false;
                        bracketStarEnd = false;
                    }
                    break;
                }
                case "/": {
                    if (lineStart) {
                        lineStart = false;
                        lineC = true;
                    } else if (bracketStarC == 0 && bracketC == 0 && !lineC) {
                        lineStart = true;
                    }
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "(": {
                    if (!lineC && bracketC == 0) {
                        bracketStarStart = true;
                    }
                    lineStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "*": {
                    if (bracketStarC > 0 && !bracketStarStart) {
                        bracketStarEnd = true;
                    } else if (bracketStarStart && bracketC == 0 && !lineC) {
                        bracketStarC++;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    break;
                }
                case ")": {
                    if (bracketStarEnd && bracketStarC > 0) {
                        bracketStarC--;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "{": {
                    if (!lineC && bracketStarC == 0) {
                        bracketC++;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "}": {
                    if (bracketC > 0) {
                        bracketC--;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                default: {
                    if (lineC && (code.substring(i - 1, i).matches("\\n") || code.substring(i - 1, i).matches(
                            "\\r"))) {
                        lineC = false;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
            }
        }
        if (lastEndIndex < code.length()) {
            newCode += code.substring(lastEndIndex, code.length());
        }
        return newCode;
    }

    private String parseComments(String code, ArrayDeque<String> arrayToSaveComments)
    {
        // nested jobs are too much for regex :(
        boolean lineStart = false;
        boolean bracketStarStart = false;
        boolean bracketStarEnd = false;

        boolean lineC = false;
        int bracketStarC = 0;
        int bracketC = 0;

        String newCode = "";
        int lastEndIndex = 0;
        for (int i = 1; i <= code.length(); i++) {
            switch (code.substring(i - 1, i)) {
                case "/": {
                    if (lineStart) {
                        lineStart = false;
                        lineC = true;
                        newCode += code.substring(lastEndIndex, i-2) + COMMENT_MARKER;
                        lastEndIndex = i - 2;
                    } else if (bracketStarC == 0 && bracketC == 0 && !lineC) {
                        lineStart = true;
                    }
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "(": {
                    if (!lineC && bracketC == 0) {
                        bracketStarStart = true;
                    }
                    lineStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "*": {
                    if (bracketStarC > 0 && !bracketStarStart) {
                        bracketStarEnd = true;
                    } else if (bracketStarStart && bracketC == 0 && !lineC) {
                        if (bracketStarC == 0) {
                            newCode += code.substring(lastEndIndex, i - 2) + COMMENT_MARKER;
                            lastEndIndex = i - 2;
                        }
                        bracketStarC++;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    break;
                }
                case ")": {
                    if (bracketStarEnd && bracketStarC > 0) {
                        bracketStarC--;
                        if (bracketStarC == 0) {
                            arrayToSaveComments.add(
                                    code.substring(lastEndIndex, i).replaceAll("\\$", "\\\\\\$"));
                            lastEndIndex = i;
                        }
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "{": {
                    if (!lineC && bracketStarC == 0) {
                        if (bracketC == 0) {
                            newCode += code.substring(lastEndIndex, i - 1) + COMMENT_MARKER;
                            lastEndIndex = i - 1;
                        }
                        bracketC++;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                case "}": {
                    if (bracketC > 0) {
                        bracketC--;
                        if (bracketC == 0) {
                            arrayToSaveComments.add(
                                    code.substring(lastEndIndex, i).replaceAll("\\$", "\\\\\\$"));
                            lastEndIndex = i;
                        }
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
                default: {
                    if (lineC && (code.substring(i - 1, i).matches("\\n") || code.substring(i - 1, i).matches(
                            "\\r"))) {
                        arrayToSaveComments.add(
                                code.substring(lastEndIndex, i - 1).replaceAll("\\$", "\\\\\\$"));
                        lineC = false;
                        lastEndIndex = i - 1;
                    }
                    lineStart = false;
                    bracketStarStart = false;
                    bracketStarEnd = false;
                    break;
                }
            }
        }
        if (lastEndIndex < code.length()) {
            newCode += code.substring(lastEndIndex);
        }
        return newCode;
    }

    //**************************************************************************
    //**********************************EXPORT**********************************
    //**************************************************************************
    /**
     * Metoda pro získání zdrojového kódu z vývojového diagramu na vstupu. Tento
     * zdrojový kód je generován na základě vyplněných funkcí symbolů, proto by
     * vývojový diagram měl mít tuto část plně vyplněnu. V případě nevyplněné
     * funkce symbolu je tato skutečnost ve výsledném kódu okomentována.
     *
     * @param flowchart vývojový diagram, ze kterého má být zdrojový kód
     * vygenerován
     * @param name název programu, který má vygenerovaný zdrojový kód nést.
     * @return vygenerovaný zdrojový kód
     */
    public static String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart,
            String name)
    {
        Pascal instance = new Pascal();
        return instance.generateSourceCode(flowchart, name);
    }
    
    private String generateSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart,
            String name)
    {
        errored = false;
        missingCommandWarning = false;
        // generovani hlavicky
        String sourceCode = "program " + normalizeAsVariable(name) + ";" + LINE_SEP + LINE_SEP;

        // vyhledani vsech pouzitych identifikatoru promennych
        TreeSet<String> vars = new TreeSet<>();
        TreeSet<String> arrayVars = new TreeSet<>();
        findAndSetVariables(flowchart, vars, arrayVars);
        if (!vars.isEmpty() || !arrayVars.isEmpty()) {
            sourceCode += "var // zde je nutne doplnit typy promennych (diagramy jsou netypove)";
            if (!vars.isEmpty()) {
                sourceCode += LINE_SEP + "\t";
                for (String var : vars) {
                    sourceCode += var + ",";
                }
                sourceCode = sourceCode.substring(0, sourceCode.length() - 1) + ": {typ_promenne};";
            }

            if (!arrayVars.isEmpty()) {
                sourceCode += LINE_SEP + "\t";
                for (String arrayVar : arrayVars) {
                    sourceCode += arrayVar + ": array[{x}..{y}] of {typ_promenne};" + LINE_SEP + "\t";
                }
                sourceCode = sourceCode.substring(0,
                        sourceCode.length() - (LINE_SEP + "\t").length());
            }

            sourceCode += LINE_SEP;
        }

        sourceCode += generateSourceCode(flowchart.getMainSegment(), "");

        if (errored) {
            sourceCode = null;
        } else if (missingCommandWarning) {
            MyJOptionPane.showMessageDialog(null,
                    "<html>Zdrojový kód byl vygenerován s následujícím upozorněním:<br />Některý symbol nemá vyplněnu svou funkci!</html>",
                    "Nevyplněná funkce symbolu", JOptionPane.WARNING_MESSAGE);
        }

        return sourceCode;
    }

    private void findAndSetVariables(Flowchart<LayoutSegment, LayoutElement> flowchart,
            TreeSet<String> vars, TreeSet<String> arrayVars)
    {
        for (LayoutSegment segment : flowchart) {
            if (segment != null) {
                for (FlowchartElement element : segment) {
                    Symbol symbol = element.getSymbol();
                    if (symbol.getCommands() != null) {
                        if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process 
                                || (symbol instanceof IO && symbol.getCommands().containsKey("var"))) {
                            if (symbol.getCommands().get("var").contains("[")) {
                                String var = symbol.getCommands().get("var");
                                arrayVars.add(var.substring(0, var.indexOf("[")));
                            } else if (symbol.getCommands().containsKey("value") && symbol.getCommands().get(
                                    "value").startsWith("[")) {
                                arrayVars.add(symbol.getCommands().get("var"));
                            } else {
                                vars.add(symbol.getCommands().get("var"));
                            }
                        } else if (symbol instanceof For) {
                            vars.add(symbol.getCommands().get("var"));
                            if (symbol.getCommands().containsKey("array")) {
                                arrayVars.add(symbol.getCommands().get("array"));
                            }
                        }
                    }
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

                // TODO u GOTO a GOTOLABEL zavest take filtr - noarrayvariable
                if (symbol instanceof cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.Process) {
                    if (symbol.getCommands() != null) {
                        sourceCode += convertCodeToPascal(
                                symbol.getCommands().get("var") + " := " + symbol.getCommands().get(
                                        "value") + maybeInsSemicolon(segment, index));
                    } else {
                        sourceCode += "{symbol Zpracování bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof IO) {
                    if (symbol.getCommands() != null) {
                        if (symbol.getCommands().containsKey("var")) {
                            sourceCode += convertCodeToPascal("readln(" + symbol.getCommands().get(
                                    "var") + ")" + maybeInsSemicolon(segment, index));
                        } else {
                            sourceCode += convertCodeToPascal("writeln(" + symbol.getCommands().get(
                                    "value").replaceAll("\"\\s*\\+", "\",").replaceAll("\\+\\s*\"",
                                            ",\"") + ")" + maybeInsSemicolon(segment, index));
                        }
                    } else {
                        sourceCode += "{symbol Vstup/Vystup bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof Switch) {
                    if (symbol.getCommands() != null) {
                        sourceCode += convertCodeToPascal("case " + symbol.getCommands().get(
                                "conditionVar") + " of");
                    } else {
                        sourceCode += "case {nevyplnena funkce!} of";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    for (int i = 1; i < element.getInnerSegmentsCount(); i++) {
                        if (symbol.getCommands() != null) {
                            sourceCode += LINE_SEP + tabsDepth + "\t" + convertCodeToPascal(
                                    symbol.getCommands().get(String.valueOf(i)) + ":");
                        } else {
                            sourceCode += LINE_SEP + tabsDepth + "\t{nevyplnena funkce vetve Switch symbolu!}:";
                        }
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "begin";
                        sourceCode += generateSourceCode(element.getInnerSegment(i),
                                tabsDepth + "\t\t");
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "end";
                    }
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "else";
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "begin";
                        sourceCode += generateSourceCode(element.getInnerSegment(0),
                                tabsDepth + "\t\t");
                        sourceCode += LINE_SEP + tabsDepth + "\t" + "end";
                    }
                    sourceCode += LINE_SEP + tabsDepth + "end" + maybeInsSemicolon(segment, index);
                } else if (symbol instanceof Decision) {
                    if (symbol.getCommands() != null) {
                        sourceCode += convertCodeToPascal("if " + symbol.getCommands().get(
                                "condition") + " then");
                    } else {
                        sourceCode += "if {nevyplnena funkce!} then";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    sourceCode += LINE_SEP + tabsDepth + "begin";
                    sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t");
                    sourceCode += LINE_SEP + tabsDepth + "end";
                    if (containsFunctionalSymbols(element.getInnerSegment(0), -1)) {
                        sourceCode += " else";
                        if (isElseIf(element.getInnerSegment(0))) {
                            sourceCode += " " + generateSourceCode(element.getInnerSegment(0),
                                    tabsDepth);
                        } else {
                            sourceCode += LINE_SEP + tabsDepth + "begin";
                            sourceCode += generateSourceCode(element.getInnerSegment(0),
                                    tabsDepth + "\t");
                            sourceCode += LINE_SEP + tabsDepth + "end";
                        }
                    }
                    sourceCode += maybeInsSemicolon(segment, index);
                } else if (symbol instanceof For) {
                    if (symbol.getCommands() != null) {
                        if (symbol.getCommands().containsKey("inc")) {
                            int increment = Integer.valueOf(symbol.getCommands().get("inc"));
                            if (increment != 1 && increment != -1) {
                                MyJOptionPane.showMessageDialog(null,
                                        "<html>Zdrojový kód nelze vytvořit, protože programovací jazyk Pascal<br />nepodporuje u For cyklu jiný inkrement než 1 nebo -1!</html>",
                                        "Chyba při generování zdrojového kódu",
                                        JOptionPane.ERROR_MESSAGE);
                                errored = true;
                            }
                            if (increment == 1) {
                                sourceCode += convertCodeToPascal("for " + symbol.getCommands().get(
                                        "var") + " := " + symbol.getCommands().get("from") + " to " + symbol.getCommands().get(
                                                "to") + " do");
                            } else {
                                sourceCode += convertCodeToPascal("for " + symbol.getCommands().get(
                                        "var") + " := " + symbol.getCommands().get("from") + " downto " + symbol.getCommands().get(
                                                "to") + " do");
                            }
                        } else {
                            sourceCode += convertCodeToPascal("for " + symbol.getCommands().get(
                                    "var") + " in " + symbol.getCommands().get("array") + " do");
                        }
                    } else {
                        sourceCode += "for {nevyplnena funkce!} do";
                        missingCommandWarning = true;
                    }
                    if (pairedCommentText != null) {
                        sourceCode += pairedCommentText; // prirazeni paroveho komentare
                        pairedCommentText = null;
                    }
                    sourceCode += LINE_SEP + tabsDepth + "begin";
                    sourceCode += generateSourceCode(element.getInnerSegment(1), tabsDepth + "\t");
                    sourceCode += LINE_SEP + tabsDepth + "end" + maybeInsSemicolon(segment, index);
                } else if (symbol instanceof LoopStart) {
                    if (symbol.isOverHang()) {
                        // while
                        if (symbol.getCommands() != null) {
                            sourceCode += convertCodeToPascal("while " + symbol.getCommands().get(
                                    "condition") + " do");
                        } else {
                            sourceCode += "while {nevyplnena funkce!} do";
                            missingCommandWarning = true;
                        }
                        if (pairedCommentText != null) {
                            sourceCode += pairedCommentText; // prirazeni paroveho komentare
                            pairedCommentText = null;
                        }
                        sourceCode += LINE_SEP + tabsDepth + "begin";
                        sourceCode += generateSourceCode(element.getInnerSegment(1),
                                tabsDepth + "\t");
                        sourceCode += LINE_SEP + tabsDepth + "end" + maybeInsSemicolon(segment,
                                index);
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
                            sourceCode += "repeat";
                            if (pairedCommentText != null) {
                                sourceCode += pairedCommentText; // prirazeni paroveho komentare
                                pairedCommentText = null;
                            }
                            sourceCode += generateSourceCode(element.getInnerSegment(1),
                                    tabsDepth + "\t");
                            sourceCode += LINE_SEP + tabsDepth + convertCodeToPascal(
                                    "until " + getNegatedCondition(condition) + maybeInsSemicolon(
                                            segment, index));
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
                                sourceCode += "break" + maybeInsSemicolon(segment, index);
                                break;
                            case "continue":
                                sourceCode += "continue" + maybeInsSemicolon(segment, index);
                                break;
                            case "goto":
                                if (symbol.getValue() != null && !symbol.getValue().equals("")) {
                                    sourceCode += "goto " + symbol.getValue() + maybeInsSemicolon(
                                            segment, index);
                                } else {
                                    sourceCode += "goto {nevyplnena funkce!}" + maybeInsSemicolon(
                                            segment, index);
                                    missingCommandWarning = true;
                                }
                                break;
                        }
                    } else {
                        sourceCode += "{symbol Spojka-break/continue/goto bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof GotoLabel) {
                    if (symbol.getValue() != null && !symbol.getValue().equals("")) {
                        sourceCode += symbol.getValue() + ":";
                    } else {
                        sourceCode += "{symbol Spojka-navesti bez vyplnene funkce!}:";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof SubRoutine) {
                    if (symbol.getValue() != null && !symbol.getValue().equals("")) {
                        sourceCode += convertCodeToPascal(symbol.getValue()) + maybeInsSemicolon(
                                segment, index);
                    } else {
                        sourceCode += "{symbol Předdefinované zpracování bez vyplnene funkce!}";
                        missingCommandWarning = true;
                    }
                } else if (symbol instanceof StartEnd) {
                    if (segment.getParentElement() == null && (index == 0 || (index == 1 && segment.getElement(
                            0).getSymbol() instanceof Comment))) {
                        // prvni begin
                        sourceCode += "begin";
                        tabsDepth = "\t";
                    } else if (segment.getParentElement() == null && index == segment.size() - 1) {
                        //posledni symbol - end.
                        sourceCode = sourceCode.substring(0,
                                sourceCode.length() - tabsDepth.length()) + "end.";
                    } else {
                        sourceCode += "exit" + maybeInsSemicolon(segment, index);
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
                MyJOptionPane.showMessageDialog(null,
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

    private String getNegatedCondition(String condition)
    {
        condition = condition.trim();

        // v pripade ze cely vyrok je jiz znegovany, jen odstranim negaci
        if (condition.startsWith("!")) {
            if (condition.startsWith("!(")) {
                int brackets = 1;
                loop:
                for (int i = 3; i <= condition.length(); i++) {
                    switch (condition.substring(i - 1, i)) {
                        case "(":
                            brackets++;
                            break;
                        case ")":
                            brackets--;
                            if (brackets == 0) {
                                if (i == condition.length()) {
                                    // pro negaci staci odstranit negaci puvodni
                                    return condition.substring(2, condition.length() - 1);
                                }
                                break loop;
                            }
                            break;
                    }
                }
            } else if (!condition.contains(" ")) {
                // pro negaci staci odstranit negaci puvodni
                return condition.substring(1);
            }
        }
        return "!(" + condition + ")";
    }

    private String maybeInsSemicolon(LayoutSegment segment, int actualElementIndex)
    {
        if (containsFunctionalSymbols(segment, actualElementIndex)) {
            return ";";
        } else {
            return "";
        }
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
        commentText = commentText.replaceAll("\r?\n", LINE_SEP + tabsDepth + "\t");
        if (commentText.contains("\n") || commentText.contains("\r")) {
            return "{" + commentText + "}";
        } else {
            return "//" + commentText;
        }
    }

    // + u stringu nahradit ,
    private String convertCodeToPascal(String code)
    {
        code = code.replaceAll("\\]\\[", ","); // multidim. pole
        code = code.replaceAll("\\\\?\\'", "''") // v pascalu je nutne escapovat znak ' (v jave se escapuje volitelne)
                .replaceAll("(?<!\\\\)\"", "'") // vsechny neescapovane dvojite uvozovky zmen na jednoduchou uvozovku
                .replaceAll("\\\\\"", "\""); // vsechny escapovane dvojite uvozovky odescapuj - v pascalu je netreba escapovat
        code = code.replaceAll("\\!\\=", "<>");
        code = code.replaceAll("\\s*\\!\\s*", " not ");
        code = code.replaceAll("\\s*\\%\\s*", " mod ");
        code = code.replaceAll("\\s*\\/\\/\\s*", " div ");
        code = code.replaceAll("\\s*\\&\\&\\s*", " and ");
        code = code.replaceAll("\\s*\\|\\|\\s*", " or ");
        return code;
    }

}
