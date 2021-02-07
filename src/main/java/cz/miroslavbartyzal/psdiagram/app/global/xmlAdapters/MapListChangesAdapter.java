/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

/**
 *
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class MapListChangesAdapter extends XmlAdapter<MapListChangesAdapter.MapType, Map<String, List<String>>>
{

    @Override
    public MapType marshal(Map<String, List<String>> map)
    {
        if (map == null) {
            return null;
        }
        MapType mapType = new MapType();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            mapType.entryList.add(new MapEntry(entry.getKey(), entry.getValue()));
        }
        return mapType;
    }

    @Override
    public Map<String, List<String>> unmarshal(MapType type) throws Exception
    {
        Map<String, List<String>> map = new TreeMap<>();
        for (MapEntry entry : type.entryList) {
            for (int i = 0; i < entry.entries.size(); i++) {
                entry.entries.set(i, entry.entries.get(i).trim());
            }
            map.put(entry.version, entry.entries);
        }
        return map;
    }

    @XmlType(namespace = "MapListChangesAdapter")
    public static class MapType
    {

        @XmlElement(name = "version")
        private List<MapEntry> entryList = new ArrayList<>();

        private MapType()
        {
        }

    }

    @XmlType(namespace = "MapListChangesAdapter")
    public static class MapEntry
    {

        @XmlAttribute(name = "since")
        private String version;
        @XmlCDATA
        @XmlElement(name = "entry")
        private List<String> entries;

        private MapEntry()
        {
        }

        public MapEntry(String version, List<String> entries)
        {
            this.version = version;
            this.entries = entries;
        }

    }

}
