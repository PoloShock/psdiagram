/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test of psdiagram_changes.xml
 * <p/>
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class CurrentCondenserParseTest
{

    @Test
    public void currentChangesXMLTest() throws UnsupportedEncodingException
    {
        ChangesCondenser condenser = null;
        try {
            condenser = ChangesCondenserTest.JAXBCondenserUnmarshal(new FileInputStream(
                    System.getenv("OPENSHIFT_DATA_DIR") + "versioninfo.xml"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        Assert.assertNotNull(condenser);
    }

}
