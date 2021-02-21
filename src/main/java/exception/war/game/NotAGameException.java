package exception.war.game;

import java.awt.Color;

public class NotAGameException extends GameException
{
    public NotAGameException(String name)
    {
        super(Color.YELLOW, "The game `" + name + "` does not exist");
    }
}
