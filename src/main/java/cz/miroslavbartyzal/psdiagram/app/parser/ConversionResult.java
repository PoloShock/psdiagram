package cz.miroslavbartyzal.psdiagram.app.parser;

import cz.miroslavbartyzal.psdiagram.app.flowchart.Flowchart;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutElement;
import cz.miroslavbartyzal.psdiagram.app.flowchart.layouts.LayoutSegment;

public class ConversionResult
{
    
    private Flowchart<LayoutSegment, LayoutElement> flowchart;
    private String errorMessage;
    
    public Flowchart<LayoutSegment, LayoutElement> getFlowchart()
    {
        return flowchart;
    }
    
    public boolean isInputValid()
    {
        return flowchart != null && errorMessage == null;
    }
    
    public void setFlowchart(Flowchart<LayoutSegment, LayoutElement> flowchart)
    {
        this.flowchart = flowchart;
    }
    
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
    
}
