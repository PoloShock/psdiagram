package cz.miroslavbartyzal.directoryfootprint;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathAdapter extends XmlAdapter<String, Path>
{

    @Override
    public String marshal(Path path)
    {
        if (path == null) {
            return null;
        }
        return path.toString();
    }

    @Override
    public Path unmarshal(String s)
    {
        return Paths.get(s);
    }

}
