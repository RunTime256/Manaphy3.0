package exception.war.team;

import exception.BotException;

import java.awt.Color;

public class TeamException extends BotException
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
