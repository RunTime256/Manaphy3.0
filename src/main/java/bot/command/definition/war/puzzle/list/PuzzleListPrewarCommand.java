package bot.command.definition.war.puzzle.list;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.puzzle.PuzzleMapper;

import java.awt.Color;
import java.util.List;

public class PuzzleListPrewarCommand
{
    private static final String NAME = "prewar";
    private static final String DESCRIPTION = "List of discovered and solved prewar puzzles";

    private PuzzleListPrewarCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .executor(PuzzleListPrewarCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        PuzzleListPrewarFunctionality functionality = new PuzzleListPrewarFunctionality(info, session);
        functionality.execute();
    }

    private static class PuzzleListPrewarFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;

        PuzzleListPrewarFunctionality(MessageReceivedInformation info, Session session)
        {
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            PuzzleMapper mapper = session.getMapper(PuzzleMapper.class);
            List<String> unsolvedDiscoveredInfinitePuzzles = mapper.getUnsolvedDiscoveredInfinitePrewarPuzzles(info.getUser().getId());
            List<String> solvedInfinitePuzzles = mapper.getSolvedInfinitePrewarPuzzles(info.getUser().getId());
            DMessage.sendMessage(info.getChannel(), createEmbed(unsolvedDiscoveredInfinitePuzzles, solvedInfinitePuzzles));
        }

        private EmbedBuilder createEmbed(List<String> unsolvedDiscoveredInfinitePuzzles, List<String> solvedInfinitePuzzles)
        {
            EmbedBuilder builder = new EmbedBuilder();
            User user = info.getUser();

            builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setDescription("Prewar puzzles")
                    .setColor(Color.ORANGE);
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
