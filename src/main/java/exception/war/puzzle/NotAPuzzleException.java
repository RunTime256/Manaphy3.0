package exception.war.puzzle;

import java.awt.Color;

public class NotAPuzzleException extends PuzzleException
{
    public NotAPuzzleException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` does not exist");
    }
}
