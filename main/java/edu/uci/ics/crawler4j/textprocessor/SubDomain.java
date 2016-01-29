package edu.uci.ics.crawler4j.textprocessor;

import java.util.LinkedList;

/**
 * This is not used!
 */
public class SubDomain
{
    private final String url;
    private final LinkedList<String> pages;

    public SubDomain(String page)
    {
        this.url = page;
        pages = new LinkedList<>();
    }

    public int getPageCount()
    {
        return pages.size();
    }

    public void addPage(String page)
    {
        if (!constainsPage(page))
        {
            pages.add(page);
        }
    }

    public boolean constainsPage(String page)
    {
        return pages.contains(page);
    }

    public LinkedList<String> getPages()
    {
        return pages;
    }
    
    @Override
    public String toString()
    {
        return url + ", " + getPageCount();
    }

}
