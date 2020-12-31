package bot.command.definition.war.achievements;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import bot.log.AchievementLogger;
import bot.util.IdExtractor;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.war.achievement.AchievementException;
import exception.war.achievement.AchievementFailedForUsersException;
import exception.war.achievement.NotAnAchievementException;
import exception.war.team.TeamException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.achievement.Achievement;
import war.achievement.WarAchievement;
import war.pair.Pair;
import war.team.Team;
import war.team.WarTeam;

import java.util.ArrayList;
import java.util.List;

public class AchievementGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Grant an achievement";
    private static final String SYNTAX = "<user> <achievement>";
    private static AchievementLogger achievementLogger;

    private AchievementGrantCommand()
    {
    }

    public static void setAchievementLogger(AchievementLogger achievementLogger)
    {
        AchievementGrantCommand.achievementLogger = achievementLogger;
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(AchievementGrantCommand::function).build();
    }

    public static MessageCommand createBotCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(AchievementGrantCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("user");
        else if (vars.size() == 1)
            throw new MissingArgumentException("achievement");

        boolean response;
        if (vars.size() == 3)
            response = Boolean.parseBoolean(vars.remove(2));
        else
            response = true;

        AchievementGrantFunctionality functionality = new AchievementGrantFunctionality(api, info, vars, session, response);
        functionality.execute();
    }

    private static class AchievementGrantFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final List<Long> userIds;
        final String achievementName;
        final boolean response;

        AchievementGrantFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session,
                                      boolean response)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            this.response = response;

            userIds = new ArrayList<>();
            for (int i = 0; i < vars.size() - 1; i++)
            {
                long userId = IdExtractor.getId(vars.get(i));
                if (userId == 0)
                    throw new InvalidArgumentException("user");
                userIds.add(userId);
            }

            achievementName = vars.get(vars.size() - 1).toLowerCase();
        }

        void execute()
        {
            try
            {
                grantAchievements();
            }
            catch (TeamException | AchievementException e)
            {
                if (response)
                    DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        private void grantAchievements()
        {
            if (!Achievement.isAchievement(achievementName, session))
                throw new NotAnAchievementException(achievementName);

            List<Long> bannedMembers = new ArrayList<>();
            List<Long> alreadyObtained = new ArrayList<>();
            List<Long> notUsers = new ArrayList<>();

            for (long userId: userIds)
            {
                grantAchievement(bannedMembers, alreadyObtained, notUsers, userId);
            }

            if (!bannedMembers.isEmpty() || !alreadyObtained.isEmpty() || !notUsers.isEmpty())
                throw new AchievementFailedForUsersException(bannedMembers, alreadyObtained, notUsers);
        }

        private void grantAchievement(List<Long> bannedMembers, List<Long> alreadyObtained, List<Long> notUsers, long userId)
        {
            if (Team.isTeamMember(userId, session) && Team.isBanned(userId, session))
            {
                bannedMembers.add(userId);
                return;
            }

            if (Achievement.hasAchievement(userId, achievementName, session))
            {
                alreadyObtained.add(userId);
                return;
            }

            User user = DUser.getUser(api, userId);

            if (user == null)
            {
                notUsers.add(userId);
                return;
            }

            Achievement.grantAchievement(userId, achievementName, info.getTime(), session);
            WarAchievement achievement = Achievement.getAchievement(achievementName, session);
            WarTeam team;
            if (Team.isTeamMember(userId, session))
                team = Team.getTeam(userId, session);
            else
                team = null;

            if (achievementLogger != null)
            {
                if (!achievement.getCategory().equalsIgnoreCase("secret") || achievement.getName().startsWith("mod-"))
                    achievementLogger.log(user, achievement, team);
                else
                    achievementLogger.logSecret(user, team, Pair.getValue("secret_achievement", session));
            }
            DMessage.sendPrivateMessage(api, userId, achievementEmbed(user, achievement, team));
            if (response)
                DMessage.sendMessage(info.getChannel(), "Medal `" + achievementName + "` granted to user `" + userId + "`");
        }

        private EmbedBuilder achievementEmbed(User user, WarAchievement achievement, WarTeam team)
        {
            EmbedBuilder builder = new EmbedBuilder();

            String title = String.format("%s - %s", achievement.getCategoryEmoji(),achievement.getFullName());
            String description = "*" + achievement.getDescription() + "*\n`Unlock: " + achievement.getUnlockMethod() + "`" +
                    "\n\nUse the command `+war medals` to view all medals earned.";
            builder.setTitle(title).setDescription(description).setThumbnail(achievement.getImage())
                    .addField("Value:", String.valueOf(achievement.getValue()));

            String authorName = "You earned a medal!";
            builder.setAuthor(authorName, null, user.getAvatar());
            if (team != null)
                builder.setColor(team.getColor());

            return builder;
        }
    }
}
