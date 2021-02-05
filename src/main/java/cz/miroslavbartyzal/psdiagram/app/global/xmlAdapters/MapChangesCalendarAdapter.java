/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MapChangesCalendarAdapter extends XmlAdapter<MapChangesCalendarAdapter.MapType, Map<String, Calendar>>
{

    @Override
    public MapType marshal(Map<String, Calendar> map)
    {
        if (map == null) {
            return null;
        }
        MapType mapType = new MapType();
        for (Map.Entry<String, Calendar> entry : map.entrySet()) {
            mapType.entryList.add(new MapEntry(entry.getKey(),
                    entry.getValue().get(Calendar.YEAR) + "-" + entry.getValue().get(Calendar.MONTH) + "-" + entry.getValue().get(
                            Calendar.DAY_OF_MONTH)));
        }
        return mapType;
    }

    @Override
    public Map<String, Calendar> unmarshal(MapType type) throws Exception
    {
        Map<String, Calendar> map = new TreeMap<>();
        for (MapEntry entry : type.entryList) {
            String[] s = entry.date.trim().split("\\-");
            map.put(entry.version, new GregorianCalendar(Integer.parseInt(s[0]), Integer.parseInt(
                    s[1]), Integer.parseInt(s[2])));
        }
        return map;
    }

    @XmlType(namespace = "MapChangesCalendarAdapter")
    public static final class MapType
    {

        @XmlElement(name = "version")
        private List<MapEntry> entryList = new ArrayList<>();

        private MapType()
        {
        }

    }

    @XmlType(namespace = "MapChangesCalendarAdapter")
    public static final class MapEntry
    {

        @XmlAttribute(name = "id")
        private String version;
        @XmlValue
        private String date;

        private MapEntry()
        {
        }

        public MapEntry(String version, String date)
        {
            this.version = version;
            this.date = date;
        }

    }

}
