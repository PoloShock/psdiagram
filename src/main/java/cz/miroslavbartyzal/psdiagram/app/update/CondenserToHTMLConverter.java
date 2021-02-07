/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.util.JAXBSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Miroslav Bartyzal
 */
public class CondenserToHTMLConverter
{

    public static String convertToHTML(ChangesCondenser changesCondenser, Charset charset)
    {
        // Source
        JAXBSource source;
        try {
            source = new JAXBSource(JAXBUpdateContext.getMarshaller(), changesCondenser);
        } catch (JAXBException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }

        // Result
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);

        // Create Transformer
        TransformerFactory tf = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(CondenserToHTMLConverter.class.getResourceAsStream(
                "/psdiagram_changes.xsl"));
        Transformer transformer;
        try {
            transformer = tf.newTransformer(xslt);
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // don't include META tags
        } catch (TransformerConfigurationException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
        // Transform
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
        return new String(((ByteArrayOutputStream) (result.getOutputStream())).toByteArray(),
                charset);
//            return ((ByteArrayOutputStream) (result.getOutputStream())).toString(charset);
    }

}
