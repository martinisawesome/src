package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.def.Strings;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.storage.CrawlerRecorder;
import edu.uci.ics.crawler4j.storage.FileSystem;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.http.Header;

public class IcsCrawler extends WebCrawler
{
    private CrawlerRecorder recorder;
    private static final boolean SKIP_QUERIES_AND_PARAMETERS = true;
    private static final HashMap<String, LinkedList<String>> PATHING_COMPARES = new HashMap<>();
    private static final LinkedList<String> SCHEDULED_URLS = new LinkedList<>();
    private static final Pattern BAD_EXTENSIONS = Pattern.compile(".*\\.(css|js|mp[2-4]|zip|gz|bmp|gif|mpeg"
                                                                  + "|xls|xlsx|jpg|png|pdf|ico|tiff|mid|names"
                                                                  + "|ppt|pptx|bin|7z|rar|dmg|iso|mov|jar|lzip|tar|tgz)$");
    private static final Pattern CODE_EXTENSIONS = Pattern.compile(".*\\.(java|javac|py|h|cpp|cc|pyc|cs)$");
    private static int CURRENT_INDEX = 0;
    private int index;

    @Override
    public void init(int id, CrawlController crawlController)
    {
        try
        {
            super.init(id, crawlController);
            this.index = CURRENT_INDEX++;
            recorder = new CrawlerRecorder(index);
        }
        catch (IOException e)
        {
            System.err.println("Failed to create recorder for crawler: " + index);
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeExit()
    {
        recorder.close();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url)
    {

        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (BAD_EXTENSIONS.matcher(href).matches())
        {
            recorder.writePath("  Bad Extension, Skipping URL: " + url);
            return false;
        }

        // Ignore code text files
        if (CODE_EXTENSIONS.matcher(href).matches())
        {
            recorder.writePath("  Extension is for code, Skipping URL: " + url);
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        if (!(href.startsWith("http://") || href.startsWith("https://"))
            || !href.contains("ics.uci.edu/"))
        {
            recorder.writePath("  Bad domain, not ics.uci.edu, Skipping URL: " + url);
            return false;
        }

        // this URL is giving me the Bad URL cookie thing, so skip it
        if (url.getPath().contains("/LUCICodeRepository/nomaticIM"))
        {
            recorder.writePath("  Avoiding Cookie place, Skipping URL: " + url);
            System.out.println("Avoiding Cookie place, Skipping URL: " + url);
            return false;
        }
//        if (url.getSubDomain().contains("djp3-pc2"))
//        {
//            recorder.writePath("  Avoiding bad connection subdomain, Skipping URL: " + url);
//            return false;
//        }

        if (SKIP_QUERIES_AND_PARAMETERS)
        {
            // Skip Queries
            if (href.contains("?"))
            {
                recorder.writePath("  This is a Query, Skipping URL: " + url);
                return false;
            }

            // Skip Parameters
            if (href.contains(";"))
            {
                recorder.writePath("  This is a Parameter, Skipping URL: " + url);
                return false;
            }
        }

        if (isSimilarUrl(url))
        {
            recorder.writePath("  This URL is too similar to existing URL, Skipping URL: " + url);
            return false;
        }

        // Do not visit similar URL's
        // Do not visit the same page again
        synchronized (SCHEDULED_URLS)
        {
            if (SCHEDULED_URLS.contains(url.getURL()))
            {
                recorder.writePath("  Already Visited this URL: " + url);
                return false;
            }
            //This URL is good to schedule!
            SCHEDULED_URLS.add(url.getURL());
        }

        recorder.writePath("OKAY - Schedule: " + url);
        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page)
    {

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
        String language = page.getLanguage();

        String docUrl = docid + " " + url;

        recorder.writeHead(String.format("DocId: %s%n"
                                         + "Domain: %s Sub-Domain: %s Path: %s%n"
                                         + "Language: %s%n"
                                         + "Parent Page:%s Anchor Text: %s",
                                         docUrl, domain, subDomain, path, language, parentUrl, anchor));
        
        //======================================================================
        //  _____             _             _     _   _                 _ _ _             
        // /  __ \           | |           | |   | | | |               | | (_)            
        // | /  \/ ___  _ __ | |_ ___ _ __ | |_  | |_| | __ _ _ __   __| | |_ _ __   __ _ 
        // | |    / _ \| '_ \| __/ _ \ '_ \| __| |  _  |/ _` | '_ \ / _` | | | '_ \ / _` |
        // | \__/\ (_) | | | | ||  __/ | | | |_  | | | | (_| | | | | (_| | | | | | | (_| |
        //  \____/\___/|_| |_|\__\___|_| |_|\__| \_| |_/\__,_|_| |_|\__,_|_|_|_| |_|\__, |
        //                                                                          |___/
        //======================================================================
        StringBuilder textBlob = new StringBuilder();

        if (page.getParseData() instanceof HtmlParseData)
        {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String title = htmlParseData.getTitle();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            String moreInformation = String.format("Text Length: %d, Html Length: %d # Outgoing Links: %d",
                                                   text.length(), html.length(), links.size());
            recorder.writeHead(moreInformation);  //put this in crawlerHeaderFileName

            textBlob.append(page.getWebURL()).append("\n");
            textBlob.append(Strings.getBasicHeader("Title")).append("\n");
            textBlob.append(title).append("\n");
            textBlob.append(Strings.getBasicHeader(FileSystem.TEXT)).append("\n");
            textBlob.append(text).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            textBlob.append(Strings.getBasicHeader("HTML")).append("\n");
            textBlob.append(html).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            textBlob.append(Strings.getBasicHeader("Meta Tags")).append("\n");
            textBlob.append(htmlParseData.getMetaTags()).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            textBlob.append(Strings.getBasicHeader("Links")).append("\n");
            textBlob.append(links).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            recorder.writeContent(docid, textBlob.toString());
            
            //TODO if the link is not in english, remove all outgoing links from this page
        }
        else if (page.getParseData() instanceof TextParseData)
        {
            TextParseData htmlParseData = (TextParseData) page.getParseData();
            String text = htmlParseData.getTextContent();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            String moreInformation = String.format("Text Length: %d, # Outgoing Links: %d",
                                                   text.length(), links.size());
            recorder.writeHead(moreInformation);

            textBlob.append(page.getWebURL()).append("\n");
            textBlob.append(Strings.getBasicHeader(FileSystem.TEXT)).append("\n");
            textBlob.append(text).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            textBlob.append(Strings.getBasicHeader("Links")).append("\n");
            textBlob.append(links).append("\n");
            textBlob.append(Strings.SPACER).append("\n");
            recorder.writeContent(docid, textBlob.toString());
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
                response.append("\n: ").append(header.getName()).append(header.getValue());
            }
            recorder.writeHead(response.toString());
        }

        //=================================
        //  _____          _ 
        // |  ___|        | |
        // | |__ _ __   __| |
        // |  __| '_ \ / _` |
        // | |__| | | | (_| |
        // \____/_| |_|\__,_|
        //================================
        recorder.writeHead(Strings.SPACER);
    }

    /**
     * Don't visit URL's that are very similar, just remove numbers
     *
     * @return
     */
    private boolean isSimilarUrl(WebURL url)
    {
        // Allow non-ending paths to continue
        String urlPath = url.getPath();
        if (urlPath.endsWith("/"))
        {
            return false;
        }

        boolean isSimilar = false;
        StringBuilder sb = new StringBuilder();
        sb.append(url.getSubDomain());
        String[] path = urlPath.split("/");
        HashMap<String, Integer> pathCounter = new HashMap<>();
        for (int i = 0; i < path.length - 1; i++)
        {
            String pathPart = path[i];
            
            // Kill URLs that have repeated pathing
            Integer count = pathCounter.get(pathPart);
            if (count == null)
            {
                pathCounter.put(pathPart, 1);
            }
            else if (count > 2)
            {
                return true;
            }
            else
            {
                pathCounter.put(pathPart, count+1);
            }
            
            sb.append(pathPart).append('/');
        }

        String alphaString = path[path.length - 1].replaceAll("[0-9]", "");

        String fullPath = sb.toString();

        synchronized (PATHING_COMPARES)
        {
            LinkedList<String> foundPaths = PATHING_COMPARES.get(fullPath);
            if (foundPaths == null)
            {
                foundPaths = new LinkedList<>();
                foundPaths.add(alphaString);
                PATHING_COMPARES.put(fullPath, foundPaths);
            }
            //Otherwise, check if we visited a similar path already
            else if (foundPaths.contains(alphaString))
            {
                isSimilar = true;
            }
            else
            {
                foundPaths.add(alphaString);
            }
        }

        return isSimilar;
    }

}
