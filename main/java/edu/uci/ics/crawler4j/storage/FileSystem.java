package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
        LinkedList<Integer> hashedContents = new LinkedList<>();
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
                    domains.add(parseContentFileForText(hashedContents, titles, f));
                }
            }
        }
        return domains;
    }

    /**
     * Parses a content file and returns a new file with no HTML markup
     *
     * @param hashedContents
     * @param titles
     * @param file
     * @return
     */
    private static File parseContentFileForText(LinkedList<Integer> hashedContents, LinkedList<String> titles, File file)
    {
        File content = new File("Text" + file.getName());

        try
        {
            StringBuilder sb = new StringBuilder();
            FileWriter wr = new FileWriter(content, false);
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

                //Do not include HTML markup
                if (curr.equals(Strings.SPACER))
                {
                    break;
                }
                else if (found && !curr.isEmpty())
                {
                    sb.append(curr);
                    sb.append(' ');
                }

            }

            String strings = sb.toString();
            int hash = strings.hashCode();
            if (hashedContents.contains(hash))
            {
                System.out.println("Duplicate hash for file: " + file.getName());
            }
            else
            {
                hashedContents.add(hash);
            }

            wr.write(strings);
            fr.close();
            wr.close();
        }

        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return content;
    }

    public static HashMap<String, Integer> findAllSubdomains()
    {
        HashMap<String, Integer> domains = new HashMap<>();
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
                                        && !"www".equals(text))
                                    {
                                        Integer count = domains.get(text);
                                        if (count == null)
                                        {
                                            domains.put(text, 1);
                                        }
                                        else
                                        {
                                            domains.put(text, count + 1);
                                        }
                                    }
                                }

                                // Keep looking
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
