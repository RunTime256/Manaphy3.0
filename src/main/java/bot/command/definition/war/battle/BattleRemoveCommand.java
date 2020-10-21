package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class BattleRemoveCommand
{
    private static final String NAME = "remove";
    private static final String DESCRIPTION = "Remove an invalid battle";
    private static final String SYNTAX = "<battle id>";

    private BattleRemoveCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .syntax(SYNTAX).executor(BattleRemoveCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("battle id");

        BattleRemoveFunctionality functionality = new BattleRemoveFunctionality();
        functionality.execute();
    }

    private static class BattleRemoveFunctionality
    {

        BattleRemoveFunctionality()
        {
        }

        void execute()
        {
        }
    }
}
