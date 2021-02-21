package exception.war.game;

import java.awt.Color;

public class IncorrectGameChannelException extends GameException
{
    public IncorrectGameChannelException(String name)
    {
        super(Color.YELLOW, "The game `" + name + "` cannot be accepted from this channel");
    }
}
