package bot.command.definition.war.code;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.code.Code;

import java.util.List;

public class CodeCommand
{
    private static final String NAME = "code";
    private static final String DESCRIPTION = "Add a code for a user";
    private static final String SYNTAX = "<user id> <code>";

    private CodeCommand()
    {
    }

    public static MessageCommand createBotCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.VERIFIED).executor(CodeCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {

        CodeFunctionality functionality = new CodeFunctionality(info, vars, session);
        functionality.execute();
    }

    private static class CodeFunctionality
    {
        private final MessageReceivedInformation info;
        private final Session session;
        private final Long userId;
        private final String code;

        CodeFunctionality(MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.info = info;
            this.session = session;

            if (vars.isEmpty())
                throw new MissingArgumentException("user id");
            else if (vars.size() == 1)
                throw new MissingArgumentException("code");

            try
            {
                userId = Long.parseLong(vars.get(0));
            }
            catch (NumberFormatException ignored)
            {
                throw new InvalidArgumentException("user id");
            }
            code = vars.get(1);
        }

        void execute()
        {
            Code.addCode(userId, code, info.getChannel().getId(), session);
            DMessage.sendMessage(info.getChannel(), "Code `" + code + "` added for `" + userId + "`");
        }
    }
}
