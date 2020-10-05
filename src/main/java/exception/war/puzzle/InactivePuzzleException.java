package exception.war.puzzle;

import java.awt.Color;

public class InactivePuzzleException extends PuzzleException
{
    public InactivePuzzleException(String name)
    {
        super(Color.YELLOW, "The puzzle `" + name + "` is no longer active");
    }
}
