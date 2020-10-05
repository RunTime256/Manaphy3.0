package bot.command.definition.owner.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import exception.BotException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.awt.Color;
import java.util.List;

public class TestErrorCommand
{
    private static final String NAME = "error";
    private static final String DESCRIPTION = "Test error functionality";

    private TestErrorCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER)
                .executor(TestErrorCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        TestErrorFunctionality functionality = new TestErrorFunctionality();
        functionality.execute();
    }

    private static class TestErrorFunctionality
    {
        TestErrorFunctionality()
        {
        }

        void execute()
        {
            throw new BotException(Color.GREEN, "Test exception succeeded!");
        }
    }
}
