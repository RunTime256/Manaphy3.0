package war.fanart;

public class FanartUser
{
    private final long userId;
    private int participation;
    private int bonus;

    public FanartUser(long userId)
    {
        this.userId =  userId;
        participation = 0;
        bonus = 0;
    }

    public void increaseParticipation()
    {
        participation++;
    }

    public void increaseBonus()
    {
        bonus++;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getParticipation()
    {
        return participation;
    }

    public int getBonus()
    {
        return bonus;
    }
}
