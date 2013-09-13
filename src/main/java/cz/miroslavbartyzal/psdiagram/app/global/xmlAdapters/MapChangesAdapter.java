/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

/**
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MapChangesAdapter extends XmlAdapter<MapChangesAdapter.MapType, Map<String, String>>
{

    @Override
    public MapType marshal(Map<String, String> map)
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

    @Override
    public Map<String, String> unmarshal(MapType type) throws Exception
    {
        Map<String, String> map = new HashMap<>();
        for (MapEntry entry : type.entryList) {
            map.put(entry.version, entry.message.trim());
        }
        return map;
    }

    @XmlType(namespace = "MapChangesAdapter")
    public static final class MapType
    {

        @XmlElement(name = "version")
        private List<MapEntry> entryList = new ArrayList<>();

        private MapType()
        {
        }

    }

    @XmlType(namespace = "MapChangesAdapter")
    public static final class MapEntry
    {

        @XmlAttribute(name = "id")
        private String version;
        @XmlCDATA
        @XmlValue
        private String message;

        private MapEntry()
        {
        }

        public MapEntry(String version, String message)
        {
            this.version = version;
            this.message = message;
        }

    }

}
