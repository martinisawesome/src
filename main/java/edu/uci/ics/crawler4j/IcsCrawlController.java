package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.def.UrlStartingSeed;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.storage.FileSystem;

public class IcsCrawlController
{

    public static void main(String[] args) throws Exception
    {

        int numberOfCrawlers = 1;   // TODO more? 7?

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(FileSystem.CRAWLER_DIRECTORY);

        config.setUserAgentString("Test Crawler");  //TODO remove this!!!

        config.setPolitenessDelay(5000);    //TODO

        /*
         * You can set the maximum crawl depth here. The default value is -1 for
         * unlimited depth
         */
        config.setMaxDepthOfCrawling(-1);

        config.setMaxPagesToFetch(1000);

        config.setIncludeBinaryContentInCrawling(false);

        /*
         * This config parameter can be used to set your crawl to be resumable
         * (meaning that you can resume the crawl from a previously
         * interrupted/crashed crawl). Note: if you enable resuming feature and
         * want to start a fresh crawl, you need to delete the contents of
         * rootFolder manually.
         */
        config.setResumableCrawling(false);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(UrlStartingSeed.ICS_DOMAIN);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(IcsCrawler.class, numberOfCrawlers);

        // Let the crawler then finish, then shut it down
        controller.shutdown();
        controller.waitUntilFinish();
    }
}
