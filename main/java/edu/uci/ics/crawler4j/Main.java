package edu.uci.ics.crawler4j;

import edu.uci.ics.crawler4j.storage.FileSystem;
import edu.uci.ics.crawler4j.storage.Storage;
import edu.uci.ics.crawler4j.textprocessor.TextProcessor;
import java.io.File;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        firstPart();
        secondPart();
    }

    private static void firstPart()
    {
        Storage storage = new Storage();
        int pageCount = storage.processTokenPages();

        //2. How many unique pages did you find in the entire domain? (Uniqueness is established by the URL)
        System.out.println(String.format("Unique Pages: %d", pageCount));

        //3. How many subdomains did you find? 
        //Submit the list of subdomains ordered alphabetically and the number of unique pages detected in each subdomain. 
        //The file should be called Subdomains.txt, and its content should be lines containing: URL, number of unique pages detected
        System.out.println(String.format("Subdomain Count: %d", storage.getSubDomainCount()));
        storage.writeSubDomainsToText();

        //4. What is the longest page in terms of number of words? (HTML markup doesn’t count as words) //TODO do not cut out stop words for this
        System.out.println(String.format("Longest Page: %s", storage.getLongestPageName()));

        storage.computeFrequencies();
    }

    private static void secondPart() throws Exception
    {
        File mergedFreq = FileSystem.binaryMergeAllFreq("Freq", 0);
        File merged3Gram = FileSystem.binaryMergeAllFreq("3Gram", 0);

        List list1 = TextProcessor.countHighest(mergedFreq, 500);
        List list2 = TextProcessor.countHighest(merged3Gram, 20);

        //5. What are the 500 most common words in this domain? (Ignore English stop words, which can be found, for example, here) Submit the list of the 500 most common words ordered by frequency.
        System.out.println(String.format("500 Common Words: %n%s", printf(list1)));
        //6. What are the 20 most common 3-grams? (again ignore English stop words) 
        //A 2-gram, in this case, is a sequence of 2 words that aren’t stop words and that haven’t had a stop word in between them. Submit the list of 20 2-grams ordered by frequency.
        System.out.println(String.format("20 Common N-Grams: %n%s", printf(list2)));
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
