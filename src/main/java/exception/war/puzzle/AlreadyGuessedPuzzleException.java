package exception.war.puzzle;

import java.awt.Color;

public class AlreadyGuessedPuzzleException extends PuzzleException
{
    public AlreadyGuessedPuzzleException(String name)
    {
        super(Color.YELLOW, "You have already submitted a guess for the puzzle `" + name + "`");
    }
}
