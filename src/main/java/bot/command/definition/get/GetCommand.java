package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.util.Arrays;
import java.util.List;

public class GetCommand
{
    private static final String NAME = "get";
    private static final String DESCRIPTION = "Get information";
    private static final String SYNTAX = "<item>";

    private GetCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                GetUserCommand.createCommand(),
                GetTextChannelCommand.createCommand(),
                GetVoiceChannelCommand.createCommand(),
                GetCategoryCommand.createCommand(),
                GetRoleCommand.createCommand(),
                GetServerCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .subCommands(subCommands).executor(GetCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        GetFunctionality functionality = new GetFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class GetFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;
        final List<String> vars;
        final Session session;

        GetFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.name = CombineContent.combine(vars);
            this.vars = vars;
            this.session = session;
            if (vars.size() == 1)
                id = IdExtractor.getId(name);
            else
                id = 0L;
        }

        void execute()
        {
            User user = GetUserCommand.getUser(info, id, name);
            ServerTextChannel textChannel = GetTextChannelCommand.getTextChannel(info, id, name);
            ServerVoiceChannel voiceChannel = GetVoiceChannelCommand.getVoiceChannel(info, id, name);
            ChannelCategory category = GetCategoryCommand.getCategory(info, id, name);
            Role role = GetRoleCommand.getRole(info, id, name);
            Server server = GetServerCommand.getServer(info, id, name);

            if (user != null)
                GetUserCommand.function(api, info, vars, session);
            else if (textChannel != null)
                GetTextChannelCommand.function(api, info, vars, session);
            else if (voiceChannel != null)
                GetVoiceChannelCommand.function(api, info, vars, session);
            else if (category != null)
                GetCategoryCommand.function(api, info, vars, session);
            else if (role != null)
                GetRoleCommand.function(api, info, vars, session);
            else if (server != null)
                GetServerCommand.function(api, info, vars, session);
            else
                DMessage.sendMessage(info.getChannel(), "Item `" + name + "` could not be found.");
        }
    }
}
