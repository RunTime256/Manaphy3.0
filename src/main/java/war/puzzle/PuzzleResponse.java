package war.puzzle;

public class PuzzleResponse
{
    private boolean correct;
    private boolean infinite;

    public PuzzleResponse(boolean correct, boolean infinite)
    {
        this.correct = correct;
        this.infinite = infinite;
    }

    public boolean isCorrect()
    {
        return correct;
    }

    public boolean isInfinite()
    {
        return infinite;
    }
}
