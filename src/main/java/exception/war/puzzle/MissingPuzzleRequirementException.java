package exception.war.puzzle;

import java.awt.Color;

public class MissingPuzzleRequirementException extends PuzzleException
{
    public MissingPuzzleRequirementException(String name)
    {
        super(Color.YELLOW, "You are missing a requirement for the puzzle `" + name + "`");
    }
}
