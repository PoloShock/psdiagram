/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * <p>
 * Tato třída představuje obecný formulář pro ukládání/otevírání souborů.</p>
 *
 * <p>
 * Obsahuje enumerační třídu, která popisuje sadu použitelných filtrů. Tyto
 * filtry pak lze aplikovat při výběru souboru, či při jeho ukládání.<br />
 * Pro uživatelův komfort třída automaticky ukládá pomocí třídy SettingsHolder
 * poslední použitý adresář, aby jej při příští invokaci dialogu mohla použít
 * jako výchozí adresář.</p>
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MyFileChooser
{

    /**
     * Enumerační třída, která popisuje sadu použitelných filtrů. Tyto
     * filtry lze aplikovat při výběru souboru, či při jeho ukládání.
     */
    public static enum FilterType
    {

        IMAGE
                {
                    private final FileFilter[] filters;

                    {
                        TreeSet<String> formats = new TreeSet<>();
                        for (String format : ImageIO.getWriterFormatNames()) {
                            if (!format.toLowerCase().equals("wbmp")) {
                                formats.add(format.toLowerCase());
                            }
                        }
                        filters = new FileFilter[formats.size()];
                        int i = 0;
                        for (Iterator<String> it = formats.iterator(); it.hasNext(); i++) {
                            String format = it.next();
                            filters[i] = new FileNameExtensionFilter("*." + format, format);
                        }
                    }

                    @Override
                    FileFilter[] getFileFilters()
                    {
                        return filters;
                    }

                    @Override
                    FileFilter getDefaultFileFilter()
                    {
                        for (FileFilter fileFilter : filters) {
                            if (fileFilter.getDescription().equals("*.png")) {
                                return fileFilter;
                            }
                        }
                        return null;
                    }
                },
        PDF
                {
                    private final FileFilter[] filters = {
                        new FileNameExtensionFilter("*.pdf", "pdf")
                    };

                    @Override
                    FileFilter[] getFileFilters()
                    {
                        return filters;
                    }

                    @Override
                    FileFilter getDefaultFileFilter()
                    {
                        return filters[0];
                    }
                },
        XML
                {
                    private final FileFilter[] filters = {
                        new FileNameExtensionFilter("*.xml", "xml")
                    };

                    @Override
                    FileFilter[] getFileFilters()
                    {
                        return filters;
                    }

                    @Override
                    FileFilter getDefaultFileFilter()
                    {
                        return filters[0];
                    }
                },
        PSDIAGRAM
                {
                    private final FileFilter[] filters = {
                        new FileNameExtensionFilter("*.psdiagram", "psdiagram")
                    };

                    @Override
                    FileFilter[] getFileFilters()
                    {
                        return filters;
                    }

                    @Override
                    FileFilter getDefaultFileFilter()
                    {
                        return filters[0];
                    }
                },
        XML_AND_PSDIAGRAM
                {
                    private final FileFilter[] filters = {
                        new FileNameExtensionFilter("*.psdiagram, *.xml", "psdiagram", "xml")
                    };

                    @Override
                    FileFilter[] getFileFilters()
                    {
                        return filters;
                    }

                    @Override
                    FileFilter getDefaultFileFilter()
                    {
                        return filters[0];
                    }
                };

        abstract FileFilter[] getFileFilters();

        abstract FileFilter getDefaultFileFilter();

    }

    /**
     * Vytvoří dialog pro ukládání souborů s danými paramtery.
     * <p/>
     * @param filterType typ filtru, který se má na soubory aplikovat
     * @param title titulek dialogu pro ukládání souborů
     * @param defaultFileName výchozí název ukládáného souboru
     * @return vybrané umístění a název souboru k uložení
     */
    public static File saveFileDialog(FilterType filterType, String title, String defaultFileName)
    {
        if (defaultFileName == null || defaultFileName.equals("")) {
            defaultFileName = "Bez_názvu";
        }
        JFileChooser jFileChooser = new JFileChooser(SettingsHolder.settings.getLastDir())
        {
            @Override
            public void approveSelection()
            {
                File f = checkExtension(getSelectedFile(),
                        ((FileNameExtensionFilter) this.getFileFilter()).getExtensions()[0]);
                if (getDialogType() == SAVE_DIALOG && f.exists()) {
                    int result = JOptionPane.showConfirmDialog(this, "Soubor již existuje, přepsat?",
                            "Přepsat soubor?", JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        jFileChooser.setDialogTitle(title);
        jFileChooser.setSelectedFile(new File(defaultFileName));
        jFileChooser.setAcceptAllFileFilterUsed(false);

        for (FileFilter fileFilter : filterType.getFileFilters()) {
            jFileChooser.addChoosableFileFilter(fileFilter);
        }
        jFileChooser.setFileFilter(filterType.getDefaultFileFilter());

        if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            SettingsHolder.settings.setLastDir(jFileChooser.getCurrentDirectory().getPath());
            return checkExtension(jFileChooser.getSelectedFile(),
                    ((FileNameExtensionFilter) jFileChooser.getFileFilter()).getExtensions()[0]);
        } else {
            return null;
        }
    }

    /**
     * Vytvoří dialog pro otevírání souborů s danými paramtery.
     * <p/>
     * @param filterType typ filtru, který se má na soubory aplikovat
     * @param title titulek dialogu pro otevírání souborů
     * @return vybrané umístění a název souboru k otevření
     */
    public static File openFileDialog(FilterType filterType, String title)
    {
        JFileChooser jFileChooser = new JFileChooser(SettingsHolder.settings.getLastDir());
        jFileChooser.setDialogTitle(title);
        jFileChooser.setAcceptAllFileFilterUsed(false);

        for (FileFilter fileFilter : filterType.getFileFilters()) {
            jFileChooser.addChoosableFileFilter(fileFilter);
        }
        jFileChooser.setFileFilter(filterType.getDefaultFileFilter());

        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            SettingsHolder.settings.setLastDir(jFileChooser.getCurrentDirectory().getPath());
            return jFileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private static File checkExtension(File file, String extension)
    {
        if (file.getName().endsWith("." + extension)) {
            return file;
        } else {
            return new File(file.getPath() + "." + extension);
        }
    }

}
