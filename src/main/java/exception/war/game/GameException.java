package exception.war.game;

import exception.war.WarException;

import java.awt.Color;

public class GameException extends WarException
{
    public GameException(Color color)
    {
        super(color);
    }

    public GameException(Color color, String message)
    {
        super(color, message);
    }
}
