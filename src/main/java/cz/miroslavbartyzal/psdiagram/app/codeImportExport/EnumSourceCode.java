/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.codeImportExport;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;

/**
 * Třída výčtového typu, která udává všechny podporované programovací jazyky,
 * pomocí níž je možný import/export z/do zdrojového kódu/vývojového diagramu.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public enum EnumSourceCode
{

    PASCAL
    {
        @Override
        public Flowchart<LayoutSegment, LayoutElement> getFlowchart(String code)
        {
            return Pascal.getFlowchart(code);
        }

        @Override
        public String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name)
        {
            return Pascal.getSourceCode(flowchart, name);
        }

        @Override
        public String getUniqueTextValue()
        {
            return "Pascal";
        }

        @Override
        public String getGuideText()
        {
            return "vložte jen část mezi Begin a End.";
        }
    },
	
	JAVA
    {
        @Override
        public Flowchart<LayoutSegment, LayoutElement> getFlowchart(String code)
        {
            return Java.getFlowchart(code);
        }

        @Override
        public String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart, String name)
        {
            return Java.getSourceCode(flowchart, name);
        }

        @Override
        public String getUniqueTextValue()
        {
            return "Java";
        }

        @Override
        public String getGuideText()
        {
            return "vlozte len telo jednej funckie !! od '{' po '}' !!";
        }
    };

    /**
     * Vrátí unikátní název programovacího jazyka. Používá se pro generování
     * JRadioButtonů.
     *
     * @return unikátní název programovacího jazyka
     */
    public abstract String getUniqueTextValue();

    /**
     * Vrátí případný doplňující text pro uživatele, týkající se funkce importu.
     *
     * @return případný doplňující text pro uživatele, týkající se funkce
     * importu
     */
    public abstract String getGuideText();

    /**
     * Metoda pro získání instance Flowchart, tedy vývojového diagramu, ze
     * zdrojového kódu na vstupu.
     *
     * @param code zdrojový kód, ze kterého má být vývojový diagram vygenerován
     * @return vygenerovaný vývojový diagram
     */
    public abstract Flowchart<LayoutSegment, LayoutElement> getFlowchart(String code);

    /**
     * Metoda pro získání zdrojového kódu z vývojového diagramu na vstupu. Tento
     * zdrojový kód je generován na základě vyplněných funkcí symbolů, proto by
     * vývojový diagram měl mít tuto část plně vyplněnu.
     *
     * @param flowchart vývojový diagram, ze kterého má být zdrojový kód
     * vygenerován
     * @param name název programu, který má vygenerovaný zdrojový kód nést.
     * @return vygenerovaný zdrojový kód
     */
    public abstract String getSourceCode(Flowchart<LayoutSegment, LayoutElement> flowchart,
            String name);

}
