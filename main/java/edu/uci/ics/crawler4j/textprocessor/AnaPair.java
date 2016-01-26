package edu.uci.ics.crawler4j.textprocessor;

import java.util.LinkedList;

public class AnaPair implements Comparable<AnaPair>
{
    public final String token;
    public final LinkedList<String> anagrams;

    public AnaPair(String token)
    {
        this(token, new LinkedList<String>());
    }

    public AnaPair(String token, LinkedList<String> anagrams)
    {
        this.token = token;
        this.anagrams = anagrams;
    }

    public void addAnagram(String a)
    {
        anagrams.add(a);
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s", token, anagrams);
    }

    @Override
    public int compareTo(AnaPair p2)
    {
        return token.compareTo(p2.token);
    }
}
