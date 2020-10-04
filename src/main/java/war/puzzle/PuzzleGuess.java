package war.puzzle;

import java.time.Instant;

public class PuzzleGuess
{
    private final String name;
    private final String guess;
    private final Long userId;
    private final Instant time;

    public PuzzleGuess(String name, String puzzleGuess, Long userId, Instant time)
    {
        this.name = name;
        this.guess = puzzleGuess;
        this.userId = userId;
        this.time = time;
    }

    public String getName()
    {
        return name;
    }

    public String getGuess()
    {
        return guess;
    }

    public Long getUserId()
    {
        return userId;
    }

    public Instant getTime()
    {
        return time;
    }
}
