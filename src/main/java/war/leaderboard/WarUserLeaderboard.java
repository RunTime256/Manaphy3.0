package war.leaderboard;

public class WarUserLeaderboard
{
    private final long userId;
    private final String teamName;
    private final int colorValue;
    private final int tokens;

    public WarUserLeaderboard(long userId, String teamName, int colorValue, int tokens)
    {
        this.userId = userId;
        this.teamName = teamName;
        this.tokens = tokens;
        this.colorValue = colorValue;
    }

    public long getUserId()
    {
        return userId;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public int getColorValue()
    {
        return colorValue;
    }

    public int getTokens()
    {
        return tokens;
    }
}
