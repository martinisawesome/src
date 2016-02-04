package edu.uci.ics.crawler4j.storage;

import edu.uci.ics.crawler4j.def.Strings;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    public static final String FREQ_FILE = "Freq";
    public static final String THREE_GRAM = "3Gram";

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
     * Clears all files with content only
     */
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

    /**
     * Clears all files related to frequency counts from text processor
     */
    public static void clearFreqData()
    {
        File[] files = new File(CRAWLER_DIRECTORY).listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.getName().contains(FREQ_FILE) || f.getName().contains(THREE_GRAM))

                {

                    f.delete();
                }
            }
        }
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

    /**
     * Parses all files to retrieve content from all files, and creates new files with content only.
     * We will also pull the subdomain name from first line of file
     *
     * @param subdomains
     * @return
     */
    public LinkedList<File> getAllContentTextFiles(HashMap<String, Integer> subdomains)
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
                    File file = parseContentFileForText(subdomains, hashedContents, f);
                    if (file != null)
                    {
                        domains.add(file);
                    }
                }
            }
        }
        return domains;
    }

    /**
     * Looks for all files with Token in it
     *
     * @return
     */
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
    private File parseContentFileForText(HashMap<String, Integer> subdomains, LinkedList<Integer> hashedContents, File file)
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
            String url = br.readLine();
            WebURL wUrl = new WebURL();
            wUrl.setURL(url);
            String sub = wUrl.getSubDomain();

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
                        documentMap.add(Integer.parseInt(file.getName()), curr);
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
                //System.out.println("Duplicate hash for file: " + file.getName() + " with hash: " + hash);
                badFile = true;
            }
            else
            {
                if (!sub.equals("www"))
                {
                    Integer i = subdomains.get(sub);
                    if (i == null)
                    {
                        subdomains.put(sub, 1);
                    }
                    else
                    {
                        subdomains.put(sub, i + 1);
                    }

                }
                //System.out.println("  Hasing file: " + file.getName() + " with hash " + hash);
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

        // Do not return duplicate hashed files
        if (badFile)
        {
            return null;
        }

        return contentFile;
    }

    /**
     * Binary merges all files
     *
     * @param nameHas(either 3Gram or Freq)
     * @param index
     * @return
     * @throws IOException
     */
    public static File binaryMergeAllFreq(String nameHas, int index) throws IOException
    {
        File directory = new File(CRAWLER_DIRECTORY);
        File[] files = directory.listFiles();
        ArrayList<File> targetFiles = new ArrayList<>();
        if (files == null)
        {
            return null;
        }

        // First add all candidate files
        for (File f : files)
        {
            if (f.isDirectory())
            {

            }
            else if (f.getName().contains(nameHas))
            {
                targetFiles.add(f);
            }
        }

        if (targetFiles.size() == 1)
        {
            return targetFiles.get(0);
        }

        File exceptionalFile = (targetFiles.size() % 2 == 0)
                               ? null : targetFiles.get(targetFiles.size() - 1);

        for (int i = 0; i +1 < targetFiles.size(); i += 2)
        {
            File first = targetFiles.get(i);
            File second = targetFiles.get(i+1);
            
        //     System.out.println(first + " Open");
        //      System.out.println(second + " Open");

            FileReader fr = new FileReader(first);
            BufferedReader br0 = new BufferedReader(fr);
            FileReader fr1 = new FileReader(second);
            BufferedReader br1 = new BufferedReader(fr1);
            FileWriter fw = new FileWriter(CRAWLER_DIRECTORY + nameHas + "0" + (index++));
            StringBuilder sb = new StringBuilder();
            boolean f0Has = true;
            boolean f1Has = true;

            String curr1 = null;
            String curr0 = null;

            boolean curr1Clear = true;
            boolean curr0Clear = true;

            //loop binary
            for (int writeIndex = 0;; writeIndex++)
            {
                // Flush to file if sb too large
                if (writeIndex > 10000)
                {
                    writeIndex = 0;
                    fw.write(sb.toString());
                    sb = new StringBuilder();
                }

                // both files are empty
                if (!f0Has && !f1Has)
                {
                    break;
                }

                // does file 0 need to update?
                if (f0Has && curr0Clear)
                {
                    curr0 = br0.readLine();
                    if (curr0 == null || curr0.isEmpty())
                    {
                        f0Has = false;
                    }
                    curr0Clear = false;

                }
                // does file 1 need to update?
                if (f1Has && curr1Clear)
                {
                    curr1 = br1.readLine();
                    if (curr1 == null || curr1.isEmpty())
                    {
                        f1Has = false;
                    }
                    curr1Clear = false;
                }

                // need to compare both files
                if (f0Has && f1Has)
                {
                    String[] parm0 = curr0.split(":");
                    String[] parm1 = curr1.split(":");
                    int count0 = Integer.parseInt(parm0[1].trim());
                    int count1 = Integer.parseInt(parm1[1].trim());

                    int compares = parm0[0].compareTo(parm1[0]);

                    if (compares == 0)
                    {
                        String s = String.format("%s: %d", parm0[0], (count0 + count1));
                        sb.append(s);
                        sb.append("\n");
                        curr1Clear = true;
                        curr0Clear = true;
                    }
                    else if (compares < 0)
                    {
                        sb.append(curr0);
                        sb.append("\n");
                        curr0Clear = true;
                    }
                    else
                    {
                        sb.append(curr1);
                        sb.append("\n");
                        curr1Clear = true;
                    }

                }
                else if (f0Has)
                {
                    sb.append(curr0);
                    sb.append("\n");
                    curr0Clear = true;
                }
                else if (f1Has)
                {
                    sb.append(curr1);
                    sb.append("\n");
                    curr1Clear = true;
                }

            }

            fw.write(sb.toString());
            fr1.close();
            fw.close();
            fr.close();
            
        //    System.out.println("  Done!");

        }   // end of each file binary

        for (File f : targetFiles)
        {
            //don't delete the odd file out
            if (!f.equals(exceptionalFile))
            {
                f.delete();
            }
        }

        // Continually merge until 1 file is left
        return binaryMergeAllFreq(nameHas, index);
    }

}
