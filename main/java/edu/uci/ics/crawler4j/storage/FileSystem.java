package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Used for post-processing all the file locations
 */
public class FileSystem
{

    public static final String CRAWLER_DIRECTORY = "E:\\Crawl\\";
    public static final String DOCUMENT_MAP_NAME = "Doc_Map";
    public static final String HEADER_FILE_NAME = "Crawler_Header";
    public static final String CRAWLER_PATHING_NAME = "Crawler_Path";

    private final DocumentMap documentMap;

    // Store Header Logs
    // Store Content Logs
    // Store Document ID logs
    // Store Indexer Logs
    public FileSystem()
    {
        this.documentMap = new DocumentMap();
    }

    public DocumentMap getDocumentMap()
    {
        return documentMap;
    }

    /**
     * Don't do this!!!!
     */
    public static void clearEverything()
    {
        deleteFolder(new File(CRAWLER_DIRECTORY));
    }

    public static void clearFrontierDirectory()
    {
        String location = CRAWLER_DIRECTORY + "frontier";
        deleteFolder(new File(location));
    }

    // DISCLAIMER!!! This method was taken from stackoverflow.com
    public static void deleteFolder(File folder)
    {
        File[] files = folder.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                {
                    deleteFolder(f);
                }
                else
                {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static LinkedList<File> getAllContentTextFiles()
    {
        LinkedList<String> titles = new LinkedList<>();
        Pattern numberic = Pattern.compile("[^0-9]");

        LinkedList<File> domains = new LinkedList<>();
        File directory = new File(CRAWLER_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                {

                }
                else if (!numberic.matcher(f.getName()).matches())
                {
                    domains.add(parseContentFileForText(titles, f));
                }
            }
        }
        return domains;
    }

    /**
     * Parses a content file and returns a new file with no HTML markup
     *
     * @param titles
     * @param file
     * @return
     */
    public static File parseContentFileForText(LinkedList<String> titles, File file)
    {
        File content = new File("Text" + file.getName());

        try
        {
            FileWriter wr = new FileWriter(content);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String curr;
            boolean found = false;

            //Scan for the Sub-Domains text line
            while ((curr = br.readLine()) != null)
            {
                if (!found)
                {
                    if ("#Title#".equals(curr))
                    {
                        //Skip Title of the text
                        String title = br.readLine();
                        if (titles.contains(title))
                        {
                            System.out.println("Dupe Title: " + title);
                        }
                        else
                        {
                            titles.add(title);
                        }
                    }
                    else if ("#Text#".equals(curr))
                    {
                        found = true;

                    }
                    else
                    {
                        throw new IOException("Content File does not start with correct header!");
                    }
                }

                if (curr.equals(Strings.SPACER))
                {
                    break;
                }
                else if (found && !curr.isEmpty())
                {
                    wr.write(curr);
                    wr.write(' ');
                }

            }

            fr.close();
            wr.close();
        }

        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return content;
    }

    public static LinkedList<String> findAllSubdomains()
    {
        LinkedList<String> domains = new LinkedList<>();
        File directory = new File(CRAWLER_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                {

                }
                else if (f.getName().contains(HEADER_FILE_NAME))
                {
                    try
                    {
                        FileReader fr = new FileReader(f);
                        BufferedReader br = new BufferedReader(fr);
                        String curr;

                        //Scan for the Sub-Domains text line
                        while ((curr = br.readLine()) != null)
                        {
                            if (curr.contains(Strings.SUBDOMAIN_TEXT))
                            {
                                String[] strings = curr.split(" ");
                                for (int i = 0; i + 1 < strings.length; i++)
                                {
                                    String text = strings[i + 1];
                                    if (strings[i].equals(Strings.SUBDOMAIN_TEXT)
                                        && !text.equals(Strings.EMPTY_FIELD)
                                        && !"www".equals(text)
                                        && !domains.contains(text))
                                    {
                                        domains.add(text);
                                    }
                                }

                                break;
                            }
                        }

                        fr.close();
                    }

                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                }
            }

        }
        return domains;
    }

}
