package edu.uci.ics.crawler4j.storage;

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
}
