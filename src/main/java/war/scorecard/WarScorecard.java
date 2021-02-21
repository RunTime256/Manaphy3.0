package war.scorecard;

public class WarScorecard
{
    private final long userId;
    private final int battleTokens;
    private final int puzzleTokens;
    private final int artTokens;
    private final int gameTokens;
    private final int bonusTokens;

    public WarScorecard(long userId, int battleTokens, int puzzleTokens, int artTokens, int gameTokens, int bonusTokens)
    {
        this.userId = userId;
        this.battleTokens = battleTokens;
        this.puzzleTokens = puzzleTokens;
        this.artTokens = artTokens;
        this.gameTokens = gameTokens;
        this.bonusTokens = bonusTokens;
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

    public int getBonusTokens()
    {
        return bonusTokens;
    }

    public int getTotalTokens()
    {
        return battleTokens + puzzleTokens + artTokens + gameTokens + bonusTokens;
    }
}
