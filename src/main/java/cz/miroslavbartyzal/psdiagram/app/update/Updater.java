/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.update;

/**
 *
 * @author Miroslav Bartyzal
 */
public final class Updater
{

    private long availableVersion;
    private String changeLog;
    private boolean infoSearched;

    public Updater(boolean loadInfo)
    {
        if (loadInfo) {
            loadInfo();
        }
    }

    public boolean loadInfo()
    {
        // TODO load info
        infoSearched = true;
        return false;
    }

    public long getAvailableVersion()
    {
        if (!infoSearched) {
            loadInfo();
        }
        return availableVersion;
    }

    public String getChangeLog()
    {
        if (!infoSearched) {
            loadInfo();
        }
        return changeLog;
    }

}
