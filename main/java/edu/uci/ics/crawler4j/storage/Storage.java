package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.textprocessor.FreqPair;
import edu.uci.ics.crawler4j.textprocessor.NGram;
import edu.uci.ics.crawler4j.textprocessor.SubDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used to store everything that is found by the web crawler!
 */
public class Storage
{
    private final LinkedList<String> pages;
    private final LinkedList<SubDomain> subdomains;
    private final List<FreqPair<String>> wordCount;
    private final Map<String, Integer> wordCountMap;

    private final List<FreqPair<NGram>> twoGramCount;
    private final Map<NGram, Integer> twoGrmaMap;

    private int longestPageLength;
    private String longestPage;

    public Storage()
    {
        longestPage = "";
        longestPageLength = 0;
        pages = new LinkedList<>();
        subdomains = new LinkedList<>();
        wordCount = new LinkedList<>();
        twoGramCount = new LinkedList<>();
        wordCountMap = new HashMap<>();
        twoGrmaMap = new HashMap<>();
    }

    public void addPage(String page)
    {
        if (!constainsPage(page))
        {
            //==================================================================
            //Compute Longest Page

            // If  page length > longestPageLength
            {
                //longestPageLength = page length
                longestPage = page;
                longestPageLength++;    // TODO remove this
            }

            //==================================================================
            //Compute Word Freq             //TODO ignore stop words!!!
            //TODO  computeWordFrequencies(wordCountMap, wordCount, List<String> tokenList) on this page

            //TODO    computeNGramFrequencies(twoGrmaMap, nGramCount, List<String> tokenList, 2)
            pages.add(page);
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
    {
        //TODO
        //Submit the list of subdomains ordered alphabetically and the number of unique pages detected in each subdomain. 
        //The file should be called Subdomains.txt, and its content should be lines containing: URL, number of unique pages detected
    }

    public List<FreqPair<String>> getWordCount()
    {
        //find the 500 most common words in this domain? 
        Collections.sort(wordCount);
        return wordCount;
    }

    public List<FreqPair<NGram>> get2GramCount()
    {
        //find  the 20 most common 2-grams
        Collections.sort(twoGramCount);
        return twoGramCount;
    }

}