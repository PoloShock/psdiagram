/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.global.StringFunctions;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.MaxBalloonSizeCallback;
import cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip.PSDBalloonToolTip;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.AbstractSymbolFunctionForm;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.BadSyntaxJTextFieldBorder;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.SquiggleHighlighter;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.UnknownSyntaxJTextFieldBorder;
import cz.miroslavbartyzal.psdiagram.app.gui.symbolFunctionForms.ValidationListener;
import cz.miroslavbartyzal.psdiagram.app.parser.EnumRule;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParseError;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParseResult;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParser;
import cz.miroslavbartyzal.psdiagram.app.parser.PSDParserListener;
import cz.miroslavbartyzal.psdiagram.app.parser.antlr.ANTLRParser;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * <p>
 * Tato abstraktní třída představuje obecný filtr textového pole pro editaci
 * funkce symbolu.</p>
 *
 * <p>
 * Filtry jsou aplikovány za účelem zabránění nechtěnné syntaktické chyby z
 * uživatelovi strany. Měli by zpříjemnit a zároveň zpřesnit uživatelovu práci
 * při nastavování funkce symbolu.<br />
 * Vznikla-li by vložením/smazáním znaku syntaktická chyba, je tomuto vložení či
 * smazání znaku zabráněno. Uživatel je o této události spraven pomocí obecné
 * informační lišty v hlavním okně aplikace.</p>
 * <p/>
 * <p>
 * Filtry jsou řešeny pomocí gramatiky.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public abstract class AbstractFilter extends DocumentFilter
{

    private static final int MAX_CHARS = 800;
    private final PSDBalloonToolTip balloonToolTip;
    private final JTextField parentJTextField;
    private final ValidationListener validationListener;
    private final Border validBorder;
    private final Border errorBorder;
    private final UnknownSyntaxJTextFieldBorder unknownSyntaxBorder;
    private Border currentBorder;
    private final List<Object> highlighterTags = new ArrayList<>();
    private final PSDParserListener syntaxErrorListener = new SyntaxErrorListener();
//    private final PSDParser PARSER = new ParboiledParser();
    private final PSDParser PARSER = new ANTLRParser();

    public AbstractFilter(JTextField parentJTextField, ValidationListener validationListener,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        this.parentJTextField = parentJTextField;
        this.validationListener = validationListener;

        validBorder = parentJTextField.getBorder();
        currentBorder = validBorder;
        errorBorder = new BadSyntaxJTextFieldBorder((AbstractBorder) validBorder);
        unknownSyntaxBorder = new UnknownSyntaxJTextFieldBorder((AbstractBorder) validBorder);

        balloonToolTip = new PSDBalloonToolTip(parentJTextField, maxBalloonSizeCallback);
    }

    protected static boolean parseInput(String input, EnumRule rule)
    {
        if (input.matches("^\\s*$")) {
            return true;
        }

        return rule.parse(new ANTLRParser(), input);
    }

    public void parseInputAndUpdateGUI()
    {
        parseInputAndUpdateGUI(parentJTextField.getText());
    }

    protected void parseInputAndUpdateGUI(String input)
    {
        discardParseErrorInfos();

        if (!SettingsHolder.settings.isFunctionFilters()) {
            // when filters are off, input has to be converted from javascript to PS Diagram's equivalent
            input = AbstractSymbolFunctionForm.convertFromJSToPSDCommands(input);
        }
        boolean isEmpty = input.matches("^\\s*$");
        if (!isEmpty) {
            getRule().parseReportErrors(PARSER, input, syntaxErrorListener);
        } else {
            PARSER.stopParsing();
            syntaxErrorListener.onValidationComplete(true);
        }
    }

    private class SyntaxErrorListener implements PSDParserListener
    {

        @Override
        public void onValidationComplete(boolean isValid)
        {
            if (isValid) {
                refreshSyntaxInfo(isValid, false);
            } else {
                refreshSyntaxInfo(isValid, true);
            }
        }

        @Override
        public void onRecoveryFinished(PSDParseResult parseResult)
        {
            refreshSyntaxInfo(false, false);
            if (SettingsHolder.settings.isFunctionFilters()) {
                underlineParseErrors(parseResult);
                showParseErrorsAsBalloonInfo(parseResult);
            }
        }

    }

    /**
     * Nevznikla-li by vložením řetězce syntaktická chyba, je řetězec vložen.
     * Vložení by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž z vkládaného textu nahrazeny jednou
     * mezerou.
     * <p>
     * @param text
     */
    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text,
            AttributeSet attr)
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offset, text);
            if (SettingsHolder.settings.isFunctionFilters()) {
                if (sb.length() > MAX_CHARS) {
                    balloonToolTip.showTemporaryMessage(
                            "Bylo zabráněno překročení maximální délky příkazu.", 2000);
                    return;
                }
            }
            parseInputAndUpdateGUI(sb.toString());

            fb.insertString(offset, text, attr);
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Nevznikla-li by smazáním řetězce syntaktická chyba, je řetězec smazán.
     * Smazání by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž po odstranění textu eliminovány.
     * <p>
     * @param length
     * @param offset
     */
    @Override
    public void remove(FilterBypass fb, int offset, int length)
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.delete(offset, offset + length);
            parseInputAndUpdateGUI(sb.toString());

            fb.remove(offset, length);
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Nevznikla-li by nahrazením řetězce syntaktická chyba, je řetězec nahrazen.
     * Nahrazení by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž po nahrazení textu eliminovány.
     * <p>
     * @param attrs
     */
    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs)
    {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.replace(offset, offset + length, text);
            if (SettingsHolder.settings.isFunctionFilters()) {
                if (sb.length() > MAX_CHARS) {
                    balloonToolTip.showTemporaryMessage(
                            "Bylo zabráněno překročení maximální délky příkazu.", 2000);
                    return;
                }
            }
            parseInputAndUpdateGUI(sb.toString());

            fb.replace(offset, length, text, attrs);
        } catch (BadLocationException ex) {
        }
    }

    protected final void refreshSyntaxInfo(boolean isValid, boolean parsingInProgress)
    {
        if (SettingsHolder.settings.isFunctionFilters()) {
            if (isValid) {
                if (!parsingInProgress) {
                    if (currentBorder != validBorder) {
                        parentJTextField.setBorder(validBorder);
                        currentBorder = validBorder;
                    }
                } else {
                    if (currentBorder != unknownSyntaxBorder || unknownSyntaxBorder.getDefaultBorder() != validBorder) {
                        unknownSyntaxBorder.setDefaultBorder((AbstractBorder) validBorder,
                                parentJTextField, true);
                        parentJTextField.setBorder(unknownSyntaxBorder);
                        currentBorder = unknownSyntaxBorder;
                    } else {
                        unknownSyntaxBorder.initFadeIn();
                    }
                }
            } else {
                if (!parsingInProgress) {
                    if (currentBorder != errorBorder) {
                        parentJTextField.setBorder(errorBorder);
                        currentBorder = errorBorder;
                    }
                } else if (currentBorder != unknownSyntaxBorder || unknownSyntaxBorder.getDefaultBorder() != errorBorder) {
                    unknownSyntaxBorder.setDefaultBorder((AbstractBorder) errorBorder,
                            parentJTextField,
                            false);
                    parentJTextField.setBorder(unknownSyntaxBorder);
                    currentBorder = unknownSyntaxBorder;
                }
            }
        }

        // info o validite je potreba ukladat i v pripade, ze zrovna nepouzivam filtry kvuli undo/redo akcim (co kdyz by funkce byly vypnuty, nekdo dal undo, zapnul je a pak redo?...)
        if (!parsingInProgress) {
            Boolean commandValidProperty = (Boolean) parentJTextField.getDocument().getProperty(
                    "commandValid");
            if (commandValidProperty == null || commandValidProperty != isValid) {
                parentJTextField.getDocument().putProperty("commandValid", isValid);
                validationListener.validationStateChanged();
            }
        }
    }

    private void underlineParseErrors(PSDParseResult parseResult)
    {
        if (!highlighterTags.isEmpty()) {
            for (Object highlighterTag : highlighterTags) {
                parentJTextField.getHighlighter().removeHighlight(highlighterTag);
            }
            highlighterTags.clear();
        }
        if (parseResult != null) {
            for (PSDParseError errorInfo : parseResult.getParseErrors()) {
                if (errorInfo.getStartIndex() >= 0 && errorInfo.getEndIndex() >= errorInfo.getStartIndex()) {
                    try {
                        SquiggleHighlighter squiggler = new SquiggleHighlighter(Color.RED,
                                errorInfo.getStartIndex(), errorInfo.getEndIndex());
                        highlighterTags.add(parentJTextField.getHighlighter().addHighlight(
                                squiggler.getP0(), squiggler.getP1(), squiggler));
                    } catch (BadLocationException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }

    }

    private void showParseErrorsAsBalloonInfo(PSDParseResult parseResult)
    {
        balloonToolTip.dismiss();
        if (parseResult == null || parseResult.isInputValid()) {
            balloonToolTip.wipeMessage();
            return;
        }

        List<String> messagesAndCommands = new ArrayList<>();
        for (Iterator<PSDParseError> it = parseResult.getParseErrors().iterator(); it.hasNext();) {
            PSDParseError errorInfo = it.next();
            String message = "<html>";
            if (errorInfo.getErrorMessage() == null || errorInfo.getErrorMessage().length() == 0) {
                message += "Detekována syntaktická chyba!";
            } else {
                message += StringFunctions.escapeHTML(errorInfo.getErrorMessage())
                        //.replaceAll(" ", "&nbsp;") <- don't use &nbsp; because it disables multiline handling when string is too long to fit container
                        .replaceAll("\\n", "<br/>");
            }
            String command = parseResult.getInput();
            message += "<br/>&gt;&nbsp;&nbsp;";
            if (errorInfo.getStartIndex() >= 0 && errorInfo.getEndIndex() >= errorInfo.getStartIndex()) {
                message += StringFunctions.escapeHTML(
                        command.substring(0, errorInfo.getStartIndex()));
                message += "<span style=\"color: #ff0000; font-weight: bold;";
                if (errorInfo.getStartIndex() == errorInfo.getEndIndex()) {
                    message += " font-family: Arial; font-size: 18px;\">\u02F0"; // use Arial in order to make it squeeze better in between the surrounding characters
                } else {
                    message += "\">" + StringFunctions.escapeHTML(command.substring(
                            errorInfo.getStartIndex(), errorInfo.getEndIndex()));
                }
                message += "</span>";
                message += StringFunctions.escapeHTML(command.substring(errorInfo.getEndIndex()));
            } else {
                message += StringFunctions.escapeHTML(command);
            }
            if (it.hasNext()) {
                message += "<br/><br/>"; // make some space between messages
            }
            message += "</html>";
            messagesAndCommands.add(message);
        }

        balloonToolTip.showMessages(messagesAndCommands, -1);
    }

    private void discardParseErrorInfos()
    {
        refreshSyntaxInfo(true, true);
        underlineParseErrors(null);
        balloonToolTip.wipeMessage();
    }

    abstract EnumRule getRule();

}
