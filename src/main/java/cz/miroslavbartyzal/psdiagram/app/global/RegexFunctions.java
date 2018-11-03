/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tato třída prezentuje sadu funkcí s řetězci, na které byli již regulární
 * výrazy krátké.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class RegexFunctions
{

    /**
     * <p>
     * Metoda rozdělí vstupní řetězec pomocí daného regulárního výrazu.
     * Rozdíl oproti běžné funkci String.split() je ten, že neztrácíme údaje,
     * které se rovnali danému regulárnímu výrazu.</p>
     * <p>
     * Výstup je formátován tak, že liché indexy jsou ty části řetězce, které
     * se rovnali regulárnímu výrazu, a sudé indexy odpovídají lomenému
     * textu.</p>
     *
     * @param str řetězec, která má být rozdělen
     * @param regex regulární výraz, pomocí něhož chceme řetězec rozdělit
     * @return Pole s rozdělěným řetezcem. Liché indexy jsou shody s regulárním
     * výrazem, sudé lomený text.
     */
    public static String[] splitString(String str, String regex)
    {
        // liche indexy jsou matche
        ArrayList<String> strSplit = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        int lastEndIndex = 0;
        if (matcher.find()) {
            do {
                strSplit.add(str.substring(lastEndIndex, matcher.start()));
                lastEndIndex = matcher.end();
                strSplit.add(str.substring(matcher.start(), lastEndIndex));
            } while (matcher.find());
            if (lastEndIndex < str.length()) {
                strSplit.add(str.substring(lastEndIndex, str.length()));
            }
        } else {
            strSplit.add(str);
        }
        return strSplit.toArray(new String[0]);
    }

    /**
     * Tato metoda je totožná s metodou splitString(String str, String regex) s
     * tím rozdílem, že ignoruje obsah textu ohraničený dvojitými uvozovkami.
     *
     * @param str řetězec, která má být rozdělen
     * @param regex regulární výraz, pomocí něhož chceme řetězec rozdělit
     * @return Pole s rozdělěným řetezcem. Liché indexy jsou shody s regulárním
     * výrazem, sudé lomený text.
     */
    public static String[] splitStringIgnoreQuotesInsides(String str, String regex)
    {
        // liche indexy jsou matche
        ArrayList<String> strSplit = new ArrayList<>();
        String[] withoutQ = splitString(str, "\"([^\"\\\\]|\\\\.)*\"?");
        String part = "";
        for (int i = 0; i < withoutQ.length; i++) {
            if (i % 2 == 1) {
                part += withoutQ[i];
            } else {
                String[] split = splitString(withoutQ[i], regex);
                for (int j = 0; j < split.length; j++) {
                    if (j % 2 == 0) {
                        part += split[j];
                    } else {
                        strSplit.add(part);
                        part = "";
                        strSplit.add(split[j]);
                    }
                }
            }
        }
        if (!part.equals("")) {
            strSplit.add(part);
        }
        return strSplit.toArray(new String[0]);
    }

    /**
     * <p>
     * Metoda vrátí vnitřky hranatých závorek z vstupního textu proměnné.</p>
     * <p>
     * Výstup je formátován tak, že na lichých indexech jsou vnitřky závorek
     * a sudé indexy představují zbytek řetězce.</p>
     *
     * @param variable vstupní text proměnné
     * @return Pole s rozdělěným řetezcem. Liché indexy jsou vnitřky závorek,
     * sudé lomený text.
     */
    public static String[] varBracketsInsides(String variable)
    {
        // vnitrek zavorek na lichych indexech
        ArrayList<String> strOut = new ArrayList<>();
        int brackets = 0;
        int lastEndIndex = 0;
        for (int i = 1; i <= variable.length(); i++) {
            switch (variable.substring(i - 1, i)) {
                case "[":
                    if (brackets == 0) {
                        strOut.add(variable.substring(lastEndIndex, i));
                        lastEndIndex = i;
                    }
                    brackets++;
                    break;
                case "]":
                    brackets--;
                    if (brackets == 0) {
                        strOut.add(variable.substring(lastEndIndex, i - 1));
                        lastEndIndex = i - 1;
                    }
                    break;
            }
        }
        if (lastEndIndex < variable.length()) {
            strOut.add(variable.substring(lastEndIndex, variable.length()));
        }
        return strOut.toArray(new String[0]);
    }

    /**
     * <p>
     * Metoda vrátí vnitřky libovolných závorek z vstupního textu proměnné.</p>
     * <p>
     * Výstup je formátován tak, že na lichých indexech jsou vnitřky závorek
     * a sudé indexy představují zbytek řetězce.</p>
     *
     * @param variable vstupní text proměnné
     * @param openingBracket volitelná otevírací závorka
     * @param closingingBracket volitelná zavírací závorka
     * @return Pole s rozdělěným řetezcem. Liché indexy jsou vnitřky závorek,
     * sudé lomený text.
     */
    public static String[] varBracketsInsides(String variable, String openingBracket,
            String closingingBracket)
    {
        // vnitrek zavorek na lichych indexech
        ArrayList<String> strOut = new ArrayList<>();
        int brackets = 0;
        int lastEndIndex = 0;
        for (int i = 1; i <= variable.length(); i++) {
            if (variable.substring(i - 1, i).equals(openingBracket)) {
                if (brackets == 0) {
                    strOut.add(variable.substring(lastEndIndex, i));
                    lastEndIndex = i;
                }
                brackets++;
            } else if (variable.substring(i - 1, i).equals(closingingBracket)) {
                brackets--;
                if (brackets == 0) {
                    strOut.add(variable.substring(lastEndIndex, i - 1));
                    lastEndIndex = i - 1;
                }
            }
        }
        if (lastEndIndex < variable.length()) {
            strOut.add(variable.substring(lastEndIndex, variable.length()));
        }
        return strOut.toArray(new String[0]);
    }

    /**
     * <p>
     * Metoda vrátí pole obsahující vnitřní elementy hranatých závorek,
     * oddělené
     * čárkami.</p>
     * <p>
     * Metoda prochází vstupní řetězec do doby než narazí na první otevírací
     * hranatou závorku ("[") a pokračuje ve skenování řetězce dokud nenarazí na
     * uzavírací znak té samé závorky ("]"). Metoda počítá s případným vnořením
     * závorek.</p>
     *
     * @param arrayVariable proměnná pole
     * @return pole obsahující vnitřní elementy hranatých závorek, oddělené
     * čárkami
     */
    public static String[] getBracketElements(String arrayVariable)
    {
        ArrayList<String> strOut = new ArrayList<>();
        int brackets = 0;
        int lastEndIndex = 0;
        boolean quote = false;
        for (int i = 1; i <= arrayVariable.length(); i++) {
            switch (arrayVariable.substring(i - 1, i)) {
                case "[":
                    if (!quote) {
                        if (brackets == 0) {
                            lastEndIndex = i;
                        }
                        brackets++;
                    }
                    break;
                case "]":
                    if (!quote) {
                        brackets--;
                        if (brackets == 0) {
                            strOut.add(arrayVariable.substring(lastEndIndex, i - 1).trim());
                            return strOut.toArray(new String[0]);
                        }
                    }
                    break;
                case ",":
                    if (!quote) {
                        if (brackets == 1) {
                            strOut.add(arrayVariable.substring(lastEndIndex, i - 1).trim());
                            lastEndIndex = i;
                        }
                    }
                    break;
                case "\"":
                    quote = !quote;
                    break;
            }
        }
        return strOut.toArray(new String[0]);
    }

    /**
     * <p>
     * Metoda rozdělí vstupní řetězec kolem případných metod.</p>
     * <p>
     * Výstup je formátován tak, že liché indexy jsou ty části řetězce, které
     * obsahují metody, a sudé indexy odpovídají lomenému textu.</p>
     * <p/>
     * @param value vstupní text, obsahující potencionální metody
     * @return Pole s rozdělěným řetezcem. Liché indexy jsou metody, sudé lomený
     * text.
     */
    public static String[] splitByMethods(String value)
    { // value musi byt jiz zbavena uvozovek
        // metody na lichych indexech
        String functionRegex = "([a-zA-Z\\_\\$][a-zA-Z0-9\\_\\$]*\\.)*[a-zA-Z\\_\\$][a-zA-Z0-9\\_\\$]*\\(.*\\)";
        ArrayList<String> strOut = new ArrayList<>();
        int brackets = 0;
        int lastEndIndex = 0;
        Matcher matcher = Pattern.compile(functionRegex).matcher(value);

        while (matcher.find()) {
            //int index = value.substring(lastEndIndex).replaceFirst(functionRegex, "←").indexOf("←");
            int index = lastEndIndex + matcher.start();
            strOut.add(value.substring(lastEndIndex, index));
            lastEndIndex = index;
            cyklus:
            for (int i = lastEndIndex + 1; i <= value.length(); i++) {
                switch (value.substring(i - 1, i)) {
                    case "(":
                        brackets++;
                        break;
                    case ")":
                        brackets--;
                        if (brackets == 0) {
                            strOut.add(value.substring(lastEndIndex, i));
                            lastEndIndex = i;
                            break cyklus;
                        }
                        break;
                }
            }
            matcher = Pattern.compile(functionRegex).matcher(value.substring(lastEndIndex));
        }
        if (lastEndIndex < value.length()) {
            strOut.add(value.substring(lastEndIndex, value.length()));
        }
        return strOut.toArray(new String[strOut.size()]);
    }

}
