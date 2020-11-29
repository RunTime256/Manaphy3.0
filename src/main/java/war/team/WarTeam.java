package war.team;

import java.awt.Color;

public class WarTeam
{
    private final long roleId;
    private final String shortName;
    private final String fullName;
    private final String welcomeText;
    private final String leaderImage;
    private final String tokenImage;
    private final int colorValue;
    private final int memberCount;
    private final int prewarTokens;

    private Color color;

    public WarTeam(long roleId, String shortName, String fullName, String welcomeText, String leaderImage, String tokenImage,
                   int colorValue, int memberCount, int prewarTokens)
    {
        this.roleId = roleId;
        this.shortName = shortName;
        this.fullName = fullName;
        this.welcomeText = welcomeText;
        this.leaderImage = leaderImage;
        this.tokenImage = tokenImage;
        this.colorValue = colorValue;
        color = new Color(colorValue);

        this.memberCount = memberCount;
        this.prewarTokens = prewarTokens;
    }

    public long getRoleId()
    {
        return roleId;
    }

    public String getShortName()
    {
        return shortName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getWelcomeText()
    {
        return welcomeText;
    }

    public String getLeaderImage()
    {
        return leaderImage;
    }

    public String getTokenImage()
    {
        return tokenImage;
    }

    public Color getColor()
    {
        if (color != null)
            color = new Color(colorValue);

        return color;
    }

    public int getMemberCount()
    {
        return memberCount;
    }

    public int getPrewarTokens()
    {
        return prewarTokens;
    }
}
