/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.persistence;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "flowchartSaveContainer")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchartSaveContainer")
public class FlowchartSaveContainer
{

    @XmlElement(name = "flowchart", required = true)
    public Flowchart<LayoutSegment, LayoutElement> flowchart;

    private FlowchartSaveContainer()
    {
    }

    public FlowchartSaveContainer(Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        this.flowchart = flowchart;
    }

}
