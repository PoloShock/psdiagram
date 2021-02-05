/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.persistence.recovery;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;
import cz.miroslavbartyzal.psdiagram.app.persistence.FlowchartSaveContainer;
import java.io.File;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "flowchartRecovery")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "flowchartRecovery")
public class FlowchartRecovery
{

    @XmlElement(name = "flowchartSaveContainer", required = true)
    public FlowchartSaveContainer flowchartSaveContainer;
    @XmlElement(name = "actualFlowchartFile")
    public File actualFlowchartFile;
    @XmlElement(name = "dontSaveDirectly")
    public boolean dontSaveDirectly;

    private FlowchartRecovery()
    {
    }

    public FlowchartRecovery(FlowchartSaveContainer flowchartSaveContainer,
            File actualFlowchartFile, boolean dontSaveDirectly)
    {
        this.flowchartSaveContainer = flowchartSaveContainer;
        this.actualFlowchartFile = actualFlowchartFile;
        this.dontSaveDirectly = dontSaveDirectly;
    }

}
