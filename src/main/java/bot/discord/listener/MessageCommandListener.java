package bot.discord.listener;

import bot.command.MessageCommand;
import bot.command.parser.MessageCommandParser;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;


public class MessageCommandListener implements MessageCreateListener
{
    private final MessageCommandParser parser;
    private final String prefix;
    private final boolean botCommand;

    public MessageCommandListener(String prefix)
    {
        this(prefix, false);
    }

    public MessageCommandListener(String prefix, boolean botCommand)
    {
        parser = new MessageCommandParser();
        this.prefix = prefix;
        this.botCommand = botCommand;
    }

    public void addCommand(MessageCommand command)
    {
        parser.addCommand(command);
    }

    public void onMessageCreate(MessageCreateEvent messageCreateEvent)
    {
        String message = messageCreateEvent.getMessageContent();
        if (message != null && message.startsWith(prefix) &&
                (!botCommand && !messageCreateEvent.getMessageAuthor().isBotUser() ||
                        botCommand && messageCreateEvent.getMessageAuthor().isBotUser() && messageCreateEvent.getMessageAuthor().isYourself()))
        {
            String commandString = message.substring(prefix.length());
            MessageCommand command = parser.getCommand(commandString);
        }
    }
}
