package cz.miroslavbartyzal.psdiagram.app.parser.java.pojo;

public class AssignmentFields
{
    
    private final String variable;
    private final String operator;
    private final String value;
    
    public AssignmentFields(String variable, String operator, String value)
    {
        this.variable = variable;
        this.operator = operator;
        this.value = value;
    }
    
    public String getVariable()
    {
        return variable;
    }
    
    public String getOperator()
    {
        return operator;
    }
    
    public String getValue()
    {
        return value;
    }
    
}
