package exception.discord.user;

import java.awt.Color;

public class UserDoesNotExistException extends UserException
{
    public UserDoesNotExistException(long userId)
    {
        super(Color.YELLOW, "The user `" + userId + "` does not exist");
    }
}
