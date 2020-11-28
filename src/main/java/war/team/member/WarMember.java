package war.team.member;

public class WarMember
{
    private final long userId;
    private final int prewarTokens;

    public WarMember(long userId, int prewarTokens)
    {
        this.userId = userId;
        this.prewarTokens = prewarTokens;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getPrewarTokens()
    {
        return prewarTokens;
    }
}
