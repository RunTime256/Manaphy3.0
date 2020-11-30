package war.scorecard;

import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import sql.Session;
import war.team.Team;

public class Scorecard
{
    private Scorecard()
    {
    }

    public static WarScorecard getScorecard(long userId, Session session)
    {
        if (!Team.isTeamMember(userId, session))
            throw new NotATeamMemberException(userId);
        if (Team.isBanned(userId, session))
            throw new BannedMemberException(userId);

        return session.getMapper(ScorecardMapper.class).getScorecard(userId);
    }
}
