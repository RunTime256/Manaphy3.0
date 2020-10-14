package bot.command.definition.war.typevote;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;
import war.typevote.TypeVote;

import java.util.Arrays;
import java.util.List;

public class TypeCommand
{
    private static final String NAME = "type";
    private static final String DESCRIPTION = "Pokémon types that can be selected";

    private TypeCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                TypeVoteCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).executor(TypeCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        TypeFunctionality functionality = new TypeFunctionality(info, session);
        functionality.execute();
    }

    private static class TypeFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;

        TypeFunctionality(MessageReceivedInformation info, Session session)
        {
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            long userId = info.getUser().getId();
            List<String> available = TypeVote.getAvailableTypes(userId, session);
            List<String> voted = TypeVote.getVotedTypes(userId, session);
            available.sort(String::compareTo);
            voted.sort(String::compareTo);

            DMessage.sendMessage(info.getChannel(), createTypeEmbed(available, voted));
        }

        private EmbedBuilder createTypeEmbed(List<String> available, List<String> voted)
        {
            EmbedBuilder builder = new EmbedBuilder();

            int total = TypeVote.TOTAL_VOTES;
            int count = voted.size();
            String combinedVoted = CombineContent.combine(voted).replace(" ", "\n");
            String description;

            if (count >= total)
            {
                description = "You have voted for all of your types:";
            }
            else
            {
                description = "Use the command `+war type vote <type>` to choose a type to vote for (" + (total - count) + "/" + total + " votes remain):";

                String combinedAvailable = CombineContent.combine(available).replace(" ", "\n");

                builder.addField("Available", combinedAvailable);
            }

            builder.setTitle("Type Vote").setDescription(description)
                    .addField("Voted", combinedVoted);

            return builder;
        }
    }
}
