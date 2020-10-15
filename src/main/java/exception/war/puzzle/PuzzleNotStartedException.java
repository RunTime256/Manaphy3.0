package exception.war.puzzle;

import java.awt.Color;

public class PuzzleNotStartedException extends PuzzleException
{
    public PuzzleNotStartedException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` has not started");
    }
}
