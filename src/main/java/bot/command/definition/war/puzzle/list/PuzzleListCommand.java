package bot.command.definition.war.puzzle.list;

import bot.command.MessageCommand;
import bot.command.definition.war.puzzle.PuzzleSolveCommand;
import bot.command.definition.war.puzzle.PuzzleStartCommand;
import bot.command.definition.war.puzzle.PuzzleStopCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.puzzle.PuzzleMapper;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class PuzzleListCommand
{
    private static final String NAME = "list";
    private static final String DESCRIPTION = "List of discovered and solved puzzles";

    private PuzzleListCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                PuzzleListPrewarCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).executor(PuzzleListCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        PuzzleListFunctionality functionality = new PuzzleListFunctionality(info, session);
        functionality.execute();
    }

    private static class PuzzleListFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;

        PuzzleListFunctionality(MessageReceivedInformation info, Session session)
        {
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            PuzzleMapper mapper = session.getMapper(PuzzleMapper.class);
            List<String> guessedPuzzles = mapper.getGuessedPuzzles(info.getUser().getId(), info.getTime());
            List<String> solvedPuzzles = mapper.getSolvedPuzzles(info.getUser().getId(), info.getTime());
            List<String> unsolvedDiscoveredInfinitePuzzles = mapper.getUnsolvedDiscoveredInfinitePuzzles(info.getUser().getId());
            List<String> solvedInfinitePuzzles = mapper.getSolvedInfinitePuzzles(info.getUser().getId());
            DMessage.sendMessage(info.getChannel(), createEmbed(guessedPuzzles, solvedPuzzles,
                    unsolvedDiscoveredInfinitePuzzles, solvedInfinitePuzzles));
        }

        private EmbedBuilder createEmbed(List<String> guessedPuzzles, List<String> solvedPuzzles,
                List<String> unsolvedDiscoveredInfinitePuzzles, List<String> solvedInfinitePuzzles)
        {
            EmbedBuilder builder = new EmbedBuilder();
            User user = info.getUser();

            builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setDescription("Puzzles")
                    .setColor(Color.ORANGE);
            if (!(guessedPuzzles.isEmpty() && solvedPuzzles.isEmpty()))
            {
                StringBuilder puzzles = new StringBuilder();
                if (!guessedPuzzles.isEmpty())
                {
                    puzzles.append("**Guessed Puzzles:**\n");
                    for (String puzzle: guessedPuzzles)
                    {
                        String temp = puzzle + "\n";
                        puzzles.append(temp);
                    }
                    puzzles.append("\n");
                }
                if (!solvedPuzzles.isEmpty())
                {
                    puzzles.append("**Solved Puzzles:**\n");
                    for (String puzzle: solvedPuzzles)
                    {
                        String temp = puzzle + "\n";
                        puzzles.append(temp);
                    }
                }
                builder.addField("Single-guess Puzzles", puzzles.toString());
            }

            if (!(unsolvedDiscoveredInfinitePuzzles.isEmpty() && solvedInfinitePuzzles.isEmpty()))
            {
                StringBuilder puzzles = new StringBuilder();
                if (!unsolvedDiscoveredInfinitePuzzles.isEmpty())
                {
                    puzzles.append("**Discovered Puzzles:**\n");
                    for (String puzzle: unsolvedDiscoveredInfinitePuzzles)
                    {
                        String temp = puzzle + "\n";
                        puzzles.append(temp);
                    }
                    puzzles.append("\n");
                }
                if (!solvedInfinitePuzzles.isEmpty())
                {
                    puzzles.append("**Solved Puzzles:**\n");
                    for (String puzzle: solvedInfinitePuzzles)
                    {
                        String temp = puzzle + "\n";
                        puzzles.append(temp);
                    }
                }
                builder.addField("Multi-guess Puzzles", puzzles.toString());
            }

            return builder;
        }
    }
}
