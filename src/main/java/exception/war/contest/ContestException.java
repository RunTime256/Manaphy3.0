package exception.war.contest;

import exception.war.WarException;

import java.awt.Color;

public class ContestException extends WarException
{
    public ContestException(Color color)
    {
        super(color);
    }

    public ContestException(Color color, String message)
    {
        super(color, message);
    }
}
