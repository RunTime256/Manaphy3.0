package exception.war.puzzle;

import java.awt.Color;

public class FuturePuzzleException extends PuzzleException
{
    public FuturePuzzleException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` is a future puzzle");
    }
}
