/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test of psdiagram_changes.xml
 * <p/>
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class CurrentCondenserParseTest
{

    @Test
    public void currentChangesXMLGlobalTest() throws UnsupportedEncodingException
    {
        Assume.assumeNotNull(System.getProperty("build.versioninfo")); // if we are in release profile, proceed

        ChangesCondenser condenser = null;
        try {
            condenser = ChangesCondenserTest.JAXBCondenserUnmarshal(new FileInputStream(
                    System.getenv("OPENSHIFT_DATA_DIR") + "versioninfo.xml"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        Assert.assertNotNull(condenser);
    }

    @Test
    public void currentChangesXMLBuildTest() throws UnsupportedEncodingException
    {
        Assume.assumeNotNull(System.getProperty("build.versioninfo")); // if we are in release profile, proceed

        ChangesCondenser condenser = null;
        try {
            condenser = ChangesCondenserTest.JAXBCondenserUnmarshal(new FileInputStream(
                    System.getProperty("build.versioninfo") + "versioninfo.xml"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        Assert.assertNotNull(condenser);
    }

}
