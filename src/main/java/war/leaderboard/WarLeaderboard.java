package war.leaderboard;

public class WarLeaderboard
{
    private final String teamName;
    private final int colorValue;
    private final int tokens;

    public WarLeaderboard(String teamName, int colorValue, int tokens)
    {
        this.teamName = teamName;
        this.tokens = tokens;
        this.colorValue = colorValue;
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
