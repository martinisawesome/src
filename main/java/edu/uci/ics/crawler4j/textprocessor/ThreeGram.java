package edu.uci.ics.crawler4j.textprocessor;

import java.util.Objects;

public class ThreeGram 
{
    public final String word1;
    public final String word2;
    public final String word3;

    public ThreeGram(String a, String b, String c)
    {
        this.word1 = a;
        this.word2 = b;
        this.word3 = c;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ThreeGram)
        {
            ThreeGram that = (ThreeGram) obj;
            return (this.word1.equals(that.word1) && this.word2.equals(that.word2) && this.word3.equals(that.word3));
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.word1);
        hash = 53 * hash + Objects.hashCode(this.word2);
        hash = 53 * hash + Objects.hashCode(this.word3);
        return hash;
    }

    @Override
    public String toString()
    {
        return String.format("%s, %s, %s", word1, word2, word3);
    }
    
    

}
