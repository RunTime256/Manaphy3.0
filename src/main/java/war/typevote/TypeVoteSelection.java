package war.typevote;

import java.time.Instant;

public class TypeVoteSelection
{
    private final String type;
    private final int count;
    private final long userId;
    private final Instant time;

    public TypeVoteSelection(String type, int count, long userId, Instant time)
    {
        this.type = type;
        this.count = count;
        this.userId = userId;
        this.time = time;
    }

    public String getType()
    {
        return type;
    }

    public int getCount()
    {
        return count;
    }

    public long getUserId()
    {
        return userId;
    }

    public Instant getTime()
    {
        return time;
    }
}
