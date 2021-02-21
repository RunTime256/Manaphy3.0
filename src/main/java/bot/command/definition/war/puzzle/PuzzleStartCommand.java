package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import exception.war.puzzle.NotAPuzzleException;
import exception.war.puzzle.PuzzleAlreadyEndedException;
import exception.war.puzzle.PuzzleAlreadyStartedException;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.puzzle.Puzzle;

import java.util.List;

public class PuzzleStartCommand
{
    private static final String NAME = "start";
    private static final String DESCRIPTION = "Start a puzzle";
    private static final String SYNTAX = "<puzzle name>";

    private PuzzleStartCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(PuzzleStartCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("puzzle name");

        PuzzleStartFunctionality functionality = new PuzzleStartFunctionality(info, vars, session);
        functionality.execute();
    }

    private static class PuzzleStartFunctionality
    {
        private final MessageReceivedInformation info;
        private final String name;
        private final Session session;

        PuzzleStartFunctionality(MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.info = info;
            name = vars.get(0).toUpperCase();
            this.session = session;
        }

        void execute()
        {
            try
            {
                Puzzle.start(name, info.getTime(), session);
                DMessage.sendMessage(info.getChannel(), "Puzzle `" + name + "` has now started!");
            }
            catch (PuzzleAlreadyStartedException | PuzzleAlreadyEndedException | NotAPuzzleException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }
    }
}
