/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class FileChecksum
{

    public static String getMD5Checksum(File file)
    {
        FileInputStream fs;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace(System.err);
            return null;
        }

        byte[] buffer = new byte[131072];
        int numOfBytesRead;
        try {
            while ((numOfBytesRead = fs.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return null;
        } finally {
            try {
                fs.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                return null;
            }
        }

        return bytesToHexString(md.digest());
    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
