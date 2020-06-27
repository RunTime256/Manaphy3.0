package bot.command.executor;

import sql.Session;

import java.util.List;

public interface CommandExecutor
{
    void runCommand(List<String> vars, Session session);
}
