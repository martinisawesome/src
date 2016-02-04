package edu.uci.ics.crawler4j.textprocessor;

public class FreqPair<E> implements Comparable<FreqPair>
{
    public final E token;
    private int count;

    public FreqPair(E token)
    {
        this(token, 1);
    }

    public FreqPair(E token, int count)
    {
        this.token = token;
        this.count = count;
    }

    public int incCount()
    {
        count++;
        return count;
    }

    public int getCount()
    {
        return count;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %d", token, count);
    }

    @Override
    public int compareTo(FreqPair p2)
    {
        // highest frequency first!
        //return   p2.getCount() - getCount();
        
           return   this.token.toString().compareTo(p2.token.toString());
    }

}
