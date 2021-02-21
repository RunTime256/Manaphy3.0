package exception.war.puzzle;

import exception.war.WarException;

import java.awt.Color;

public class PuzzleException extends WarException
{
    public PuzzleException(Color color)
    {
        super(color);
    }

    public PuzzleException(Color color, String message)
    {
        super(color, message);
    }
}
