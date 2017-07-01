/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ArchiveUtil
{

    private static final int BUFFER = 1024 * 1024; // 1MB

    public static byte[] compress(byte[] data) throws IOException
    {
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException
    {
        Inflater inflater = new Inflater(true);
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    public static byte[] zip(byte[] input, String fileName)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            /*
             * File is not on the disk, fileName indicates
             * only the file name to be put into the zip
             */
            ZipEntry entry = new ZipEntry(fileName);
            zos.putNextEntry(entry);
            zos.write(input);
            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        return baos.toByteArray();
    }

    /**
     *
     * @param zipFile
     * @param extractHere if true files are extracted into parent folder<br />if false files are extacted into archive name's folder
     * @param charset
     * @return
     * @throws ZipException
     * @throws IOException
     */
    public static File extractZIP(File zipFile, boolean extractHere, Charset charset) throws ZipException, IOException, IllegalArgumentException
    {
        File extractionDir;
        try (ZipFile zip = new ZipFile(zipFile, charset)) {
            if (extractHere) {
                extractionDir = zipFile.getParentFile();
            } else {
                extractionDir = new File(zipFile.getAbsolutePath().substring(0,
                        zipFile.getAbsolutePath().length() - 4));
            }

            if (!extractHere) {
                if (extractionDir.exists()) {
                    delete(extractionDir);
                }
                extractionDir.mkdir();
            }

            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(extractionDir, currentEntry);
                //destFile = new File(newPath, destFile.getName());

                if (destFile.exists()) {
                    destFile.delete();
                }
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = null;
                    FileOutputStream fos = null;
                    BufferedOutputStream dest = null;
                    try {
                        is = new BufferedInputStream(zip.getInputStream(entry));
                        int currentByte;
                        // establish buffer for writing file
                        byte data[] = new byte[BUFFER];

                        // write the current file to disk
                        fos = new FileOutputStream(destFile);
                        dest = new BufferedOutputStream(fos,
                                BUFFER);

                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    } finally {
                        if (dest != null) {
                            dest.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    }
                }
                //            if (currentEntry.endsWith(".zip")) {
                //                // found a zip file, try to open
                //                extractZIP(destFile, false);
                //            }
            }
        }
        return extractionDir;
    }

    // TODO presunout do vlastni tridy sem to moc nepatri
    public static void delete(File f) throws IOException
    {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

}
