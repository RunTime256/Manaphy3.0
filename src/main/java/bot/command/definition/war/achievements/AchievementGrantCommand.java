package bot.command.definition.war.achievements;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import bot.util.IdExtractor;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.discord.user.UserDoesNotExistException;
import exception.war.achievement.AchievementAlreadyObtainedException;
import exception.war.achievement.AchievementException;
import exception.war.achievement.NotAnAchievementException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.achievement.Achievement;

import java.util.List;

public class AchievementGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Grant an achievement";
    private static final String SYNTAX = "<user> <achievement>";

    private AchievementGrantCommand()
    {
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

        AchievementGrantFunctionality functionality = new AchievementGrantFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class AchievementGrantFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final long userId;
        final String achievementName;

        AchievementGrantFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            userId = IdExtractor.getId(vars.get(0));
            if (userId == 0)
                throw new InvalidArgumentException("user");

            achievementName = vars.get(1).toLowerCase();
        }

        void execute()
        {
            try
            {
                grantAchievement();
            }
            catch (AchievementException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        private void grantAchievement()
        {
            if (!Achievement.isAchievement(achievementName, session))
                throw new NotAnAchievementException(achievementName);
            else if (Achievement.hasAchievement(userId, achievementName, session))
                throw new AchievementAlreadyObtainedException(userId, achievementName);

            User user = DUser.getUser(api, userId);

            if (user == null) {
                throw new UserDoesNotExistException(userId);
            }

            Achievement.grantAchievement(userId, achievementName, info.getTime(), session);
            DMessage.sendMessage(info.getChannel(), "Achievement `" + achievementName + "` granted to user `" + userId + "`");
        }
    }
}
