/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.diagram.gui.symbolFunctionForms.documentFilters;

import cz.miroslavbartyzal.psdiagram.app.diagram.gui.MainWindow;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * <p>Tato abstraktní třída představuje obecný filtr textového pole pro editaci
 * funkce symbolu.</p>
 *
 * <p>Filtry jsou aplikovány za účelem zabránění nechtěnné syntaktické chyby z
 * uživatelovi strany. Měli by zpříjemnit a zároveň zpřesnit uživatelovu práci
 * při nastavování funkce symbolu.<br />
 * Vznikla-li by vložením/smazáním znaku syntaktická chyba, je tomuto vložení či
 * smazání znaku zabráněno. Uživatel je o této události spraven pomocí obecné
 * informační lišty v hlavním okně aplikace.</p>
 * <p/>
 * <p>Filtry jsou řešeny pomocí regulárních výrazů.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public abstract class AbstractFilter extends DocumentFilter
{

    /**
     * Nevznikla-li by vložením řetězce syntaktická chyba, je řetězec vložen.
     * Vložení by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž z vkládaného textu nahrazeny jednou
     * mezerou.
     */
    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String text,
            AttributeSet attr)
    {
        try {
            if (SettingsHolder.settings.isFunctionFilters()) {
                StringBuilder sb = new StringBuilder();
                sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
                text = text.replaceAll("\\s{2,}", " "); // aby nebranila jen dvojita mezera
                sb.insert(offset, text);
                if (!validMe(sb.toString())) {
                    MainWindow.setStatusText("Bylo zabráněno syntaktické chybě!", 1000);
                    return;
                }
            }
            fb.insertString(offset, text, attr);
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Nevznikla-li by smazáním řetězce syntaktická chyba, je řetězec smazán.
     * Smazání by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž po odstranění textu eliminovány.
     */
    @Override
    public void remove(FilterBypass fb, int i, int i1)
    {
        try {
            if (SettingsHolder.settings.isFunctionFilters()) {
                StringBuilder sb = new StringBuilder();
                sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.delete(i, i + i1);
                if (!validMe(sb.toString())) {
                    String nonSpaced = sb.toString().replaceAll("\\s{2,}", " ");
                    if (validMe(nonSpaced)) { // aby umazani nebranila jen dvojita mezera..
                        fb.replace(0, i, nonSpaced.substring(0, i), null);
                        fb.remove(i, fb.getDocument().getLength() - nonSpaced.length());
                    } else {
                        MainWindow.setStatusText("Bylo zabráněno syntaktické chybě!", 1000);
                    }
                    return;
                }
            }
            fb.remove(i, i1);
        } catch (BadLocationException ex) {
        }
    }

    /**
     * Nevznikla-li by nahrazením řetězce syntaktická chyba, je řetězec nahrazen.
     * Nahrazení by neměla bránit dvojitá mezera (která také neprojde filtrem),
     * všechny dvojité mezery jsou totiž po nahrazení textu eliminovány.
     */
    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
            AttributeSet attr)
    {
        try {
            if (SettingsHolder.settings.isFunctionFilters()) {
                StringBuilder sb = new StringBuilder();
                sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
                text = text.replaceAll("\\s{2,}", " "); // aby nebranila jen dvojita mezera
                sb.replace(offset, offset + length, text);
                if (!validMe(sb.toString())) {
                    String nonSpaced = sb.toString().replaceAll("\\s{2,}", " ");
                    if (validMe(nonSpaced)) { // aby umazani nebranila jen mezera na zacatku nebo na konci, dohromady tvorici dvojitou mezeru..
                        fb.replace(0, offset, nonSpaced.substring(0, offset), attr);
                        int pom = offset + length + nonSpaced.length() - fb.getDocument().getLength();
                        if (pom < offset) {
                            fb.replace(offset, length + offset - pom, "", attr);
                        } else {
                            fb.replace(offset, length, nonSpaced.substring(offset, pom), attr);
                        }
                    } else {
                        MainWindow.setStatusText("Bylo zabráněno syntaktické chybě!", 1000);
                    }
                    return;
                }
            }
            fb.replace(offset, length, text, attr);
        } catch (BadLocationException ex) {
        }
    }

    abstract boolean validMe(String text);

}
