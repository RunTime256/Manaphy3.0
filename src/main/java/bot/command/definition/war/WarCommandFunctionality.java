package bot.command.definition.war;

import exception.bot.command.InvalidCommandException;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import sql.Session;
import war.team.Team;

public class WarCommandFunctionality
{
    protected void checkBanned(long userId, Session session)
    {
        if (Team.isBanned(userId, session))
            throw new BannedMemberException(userId);
    }

    protected void checkTeamMember(long userId, Session session)
    {
        if (!Team.isTeamMember(userId, session))
            throw new NotATeamMemberException(userId);
    }

    protected void checkPrerequisites(long userId, Session session)
    {
        try
        {
            checkTeamMember(userId, session);
            checkBanned(userId, session);
        }
        catch (BannedMemberException e)
        {
            throw new InvalidCommandException("You are banned from the war.");
        }
        catch (NotATeamMemberException e)
        {
            throw new InvalidCommandException("You have not joined the war yet. Use the command `+war join` to be chosen for a team.");
        }
    }
}
