package bot.command.definition.owner.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class TestResponseCommand
{
    private static final String NAME = "response";
    private static final String DESCRIPTION = "Test response functionality";
    private static final String SYNTAX = "<response>";

    private TestResponseCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.OWNER).executor(TestResponseCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        TestResponseFunctionality functionality = new TestResponseFunctionality(info, vars);
        functionality.execute();
    }

    private static class TestResponseFunctionality
    {
        private final MessageReceivedInformation info;
        private final String response;

        TestResponseFunctionality(MessageReceivedInformation info, List<String> vars)
        {
            this.info = info;
            StringBuilder builder = new StringBuilder();
            for (String var: vars)
            {
                String piece = var + " ";
                builder.append(piece);
            }
            if (builder.length() > 0)
            {
                builder.deleteCharAt(builder.length() - 1);
            }

            response = builder.toString();
        }

        void execute()
        {
            DMessage.sendMessage(info.getChannel(), response);
        }
    }
}
