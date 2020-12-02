package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.definition.war.battle.grant.BattleGrantCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class BattleCommand
{
    private static final String NAME = "battle";
    private static final String DESCRIPTION = "Battle commands for the war";

    private BattleCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                BattleGrantCommand.createCommand(),
                BattleRemoveCommand.createCommand(),
                BattleStatsCommand.createCommand(),
                BattleSwapCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).build();
    }
}
