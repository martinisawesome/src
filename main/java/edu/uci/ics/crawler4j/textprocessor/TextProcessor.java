package edu.uci.ics.crawler4j.textprocessor;

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

    //  _    _               _  ______                                    _           
    // | |  | |             | | |  ___|                                  (_)          
    // | |  | | ___  _ __ __| | | |_ _ __ ___  __ _ _   _  ___ _ __   ___ _  ___  ___ 
    // | |/\| |/ _ \| '__/ _` | |  _| '__/ _ \/ _` | | | |/ _ \ '_ \ / __| |/ _ \/ __|
    // \  /\  / (_) | | | (_| | | | | | |  __/ (_| | |_| |  __/ | | | (__| |  __/\__ \
    //  \/  \/ \___/|_|  \__,_| \_| |_|  \___|\__, |\__,_|\___|_| |_|\___|_|\___||___/
    //                                           |_|  
    public static List<FreqPair<String>> computeWordFrequencies(List<String> tokenList)
    {
        List<FreqPair<String>> pairList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();

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

        Collections.sort(pairList);
        return pairList;
    }

    public static List<FreqPair<NGram>> computeNGramFrequencies(List<String> tokenList, int n)
    {
        List<FreqPair<NGram>> pairList = new ArrayList<>();
        Map<NGram, Integer> map = new HashMap<>();
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

        Collections.sort(pairList);
        return pairList;
    }

    // Disclaimer! these three lines of sorting are based on WordNet source code
    public static String getScramble(String word)
    {
        char[] scrambleChar;
        String scramble;
        scrambleChar = word.toCharArray();
        Arrays.sort(scrambleChar);
        scramble = new String(scrambleChar);

        return scramble;
    }

    // =========================================================================
    private static List<AnaPair> removeFakeAnagrams(List<AnaPair> pairList)
    {
        List<AnaPair> newList = new ArrayList<>();

        LinkedList tempList;
        LinkedList ana;
        for (AnaPair curr : pairList)
        {
            // Anagrams should list more than just itself, remove if list only includes itself
            ana = curr.anagrams;
            if (ana.size() > 0)
            {
                // Need to clone because lists are shared
                tempList = (LinkedList) ana.clone();

                // Remove self from own list of anagrams
                while (tempList.remove(curr.token))
                {
                }

                if (tempList.size() > 0)
                {
                    AnaPair newPair = new AnaPair(curr.token, tempList);
                    newList.add(newPair);
                }
            }
        }   //efor

        return newList;
    }

    /**
     * This is a very slow way to get anagrams through searching the
     * permutations of my words.
     *
     * @param scramble
     * @return
     */
    private static LinkedList<String> getAnagrams(char[] scramble)
    {
        LinkedList<String> list = new LinkedList<>();

        // one-letter words don't matter
        if (scramble.length > 1)
        {
            permuteAndFindDefs(list, 0, scramble);
        }

        return list;
    }

    /**
     * Disclaimer!!! This permute method was based on code from a previous class
     * where I wrote this code for a project.
     *
     * @param list
     * @param i
     * @param chars
     * @return
     */
    private static void permuteAndFindDefs(LinkedList<String> list, int i, char[] chars)
    {
        if (i == chars.length - 1)
        {
            String token = new String(chars);

            if (true) // TODO if it exists in the dictionary
            {
                list.add(token);
            }

        }

        for (int j = i; j < chars.length; j++)
        {
            // Swap
            swap(chars, i, j);

            // Will always permute
            permuteAndFindDefs(list, i + 1, chars);

            // Revert Swap
            swap(chars, i, j);

        }

    }

    public static char[] swap(char[] array, int x, int y)
    {
        char temp = array[x];
        array[x] = array[y];
        array[y] = temp;
        return array;
    }

    /**
     * Only finds anagrams within the same token list
     *
     * @param tokenList
     * @return
     * @deprecated
     */
    @Deprecated
    public static List<AnaPair> detectAnagramsExclusive(List<String> tokenList)
    {

        // Runtime is more than linear but not nearly polynomial:
        // Sample size:  15000 tokens produced run time ~200ms
        // Sample size:  50000 tokens produced run time ~950ms
        // Sample size: 110000 tokens produced run time ~1600ms
        // Sample size: 220000 tokens produced run time ~5200ms
        //
        // Return this list
        List<AnaPair> pairList = new ArrayList<>();

        // Map will store each alphabetized scramble
        Map<String, LinkedList<String>> scrambleMap = new HashMap<>();

        char[] chars;
        String scramble;
        AnaPair pair;

        // construct list of anagrams
        for (String token : tokenList)
        {

            // Disclaimer! these three lines of sorting are based on WordNet
            chars = token.toCharArray();
            Arrays.sort(chars);
            scramble = new String(chars);

            // find out which scramble this word relates to
            LinkedList list = scrambleMap.get(scramble);
            if (list == null)
            {
                list = new LinkedList<>();
                scrambleMap.put(scramble, list);
            }

            // Add to the list only if non-dupe
            if (!list.contains(token))
            {
                list.add(token);
                pair = new AnaPair(token, list);
                pairList.add(pair);
            }

        }   //efor
        Collections.sort(pairList);

        // Remove dupes then return
        return removeFakeAnagrams(pairList);
    }
}
