package exception.war.puzzle;

import java.awt.Color;

public class PuzzleAlreadyStartedException extends PuzzleException
{
    public PuzzleAlreadyStartedException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` has already started");
    }
}
