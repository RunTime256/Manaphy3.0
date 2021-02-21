package bot.command.definition.war.achievements;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.war.achievement.AchievementAlreadyExistsException;
import exception.war.achievement.NotAnAchievementCategoryException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;
import war.achievement.Achievement;
import war.achievement.WarAchievement;

import java.util.List;

public class AchievementCreateCommand
{
    private static final String NAME = "create";
    private static final String DESCRIPTION = "Create an achievement.";
    private static final String SYNTAX = "<category> <name> <full name> <description> <unlock method> <image url> <difficulty>";

    private AchievementCreateCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(AchievementCreateCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("category");
        else if (vars.size() == 1)
            throw new MissingArgumentException("name");
        else if (vars.size() == 2)
            throw new MissingArgumentException("full name");
        else if (vars.size() == 3)
            throw new MissingArgumentException("description");
        else if (vars.size() == 4)
            throw new MissingArgumentException("unlock method");

        AchievementCreateFunctionality functionality = new AchievementCreateFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class AchievementCreateFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final String category;
        final String name;
        final String fullName;
        final String description;
        final String unlockMethod;
        final String imageUrl;
        final int difficulty;

        AchievementCreateFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            category = vars.remove(0).toLowerCase();
            name = vars.remove(0).toLowerCase();

            if (vars.isEmpty())
                throw new MissingArgumentException("full name");
            fullName = CombineContent.combineQuotes(vars);
            if (fullName == null)
                throw new InvalidArgumentException("full name");

            if (vars.isEmpty())
                throw new MissingArgumentException("description");
            description = CombineContent.combineQuotes(vars);
            if (description == null)
                throw new InvalidArgumentException("description");

            if (vars.isEmpty())
                throw new MissingArgumentException("unlock method");
            unlockMethod = CombineContent.combineQuotes(vars);
            if (unlockMethod == null)
                throw new InvalidArgumentException("unlock method");

            if (vars.isEmpty())
                throw new MissingArgumentException("image url");
            imageUrl = vars.remove(0);
            if (!imageUrl.startsWith("http"))
                throw new InvalidArgumentException("image url");

            if (vars.isEmpty())
                throw new MissingArgumentException("difficulty");
            try
            {
                difficulty = Integer.parseInt(vars.remove(0));
            }
            catch (NumberFormatException ignored)
            {
                throw new InvalidArgumentException("difficulty");
            }
        }

        void execute()
        {
            if (Achievement.isAchievement(name, session))
                throw new AchievementAlreadyExistsException(name);
            else if (!Achievement.isCategory(category, session))
                throw new NotAnAchievementCategoryException(category);

            Achievement.createAchievement(name, fullName, description, category, imageUrl, unlockMethod, difficulty, session);
            WarAchievement achievement = Achievement.getAchievement(name, session);

            DMessage.sendMessage(info.getChannel(), userAchievementEmbed(achievement));
        }

        private EmbedBuilder userAchievementEmbed(WarAchievement achievement)
        {
            EmbedBuilder builder = new EmbedBuilder();

            // Create embed.
            String title = String.format("%s - %s", achievement.getCategoryEmoji(), achievement.getFullName());
            String achievementDescription = "*" + achievement.getDescription() + "*\n`Unlock: " + achievement.getUnlockMethod() + "`";
            builder.setTitle(title).setDescription(achievementDescription).setThumbnail(achievement.getImage());

            // TODO: ADD MORE INFORMATION ?

            return builder;
        }
    }
}
