/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.recovery;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import java.io.File;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "flowchartRecovery")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchartRecovery")
public class FlowchartRecovery
{

    @XmlElement(name = "flowchart", required = true)
    public Flowchart<LayoutSegment, LayoutElement> flowchart;
    @XmlElement(name = "actualFlowchartFile")
    public File actualFlowchartFile;
    @XmlElement(name = "dontSaveDirectly")
    public boolean dontSaveDirectly;

    private FlowchartRecovery()
    {
    }

    public FlowchartRecovery(Flowchart<LayoutSegment, LayoutElement> flowchart,
            File actualFlowchartFile, boolean dontSaveDirectly)
    {
        this.flowchart = flowchart;
        this.actualFlowchartFile = actualFlowchartFile;
        this.dontSaveDirectly = dontSaveDirectly;
    }

}
