package bot.command.executor;

import sql.Session;

public interface CommandExecutor
{
    void runCommand(Session session);
}
