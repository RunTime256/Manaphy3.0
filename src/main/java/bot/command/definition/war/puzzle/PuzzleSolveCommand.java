package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class PuzzleSolveCommand
{
    private static final String NAME = "solve";
    private static final String DESCRIPTION = "Solve a puzzle";
    private static final String SYNTAX = "<puzzle name> <solution>";

    private PuzzleSolveCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.VERIFIED).executor(bot.command.definition.war.puzzle.PuzzleSolveCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        PuzzleSolveFunctionality functionality = new PuzzleSolveFunctionality();
        functionality.execute();
    }

    private static class PuzzleSolveFunctionality
    {
        PuzzleSolveFunctionality()
        {
        }

        void execute()
        {
        }
    }
}
