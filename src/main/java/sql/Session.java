package sql;

import org.apache.ibatis.session.SqlSession;

public class Session implements AutoCloseable
{
    private final SqlSession sqlSession;

    Session(SqlSession session)
    {
        this.sqlSession = session;
    }

    public <T> T getMapper(Class<T> classMap)
    {
        return sqlSession.getMapper(classMap);
    }

    public void commit()
    {
        sqlSession.commit();
    }

    public void rollback()
    {
        sqlSession.rollback();
    }

    @Override
    public void close()
    {
        sqlSession.close();
    }
}
