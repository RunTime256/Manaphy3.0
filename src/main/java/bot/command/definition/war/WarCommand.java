package bot.command.definition.war;

import bot.command.MessageCommand;
import bot.command.definition.war.puzzle.PuzzleCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class WarCommand
{
    private static final String NAME = "war";
    private static final String DESCRIPTION = "Commands for the server war";

    private WarCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                PuzzleCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).build();
    }
}
