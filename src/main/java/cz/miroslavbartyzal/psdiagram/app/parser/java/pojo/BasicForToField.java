package cz.miroslavbartyzal.psdiagram.app.parser.java.pojo;

public class BasicForToField
{
    
    public enum RelationalOperatorType
    {
        GREATER_THAN,
        LESSER_THAN
    }
    
    
    private final String to;
    private final RelationalOperatorType operatorType;
    
    public BasicForToField(String to,
            RelationalOperatorType operatorType)
    {
        this.to = to;
        this.operatorType = operatorType;
    }
    
    public String getTo()
    {
        return to;
    }
    
    public RelationalOperatorType getOperatorType()
    {
        return operatorType;
    }
    
}
