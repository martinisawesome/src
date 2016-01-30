package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.def.TimeConstants;
import edu.uci.ics.crawler4j.def.UrlStartingSeed;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.storage.FileSystem;

public class IcsCrawlController
{

    public static void main(String[] args) throws Exception
    {

        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(FileSystem.CRAWLER_DIRECTORY);

        config.setPolitenessDelay(1000);    //TODO

        config.setMaxDepthOfCrawling(-1);

        //TODO set to -1
        config.setMaxPagesToFetch(-1);

        // Don't grab binary stuff as content
        config.setIncludeBinaryContentInCrawling(false);
        config.setProcessBinaryContentInCrawling(false);

        // Crawl links in stuff
        config.setIncludeHttpsPages(true);
        config.setFollowRedirects(true);

        //setMaxConnectionsPerHost
        //setMaxTotalConnections
        //setMaxOutgoingLinksToFollow
        config.setConnectionTimeout(60 * TimeConstants.SEC_IN_MS);
        config.setSocketTimeout(60 * TimeConstants.SEC_IN_MS);

        //set AuthInfo?
        //set proxy?
        //setOnlineTldListUpdate?
        config.setResumableCrawling(false);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // Add Seed URL
        controller.addSeed(UrlStartingSeed.ICS_DOMAIN);
        controller.addSeed(UrlStartingSeed.TRAP_TESTING);
        controller.addSeed(UrlStartingSeed.STARTING_DOMAINS);

        // Start blocking Crawl
        controller.start(IcsCrawler.class, numberOfCrawlers);

        // Wait for 30 seconds
        Thread.sleep(30 * TimeConstants.SEC_IN_MS);

        // Let the crawler then finish, then shut it down
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
