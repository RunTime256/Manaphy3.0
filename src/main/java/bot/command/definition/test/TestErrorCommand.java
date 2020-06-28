package bot.command.definition.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.listener.MessageReceivedInformation;
import bot.discord.message.DMessage;
import sql.Session;

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

    private static void function(MessageReceivedInformation info, List<String> vars, Session session)
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
            throw new RuntimeException();
        }
    }
}
