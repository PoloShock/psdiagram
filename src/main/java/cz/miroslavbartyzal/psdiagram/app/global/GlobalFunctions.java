/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

/**
 * Tato třída představuje globální funkce pro celý projekt PS Diagram.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class GlobalFunctions
{

    /**
     * Metoda provede nekontrolované přetypování daného objektu.<br />
     * Tato metoda je využita jen v krajním případě, kdy chráněné přetypování
     * není možné - například po výstupu unmarshallera JAXB.
     *
     * @param <T> požadovaný typ objektu
     * @param o objekt určený k přetypování
     * @return přetypovaný objekt
     */
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object o)
    {
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static boolean isWindows()
    {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static int getJavaVersion()
    {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf('.');
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

}
