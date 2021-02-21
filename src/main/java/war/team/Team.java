package war.team;

import sql.Session;
import util.WeightedRandomNumber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Team
{
    private Team()
    {
    }

    public static WarTeam joinTeam(long userId, String selectedClass, Instant joinTime, Session session)
    {
        TeamMapper mapper = session.getMapper(TeamMapper.class);
        List<WarTeam> teams = mapper.getTeamsByClass(selectedClass);
        List<Integer> counts = new ArrayList<>();
        for (WarTeam team: teams)
            counts.add(team.getMemberCount());

        WarTeam team = teams.get(new WeightedRandomNumber(counts).calculateNewMemberLocation());

        mapper.addTeamMember(userId, team.getShortName(), selectedClass, joinTime);

        return team;
    }

    public static WarTeam joinPrewarTeam(long userId, int tokens, Instant joinTime, Session session)
    {
        String selectedClass = "prewar";
        TeamMapper mapper = session.getMapper(TeamMapper.class);
        List<WarTeam> teams = mapper.getTeamsByClass(selectedClass);
        List<Integer> counts = new ArrayList<>();
        for (WarTeam team: teams)
            counts.add(team.getPrewarTokens());

        WarTeam team = teams.get(new WeightedRandomNumber(counts).calculateComplexMemberLocation(tokens));

        mapper.addTeamMember(userId, team.getShortName(), selectedClass, joinTime);

        return team;
    }

    public static WarTeam getTeam(long userId, Session session)
    {
        return session.getMapper(TeamMapper.class).getTeam(userId);
    }

    public static boolean isTeamMember(long userId, Session session)
    {
        return session.getMapper(TeamMapper.class).isTeamMember(userId);
    }

    public static boolean isBanned(long userId, Session session)
    {
        return session.getMapper(TeamMapper.class).isBanned(userId);
    }

    public static boolean onSameTeam(long user1, long user2, Session session)
    {
        return session.getMapper(TeamMapper.class).onSameTeam(user1, user2);
    }
}
