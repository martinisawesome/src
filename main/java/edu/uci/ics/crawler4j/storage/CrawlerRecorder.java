package edu.uci.ics.crawler4j.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CrawlerRecorder
{
    private final int index;

    // Path Writing
    private FileWriter pathing;
    private StringBuilder pather;
    private int pathSize;

    // Header Writing
    private FileWriter heading;
    private StringBuilder header;
    private int headSize;

    public CrawlerRecorder(int index) throws IOException
    {

        this.index = 0;

        this.pathSize = 0;
        File path = new File(FileSystem.CRAWLER_DIRECTORY + FileSystem.CRAWLER_PATHING_NAME + index);
        pathing = new FileWriter(path, false);
        pather = new StringBuilder();

        this.headSize = 0;

        File path1 = new File(FileSystem.CRAWLER_DIRECTORY + FileSystem.HEADER_FILE_NAME + index);
        heading = new FileWriter(path1, false);
        header = new StringBuilder();

    }

    private void rePathFile() throws IOException
    {
        pathing.close();
        File path = new File(FileSystem.CRAWLER_DIRECTORY + FileSystem.CRAWLER_PATHING_NAME + index);
        pathing = new FileWriter(path, true);
     pather    = new StringBuilder();
    }

    private void reHeadFile() throws IOException
    {
        heading.close();
        File path = new File(FileSystem.CRAWLER_DIRECTORY + FileSystem.HEADER_FILE_NAME + index);
        heading = new FileWriter(path, true);
      header   = new StringBuilder();
    }

    public void writePath(String message)
    {
        try
        {
            pather.append(message).append("\n");
            pathSize++;
            if (pathSize > 1000)
            {
                pathSize = 0;
                pathing.write(pather.toString());
                rePathFile();
            }
        }
        catch (IOException e)
        {
            System.err.println("Failed to print pathing: " + index);
            e.printStackTrace();
        }
    }

    public void writeHead(String message)
    {
        try
        {
            header.append(message).append("\n");
            headSize++;
            if (headSize > 200)
            {
                headSize = 0;
                heading.write(header.toString());
                reHeadFile();
            }
        }
        catch (IOException e)
        {
            System.err.println("Failed to print heading: " + index);
            e.printStackTrace();
        }
    }

    public void writeContent(int docId, String text)
    {
        try
        {
            File content = new File(FileSystem.CRAWLER_DIRECTORY + docId);
            FileWriter wr = new FileWriter(content, false);
            wr.write(text);
            wr.close();
        }
        catch (IOException e)
        {
            System.err.println("Failed to print documentID: " + docId);
            e.printStackTrace();
        }
    }

    public void close()
    {
        try
        {
            pathing.write(pather.toString());
            pathing.close();
            pather = null;

            heading.write(header.toString());
            heading.close();
            header = null;
        }
        catch (IOException e)
        {
            System.err.println("Failed to close Printer: " + index);
            e.printStackTrace();
        }
    }
}
