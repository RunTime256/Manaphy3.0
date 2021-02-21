package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.definition.war.puzzle.list.PuzzleListCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class PuzzleCommand
{
    private static final String NAME = "puzzle";
    private static final String DESCRIPTION = "Puzzle commands for the war";

    private PuzzleCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                PuzzleSolveCommand.createCommand(),
                PuzzleStartCommand.createCommand(),
                PuzzleStopCommand.createCommand(),
                PuzzleListCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .subCommands(subCommands).build();
    }

    public static MessageCommand createBotCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                PuzzleGrantCommand.createBotCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .subCommands(subCommands).build();
    }
}
