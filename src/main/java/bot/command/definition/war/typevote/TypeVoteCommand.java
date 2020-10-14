package bot.command.definition.war.typevote;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class TypeVoteCommand
{
    private static final String NAME = "vote";
    private static final String DESCRIPTION = "Vote on Pokémon types";
    private static final String SYNTAX = "<type>";

    private TypeVoteCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.VERIFIED).executor(TypeVoteCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("type");
        if (vars.size() > 1)
            throw new IllegalArgumentException("Too many arguments");
        TypeVoteFunctionality functionality = new TypeVoteFunctionality(info, vars);
        functionality.execute();
    }

    private static class TypeVoteFunctionality
    {
        private final MessageReceivedInformation info;
        private final String type;

        TypeVoteFunctionality(MessageReceivedInformation info, List<String> vars)
        {
            this.info = info;
            type = vars.get(0);
        }

        void execute()
        {

        }
    }
}
