package edu.uci.ics.crawler4j.def;

public class UrlStartingSeed
{
    public static final String ICS_DOMAIN = "http://www.ics.uci.edu/";
    public static final String[] URL_START = new String[]
    {
        ICS_DOMAIN
    };

    // from MultipleCrawlerController
    //controller1.addSeed("http://www.cnn.com/");
    //controller1.addSeed("http://www.ics.uci.edu/~lopes/");
    //controller1.addSeed("http://www.cnn.com/POLITICS/");
    //controller2.addSeed("http://en.wikipedia.org/wiki/Main_Page");
    //controller2.addSeed("http://en.wikipedia.org/wiki/Obama");
    //controller2.addSeed("http://en.wikipedia.org/wiki/Bing");
    // from basicCrawler + crawlerWithShutdown + statusHandlerCrawlController
    //controller.addSeed("http://www.ics.uci.edu/~lopes/");
    //controller.addSeed("http://www.ics.uci.edu/~welling/");
}
