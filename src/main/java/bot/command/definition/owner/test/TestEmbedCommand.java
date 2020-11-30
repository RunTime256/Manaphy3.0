package bot.command.definition.owner.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.util.List;

public class TestEmbedCommand
{
    private static final String NAME = "embed";
    private static final String DESCRIPTION = "Test embed functionality";

    private TestEmbedCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .requirement(RoleRequirement.OWNER).executor(TestEmbedCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        TestResponseFunctionality functionality = new TestResponseFunctionality(info);
        functionality.execute();
    }

    private static class TestResponseFunctionality
    {
        private final MessageReceivedInformation info;

        TestResponseFunctionality(MessageReceivedInformation info)
        {
            this.info = info;
        }

        void execute()
        {
            DMessage.sendMessage(info.getChannel(), new EmbedBuilder());
        }
    }
}
