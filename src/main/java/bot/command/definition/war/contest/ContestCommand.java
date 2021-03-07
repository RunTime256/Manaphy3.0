package bot.command.definition.war.contest;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class ContestCommand
{
    private static final String NAME = "contest";
    private static final String DESCRIPTION = "Contest commands for the war";

    private ContestCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                ContestAddCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .subCommands(subCommands).build();
    }
}
