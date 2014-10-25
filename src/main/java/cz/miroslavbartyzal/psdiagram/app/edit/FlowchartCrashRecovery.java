/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.edit;

import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.Layout;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartCrashRecovery
{

    public static final int TIMER_DELAY = 1000;
    private final Layout layout;
    public final File fileToSaveTo;
    private final Marshaller jAXBmarshaller;
    private final ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(
            1);
    private ByteArrayOutputStream backedUpFlowchart;
    private ByteArrayOutputStream savedFlowchart;
    private ScheduledFuture<?> task;

    public FlowchartCrashRecovery(Layout layout, File fileToSaveTo, Marshaller jAXBmarshaller)
    {
        this.layout = layout;
        this.fileToSaveTo = fileToSaveTo;
        this.jAXBmarshaller = jAXBmarshaller;
    }

    public void startPolling()
    {
        task = scheduledExecutorService.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                backupFlowchart();
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

    public void updateSavedFlowchart()
    {
        if (SettingsHolder.settings.getActualFlowchartFile() != null) {
            try {
                savedFlowchart = new ByteArrayOutputStream();
                jAXBmarshaller.marshal(layout.getFlowchart(), savedFlowchart);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        } else {
            savedFlowchart = null;
        }
        backupFlowchart();
    }

    public void backupFlowchart()
    {
        if (layout.getFlowchart().getMainSegment().size() > 2) { // TODO here and in mainWindow - in future user could wish to save edited start / end symbols
            try {
                ByteArrayOutputStream currentFlowchart = new ByteArrayOutputStream();
                jAXBmarshaller.marshal(layout.getFlowchart(), currentFlowchart);
                if (savedFlowchart == null || !Arrays.equals(savedFlowchart.toByteArray(),
                        currentFlowchart.toByteArray())) {
                    // current flowchart is not saved -> should be backed up
                    if (backedUpFlowchart == null || !Arrays.equals(backedUpFlowchart.toByteArray(),
                            currentFlowchart.toByteArray())) {
                        // current flowchart is not backed up
                        try (FileOutputStream fos = new FileOutputStream(fileToSaveTo)) {
//                            System.out.println("DEBUG: writing into tmp.xml");
                            currentFlowchart.writeTo(fos);
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                        backedUpFlowchart = currentFlowchart;
                    }
                } else {
                    deleteBackup();
                }
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        } else {
            deleteBackup();
        }
    }

    public void deleteBackup()
    {
        if (fileToSaveTo.exists()) {
//            System.out.println("DEBUG: deleting tmp.xml");
            fileToSaveTo.delete();
        }
        backedUpFlowchart = null;
    }

}
