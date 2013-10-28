/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class JAXBUpdateContext
{

    private static JAXBContext jAXBContext;

    public static JAXBContext getJAXBContext()
    {
        if (jAXBContext == null) {
            try {
//                jAXBContext = JAXBContext.newInstance(ChangesCondenser.class);
                jAXBContext = JAXBContextFactory.createContext(new Class[]{ChangesCondenser.class},
                        null);
            } catch (JAXBException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return jAXBContext;
    }

    public static Marshaller getMarshaller()
    {
        try {
            return getJAXBContext().createMarshaller();
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    public static Unmarshaller getUnmarshaller()
    {
        try {
            return getJAXBContext().createUnmarshaller();
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

}
