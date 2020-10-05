package exception.war.puzzle;

import java.awt.Color;

public class NotAPuzzleException extends PuzzleException
{
    public NotAPuzzleException()
    {
        super(Color.YELLOW, "Puzzle name does not exist");
    }
}
