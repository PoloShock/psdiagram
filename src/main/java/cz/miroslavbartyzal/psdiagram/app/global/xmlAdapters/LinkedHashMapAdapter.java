/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Tato třída představuje adaptér JAXB pro třídu LinkedHashMap.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class LinkedHashMapAdapter extends XmlAdapter<LinkedHashMapAdapter.MapType, LinkedHashMap<String, String>>
{

    /**
     * Marshalovací metoda JAXB.
     *
     * @param map mapa, která má být marshalována
     * @return XML reprezentace mapy na vstupu
     */
    @Override
    public MapType marshal(LinkedHashMap<String, String> map)
    {
        if (map == null) {
            return null;
        }
        MapType mapType = new MapType();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapType.entryList.add(new MapEntry(entry.getKey(), entry.getValue()));
        }
        return mapType;
    }

    /**
     * Unmarshalovací metoda JAXB.
     *
     * @param type instance třídy MapType, ze které se má objekt unmarshalovat
     * @return instance třídy LinkedHashMap
     */
    @Override
    public LinkedHashMap<String, String> unmarshal(MapType type) throws Exception
    {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (MapEntry entry : type.entryList) {
            map.put(entry.key, entry.value);
        }
        return map;
    }

    /**
     * Třída, kterou je JAXB schopen zpracovat.
     */
    public static class MapType
    {

        @XmlElement(name = "entry")
        private ArrayList<MapEntry> entryList = new ArrayList<>();

        private MapType()
        {
        }

    }

    /**
     * Dílčí třída, kterou je JAXB schopen zpracovat.
     */
    public static class MapEntry
    {

        @XmlAttribute(name = "key")
        private String key;
        @XmlValue
        private String value;

        private MapEntry()
        {
        }

        public MapEntry(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

    }

}
