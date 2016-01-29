package edu.uci.ics.crawler4j.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parses the Header File Created by IcsCrawler
 */
public class CrawlerHeaderParser
{

    /**
     * Crawler Index: 0
     * DocId: 1 http://calendar.ics.uci.edu/
     * Domain: ics.uci.edu Sub-Domain: calendar Path: /
     * Parent Page:null Anchor Text: null
     * Text Length: 0, Html Length: 149 # Outgoing Links: 0
     * Response headers:
     * : DateFri, 29 Jan 2016 18:38:10 GMT
     * : ServerApache/2.2.15 (CentOS)
     * : Last-ModifiedWed, 18 Apr 2012 16:43:35 GMT
     * : ETag"234844d9-95-4bdf6c1a7012b"
     * : Accept-Rangesbytes
     * : Content-Typetext/html; charset=UTF-8
     * : Content-Length149
     * : Age0
     * : Via1.1 localhost.localdomain
     */
    public CrawlerHeaderParser(File file) throws IOException
    {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String curr;

            
            //TODO modify this to parse everything and create new variables to store into this class
            // ignore Crawler index
            // doc ID
            // url
            // domain
            //sub domain
            // path
            // parent page
            // anchor text
            // text length
            // html length
            // outgoing links <- in a list
            // response headers <- in a list
            while ((curr = br.readLine()) != null)
            {
                
            }
    }
}
