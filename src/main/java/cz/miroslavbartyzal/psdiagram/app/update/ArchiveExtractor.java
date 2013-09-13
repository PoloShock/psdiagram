/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ArchiveExtractor
{

    private static final int BUFFER = 4096;

    /**
     *
     * @param zipFile
     * @param extractHere if true files are extracted into parent folder<br />if false files are extacted into archive name's folder
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
                        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
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
