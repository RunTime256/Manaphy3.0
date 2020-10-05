package exception.war.code;

import java.awt.Color;

public class IncorrectCodeChannelException extends CodeException
{
    public IncorrectCodeChannelException(String name)
    {
        super(Color.YELLOW, "The code `" + name + "` cannot be accepted from this channel");
    }
}
