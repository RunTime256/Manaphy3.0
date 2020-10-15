package exception.war.puzzle;

import java.awt.Color;

public class PuzzleAlreadyEndedException extends PuzzleException
{
    public PuzzleAlreadyEndedException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` has already ended");
    }
}
