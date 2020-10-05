package exception.war.code;

import java.awt.Color;

public class AlreadyRetrievedCodeException extends CodeException
{
    public AlreadyRetrievedCodeException(String name, Long userId)
    {
        super(Color.YELLOW, "The code `" + name + "` has already been retrieved by user `" + userId + "`");
    }
}
