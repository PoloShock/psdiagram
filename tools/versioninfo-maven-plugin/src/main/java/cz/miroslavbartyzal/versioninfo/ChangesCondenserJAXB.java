/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.versioninfo;

import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ChangesCondenserJAXB
{

    private static JAXBContext jAXBContext;

    private static JAXBContext getjAXBContext()
    {
        if (jAXBContext == null) {
            try {
                jAXBContext = JAXBContextFactory.createContext(new Class[]{ChangesCondenser.class},
                        null);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return jAXBContext;
    }

    public static ChangesCondenser getCurrentChangesCondenser(File file)
    {
        if (file != null) {
            try {
                return (ChangesCondenser) getjAXBContext().createUnmarshaller().unmarshal(file);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return null;
    }

    public static void saveChangesCondenser(ChangesCondenser changesCondenser, File file)
    {
        try {
            Marshaller marshaller = getjAXBContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            file.getParentFile().mkdirs();file.getParentFile().mkdirs();
            marshaller.marshal(changesCondenser, file); //new File(System.getenv("OPENSHIFT_DATA_DIR") + "versioninfo.xml")
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
