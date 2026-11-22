package cz.miroslavbartyzal.versioninfo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mojo(name = "versioninfo")
public class VersionInfoMojo extends AbstractMojo
{

    @Parameter
    private File inputFileToMerge;
    @Parameter(required = true)
    private File outputFile;

    @Parameter(required = true)
    private String version;
    @Parameter
    private String changelogurl;
    @Parameter
    private MyDate releasedate;
    @Parameter
    private String releaseurl;

    @Parameter
    private String headline;
    @Parameter
    private String description;

    @Parameter
    private List<String> features;
    @Parameter
    private List<String> enhancements;
    @Parameter
    private List<String> extensions;
    @Parameter
    private List<String> changes;
    @Parameter
    private List<String> fixes;
    @Parameter
    private List<String> others;

    public void execute() throws MojoExecutionException
    {
        Calendar calendar;
        if (releasedate != null) {
            calendar = new GregorianCalendar(releasedate.year, releasedate.month, releasedate.day);
        } else {
            calendar = new GregorianCalendar();
        }

        ChangesCondenser condenser = ChangesCondenserJAXB.getCurrentChangesCondenser(
                inputFileToMerge);
        if (condenser == null) {
            super.getLog().warn("previous version info not found -> creating new one");
            condenser = new ChangesCondenser();
        } else {
            super.getLog().info("got previous version info -> merging");
        }

        if (changelogurl != null && !changelogurl.equals("")) {
            condenser.setChanglelogURL(changelogurl);
        }
        if (condenser.getReleaseDates() == null) {
            condenser.setReleaseDates(new TreeMap<String, Calendar>());
        }
        condenser.getReleaseDates().put(version, calendar);
        condenser.setReleaseURLs(putEntry(condenser.getReleaseURLs(), releaseurl));

        condenser.setHeadlines(putEntry(condenser.getHeadlines(), headline));
        condenser.setDescriptions(putEntry(condenser.getDescriptions(), description));

        condenser.setFeatures(putEntry(condenser.getFeatures(), features));
        condenser.setEnhancements(putEntry(condenser.getEnhancements(), enhancements));
        condenser.setExtensions(putEntry(condenser.getExtensions(), extensions));
        condenser.setChanges(putEntry(condenser.getChanges(), changes));
        condenser.setFixes(putEntry(condenser.getFixes(), fixes));
        condenser.setOther(putEntry(condenser.getOther(), others));

        ChangesCondenserJAXB.saveChangesCondenser(condenser, outputFile);
    }

    private Map<String, String> putEntry(Map<String, String> where, String what)
    {
        if (what != null && !what.equals("")) {
            if (where == null) {
                where = new TreeMap<String, String>();
            }
            where.put(version, what);
        }
        return where;
    }

    private Map<String, List<String>> putEntry(Map<String, List<String>> where, List<String> what)
    {
        if (what != null && !what.isEmpty()) {
            for (Iterator<String> it = what.iterator(); it.hasNext();) {
                String str = it.next();
                if (str == null || str.equals("")) {
                    it.remove();
                }
            }
            if (!what.isEmpty()) {
                if (where == null) {
                    where = new TreeMap<String, List<String>>();
                }
                where.put(version, what);
            }
        }

        return where;
    }

}
