package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import exception.war.puzzle.NotAPuzzleException;
import exception.war.puzzle.PuzzleAlreadyEndedException;
import exception.war.puzzle.PuzzleNotStartedException;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.puzzle.Puzzle;

import java.util.List;

public class PuzzleStopCommand
{
    private static final String NAME = "stop";
    private static final String DESCRIPTION = "Stop a puzzle";
    private static final String SYNTAX = "<puzzle name>";

    private PuzzleStopCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(PuzzleStopCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("puzzle name");

        PuzzleStopFunctionality functionality = new PuzzleStopFunctionality(info, vars, session);
        functionality.execute();
    }

    private static class PuzzleStopFunctionality
    {
        private final MessageReceivedInformation info;
        private final String name;
        private final Session session;

        PuzzleStopFunctionality(MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.info = info;
            name = vars.get(0).toUpperCase();
            this.session = session;
        }

        void execute()
        {
            try
            {
                Puzzle.end(name, info.getTime(), session);
                DMessage.sendMessage(info.getChannel(), "Puzzle `" + name + "` has now ended!");
            }
            catch (PuzzleNotStartedException | PuzzleAlreadyEndedException | NotAPuzzleException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }
    }
}
