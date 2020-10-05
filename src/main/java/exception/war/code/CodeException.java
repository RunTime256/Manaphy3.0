package exception.war.code;

import exception.BotException;

import java.awt.Color;

public class CodeException extends BotException
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
