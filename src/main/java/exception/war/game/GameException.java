package exception.war.game;

import exception.BotException;

import java.awt.Color;

public class GameException extends BotException
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
