package edu.uci.ics.crawler4j.textprocessor;

import edu.uci.ics.crawler4j.def.StopWords;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TextProcessor<E>
{

    // =========================================================================
    //  _   _ _   _ _ _ _   _           
    // | | | | | (_) (_) | (_)          
    // | | | | |_ _| |_| |_ _  ___  ___ 
    // | | | | __| | | | __| |/ _ \/ __|
    // | |_| | |_| | | | |_| |  __/\__ \
    // \___/ \__|_|_|_|\__|_|\___||___/
    public static List<String> tokenizeFile(File textFile) throws IOException
    {
        // Init method
        List<String> tokenList = new LinkedList<>();
        FileReader fr = new FileReader(textFile);
        BufferedReader br = new BufferedReader(fr);
        String curr;
        String token;

        while ((curr = br.readLine()) != null)
        {
            //curr = scanner.next();
            curr = curr.toLowerCase();

            // Hyphenated words treated as different words for now
            curr = curr.replace("'", "");                   // Handle apostrophe differently
            curr = curr.replaceAll("[^a-zA-Z0-9]", " ");    // Remove all other non-alphanumeric chars

            // get the string list after regex
            String[] strings = curr.split(" ");
            for (String string : strings)
            {
                //TODO ignore stop words!!!

                // Handle all empty strings
                token = string.trim();
                if (!token.isEmpty())
                {
                    tokenList.add(token);
                }
            }

        }   //eWhile

        // End Method
        br.close();
        return tokenList;

    }

    public static <E> void print(List<E> tokenList)
    {
        StringBuilder sb = new StringBuilder();
        for (E token : tokenList)
        {
            // Print
            sb.append("\"");
            sb.append(token);
            sb.append("\",");
            sb.append("\n");

        }
        // New line
        System.out.println(sb.toString());
    }

    public static void removeStopWords(List<String> tokens)
    {
        for (String word : StopWords.WORDS)
        {
            while (tokens.remove(word))
            {
                //keep removing the same word
            }
        }
    }

    //  _    _               _  ______                                    _           
    // | |  | |             | | |  ___|                                  (_)          
    // | |  | | ___  _ __ __| | | |_ _ __ ___  __ _ _   _  ___ _ __   ___ _  ___  ___ 
    // | |/\| |/ _ \| '__/ _` | |  _| '__/ _ \/ _` | | | |/ _ \ '_ \ / __| |/ _ \/ __|
    // \  /\  / (_) | | | (_| | | | | | |  __/ (_| | |_| |  __/ | | | (__| |  __/\__ \
    //  \/  \/ \___/|_|  \__,_| \_| |_|  \___|\__, |\__,_|\___|_| |_|\___|_|\___||___/
    //                                           |_|  
    public static List<FreqPair<String>> computeWordFrequencies(Map<String, Integer> map, List<FreqPair<String>> pairList, List<String> tokenList)
    {

        for (String token : tokenList)
        {
            // Check if token already exists
            Integer index = map.get(token);

            // Token already added
            if (index != null)
            {
                pairList.get(index).incCount();
            }
            // Otherwise, is a new token
            else
            {
                index = pairList.size();
                pairList.add(new FreqPair<>(token));
                map.put(token, index);
            }
        }

        return pairList;
    }

    public static List<FreqPair<NGram>> computeNGramFrequencies(Map<NGram, Integer> map, List<FreqPair<NGram>> pairList, List<String> tokenList, int n)
    {
        String[] pos = new String[n];

        if (tokenList.size() < n)
        {
            return pairList;
        }

        // Initialize the first three gram
        for (int i = 0; i < n; i++)
        {
            pos[i] = tokenList.get(i);
        }
        NGram gram = new NGram(pos);
        pairList.add(new FreqPair<>(gram));
        map.put(gram, 0);   //goes to index 0, starting

        if (tokenList.size() == n)
        {
            return pairList;
        }

        for (int i = n; i < tokenList.size(); i++)
        {
            // Initialize current three-gram
            String token = tokenList.get(i);
            for (int j = 0; j < n - 1; j++)
            {
                pos[j] = pos[j + 1];
            }
            pos[n - 1] = token;
            gram = new NGram(Arrays.copyOf(pos, n));

            // Check if N-gram already exists
            Integer index = map.get(gram);

            // 3-gram already added
            if (index != null)
            {
                pairList.get(index).incCount();
            }
            // Otherwise, is a new 3-gram
            else
            {
                index = pairList.size();
                pairList.add(new FreqPair<>(gram));
                map.put(gram, index);
            }
        }

        return pairList;
    }
}
