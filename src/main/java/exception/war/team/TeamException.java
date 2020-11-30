package exception.war.team;

import exception.war.WarException;

import java.awt.Color;

public class TeamException extends WarException
{
    public TeamException(Color color)
    {
        super(color);
    }

    public TeamException(Color color, String message)
    {
        super(color, message);
    }
}
