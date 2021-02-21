package bot.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class ShowdownUrlEvaluator
{
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String HTTPS_SHOWDOWN = "https://replay.pokemonshowdown.com/";

    private ShowdownUrlEvaluator()
    {
    }

    public static String convertUrl(String url)
    {
        if (url.startsWith(HTTP))
            return HTTPS + url.substring(HTTP.length());
        else
            return url;
    }

    public static String getFormat(String url)
    {
        String battle = url.substring(HTTPS_SHOWDOWN.length());
        return battle.substring(0, battle.indexOf('-'));
    }

    public static boolean isValidUrl(String link)
    {
        if (!link.startsWith(HTTPS_SHOWDOWN))
            return false;

        try
        {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            return connection.getResponseCode() == 200;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
