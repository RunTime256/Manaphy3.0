package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.battle.Battle;

import java.util.List;

public class BattleFormatCommand
{
    private static final String NAME = "format";
    private static final String DESCRIPTION = "Set the bonus format for battles, or nothing for no bonus format";
    private static final String SYNTAX = "[format]";

    private BattleFormatCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .syntax(SYNTAX).executor(BattleFormatCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        BattleFormatFunctionality functionality = new BattleFormatFunctionality(info, vars, session);
        functionality.execute();
    }

    private static class BattleFormatFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;
        private final String format;

        BattleFormatFunctionality(MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.info = info;
            this.session = session;

            if (vars.isEmpty())
                format = "";
            else
                format = vars.get(0);
        }

        void execute()
        {
            Battle.updateBonusFormat(format, session);

            DMessage.sendMessage(info.getChannel(), "Bonus format has been set to `" + format + "`");
        }
    }
}
