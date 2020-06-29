package bot.command.definition.owner.stop;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class StopCommand
{
    private static final String NAME = "stop";
    private static final String DESCRIPTION = "Deactivate the bot";

    private StopCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER)
                .executor(StopCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        StopFunctionality functionality = new StopFunctionality(api, info);
        functionality.execute();
    }

    private static class StopFunctionality
    {
        MessageReceivedInformation info;
        DiscordApi api;

        StopFunctionality(DiscordApi api, MessageReceivedInformation info)
        {
            this.api = api;
            this.info = info;
        }

        void execute()
        {
            DMessage.sendMessage(info.getChannel(), "Disconnecting...");
            api.disconnect();
            System.exit(0);
        }
    }
}
