package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class BattleSwapCommand
{
    private static final String NAME = "swap";
    private static final String DESCRIPTION = "Swap the winner and loser from a battle if submitted incorrectly";
    private static final String SYNTAX = "<battle id>";

    private BattleSwapCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .syntax(SYNTAX).executor(BattleSwapCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("battle id");

        BattleSwapFunctionality functionality = new BattleSwapFunctionality();
        functionality.execute();
    }

    private static class BattleSwapFunctionality
    {

        BattleSwapFunctionality()
        {
        }

        void execute()
        {
        }
    }
}
