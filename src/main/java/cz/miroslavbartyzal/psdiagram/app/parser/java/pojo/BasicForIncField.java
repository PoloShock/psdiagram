package cz.miroslavbartyzal.psdiagram.app.parser.java.pojo;

public class BasicForIncField
{
    private final String inc;
    private final boolean simpleFormWithoutVariables;
    
    public BasicForIncField(String inc, boolean simpleFormWithoutVariables)
    {
        this.inc = inc;
        this.simpleFormWithoutVariables = simpleFormWithoutVariables;
    }
    
    public String getInc()
    {
        return inc;
    }
    
    public boolean isSimpleFormWithoutVariables()
    {
        return simpleFormWithoutVariables;
    }
    
}
