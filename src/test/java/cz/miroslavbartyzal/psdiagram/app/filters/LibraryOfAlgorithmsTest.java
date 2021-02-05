/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.filters;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.flowchart.symbols.EnumSymbol;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.ExamplesLoader;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import cz.miroslavbartyzal.psdiagram.app.persistence.FlowchartSaveContainer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import jakarta.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class LibraryOfAlgorithmsTest
{

    @Test
    public void libraryTest() throws JAXBException, FileNotFoundException
    {
        ArrayList<File> libraryItems = ExamplesLoader.loadFiles(new File(
                SettingsHolder.MY_WORKING_DIR, "src/main/resources/intozip/examples"));
        Assert.assertTrue("Library of algorithms appears to be empty.", !libraryItems.isEmpty());
        for (File libraryItem : libraryItems) {
            if (!libraryItem.isDirectory()) {
                Flowchart<LayoutSegment, LayoutElement> flowchart;
                if (libraryItem.getName().endsWith(".xml")) {
                    flowchart = GlobalFunctions.unsafeCast(
                            MainWindow.unmarshal(libraryItem));
                } else {
                    FlowchartSaveContainer flowchartSaveContainer = GlobalFunctions.unsafeCast(
                            MainWindow.unmarshal(libraryItem));
                    flowchart = flowchartSaveContainer.flowchart;
                }
                for (LayoutSegment segment : flowchart) {
                    if (segment != null) {
                        for (LayoutElement element : segment) {
                            Assert.assertTrue(
                                    "Symbol from Library of Algorithms has non-valid command(s)!",
                                    EnumSymbol.getEnumSymbol(element.getSymbol().getClass()).areCommandsValid(
                                            element.getSymbol()));
                        }
                    }
                }
            }
        }
    }

}
