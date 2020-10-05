package exception.war.puzzle;

import java.awt.Color;

public class AlreadySolvedPuzzleException extends PuzzleException
{
    public AlreadySolvedPuzzleException(String name)
    {
        super(Color.YELLOW, "You have already solved the puzzle `" + name + "`");
    }
}
