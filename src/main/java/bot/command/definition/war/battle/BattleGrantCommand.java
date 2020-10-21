package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class BattleGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Loser of a battle grants points to the winner";
    private static final String SYNTAX = "<user> <showdown replay>";

    private BattleGrantCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .syntax(SYNTAX).executor(BattleGrantCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("user");
        else if (vars.size() == 1)
            throw new MissingArgumentException("showdown replay");

        BattleGrantFunctionality functionality = new BattleGrantFunctionality();
        functionality.execute();
    }

    private static class BattleGrantFunctionality
    {

        BattleGrantFunctionality()
        {
        }

        void execute()
        {
        }
    }
}
