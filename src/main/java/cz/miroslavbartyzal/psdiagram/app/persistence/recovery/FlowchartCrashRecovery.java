/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.persistence.recovery;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.persistence.FlowchartSaveContainer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * This class handles flowchart backups in case of for example sudden termination of PS Diagram.
 * <p>
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartCrashRecovery
{

    public static final String BACKUP_FILE_PREFIX = "backup_";
    private static final int TIMER_DELAY = 1000;
    private static final int MAX_BACKUPS_COUNT = 20;
    private final Layout layout;
    private final Marshaller jAXBmarshaller;
    private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(
            1);
    private ByteArrayOutputStream backedUpFlowchart;
    private File backedUpActualFlowchartFile;
    private ByteArrayOutputStream savedFlowchart;
    private ScheduledFuture<?> task;
    private File backupFile = null;
    private RandomAccessFile backupRAFile = null;
    private FileLock backupFileLock = null;
    private boolean xmlMode = false; // indicates whether we are in compatibility mode (using only flowchart class in .xml file), or if we are saving in modern .psdiagram file with metadata

    public FlowchartCrashRecovery(Layout layout, Marshaller jAXBmarshaller)
    {
        this.layout = layout;
        this.jAXBmarshaller = jAXBmarshaller;
    }

    public void startPolling()
    {
        task = scheduledExecutorService.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    backupFlowchart();
                } catch (Throwable t) {  // Catch Throwable rather than Exception (a subclass).
                    System.err.println(
                            "Caught exception in ScheduledExecutorService. StackTrace:\n" + Arrays.toString(
                                    t.getStackTrace()));
                }
            }
        }, TIMER_DELAY, TIMER_DELAY, TimeUnit.MILLISECONDS);
    }

    public void stopPolling()
    {
        if (task != null) {
            task.cancel(false);
            scheduledExecutorService.purge();
        }
    }

    /**
     * Has to be called on every change of saved flowchart file in order to compare it
     * to the current flowchart displayed.
     */
    public void updateSavedFlowchart()
    {
        if (SettingsHolder.settings.getActualFlowchartFile() != null) {
            // current flowchart has its save file
            xmlMode = SettingsHolder.settings.getActualFlowchartFile().getName().endsWith(".xml");
            try {
                savedFlowchart = new ByteArrayOutputStream();
                byte[] actualFFBytes = Files.readAllBytes(
                        SettingsHolder.settings.getActualFlowchartFile().toPath());
                savedFlowchart.write(actualFFBytes, 0, actualFFBytes.length);
            } catch (Exception ex) {
                MyExceptionHandler.handle(ex);
                savedFlowchart = null;
            }
        } else {
            xmlMode = false;
            savedFlowchart = null;
        }
        backupFlowchart();
    }

    /**
     * Backs up current flowchart if not backed up already.
     */
    public void backupFlowchart()
    {
        if (layout.getFlowchart().getMainSegment().size() > 2) { // TODO here and in mainWindow - in future user could wish to save edited start / end symbols
            try {
                ByteArrayOutputStream currentFlowchart = new ByteArrayOutputStream();
                if (xmlMode) {
                    jAXBmarshaller.marshal(layout.getFlowchart(), currentFlowchart);
                } else {
                    jAXBmarshaller.marshal(new FlowchartSaveContainer(layout.getFlowchart()),
                            currentFlowchart);
                }

                if (savedFlowchart == null || !Arrays.equals(savedFlowchart.toByteArray(),
                        currentFlowchart.toByteArray())) {
                    // current flowchart (or its changes) is not saved -> should be backed up
                    if (backedUpFlowchart == null
                            || backedUpActualFlowchartFile == null && SettingsHolder.settings.getActualFlowchartFile() != null
                            || backedUpActualFlowchartFile != null && !backedUpActualFlowchartFile.equals(
                                    SettingsHolder.settings.getActualFlowchartFile())
                            || !Arrays.equals(backedUpFlowchart.toByteArray(),
                                    currentFlowchart.toByteArray())) {
                        // current flowchart is not backed up or it is backed up with obsolete actualFlowchartFile record (or any other metadata tested)
                        alocateBackupFile();
                        if (backupRAFile != null) {
                            try {
//                                System.out.println("DEBUG: writing into backup file");
                                backedUpActualFlowchartFile = SettingsHolder.settings.getActualFlowchartFile();
                                backedUpFlowchart = currentFlowchart;

                                ByteArrayOutputStream flowchartRecoveryBaos = new ByteArrayOutputStream();
                                FlowchartRecovery flowchartRecovery = new FlowchartRecovery(
                                        new FlowchartSaveContainer(layout.getFlowchart()),
                                        backedUpActualFlowchartFile,
                                        SettingsHolder.settings.isDontSaveDirectly());
                                jAXBmarshaller.marshal(flowchartRecovery, flowchartRecoveryBaos);
                                backupRAFile.getChannel().truncate(0);
                                backupRAFile.write(flowchartRecoveryBaos.toByteArray());
                            } catch (IOException ex) {
                                MyExceptionHandler.handle(ex);
                            }
                        }
                    }
                } else {
                    // current flowchart is saved at his current state - no need for its backup
                    deleteBackup();
                }
            } catch (JAXBException ex) {
                MyExceptionHandler.handle(ex);
            }
        } else {
            // current flowchart is empty - no need for its backup
            deleteBackup();
        }
    }

    private void alocateBackupFile()
    {
        if (backupRAFile == null) {
            for (int i = 1; i <= MAX_BACKUPS_COUNT; i++) { // support up to MAX_BACKUPS_COUNT backups
                File file = new File(SettingsHolder.WORKING_DIR, BACKUP_FILE_PREFIX + i + ".xml");
                if (!file.exists()) {
                    RandomAccessFile raFile = null;
                    try {
                        raFile = new RandomAccessFile(file, "rw");
                        FileLock fileLock = raFile.getChannel().tryLock();
                        if (fileLock != null) {
                            backupFile = file;
                            backupRAFile = raFile;
                            backupFileLock = fileLock;
                            break;
                        } else {
                            raFile.close();
                        }
                    } catch (IOException | OverlappingFileLockException ex) {
                        if (raFile != null) {
                            try {
                                raFile.close();
                            } catch (IOException ex1) {
                                MyExceptionHandler.handle(ex1);
                            }
                        }
                        MyExceptionHandler.handle(ex);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Realocates current backupFile to another backupFile. Old backup file will be deleted.
     * This method is used when restoring backup. If the provided filename can't be opened,
     * no change will happen.
     * <p>
     * @param filename
     */
    public void realocateBackupFile(String filename)
    {
        if (filename.matches(BACKUP_FILE_PREFIX + "[0-9]+\\.xml")) {
            File file = new File(SettingsHolder.WORKING_DIR, filename);
            if (file.exists()) {
                RandomAccessFile raFile = null;
                try {
                    raFile = new RandomAccessFile(file, "rw");
                    FileLock fileLock = raFile.getChannel().tryLock();
                    if (fileLock != null) {
                        deleteBackup();
                        backupFile = file;
                        backupRAFile = raFile;
                        backupFileLock = fileLock;
                        backupFlowchart();
                    } else {
                        raFile.close();
                    }
                } catch (IOException | OverlappingFileLockException ex) {
                    if (raFile != null) {
                        try {
                            raFile.close();
                        } catch (IOException ex1) {
                            MyExceptionHandler.handle(ex1);
                        }
                    }
                    MyExceptionHandler.handle(ex);
                }
            }
        }
    }

    /**
     * Indicates if there is a backup saved. If it is, flowchart was not saved.
     * <p>
     * @return true if there is an unsaved flowchart
     */
    public boolean isFlowchartRestorationAvailable()
    {
        for (int i = 1; i <= MAX_BACKUPS_COUNT; i++) { // support up to MAX_BACKUPS_COUNT backups
            File file = new File(SettingsHolder.WORKING_DIR, BACKUP_FILE_PREFIX + i + ".xml");
            if (file.exists()) {
                RandomAccessFile raFile = null;
                try {
                    raFile = new RandomAccessFile(file, "rw");
                    FileLock fileLock = raFile.getChannel().tryLock();
                    if (fileLock != null) {
                        fileLock.release();
                        raFile.close();
                        return true;
                    } else {
                        raFile.close();
                    }
                } catch (IOException | OverlappingFileLockException ex) {
                    if (raFile != null) {
                        try {
                            raFile.close();
                        } catch (IOException ex1) {
                            MyExceptionHandler.handle(ex1);
                        }
                    }
                    MyExceptionHandler.handle(ex);
                }
            }
        }
        return false;
    }

    public void deleteBackup()
    {
        if (backupRAFile != null && backupFile.exists()) {
            if (backupFileLock != null) {
                try {
                    backupFileLock.release();
                } catch (IOException ex) {
                    MyExceptionHandler.handle(ex);
                }
            }
            try {
                backupRAFile.close();
            } catch (IOException ex) {
                MyExceptionHandler.handle(ex);
            }
            backupFile.delete();
        }
        backupFile = null;
        backupRAFile = null;
        backupFileLock = null;
        backedUpFlowchart = null;
        backedUpActualFlowchartFile = null;
    }

    public List<File> getFilesToRestore()
    {
        ArrayList<File> filesToRestore = new ArrayList<>();
        for (int i = 1; i <= MAX_BACKUPS_COUNT; i++) { // support up to MAX_BACKUPS_COUNT backups
            File file = new File(SettingsHolder.WORKING_DIR, BACKUP_FILE_PREFIX + i + ".xml");
            if (file.exists()) {
                RandomAccessFile raFile = null;
                try {
                    raFile = new RandomAccessFile(file, "rw");
                    FileLock fileLock = raFile.getChannel().tryLock();
                    if (fileLock != null) {
                        fileLock.release();
                        raFile.close();
                        filesToRestore.add(file);
                    } else {
                        raFile.close();
                    }
                } catch (IOException | OverlappingFileLockException ex) {
                    if (raFile != null) {
                        try {
                            raFile.close();
                        } catch (IOException ex1) {
                            MyExceptionHandler.handle(ex1);
                        }
                    }
                    MyExceptionHandler.handle(ex);
                }
            }
        }
        return filesToRestore;
    }

}
