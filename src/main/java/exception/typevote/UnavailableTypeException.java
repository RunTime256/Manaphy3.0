package exception.typevote;

import exception.war.WarException;

import java.awt.Color;

public class UnavailableTypeException extends WarException
{
    public UnavailableTypeException(String type)
    {
        super(Color.YELLOW, "`" + type + "` is not an available type");
    }
}
