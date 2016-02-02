package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.StopWords;
import edu.uci.ics.crawler4j.textprocessor.FreqPair;
import edu.uci.ics.crawler4j.textprocessor.NGram;
import edu.uci.ics.crawler4j.textprocessor.TextProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

        subdomains = FileSystem.findAllSubdomains();
        wordCount = new LinkedList<>();
        threeGramCount = new LinkedList<>();
        wordCountMap = new HashMap<>();
        threeGramMap = new HashMap<>();

    }

    public int processTokenPages()
    {
        // Clear all text data before making new
        FileSystem.clearTextData();
        LinkedList<String> pages = new LinkedList<>();
        for (File page : FileSystem.getAllContentTextFiles())   //turns all files into text only
        {

            try
            {
                if (page == null)
                {
                    continue;
                }
                
                if (!pages.contains(page.getName()))
                {
                    List<String> tokenList = TextProcessor.tokenizeFile(page);

                    //don't process empty files...
                    if (tokenList.isEmpty())
                    {
                        continue;
                    }

                    File content = new File(FileSystem.CRAWLER_DIRECTORY + page.getName() + FileSystem.TOKEN);
                    FileWriter wr = new FileWriter(content, false);
                    StringBuilder sb = new StringBuilder();
                    int counter = 0;
                    for (String s : tokenList)
                    {
                        sb.append(s);
                        counter++;
                        
                        if (counter % 100 == 0)
                        {
                            sb.append("\n");
                        }
                        else
                        {
                            sb.append(" ");
                        }
                        
                        if (counter > 5000)
                        {
                            counter = 0;
                            wr.write(sb.toString());
                            sb = new StringBuilder();
                        }
                    }
                    wr.write(sb.toString());
                    wr.close();

                    //==================================================================
                    //Compute Longest Page
                    if (tokenList.size() > longestPageLength)
                    {
                        longestPage = page.getName();
                        longestPageLength = tokenList.size();
                    }

                    //==================================================================
                  //  TextProcessor.computeWordFrequencies(wordCountMap, wordCount, tokenList);
                  //  TextProcessor.computeNGramFrequencies(threeGramMap, threeGramCount, tokenList, 3);
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

        return pages.size();
    }

    public void computeFrequencies()
    {
        LinkedList<File> files = FileSystem.getAllTokenTextFiles();
        for (File f : files)
        {
            try
            {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String curr;
                LinkedList<String> tokenList = new LinkedList<>();

                // fina all the words in this line
                while ((curr = br.readLine()) != null)
                {
                    for (String word : curr.split(" "))
                    {
                        //Do not use stop words
                        boolean isStop = false;
                        for (String stopWords : StopWords.WORDS)
                        {
                            if (stopWords.equals(word))
                            {
                                isStop = true;
                                break;
                            }
                        }

                        if (!isStop)
                        {
                            // Find file where this belongs
                            tokenList.add(word);
                        }
                    }
                }

                br.close();
                fr.close();

                System.out.println("Computing Frequencies on File: " + f.getName());
                TextProcessor.computeWordFrequencies(wordCountMap, wordCount, tokenList);
                TextProcessor.computeNGramFrequencies(threeGramMap, threeGramCount, tokenList, 3);

            }
            catch (IOException e)
            {
                System.out.println("Cannot read file: " + f.getName());
                e.printStackTrace();
            }
        }
    }

    public String getLongestPageName()
    {
        return longestPage;
    }

    public int getSubDomainCount()
    {
        return subdomains.size();
    }

    public void writeSubDomainsToText()
    {
        try
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
        if (wordCount.size() > 500)
        {
            return wordCount.subList(0, 500);
        }
        else
        {
            return wordCount;
        }
    }

    public List<FreqPair<NGram>> get3GramCount()
    {
        //find  the 20 most common 3-grams
        Collections.sort(threeGramCount);
        return threeGramCount.subList(0, 20);
    }

}
