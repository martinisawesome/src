package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.def.Strings;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.storage.FileSystem;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.http.Header;

public class IcsCrawler extends WebCrawler
{
    private static final LinkedList<String> visitedUrls = new LinkedList<>();
    private static final Pattern BAD_EXTENSIONS = Pattern.compile(".*\\.(css|js|mp3|zip|gz|bmp|gif|jpg|png|pdf)$");
    private static int CURRENT_INDEX = 0;
    private int index;

    @Override
    public void init(int id, CrawlController crawlController)
    {
        super.init(id, crawlController);
        this.index = CURRENT_INDEX++;
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     *
     * @return
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
        String pathingFileName = index + FileSystem.CRAWLER_PATHING_NAME;

        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (BAD_EXTENSIONS.matcher(href).matches())
        {
            System.out.println("  Bad Extension, Skipping URL: " + url);
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        if (!href.startsWith("http://") || !href.contains("ics.uci.edu/"))
        {
            System.out.println("  Bad domain, not ics.uci.edu, Skipping URL: " + url);
            return false;
        }

        // Do not visit the same page again
        if (visitedUrls.contains(url.getURL()))
        {
            System.out.println("  Already Visited this URL: " + url);
            return false;
        }

        System.out.println("OKAY - Schedule: " + url);
        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page)
    {
        //Do not visit this page again

        System.out.println("Crawler Index: " + index);

        //======================================================================
        //  _   _                _             _____       __      
        // | | | |              | |           |_   _|     / _|     
        // | |_| | ___  __ _  __| | ___ _ __    | | _ __ | |_ ___  
        // |  _  |/ _ \/ _` |/ _` |/ _ \ '__|   | || '_ \|  _/ _ \ 
        // | | | |  __/ (_| | (_| |  __/ |     _| || | | | || (_) |
        // \_| |_/\___|\__,_|\__,_|\___|_|     \___/_| |_|_| \___/
        //======================================================================
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        visitedUrls.add(url);

        //TODO print to indexer
        String title = docid + " " + url;

        String crawlerHeaderFileName = index + FileSystem.HEADER_FILE_NAME;

        //TODO print to logger
        String information = String.format("DocId: %s%n"
                                           + "Domain: %s Sub-Domain: %s Path: %s%n"
                                           + "Parent Page:%s Anchor Text: %s",
                                           title, domain, subDomain, path, parentUrl, anchor);
        System.out.println(information);

        //======================================================================
        //  _____             _             _     _   _                 _ _ _             
        // /  __ \           | |           | |   | | | |               | | (_)            
        // | /  \/ ___  _ __ | |_ ___ _ __ | |_  | |_| | __ _ _ __   __| | |_ _ __   __ _ 
        // | |    / _ \| '_ \| __/ _ \ '_ \| __| |  _  |/ _` | '_ \ / _` | | | '_ \ / _` |
        // | \__/\ (_) | | | | ||  __/ | | | |_  | | | | (_| | | | | (_| | | | | | | (_| |
        //  \____/\___/|_| |_|\__\___|_| |_|\__| \_| |_/\__,_|_| |_|\__,_|_|_|_| |_|\__, |
        //                                                                          |___/
        //======================================================================
        String documentIdFileName = "" + docid;

        if (page.getParseData() instanceof HtmlParseData)
        {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            String moreInformation = String.format("Text Length: %d, Html Length: %d # Outgoing Links: %d",
                                                   text.length(), html.length(), links.size());
            System.out.println(moreInformation);

            //TODO print to document
            // System.out.println(text);
            // System.out.println(html);
        }
        else if (page.getParseData() instanceof TextParseData)
        {
            TextParseData htmlParseData = (TextParseData) page.getParseData();
            String text = htmlParseData.getTextContent();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            String moreInformation = String.format("Text Length: %d, # Outgoing Links: %d",
                                                   text.length(), links.size());
            System.out.println(moreInformation);

            //TODO print to document
            //System.out.println(text);
        }
        else
        {
            //TODO how to get other text?
        }

        //======================================================================
        // ______                                       _   _                _               
        // | ___ \                                     | | | |              | |              
        // | |_/ /___  ___ _ __   ___  _ __  ___  ___  | |_| | ___  __ _  __| | ___ _ __ ___ 
        // |    // _ \/ __| '_ \ / _ \| '_ \/ __|/ _ \ |  _  |/ _ \/ _` |/ _` |/ _ \ '__/ __|
        // | |\ \  __/\__ \ |_) | (_) | | | \__ \  __/ | | | |  __/ (_| | (_| |  __/ |  \__ \
        // \_| \_\___||___/ .__/ \___/|_| |_|___/\___| \_| |_/\___|\__,_|\__,_|\___|_|  |___/                                                            
        //                |_|    
        //======================================================================
        //write to crawlerHeaderFileName
        Header[] responseHeaders = page.getFetchResponseHeaders();

        if (responseHeaders != null)
        {
            StringBuilder response = new StringBuilder();
            response.append("Response headers:");
            for (Header header : responseHeaders)
            {
                response.append("\t: ").append(header.getName()).append(header.getValue());
            }
            //System.out.println(response);
        }

        //=================================
        //  _____          _ 
        // |  ___|        | |
        // | |__ _ __   __| |
        // |  __| '_ \ / _` |
        // | |__| | | | (_| |
        // \____/_| |_|\__,_|
        //================================
        System.out.println(Strings.SPACER);
    }
}
