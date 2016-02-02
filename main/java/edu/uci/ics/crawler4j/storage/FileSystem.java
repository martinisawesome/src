package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Used for post-processing all the file locations
 */
public class FileSystem
{
    public static final String TOKEN = "Token";
    public static final String TEXT = "Text";
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

    public static void clearTextData()
    {
        File[] files = new File(CRAWLER_DIRECTORY).listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.getName().contains(TEXT))
                {

                    f.delete();
                }
            }
        }
    }

    @Deprecated
    public static boolean findDuplicateFile(List<String> tokenList)
    {
        File[] files = new File(CRAWLER_DIRECTORY).listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.getName().contains(TOKEN))
                {
                    int count = 0;
                    int index = 0;

                    try
                    {
                        FileReader fr = new FileReader(f);
                        BufferedReader br = new BufferedReader(fr);
                        String curr;

                        Iterator it = tokenList.iterator();

                        //Scan for the Sub-Domains text line
                        while ((curr = br.readLine()) != null && it.hasNext())
                        {
                            if (curr.equals(it.next()))
                            {
                                count++;
                            }

                            index++;

                            //not too similar, just end this
                            if (index == 1000)
                            {
                                break;
                            }
                        }

                        //over 90% similarity!
                        if (count * 100 / index > 95)
                        {
                            return true;
                        }

                        fr.close();
                    }
                    catch (IOException e)
                    {
                        System.out.println("Failed to read file: " + f.getName());
                    }

                }
            }
        }

        return false;
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
        Pattern numberic = Pattern.compile("[0-9]*$");

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
                else if (numberic.matcher(f.getName()).matches() && !f.getName().contains("Crawler"))
                {
                    File file = parseContentFileForText(hashedContents, f);
                    if (file != null)
                    {
                        domains.add(file);
                    }
                }
            }
        }
        return domains;
    }

    public static LinkedList<File> getAllTokenTextFiles()
    {

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
                else if (f.getName().contains(TOKEN))
                {
                    domains.add(f);
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
    private static File parseContentFileForText(LinkedList<Integer> hashedContents, File file)
    {
        File contentFile = new File(CRAWLER_DIRECTORY + TEXT + file.getName());
        boolean badFile = false;
        try
        {

            StringBuilder sb = new StringBuilder();
            FileWriter wr = new FileWriter(contentFile, false);
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
                        while (!"#Text#".equals(curr = br.readLine()))
                        {
                            // keep skipping title
                        }
                        found = true;
                    }
                    else if ("#Text#".equals(curr))
                    {
                        found = true;

                    }
                    else
                    {
                        System.out.println(file.getName() + " string: " + curr);
                        throw new IOException("Content File does not start with correct header!");
                    }
                }   //end if found

                //Do not include HTML markup
                if (curr.equals(Strings.SPACER))
                {
                    break;
                }
                else if (found && !curr.isEmpty() && !curr.contains("#Text#"))
                {
                    sb.append(curr);
                    sb.append(' ');
                }

            }

            String strings = sb.toString();
            int hash = strings.hashCode();
            if (hashedContents.contains(hash))
            {
                System.out.println("Duplicate hash for file: " + file.getName() + " with hash: " + hash);
                badFile = true;
            }
            else
            {
                System.out.println("  Hasing file: " + file.getName() + " with hash " + hash);
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

        if (badFile)
        {
            return null;
        }
        return contentFile;
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
                                    if (strings[i].equals(Strings.SUBDOMAIN_TEXT + ':')
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
