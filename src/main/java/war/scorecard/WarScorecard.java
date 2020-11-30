package war.scorecard;

public class WarScorecard
{
    private final long userId;
    private final int battleTokens;
    private final int puzzleTokens;
    private final int artTokens;
    private final int gameTokens;
    private final int miscTokens;

    public WarScorecard(long userId, int battleTokens, int puzzleTokens, int artTokens, int gameTokens, int miscTokens)
    {
        this.userId = userId;
        this.battleTokens = battleTokens;
        this.puzzleTokens = puzzleTokens;
        this.artTokens = artTokens;
        this.gameTokens = gameTokens;
        this.miscTokens = miscTokens;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getBattleTokens()
    {
        return battleTokens;
    }

    public int getPuzzleTokens()
    {
        return puzzleTokens;
    }

    public int getArtTokens()
    {
        return artTokens;
    }

    public int getGameTokens()
    {
        return gameTokens;
    }

    public int getMiscTokens()
    {
        return miscTokens;
    }

    public int getTotalTokens()
    {
        return battleTokens + puzzleTokens + artTokens + gameTokens + miscTokens;
    }
}
