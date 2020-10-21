package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class BattleStatsCommand
{
    private static final String NAME = "stats";
    private static final String DESCRIPTION = "Look up your battle stats";
    private static final String SYNTAX = "[user]";

    private BattleStatsCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .syntax(SYNTAX).executor(BattleStatsCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        BattleStatsFunctionality functionality = new BattleStatsFunctionality();
        functionality.execute();
    }

    private static class BattleStatsFunctionality
    {

        BattleStatsFunctionality()
        {
        }

        void execute()
        {
        }
    }
}
