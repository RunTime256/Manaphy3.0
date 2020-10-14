package exception.typevote;

import exception.war.WarException;

import java.awt.Color;

public class InvalidTypeException extends WarException
{
    public InvalidTypeException(String type)
    {
        super(Color.YELLOW, "`" + type + "` is not a valid type");
    }
}
