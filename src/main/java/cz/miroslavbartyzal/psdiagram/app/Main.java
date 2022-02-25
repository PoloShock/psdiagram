/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import cz.miroslavbartyzal.psdiagram.app.global.PrintStreamWithTimestamp;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

/**
 * Hlavní třída obsahuje main metodu, sloužící pro spuštění hlavního
 * okna aplikace.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Main
{

    /**
     * Metoda pro spuštění hlavního okna aplikace. Nejsou přijímány žádné
     * parametry.
     * <p>
     * @param args
     */
    public static void main(String[] args)
    {
        // let's not use exclusive file logging when in development mode.. (in such case, console is better)
        if (SettingsHolder.IS_DEPLOYMENT_MODE) {
            initFileLogging();
        }

        initLookAndFeel();

        ToolTipManager.sharedInstance().setDismissDelay(12000); // nastavení tooltipů tak, aby zůstaly 12 sekund
        UIManager.put("info", Color.WHITE);

        MainWindow.main(args);
    }

    private static void initFileLogging()
    {
        setupGlobalExceptionHandling();

        Path logFile = Paths.get(System.getProperty("user.home"), ".psdiagram", "psdiagram.log");
        try {
            PrintStream outputFileStream = new PrintStreamWithTimestamp(Files.newOutputStream(logFile),
                    true, StandardCharsets.UTF_8.toString());
            System.setOut(outputFileStream);
            System.setErr(outputFileStream);
        } catch (IOException ex) {
            MyExceptionHandler.handle(ex);
        }
    }

    private static void setupGlobalExceptionHandling()
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }

    private static void initLookAndFeel()
    {
        /*
         * Set the Nimbus look and feel
         *
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            MyExceptionHandler.handle(ex);
        }
    }

}
