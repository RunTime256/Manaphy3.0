package exception.war.code;

import exception.war.WarException;

import java.awt.Color;

public class CodeException extends WarException
{
    public CodeException(Color color)
    {
        super(color);
    }

    public CodeException(Color color, String message)
    {
        super(color, message);
    }
}
