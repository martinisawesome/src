package edu.uci.ics.crawler4j.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tracks which document ID is associated with what URL
 */
public class DocumentMap
{
    private final HashMap<Integer, String> map;

    public DocumentMap()
    {
        map = new HashMap<>();
    }

    public DocumentMap(String file)
    {
        map = new HashMap<>();
        readFromFile(file);
    }

    public final void readFromFile(String file)
    {
        // TODO read from some file that stores this and add each one
    }

    public void writeToFile() throws IOException
    {
        FileWriter wr = new FileWriter(FileSystem.CRAWLER_DIRECTORY + FileSystem.DOCUMENT_MAP_NAME, false);
        for (Map.Entry<Integer, String> entry : map.entrySet())
        {
            wr.write("" + entry.getKey());
            wr.write(" ");
            wr.write(entry.getValue());
            wr.write("\n");
        }
        wr.close();
    }

    public void add(Integer id, String url)
    {
        map.put(id, url);
    }

    public Set<Integer> getAllIds()
    {
        return map.keySet();
    }

}
