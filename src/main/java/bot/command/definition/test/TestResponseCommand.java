package bot.command.definition.test;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

public class TestResponseCommand
{
    private static final String NAME = "error";
    private static final String DESCRIPTION = "Test response functionality";

    private TestResponseCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER).build();
    }
}
