/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class XMLtoHTMLConversionTest
{

    @Test
    public void conversionTestGlobal() throws TransformerConfigurationException, JAXBException, TransformerException
    {
        // Source
        JAXBContext jc = JAXBUpdateContext.getJAXBContext();
        ChangesCondenser condenser = (ChangesCondenser) JAXBUpdateContext.getUnmarshaller().unmarshal(
                new File(System.getenv("OPENSHIFT_DATA_DIR") + "versioninfo.xml"));
        JAXBSource source = new JAXBSource(jc, condenser);

        // Result
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);

        // Create Transformer
        TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(super.getClass().getResourceAsStream(
                "/psdiagram_changes.xsl"));
        Transformer transformer = tf.newTransformer(xslt);
        // Transform
        transformer.transform(source, result);

        Assert.assertFalse(result.getOutputStream().toString().isEmpty());
    }

    @Test
    public void conversionTestBuild() throws TransformerConfigurationException, JAXBException, TransformerException
    {
        Assume.assumeNotNull(System.getProperty("build.versioninfo")); // if we are in release profile, proceed

        // Source
        JAXBContext jc = JAXBUpdateContext.getJAXBContext();
        ChangesCondenser condenser = (ChangesCondenser) JAXBUpdateContext.getUnmarshaller().unmarshal(
                new File(System.getProperty("build.versioninfo") + "versioninfo.xml"));
        JAXBSource source = new JAXBSource(jc, condenser);

        // Result
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);

        // Create Transformer
        TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(super.getClass().getResourceAsStream(
                "/psdiagram_changes.xsl"));
        Transformer transformer = tf.newTransformer(xslt);
        // Transform
        transformer.transform(source, result);

        Assert.assertFalse(result.getOutputStream().toString().isEmpty());
    }

}
