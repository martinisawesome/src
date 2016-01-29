package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.def.Strings;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.http.Header;

public class IcsCrawler extends WebCrawler
{

    private static final Pattern BAD_EXTENSIONS = Pattern.compile(".*\\.(css|js|mp3|zip|gz|bmp|gif|jpg|png|pdf)$");

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     *
     * @return
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {
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

        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        //TODO print to indexer
        String title = docid + " " + url;

        //TODO print to logger
        String information = String.format("DocId: %s%n"
                                           + "Domain: %s Sub-Domain: %s Path: %s%n"
                                           + "Parent Page:%s Anchor Text: %s",
                                           title, domain, subDomain, path, parentUrl, anchor);
        System.out.println(information);

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
            //System.out.println(text);
        }
        else
        {
            //TODO how to get other text?
        }

        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null)
        {
            System.out.println("Response headers:");
            for (Header header : responseHeaders)
            {
                System.out.println("\t: " + header.getName() + header.getValue());
            }
        }

        System.out.println(Strings.SPACER);
    }
}
