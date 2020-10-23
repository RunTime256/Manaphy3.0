package bot.discord.listener;

import bot.command.HelpMessageCommand;
import bot.command.MessageCommand;
import bot.command.parser.MessageCommandParser;
import bot.command.verification.RoleCheck;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.bot.argument.NoExecutorException;
import bot.log.ErrorLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sql.Session;
import sql.SessionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageCommandListener implements MessageCreateListener
{
    private static final Logger logger = LogManager.getLogger(MessageCommandListener.class);
    private final MessageCommandParser parser;
    private final String prefix;
    private final boolean botCommand;
    private final DiscordApi api;
    private final ErrorLogger errorLogger;

    public MessageCommandListener(String prefix, DiscordApi api, ErrorLogger errorLogger)
    {
        this(prefix, false, api, errorLogger);
    }

    public MessageCommandListener(String prefix, boolean botCommand, DiscordApi api, ErrorLogger errorLogger)
    {
        parser = new MessageCommandParser();
        this.prefix = prefix;
        this.botCommand = botCommand;
        this.api = api;
        this.errorLogger = errorLogger;
    }

    public void addCommand(MessageCommand command)
    {
        parser.addCommand(command);
    }

    public void addHelpCommand(HelpMessageCommand command)
    {
        parser.addCommand(command);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent)
    {
        MessageReceivedInformation info = new MessageReceivedInformation(messageCreateEvent);
        String message = info.getContent();
        if (message != null && message.startsWith(prefix) &&
                (!botCommand && !messageCreateEvent.getMessageAuthor().isBotUser() ||
                        botCommand && messageCreateEvent.getMessageAuthor().isBotUser() && !messageCreateEvent.getMessageAuthor().isYourself()))
        {
            String commandString = message.substring(prefix.length());
            List<String> vars = new ArrayList<>(Arrays.asList(commandString.split(" ")));
            MessageCommand command = parser.getCommand(vars);
            if (command == null)
            {
                return;
            }

            executeCommand(info, commandString, vars, command);
        }
    }

    private void executeCommand(MessageReceivedInformation info, String commandString, List<String> vars, MessageCommand command)
    {
        User user = info.getUser();
        if (user == null)
        {
            logger.error("User was null");
            return;
        }

        try (Session session = SessionFactory.getSession())
        {
            if (RoleCheck.hasPermission(session, api, user, command.getRequirement()))
            {
                try
                {
                    if (command instanceof HelpMessageCommand)
                        ((HelpMessageCommand)command).execute(api, info, vars, session, parser);
                    else
                        command.execute(api, info, vars, session);
                    session.commit();
                }
                catch (MissingArgumentException | InvalidArgumentException | NoExecutorException e)
                {
                    List<String> helpVars = new ArrayList<>(Arrays.asList(commandString.split(" ")));
                    helpVars.add(0, "help");
                    MessageCommand help = parser.getCommand(helpVars);
                    if (help instanceof HelpMessageCommand)
                        ((HelpMessageCommand)help).execute(api, info, helpVars, session, parser);
                }
                catch (Exception e)
                {
                    logger.fatal("Exception occurred", e);
                    if (errorLogger != null)
                        errorLogger.log(info.getContent(), info.getUser().getId(), e);
                    DMessage.sendMessage(info.getChannel(), null, e, false);
                    session.rollback();
                }
            }
        }
    }
}
