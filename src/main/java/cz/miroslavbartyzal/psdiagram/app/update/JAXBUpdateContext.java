/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import cz.miroslavbartyzal.psdiagram.app.global.MyExceptionHandler;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
                jAXBContext = JAXBContext.newInstance(ChangesCondenser.class);
//                jAXBContext = JAXBContextFactory.createContext(new Class[]{ChangesCondenser.class}, null);
            } catch (JAXBException ex) {
                MyExceptionHandler.handle(ex);
            }
        }
        return jAXBContext;
    }

    public static Marshaller getMarshaller()
    {
        try {
            return getJAXBContext().createMarshaller();
        } catch (JAXBException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
    }

    public static Unmarshaller getUnmarshaller()
    {
        try {
            return getJAXBContext().createUnmarshaller();
        } catch (JAXBException ex) {
            MyExceptionHandler.handle(ex);
            return null;
        }
    }

}
