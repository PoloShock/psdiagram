/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.MapChangesAdapter;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.MapChangesCalendarAdapter;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.MapListChangesAdapter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * In order to save bandwidth, only certain range of versions is included. This way if sent from
 * server, size of the class (or xml) is minimal.
 * <p/>
 * @author Miroslav Bartyzal
 */
@XmlRootElement(name = "versionsinfo")
@XmlAccessorType(XmlAccessType.NONE)
public class ChangesCondenser
{

    private String topVersion = null; // don't include it in JAXB
    private String bottomVersion = null; // don't include it in JAXB
    @XmlCDATA
    @XmlElement(name = "changelogurl")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String changlelogURL;
    @XmlElement(name = "releasedates")
    @XmlJavaTypeAdapter(MapChangesCalendarAdapter.class)
    private Map<String, Calendar> releaseDates;
    @XmlElement(name = "releaseurl")
    @XmlJavaTypeAdapter(MapChangesAdapter.class)
    private Map<String, String> releaseURLs;
    @XmlElement(name = "headlines")
    @XmlJavaTypeAdapter(MapChangesAdapter.class)
    private Map<String, String> headlines;
    @XmlElement(name = "descriptions")
    @XmlJavaTypeAdapter(MapChangesAdapter.class)
    private Map<String, String> descriptions;
    @XmlElement(name = "features")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> features;
    @XmlElement(name = "enhancements")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> enhancements;
    @XmlElement(name = "extensions")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> extensions;
    @XmlElement(name = "changes")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> changes;
    @XmlElement(name = "fixes")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> fixes;
    @XmlElement(name = "others")
    @XmlJavaTypeAdapter(MapListChangesAdapter.class)
    private Map<String, List<String>> other;

    public ChangesCondenser()
    {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    public ChangesCondenser(
            String changlelogURL,
            Map<String, Calendar> releaseDates,
            Map<String, String> releaseURLs,
            Map<String, String> headlines,
            Map<String, String> descriptions,
            Map<String, List<String>> features,
            Map<String, List<String>> enhancements,
            Map<String, List<String>> extensions,
            Map<String, List<String>> changes,
            Map<String, List<String>> fixes,
            Map<String, List<String>> other)
    {
        this.changlelogURL = changlelogURL;
        this.releaseDates = releaseDates;
        this.releaseURLs = releaseURLs;
        this.headlines = headlines;
        this.descriptions = descriptions;
        this.features = features;
        this.enhancements = enhancements;
        this.extensions = extensions;
        this.changes = changes;
        this.fixes = fixes;
        this.other = other;
    }

    public String getChanglelogURL()
    {
        return changlelogURL;
    }

    public Map<String, Calendar> getReleaseDates()
    {
        return releaseDates;
    }

    public Map<String, String> getReleaseURLs()
    {
        return releaseURLs;
    }

    public Map<String, String> getHeadlines()
    {
        return headlines;
    }

    public Map<String, String> getDescriptions()
    {
        return descriptions;
    }

    public Map<String, List<String>> getFeatures()
    {
        return features;
    }

    public Map<String, List<String>> getEnhancements()
    {
        return enhancements;
    }

    public Map<String, List<String>> getExtensions()
    {
        return extensions;
    }

    public Map<String, List<String>> getChanges()
    {
        return changes;
    }

    public Map<String, List<String>> getFixes()
    {
        return fixes;
    }

    public Map<String, List<String>> getOther()
    {
        return other;
    }

    /**
     *
     * @return version included as upper bound
     */
    public String getTopVersion()
    {
        if (topVersion == null) {
            setTopBottomVersions(); // load it lazily
        }
        return topVersion;
    }

    /**
     *
     * @return version included as lower bound
     */
    public String getBottomVersion()
    {
        if (bottomVersion == null) {
            setTopBottomVersions(); // load it lazily
        }
        return bottomVersion;
    }

    public ChangesCondenser getFractionCondenser(String fromVersion)
    {
        float fv = parseVersion(fromVersion);
        return new ChangesCondenser(changlelogURL, getFractionMap(fv, releaseDates), getFractionMap(
                fv,
                releaseURLs),
                getFractionMap(fv, headlines), getFractionMap(fv, descriptions), getFractionMap(fv,
                features), getFractionMap(fv, enhancements), getFractionMap(fv, extensions),
                getFractionMap(fv, changes), getFractionMap(fv, fixes), getFractionMap(fv, other));
    }

    private <E> Map<String, E> getFractionMap(float fromVersion, Map<String, E> map)
    {
        if (map == null) {
            return null;
        }
        HashMap<String, E> retMAP = new HashMap<>(map);
        for (Iterator<Map.Entry<String, E>> it = retMAP.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, E> entry = it.next();
            if (parseVersion(entry.getKey()) < fromVersion) {
                it.remove();
            }
        }
        return retMAP;
    }

    public static float parseVersion(String version)
    {
        String ver = version.replaceAll("[^0-9]", "");
        return Float.parseFloat(ver.substring(0, 1) + "." + ver.substring(1));
    }

    private void setTopBottomVersions()
    {
        float top = Long.MIN_VALUE;
        float bottom = Long.MAX_VALUE;
        HashSet<String> versions = new HashSet<>();

        if (releaseDates != null) {
            versions.addAll(releaseDates.keySet());
        }
        if (releaseURLs != null) {
            versions.addAll(releaseURLs.keySet());
        }
        if (headlines != null) {
            versions.addAll(headlines.keySet());
        }
        if (descriptions != null) {
            versions.addAll(descriptions.keySet());
        }
        if (features != null) {
            versions.addAll(features.keySet());
        }
        if (enhancements != null) {
            versions.addAll(enhancements.keySet());
        }
        if (extensions != null) {
            versions.addAll(extensions.keySet());
        }
        if (changes != null) {
            versions.addAll(changes.keySet());
        }
        if (fixes != null) {
            versions.addAll(fixes.keySet());
        }
        if (other != null) {
            versions.addAll(other.keySet());
        }
        for (String version : versions) {
            float versionNumber = parseVersion(version);
            if (versionNumber > top) {
                top = versionNumber;
                topVersion = version;
            }
            if (versionNumber < bottom) {
                bottom = versionNumber;
                bottomVersion = version;
            }
        }
    }

}
