package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.category.DCategory;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import exception.bot.command.InvalidCommandException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetCategoryCommand
{
    private static final String NAME = "category";
    private static final String DESCRIPTION = "Get category information";
    private static final String SYNTAX = "<category>";

    private GetCategoryCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetCategoryCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        GetCategoryFunctionality functionality = new GetCategoryFunctionality(api, info, vars);
        functionality.execute();
    }

    public static ChannelCategory getCategory(MessageReceivedInformation info, long id, String name)
    {
        ChannelCategory category = null;

        if (name.isBlank())
        {
            ServerChannel channel = info.getChannel().asServerChannel().orElse(null);
            if (channel != null)
                category = channel.asChannelCategory().orElse(null);
        }
        else
        {
            category = DCategory.getCategory(info.getServer(), id, name);
        }
        return category;
    }

    private static class GetCategoryFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetCategoryFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
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
            ChannelCategory category = getCategory(info, id, name);

            if (category == null)
                throw new InvalidCommandException("Server category `" + name + "` could not be found.");

            EmbedBuilder builder = categoryEmbed(category);
            DMessage.sendMessage(info.getChannel(), builder);
        }

        private EmbedBuilder categoryEmbed(ChannelCategory category)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Category: " + category.getName().toUpperCase())
                    .addField("ID:", category.getIdAsString(), true)
                    .addField("Server:", category.getServer().getName(), true)
                    .addField("Creation Date:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(category.getCreationTimestamp()) + " UTC", true)
                    .addField("Channel Count:", Integer.toString(category.getChannels().size()), true);

            return builder;
        }
    }
}
