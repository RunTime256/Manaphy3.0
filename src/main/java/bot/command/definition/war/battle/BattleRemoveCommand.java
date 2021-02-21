package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.ShowdownUrlEvaluator;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.battle.Battle;

import java.util.List;

public class BattleRemoveCommand
{
    private static final String NAME = "remove";
    private static final String DESCRIPTION = "Remove an invalid battle";
    private static final String SYNTAX = "<showdown url>";

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
            throw new MissingArgumentException("showdown url");

        BattleRemoveFunctionality functionality = new BattleRemoveFunctionality(info, vars, session);
        functionality.execute();
    }

    private static class BattleRemoveFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;
        private final String url;

        BattleRemoveFunctionality(MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.info = info;
            this.session = session;
            url = ShowdownUrlEvaluator.convertUrl(vars.get(0));
        }

        void execute()
        {
            int count = Battle.deleteBattle(url, session);
            String message;
            if (count > 0)
                message = "Battle `" + url + "` successfully removed.";
            else
                message = "Battle `" + url + "` failed to remove.";

            DMessage.sendMessage(info.getChannel(), message);
        }
    }
}
