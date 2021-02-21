package bot.command.definition.war.fanart;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class FanartCommand
{
    private static final String NAME = "fanart";
    private static final String DESCRIPTION = "Fanart commands for the war";

    private FanartCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                FanartAddCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .subCommands(subCommands).build();
    }
}
