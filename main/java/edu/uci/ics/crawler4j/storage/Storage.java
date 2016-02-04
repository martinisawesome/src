package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.StopWords;
import edu.uci.ics.crawler4j.textprocessor.TextProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to store everything that is found by the web crawler! Basic indexer just
 * to initial rip some data from raw documents.
 */
public final class Storage
{
    private final HashMap<String, Integer> subdomains;

    private int longestPageLength;
    private String longestPage;

    public Storage()
    {
        longestPage = "";
        longestPageLength = 0;

        //subdomains = FileSystem.findAllSubdomains();
        subdomains = new HashMap<>();
    }

    public void deleteAll()
    {
        try
        {
            LinkedList<File> deletes = new LinkedList<>();

            File directory = new File(FileSystem.CRAWLER_DIRECTORY);
            File[] files = directory.listFiles();
            if (files != null)
            {
                for (File f : files)
                {
                    if (f.isDirectory())
                    {
                        continue;
                    }
                    FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);
                    String curr = br.readLine();

                    if (curr.contains("asdfghjkl"))
                    {
                        deletes.add(f);
                    }

                    fr.close();
                }
            }

            for (File f : deletes)
            {
                f.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int processTokenPages()
    {
        // Clear all text data before making new
        FileSystem.clearTextData();
        LinkedList<String> pages = new LinkedList<>();
        FileSystem fs = new FileSystem();
        for (File page : fs.getAllContentTextFiles(subdomains))   //turns all files into text only
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
                    pages.add(page.getName());
                }
                else
                {
                    //System.out.println("Duplicate Page: " + page.getName());
                }
            }
            catch (IOException e)
            {
                System.out.println("Failed to open and tokenize a file: " + page.getName());
                e.printStackTrace();
            }
        }

        try
        {
            fs.getDocumentMap().writeToFile();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return pages.size();
    }

    /**
     * Creates a bunch of small files that have the frequencies of some content files.
     */
    public void computeFrequencies()
    {
        FileSystem.clearFreqData();
        LinkedList<File> files = FileSystem.getAllTokenTextFiles();
        TextProcessor p = new TextProcessor();
        for (File f : files)
        {
            try
            {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String curr;
                ArrayList<String> tokenList = new ArrayList<>();

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

                //System.out.println("  Computing Frequencies on File: " + f.getName());
                
                int id = Integer.parseInt(f.getName().replaceAll("[^0-9]", ""));

                p.computeWordFrequencies(id, tokenList);
                p.computeNGramFrequencies( id, tokenList, 3);

            }
            catch (IOException e)
            {
                System.out.println("Cannot read file: " + f.getName());
                e.printStackTrace();
            }
        }

        try
        {
            p.flush();
        }
        catch (IOException ex)
        { System.out.println("Cannot flush processor");
              ex.printStackTrace();
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

    /**
     * to be called after finding all sub-domains. Will just them to text file.
     */
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

}
