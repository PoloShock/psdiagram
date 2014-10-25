/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.global.GlobalFunctions;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ExamplesLoader
{

    public static final String EXAMPLES_DIR = "examples";
    public static final File WORKING_DIR_EXAMPLES = new File(SettingsHolder.WORKING_DIR,
            EXAMPLES_DIR);
    public static final File MYDIR_DIR_EXAMPLES = new File(SettingsHolder.MY_DIR, EXAMPLES_DIR);

    public static ArrayList<Component> getExamplesMenuItems(
            ExampleActionListener exampleActionListener)
    {
        TreeSet<File> examples = new TreeSet<>(new Comparator<File>()
        {
            @Override
            public int compare(File f1, File f2)
            {
                String path1 = f1.getAbsolutePath().replaceFirst("^.+" + EXAMPLES_DIR, "");
                String path2 = f2.getAbsolutePath().replaceFirst("^.+" + EXAMPLES_DIR, "");
                String path1L = path1.toLowerCase(Locale.ENGLISH);
                String path2L = path2.toLowerCase(Locale.ENGLISH);
                if (!path1L.equals(path2L)) {
                    return path1L.compareTo(path2L); // measure to have 'b' immediately after capital 'B' :)
                } else {
                    return path1.compareTo(path2); // keep case sensitive sort though
                }
            }
        });
        if (WORKING_DIR_EXAMPLES.exists() || WORKING_DIR_EXAMPLES.mkdirs()) {
            examples.addAll(loadFiles(WORKING_DIR_EXAMPLES));
        }
        if (MYDIR_DIR_EXAMPLES.exists()) {
            examples.addAll(loadFiles(MYDIR_DIR_EXAMPLES));
        }

        if (!examples.isEmpty()) {
            return loadExamples(examples, exampleActionListener);
        } else {
            ArrayList<Component> ret = new ArrayList<>();
            JMenuItem item = new JMenuItem("nenalezeny žádné algoritmy...");
            item.setEnabled(false);
            item.setToolTipText(getExamplesLocationLoadToolTip());
            ret.add(item);
            return ret;
        }
    }

    private static ArrayList<File> loadFiles(File parentDirectory)
    {
        ArrayList<File> files = new ArrayList<>();
        for (File f : parentDirectory.listFiles()) {
            if (!f.isDirectory()) {
                if (isValidFlowchartFile(f, true)) {
                    files.add(f);
                }
            } else {
                ArrayList<File> subFiles = loadFiles(f);
                if (!subFiles.isEmpty()) {
                    files.add(f);
                    for (File file : subFiles) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    private static ArrayList<Component> loadExamples(SortedSet<File> filesCollection,
            final ExampleActionListener exampleActionListener)
    {
        ArrayList<Component> jMenuItems = new ArrayList<>();
        int dirsAddedCount = 0;
        while (!filesCollection.isEmpty()) {
            File f = filesCollection.first();
            filesCollection.remove(f);
            if (!f.isDirectory()) {
                JMenuItem item = new JMenuItem(f.getName().substring(0, f.getName().lastIndexOf(
                        ".")));
                item.setToolTipText(f.getAbsolutePath());
                item.setActionCommand(f.getAbsolutePath());
                item.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        exampleActionListener.exampleActionPerformed(e.getActionCommand());
                    }
                });
                item.setIcon(new javax.swing.ImageIcon(ExamplesLoader.class.getResource(
                        "/img/menuitems/16-File.png")));
                jMenuItems.add(item);
            } else {
                JMenu subMenu = new JMenu(f.getName());
                for (File toFile : filesCollection) {
                    if (!toFile.getAbsolutePath().startsWith(f.getAbsolutePath()) || toFile.equals(
                            filesCollection.last())) {
                        ArrayList<Component> subItems;
                        if (!toFile.getAbsolutePath().startsWith(f.getAbsolutePath())) {
                            subItems = loadExamples(filesCollection.headSet(toFile),
                                    exampleActionListener);
                        } else {
                            subItems = loadExamples(filesCollection, exampleActionListener);
                        }
                        for (Component component : subItems) {
                            subMenu.add(component);
                        }
                        break;
                    }
                }
                subMenu.setIcon(new javax.swing.ImageIcon(ExamplesLoader.class.getResource(
                        "/img/menuitems/16-Open.png")));
                jMenuItems.add(dirsAddedCount, subMenu);
                dirsAddedCount++;
            }
        }
        if (dirsAddedCount > 0) {
            jMenuItems.add(dirsAddedCount, new javax.swing.JPopupMenu.Separator());
        }
        return jMenuItems;
    }

    public static boolean isValidFlowchartFile(File file, boolean tryToLoadIt)
    {
        if (file.getAbsolutePath().endsWith(".xml")) {
            if (!tryToLoadIt) {
                return true;
            }
            try {
                Flowchart<LayoutSegment, LayoutElement> flowchart = GlobalFunctions.unsafeCast(
                        MainWindow.getJAXBcontext().createUnmarshaller().unmarshal(file));
                return true;
            } catch (JAXBException ex) {
            }
        }
        return false;
    }

    public static String getExamplesLocationLoadToolTip()
    {
        return "<html>Algoritmy jsou načítány z následujících umístění: <br />"
                + ExamplesLoader.WORKING_DIR_EXAMPLES + "<br />"
                + ExamplesLoader.MYDIR_DIR_EXAMPLES + "</html>";
    }

    public interface ExampleActionListener
    {

        public void exampleActionPerformed(String examplePath);

    }

}
