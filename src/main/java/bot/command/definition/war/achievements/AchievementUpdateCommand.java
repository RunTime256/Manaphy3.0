package bot.command.definition.war.achievements;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class AchievementUpdateCommand
{
    private static final String NAME = "update";
    private static final String DESCRIPTION = "Update an achievement.";
    private static final String SYNTAX = "<field> <achievement> <new value>";

    private AchievementUpdateCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(AchievementUpdateCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("field");
        if (vars.size() == 1)
            throw new MissingArgumentException("achievement");

        AchievementUpdateFunctionality functionality = new AchievementUpdateFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class AchievementUpdateFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final String field;
        final String name;
        final String value;

        AchievementUpdateFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            field = vars.remove(0);
            name = vars.remove(1);
            value = CombineContent.combine(vars);
        }

        void execute()
        {
            // TODO: update achievement object to store.

            switch (field.toLowerCase()) {
                case ("description"):
                    // TODO: THIS
                    break;
                case ("method"):
                    // TODO: THIS
                    break;
                case ("display_name"):
                    // TODO: THIS
                    break;
                case ("image_url"):
                    // TODO: THIS
                    break;
                default:
                    DMessage.sendMessage(info.getChannel(), "Invalid field name passed.");
                    return;
            }
        }
    }
}
