package exception.war.code;

import java.awt.Color;

public class NotACodeException extends CodeException
{
    public NotACodeException(String name)
    {
        super(Color.YELLOW, "The code `" + name + "` does not exist");
    }
}
