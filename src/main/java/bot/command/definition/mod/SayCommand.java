package bot.command.definition.mod;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import exception.bot.argument.TooManyArgumentsException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import sql.Session;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SayCommand
{
    private static final String NAME = "say";
    private static final String DESCRIPTION = "Say a message in the designated channel";
    private static final String SYNTAX = "<channel id> <message/attachments>";

    private SayCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(SayCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("channel id");
        else if (vars.size() == 1 && info.getAttachments().isEmpty())
            throw new MissingArgumentException("message/attachments");
        else if (info.getAttachments().size() > 1)
            throw new TooManyArgumentsException();

        TestResponseFunctionality functionality = new TestResponseFunctionality(api, info, vars);
        functionality.execute();
    }

    private static class TestResponseFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final long channelId;
        private final String response;

        TestResponseFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
        {
            this.api = api;
            this.info = info;
            channelId = Long.parseLong(vars.remove(0));
            StringBuilder builder = new StringBuilder();
            for (String var: vars)
            {
                String piece = var + " ";
                builder.append(piece);
            }
            if (builder.length() > 0)
            {
                builder.deleteCharAt(builder.length() - 1);
            }

            response = builder.toString();
        }

        void execute()
        {
            TextChannel channel = DChannel.getChannel(api, channelId);
            if (!response.isEmpty())
            {
                CompletableFuture<Message> futureMessage;
                if (!info.getAttachments().isEmpty())
                    futureMessage = DMessage.sendMessage(channel, response, info.getAttachments().get(0));
                else
                    futureMessage = DMessage.sendMessage(channel, response);

                futureMessage.thenAccept(completedMessage ->
                    DMessage.sendMessage(info.getChannel(), "Message sent to <#" + channelId + ">"));
            }
            else
            {
                DMessage.sendMessage(channel, info.getAttachments().get(0)).thenAccept(completedMessage ->
                    DMessage.sendMessage(info.getChannel(), "Message sent to <#" + channelId + ">"));
            }
        }
    }
}
