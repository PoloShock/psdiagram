<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- suppress nodes that are not matched -->
    <xsl:template match="text() | @*">
        <xsl:apply-templates select="text() | @*"/>
    </xsl:template>

    <xsl:template match="/">
        <html>
            <head>
                <style type="text/css">
                    body { background-color:D6D9DF; }
                    h2 { font-size: 19;
                    margin: 0;
                    margin-left: 2;
                    margin-top: 5;
                    padding: 0;  }
                    ul { margin-left: 30;
                    margin-bottom: 10; }
                    li { margin-bottom: 2; }
                </style>
            </head>
            <body>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="/versionsinfo/features[version[entry]]"><!--match for 'features' that have version children that have entry children -->
        <h2>
            Nové funkce
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                    <!--<xsl:copy-of select="child::node()"/>-->
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="/versionsinfo/enhancements[version[entry]]">
        <h2>
            Vylepšení
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="/versionsinfo/extensions[version[entry]]">
        <h2>
            Rozšíření
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="/versionsinfo/changes[version[entry]]">
        <h2>
            Změny
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="/versionsinfo/fixes[version[entry]]">
        <h2>
            Opravy
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="/versionsinfo/others[version[entry]]">
        <h2>
            Ostatní
        </h2>
        <ul>
            <xsl:for-each select="version/entry">
                <li>
                    <xsl:value-of select="normalize-space(.)" disable-output-escaping="yes" />
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

</xsl:stylesheet>