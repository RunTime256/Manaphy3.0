package sql;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class SessionFactory
{
    private static SqlSessionFactory factory;

    private SessionFactory()
    {
    }

    public static void init(String config) throws IOException
    {
        Reader reader = Resources.getResourceAsReader(config);
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static Session getSession()
    {
        return new Session(factory.openSession());
    }
}
