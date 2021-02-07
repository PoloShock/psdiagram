/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
@XmlJavaTypeAdapters(value = {
    @XmlJavaTypeAdapter(type = Color.class, value = ColorAdapter.class),
    @XmlJavaTypeAdapter(type = LinkedHashMap.class, value = LinkedHashMapAdapter.class),
    @XmlJavaTypeAdapter(type = Point2D.class, value = Point2DDoubleAdapter.class)
})
package cz.miroslavbartyzal.psdiagram.app.flowchart.symbols;

import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.ColorAdapter;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.LinkedHashMapAdapter;
import cz.miroslavbartyzal.psdiagram.app.global.xmlAdapters.Point2DDoubleAdapter;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
