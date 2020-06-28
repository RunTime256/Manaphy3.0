package bot.discord.listener;

import bot.command.MessageCommand;
import bot.command.parser.MessageCommandParser;
import bot.command.verification.RoleCheck;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sql.Session;
import sql.SessionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            List<String> vars = new ArrayList<>(Arrays.asList(commandString.split(" ")));
            MessageCommand command = parser.getCommand(vars);
            DiscordApi api = messageCreateEvent.getApi();
            Optional<User> optionalUser = messageCreateEvent.getMessageAuthor().asUser();
            optionalUser.ifPresent(user -> executeCommand(api, user, vars, command));
        }
    }

    private void executeCommand(DiscordApi api, User user, List<String> vars, MessageCommand command)
    {
        try (Session session = SessionFactory.getSession())
        {
            if (RoleCheck.hasPermission(session, api, user, command.getRequirement()))
            {
                try
                {
                    command.execute(vars, session);
                    session.commit();
                }
                catch (Exception e)
                {
                    session.rollback();
                }
            }
        }
    }
}
