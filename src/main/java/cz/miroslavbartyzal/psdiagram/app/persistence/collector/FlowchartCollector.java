/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.persistence.collector;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import cz.miroslavbartyzal.psdiagram.app.network.URLPostUploader;
import cz.miroslavbartyzal.psdiagram.app.update.ArchiveUtil;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FlowchartCollector
{

    private static final String SERVER_URL = SettingsHolder.PSDIAGRAM_SERVER + "/c";
    private boolean uploading;
    private boolean exitRequested;

    public FlowchartCollector()
    {
        uploading = false;
        exitRequested = false;
    }

    public void uploadFlowchart(String flowchartToSend, File file)
    {
        if (file == null) {
            return;
        }
        
        uploading = true;

        String fileName = file.getName();

        final Map<String, String> params = new HashMap<>();
        params.put("u", System.getProperty("user.name"));
        try {
            params.put("h", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
            params.put("h", "" + System.getenv().get("COMPUTERNAME"));
        }
        params.put("n", fileName);
        params.put("f", flowchartToSend);

        new Thread(() -> {
            Map<String, String> preparedParams = prepareParameters(params);

            new URLPostUploader().sendRequest(SERVER_URL, preparedParams, new URLPostUploader.UploadFinishListener()
            {
                @Override
                public void onUploadFinished(Integer httpResponseCode)
                {
                    uploading = false;
                    if (exitRequested) {
                        System.exit(0);
                    }
                }
            });
        }).start();
    }

    public void requestExitApp()
    {
        if (uploading) {
            exitRequested = true;
        } else {
            System.exit(0);
        }
    }

    public boolean isUploading()
    {
        return uploading;
    }

    private Map<String, String> prepareParameters(Map<String, String> params)
    {
        Map<String, String> result = new HashMap<>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            byte[] value = entry.getValue().getBytes(StandardCharsets.UTF_8);
            value = compress(value);
            value = encrypt(value);
            value = encode(value);
            
            String valuePrepared = "";
            if (value != null) {
                valuePrepared = new String(value, StandardCharsets.UTF_8);
            }
            result.put(entry.getKey(), valuePrepared);
        }
        
        return result;
    }

    private byte[] compress(byte[] input)
    {
        if (input == null) {
            return null;
        }
        
        try {
            return ArchiveUtil.compress(input);
        } catch (IOException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
    }

    private byte[] encrypt(byte[] input)
    {
        if (input == null) {
            return null;
        }
        
        // TODO
        return input;
    }

    private byte[] encode(byte[] input)
    {
        if (input == null) {
            return null;
        }
        
        return Base64.getEncoder().encode(input);
    }

}
