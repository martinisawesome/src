package edu.uci.ics.crawler4j.textprocessor;

import java.util.Arrays;

public class NGram
{
    public final String[] word;

    public NGram(String... a)
    {
        this.word = a;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof NGram)
        {
            NGram that = (NGram) obj;
            if (that.word.length != word.length)
            {
                return false;
            }

            for (int i = 0; i < word.length; i++)
            {
                if (!word[i].equals(that.word[i]))
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 41 * hash + Arrays.deepHashCode(this.word);
        return hash;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String word1 : word)
        {
            sb.append(word1).append(", ");
        }
        
        return sb.toString();
    }

}
