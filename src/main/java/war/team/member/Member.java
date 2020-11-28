package war.team.member;

import sql.Session;

public class Member
{
    private Member()
    {
    }

    public static int getPrewarTokens(long userId, Session session)
    {
        return session.getMapper(MemberMapper.class).getPrewarTokens(userId);
    }
}
