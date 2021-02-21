package war.contest;

public class ContestUser
{
    private final long userId;
    private int participation;

    public ContestUser(long userId)
    {
        this.userId =  userId;
        participation = 0;
    }

    public void increaseParticipation()
    {
        participation++;
    }

    public long getUserId()
    {
        return userId;
    }

    public int getParticipation()
    {
        return participation;
    }
}
