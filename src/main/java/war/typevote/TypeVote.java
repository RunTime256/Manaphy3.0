package war.typevote;

import sql.Session;

import java.time.Instant;
import java.util.List;

public class TypeVote
{
    public static final int TOTAL_VOTES = 3;

    private TypeVote()
    {
    }

    public static List<String> getAvailableTypes(long userId, Session session)
    {
        return session.getMapper(TypeVoteMapper.class).getAvailableTypes(userId);
    }

    public static List<String> getVotedTypes(long userId, Session session)
    {
        return session.getMapper(TypeVoteMapper.class).getVotedTypes(userId);
    }

    public static int getRemainingTypeVoteCount(long userId, Session session)
    {
        return session.getMapper(TypeVoteMapper.class).getRemainingTypeVoteCount(userId);
    }

    public void addTypeVote(String type, long userId, Instant time, Session session)
    {
        session.getMapper(TypeVoteMapper.class).addTypeVote(type, userId, time);
    }
}
