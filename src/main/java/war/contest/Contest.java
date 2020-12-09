package war.contest;

import exception.war.contest.NotAContestException;
import exception.war.contest.NotAContestPlaceException;
import sql.Session;

public class Contest
{
    public static boolean isContest(String name, Session session)
    {
        return session.getMapper(ContestMapper.class).isContest(name);
    }

    public static boolean isPlace(String name, int place, Session session)
    {
        return session.getMapper(ContestMapper.class).isPlace(name, place);
    }

    public static void addParticipant(String name, long userId, Session session)
    {
        if (!isContest(name, session))
            throw new NotAContestException(name);

        session.getMapper(ContestMapper.class).addParticipant(name, userId);
    }

    public static void addWinner(String name, long userId, int place, Session session)
    {
        if (!isContest(name, session))
            throw new NotAContestException(name);
        if (!isPlace(name, place, session))
            throw new NotAContestPlaceException(name, place);
        session.getMapper(ContestMapper.class).addWinner(name, userId, place);
    }
}
