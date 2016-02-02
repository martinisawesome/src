package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.storage.Storage;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        // Pre-Step
        long startTime = System.nanoTime();

        Storage storage = new Storage();
        int pageCount = storage.processTokenPages();
        storage.computeFrequencies();

        //  _____                    _     _____           _        _   _               
        // |_   _|                  | |   /  __ \         | |      | | | |              
        //   | | _ __  ___  ___ _ __| |_  | /  \/ ___   __| | ___  | |_| | ___ _ __ ___ 
        //   | || '_ \/ __|/ _ \ '__| __| | |    / _ \ / _` |/ _ \ |  _  |/ _ \ '__/ _ \
        //  _| || | | \__ \  __/ |  | |_  | \__/\ (_) | (_| |  __/ | | | |  __/ | |  __/
        //  \___/_| |_|___/\___|_|   \__|  \____/\___/ \__,_|\___| \_| |_/\___|_|  \___|
        //======================================================================
        //======================================================================
        // end of steps
        //1. How much time did it take to crawl the entire domain?
        long completionTime = System.nanoTime() - startTime;
        double time = completionTime / 1000;
        time = time / 1000;
        System.out.println(
                String.format("Time to completion: %.03fms", time));

        //2. How many unique pages did you find in the entire domain? (Uniqueness is established by the URL)
        System.out.println(String.format("Unique Pages: %d", pageCount));

        //3. How many subdomains did you find? 
        //Submit the list of subdomains ordered alphabetically and the number of unique pages detected in each subdomain. 
        //The file should be called Subdomains.txt, and its content should be lines containing: URL, number of unique pages detected
        System.out.println(String.format("Subdomain Count: %d", storage.getSubDomainCount()));
        storage.writeSubDomainsToText();

        //4. What is the longest page in terms of number of words? (HTML markup doesn’t count as words) //TODO do not cut out stop words for this
        System.out.println(String.format("Longest Page: %s", storage.getLongestPageName()));

        //5. What are the 500 most common words in this domain? (Ignore English stop words, which can be found, for example, here) Submit the list of the 500 most common words ordered by frequency.
        System.out.println(String.format("500 Common Words: %s", printf(storage.getWordCount())));

        //6. What are the 20 most common 3-grams? (again ignore English stop words) 
        //A 2-gram, in this case, is a sequence of 2 words that aren’t stop words and that haven’t had a stop word in between them. Submit the list of 20 2-grams ordered by frequency.
        System.out.println(String.format("20 Common N-Grams: %s", printf(storage.get3GramCount())));
    }

    public static <E> String printf(List<E> tokenList)
    {
        StringBuilder sb = new StringBuilder();
        for (E token : tokenList)
        {
            // Print
            sb.append(token).append("\n");

        }
        // New line
        return (sb.toString());
    }
}
