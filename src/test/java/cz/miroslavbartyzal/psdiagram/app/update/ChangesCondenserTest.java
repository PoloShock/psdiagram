/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class ChangesCondenserTest
{

    JAXBContext jAXBContext;

    @Test
    public void JAXBCondenserTest()
    {
        HashMap<String, List<String>> features = new HashMap<>();
        ArrayList<String> fs = new ArrayList<>();
        HashMap<String, String> descriptions = new HashMap<>();
        HashMap<String, Calendar> dates = new HashMap<>();

        fs.add("marshalovani ChangesCondenseru");
        fs.add("podpora jUnit");
        features.put("1.0.5.1", fs);

        fs = new ArrayList<>();
        fs.add("budouci feature");
        features.put("1.0.5.2", fs);

        descriptions.put("1.0.5.1", "extra hustej text");
        descriptions.put("1.0.5.2", "vysla dalsi verze!");

        Calendar calendar1 = new GregorianCalendar(2013, 8, 10);
        dates.put("1.0.5.1", calendar1);

        ChangesCondenser condenser1 = new ChangesCondenser("http://some.url", dates, null, null,
                descriptions, features, null, null, null, new HashMap<String, List<String>>(), null);

        ByteArrayOutputStream baos = JAXBCondenserMarshal(condenser1);
        Assert.assertTrue(baos.size() > 0);

        ChangesCondenser condenser2 = JAXBCondenserUnmarshal(new ByteArrayInputStream(
                baos.toByteArray()));
        Assert.assertNotNull(condenser2);

        Assert.assertEquals(condenser1.getTopVersion(), condenser2.getTopVersion());
        Assert.assertEquals(condenser1.getBottomVersion(), condenser2.getBottomVersion());
        Assert.assertEquals(condenser1.getDescriptions().get("1.0.5.1"),
                condenser2.getDescriptions().get("1.0.5.1"));
        Assert.assertEquals(condenser1.getDescriptions().get("1.0.5.2"),
                condenser2.getDescriptions().get("1.0.5.2"));

        Calendar calendar2 = condenser2.getReleaseDates().get("1.0.5.1");
        Assert.assertEquals(calendar1.get(Calendar.YEAR), calendar2.get(Calendar.YEAR));
        Assert.assertEquals(calendar1.get(Calendar.MONTH), calendar2.get(Calendar.MONTH));
        Assert.assertEquals(calendar1.get(Calendar.DAY_OF_MONTH), calendar2.get(
                Calendar.DAY_OF_MONTH));

        Assert.assertTrue(condenser2.getFeatures().get("1.0.5.1").contains(
                "marshalovani ChangesCondenseru"));
        Assert.assertTrue(condenser2.getFeatures().get("1.0.5.1").contains("podpora jUnit"));
        Assert.assertTrue(condenser2.getFeatures().get("1.0.5.1").size() == 2);
        Assert.assertTrue(condenser2.getFeatures().get("1.0.5.2").contains("budouci feature"));
        Assert.assertTrue(condenser2.getFeatures().get("1.0.5.2").size() == 1);
        Assert.assertTrue(condenser2.getFixes().isEmpty());
        Assert.assertTrue(condenser2.getExtensions() == null);
        Assert.assertTrue(condenser2.getChanges() == null);
        Assert.assertTrue(condenser2.getImprovements() == null);
        Assert.assertTrue(condenser2.getOther() == null);
        Assert.assertTrue(condenser2.getHeadlines() == null);
        Assert.assertTrue(condenser2.getReleaseURLs() == null);
    }

    protected static ChangesCondenser JAXBCondenserUnmarshal(InputStream bais)
    {
        Unmarshaller unmarshaller = JAXBUpdateContext.getUnmarshaller();
        Assert.assertNotNull(unmarshaller);
        ChangesCondenser condenser = null;
        try {
            condenser = (ChangesCondenser) unmarshaller.unmarshal(bais);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
            Assert.fail();
        }
        return condenser;
    }

    private ByteArrayOutputStream JAXBCondenserMarshal(ChangesCondenser condenser)
    {
        Marshaller marshaller = JAXBUpdateContext.getMarshaller();
        Assert.assertNotNull(marshaller);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(condenser, baos);
        } catch (JAXBException ex) {
            ex.printStackTrace(System.err);
            Assert.fail();
        }

        return baos;
    }

}
