package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class GetCommand
{
    private static final String NAME = "get";
    private static final String DESCRIPTION = "Get information";

    private GetCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                GetTextChannelCommand.createCommand(),
                GetVoiceChannelCommand.createCommand(),
                GetCategoryCommand.createCommand(),
                GetRoleCommand.createCommand(),
                GetServerCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER)
                .subCommands(subCommands).build();
    }
}
