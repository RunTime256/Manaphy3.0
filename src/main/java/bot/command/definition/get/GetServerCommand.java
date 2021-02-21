package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.server.DServer;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import sql.Session;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetServerCommand
{
    private static final String NAME = "server";
    private static final String DESCRIPTION = "Get server information";
    private static final String SYNTAX = "<server>";

    private GetServerCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetServerCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        GetServerFunctionality functionality = new GetServerFunctionality(api, info, vars);
        functionality.execute();
    }

    public static Server getServer(MessageReceivedInformation info, Long id, String name)
    {
        Server server;
        if (name.isBlank())
        {
            server = info.getServer();
        }
        else
        {
            server = DServer.getServer(info.getApi(), id, name);
            if (server != null && server.getId() != info.getServer().getId())
                server = null;
        }
        return server;
    }

    private static class GetServerFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetServerFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
        {
            this.api = api;
            this.info = info;
            this.name = CombineContent.combine(vars);
            if (vars.size() == 1)
                id = IdExtractor.getId(name);
            else
                id = 0L;
        }

        void execute()
        {
            Server server = getServer(info, id, name);

            if (server != null)
            {
                EmbedBuilder builder = serverEmbed(server);
                DMessage.sendMessage(info.getChannel(), builder);
            }
            else
            {
                DMessage.sendMessage(info.getChannel(), "Server `" + name + "` could not be found.");
            }
        }

        private EmbedBuilder serverEmbed(Server server)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Server: " + server.getName())
                    .addField("ID:", server.getIdAsString(), true)
                    .addField("Creation Date:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(server.getCreationTimestamp()) + " UTC", true)
                    .addField("Owner:", server.getOwner().getDiscriminatedName(), true)
                    .addField("Member Count:", Integer.toString(server.getMemberCount()), true)
                    .addField("Channel Count:", Integer.toString(server.getTextChannels().size()), true)
                    .addField("Role Count:", Integer.toString(server.getRoles().size()), true)
                    .addField("Emoji Count:", Integer.toString(server.getCustomEmojis().size()))
                    .addField("Nitro Booster Count:", Integer.toString(server.getBoostCount()));

            server.getIcon().ifPresent(icon -> builder.setThumbnail(icon.getUrl().toString()));

            return builder;
        }
    }
}
