package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.textprocessor.FreqPair;
import edu.uci.ics.crawler4j.textprocessor.NGram;
import edu.uci.ics.crawler4j.textprocessor.TextProcessor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used to store everything that is found by the web crawler! Basic indexer just
 * to initial rip some data from raw documents.
 */
public final class Storage
{
    private final LinkedList<String> pages;
    private final HashMap<String, Integer> subdomains;
    private final List<FreqPair<String>> wordCount;
    private final Map<String, Integer> wordCountMap;

    private final List<FreqPair<NGram>> threeGramCount;
    private final Map<NGram, Integer> threeGramMap;

    private int longestPageLength;
    private String longestPage;

    public Storage()
    {
        longestPage = "";
        longestPageLength = 0;
        pages = new LinkedList<>();
        subdomains = FileSystem.findAllSubdomains();
        wordCount = new LinkedList<>();
        threeGramCount = new LinkedList<>();
        wordCountMap = new HashMap<>();
        threeGramMap = new HashMap<>();

        //Process everything
        processAllPages();
    }

    public void processAllPages()
    {
        for (File f : FileSystem.getAllContentTextFiles())
        {
            addPage(f);
        }
    }

    public void addPage(File page)
    {
        try
        {
            if (!constainsPage(page.getName()))
            {
                List<String> tokenList = TextProcessor.tokenizeFile(page);

                //==================================================================
                //Compute Longest Page
                if (tokenList.size() > longestPageLength)
                {
                    longestPage = page.getName();
                    longestPageLength = tokenList.size();
                }
                
                //Ignore stop words for Word counts and stop words
                TextProcessor.removeStopWords(tokenList);

                //==================================================================
               TextProcessor.computeWordFrequencies(wordCountMap, wordCount, tokenList);
               TextProcessor.computeNGramFrequencies(threeGramMap, threeGramCount, tokenList, 3);
                pages.add(page.getName());
            }
            else
            {
                System.out.println("Duplicate Page: " + page.getName());
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed to open and tokenize a file: " + page.getName());
            e.printStackTrace();
        }
    }

    public String getLongestPageName()
    {
        return longestPage;
    }

    public int getPageCount()
    {
        return pages.size();
    }

    public int getSubDomainCount()
    {
        return subdomains.size();
    }

    public boolean constainsPage(String page)
    {
        return pages.contains(page);
    }

    public LinkedList<String> getPages()
    {
        return pages;
    }

    public void writeSubDomainsToText()
    {try
    {
        LinkedList<String> domains = new LinkedList<>(subdomains.keySet());
        Collections.sort(domains);
        FileWriter wr = new FileWriter(FileSystem.CRAWLER_DIRECTORY + "Subdomains.txt", false);
        StringBuilder sb = new StringBuilder();
        
        for (String domain : domains)
        {
            sb.append(domain);
            sb.append(", ");
            sb.append(subdomains.get(domain));
            sb.append("\n");
        }
        
        wr.write(sb.toString());
        wr.close();
    }
    catch (IOException e)
    {
        System.err.println("Failed to retrieve subdomain text!!!");
        e.printStackTrace();
    }
    }

    public List<FreqPair<String>> getWordCount()
    {
        //find the 500 most common words in this domain? 
        Collections.sort(wordCount);
        return wordCount.subList(0, 500);
    }

    public List<FreqPair<NGram>> get4GramCount()
    {
        //find  the 20 most common 3-grams
        Collections.sort(threeGramCount);
        return threeGramCount.subList(0, 20);
    }

}
