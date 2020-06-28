package bot.command.definition.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class TestCommand
{
    private static final String NAME = "test";
    private static final String DESCRIPTION = "Test basic functionality";

    private TestCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                TestResponseCommand.createCommand(),
                TestErrorCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER)
                .subCommands(subCommands).build();
    }
}
