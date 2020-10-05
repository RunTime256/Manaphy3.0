package war.code;

import exception.war.code.AlreadyRetrievedCodeException;
import exception.war.code.IncorrectCodeChannelException;
import exception.war.code.NotACodeException;
import sql.Session;

public class Code
{
    private Code()
    {
    }

    public static boolean exists(String code, Session session)
    {
        return session.getMapper(CodeMapper.class).exists(code);
    }

    public static boolean correctChannel(String code, Long channelId, Session session)
    {
        return session.getMapper(CodeMapper.class).correctChannel(code, channelId);
    }

    public static boolean hasCode(String code, Long userId, Session session)
    {
        return session.getMapper(CodeMapper.class).hasCode(code, userId);
    }

    public static void addCode(Long userId, String code, Long channelId, Session session)
    {
        if (!exists(code, session))
            throw new NotACodeException(code);
        else if (!correctChannel(code, channelId, session))
            throw new IncorrectCodeChannelException(code);
        else if (hasCode(code, userId, session))
            throw new AlreadyRetrievedCodeException(code, userId);

        session.getMapper(CodeMapper.class).addCode(code, userId);
    }
}
