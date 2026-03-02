/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.reputation.file;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.reputation.file.FileEntry;
import com.projectgoth.fusion.reputation.file.SortAndDumpChunkThread;
import com.projectgoth.fusion.reputation.util.DirectoryHolder;
import com.projectgoth.fusion.reputation.util.FileLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SortBigFile {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SortBigFile.class));
    public static final int LINES_PER_FILE = 25000;
    public static final int THREAD_COUNT = 4;
    public static final String CHUNK_POSTFIX = "chunk_";
    private DirectoryHolder directoryHolder;
    private int expectedFieldCount;
    private char delimeter = (char)124;
    private boolean checkExpectedFieldCount;
    private long start;
    private long readFromFile;

    public SortBigFile(DirectoryHolder directoryHolder) {
        this.directoryHolder = directoryHolder;
        log.info((Object)directoryHolder);
    }

    private boolean addFileEntryFromReader(List<FileEntry> topEntries, Comparator<FileEntry> comparator, BufferedReader reader) throws IOException {
        this.start = System.currentTimeMillis();
        String line = reader.readLine();
        this.readFromFile += System.currentTimeMillis() - this.start;
        if (line == null) {
            return false;
        }
        FileEntry fileEntry = new FileEntry(reader, StringUtil.splitIntoArray(line, this.delimeter));
        int index = Collections.binarySearch(topEntries, fileEntry, comparator);
        if (index < 0) {
            index = -1 * index - 1;
        }
        topEntries.add(index, fileEntry);
        return true;
    }

    private void sortAndDumpChunks(FileLocation outLocation, List<List<String>> lines, Comparator<List<String>> comparator, ChunkResult chunkResult) throws IOException {
        int i;
        log.debug((Object)("received " + lines.size() + " chunks to sort and dump"));
        int processedChunks = 0;
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[4];
        for (i = 0; i < 4; ++i) {
            if (lines.get(i).isEmpty()) continue;
            log.debug((Object)("chunk " + i + " is not empty and will be sorted and dumped"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.directoryHolder.getScratchDirectory() + outLocation.getFilename() + "." + CHUNK_POSTFIX + (chunkResult.getChunkCount() + (i + 1))));
            threads[i] = new SortAndDumpChunkThread(lines.get(i), this.expectedFieldCount, comparator, writer, this.delimeter, this.checkExpectedFieldCount);
            threads[i].start();
            ++processedChunks;
        }
        for (i = 0; i < 4; ++i) {
            try {
                if (threads[i] == null) continue;
                threads[i].join();
                chunkResult.setLastLine(lines.get(i).get(lines.get(i).size() - 1));
                lines.get(i).clear();
                continue;
            }
            catch (InterruptedException e) {
                // empty catch block
            }
        }
        log.debug((Object)("sortAndDumpChunks took " + (double)(System.currentTimeMillis() - start) / 1000.0 + " secs in wall time and wrote " + processedChunks + " chunks"));
        chunkResult.addChunkCount(processedChunks);
    }

    private ChunkResult chunkFileNewConcurrent(FileLocation inLocation, FileLocation outLocation, Comparator<List<String>> comparator) throws IOException {
        String line;
        log.info((Object)("starting chunking [" + inLocation.getDirectory() + inLocation.getFilename() + "]"));
        long start = System.currentTimeMillis();
        ArrayList<List<String>> lines = new ArrayList<List<String>>();
        for (int i = 0; i < 4; ++i) {
            lines.add(new ArrayList());
        }
        int linesRead = 0;
        ChunkResult chunkResult = new ChunkResult();
        int linesIndex = 0;
        BufferedReader reader = new BufferedReader(new FileReader(inLocation.getDirectory() + inLocation.getFilename()));
        while ((line = reader.readLine()) != null) {
            if (!StringUtils.hasLength((String)line)) continue;
            if (++linesRead % 25000 == 0) {
                ++linesIndex;
            }
            if (linesRead < 100000) {
                ((List)lines.get(linesIndex)).add(line);
                continue;
            }
            ((List)lines.get(linesIndex - 1)).add(line);
            this.sortAndDumpChunks(outLocation, lines, comparator, chunkResult);
            linesRead = 0;
            linesIndex = 0;
        }
        log.debug((Object)("chunks done after main while loop " + chunkResult.chunkCount));
        if (!lines.isEmpty()) {
            log.debug((Object)("we have some remainde chunks: " + lines.size()));
            this.sortAndDumpChunks(outLocation, lines, comparator, chunkResult);
            log.debug((Object)("chunks done after remainder has been done " + chunkResult.chunkCount));
        }
        long end = System.currentTimeMillis();
        log.info((Object)("done chunking " + inLocation.getDirectory() + inLocation.getFilename() + ", took " + (double)(end - start) / 1000.0 + " seconds"));
        return chunkResult;
    }

    private void mergeChunks(FileLocation outLocation, int chunkCount, Comparator<FileEntry> comparator) throws IOException {
        log.debug((Object)"parsing chunks");
        BufferedWriter finalWriter = new BufferedWriter(new FileWriter(outLocation.getDirectory() + outLocation.getFilename() + ".sorted"));
        ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>(chunkCount);
        LinkedList<FileEntry> topEntries = new LinkedList<FileEntry>();
        for (int i = 1; i <= chunkCount; ++i) {
            BufferedReader localReader = new BufferedReader(new FileReader(this.directoryHolder.getScratchDirectory() + outLocation.getFilename() + "." + CHUNK_POSTFIX + i));
            readers.add(localReader);
            this.addFileEntryFromReader(topEntries, comparator, localReader);
        }
        if (topEntries.size() != chunkCount) {
            log.error((Object)("entries after readers has been setup: " + topEntries.size() + " (this should have equaled chunk count [" + chunkCount + "])"));
            throw new IOException("Read chunks does not equal chunk count?");
        }
        long removeFirst = 0L;
        long writeToFile = 0L;
        long readFromFileAndAdd = 0L;
        while (!readers.isEmpty()) {
            long start = System.currentTimeMillis();
            FileEntry smallestEntry = (FileEntry)topEntries.removeFirst();
            removeFirst += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            for (int i = 0; i < smallestEntry.getLine().length; ++i) {
                finalWriter.write(smallestEntry.getLine()[i]);
                if (i >= smallestEntry.getLine().length - 1) continue;
                finalWriter.write(this.delimeter);
            }
            finalWriter.newLine();
            writeToFile += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            if (!this.addFileEntryFromReader(topEntries, comparator, smallestEntry.getReader())) {
                readers.remove(smallestEntry.getReader());
            }
            readFromFileAndAdd += System.currentTimeMillis() - start;
        }
        log.debug((Object)"all the readers are empty...");
        log.debug((Object)(topEntries.size() + " entries left in topEntries"));
        log.debug((Object)("removeFirst: " + removeFirst));
        log.debug((Object)("writeToFile: " + writeToFile));
        log.debug((Object)("readFromFile: " + this.readFromFile));
        log.debug((Object)("readFromFileAndAdd: " + readFromFileAndAdd));
        for (FileEntry entry : topEntries) {
            finalWriter.write(StringUtil.asStringWithoutQuotes(entry.getLine()));
            finalWriter.newLine();
        }
        for (int i = 0; i < readers.size(); ++i) {
            ((BufferedReader)readers.get(i)).close();
        }
        finalWriter.close();
    }

    public String go(FileLocation inLocation, FileLocation outLocation, Comparator<FileEntry> fileEntryComparator, Comparator<List<String>> linePartsComparator, int expectedFieldCount, char delimeter) throws IOException {
        return this.go(inLocation, outLocation, fileEntryComparator, linePartsComparator, expectedFieldCount, delimeter, false);
    }

    public String go(FileLocation inLocation, FileLocation outLocation, Comparator<FileEntry> fileEntryComparator, Comparator<List<String>> linePartsComparator, int expectedFieldCount, char delimeter, boolean checkExpectedFieldCount) throws IOException {
        this.expectedFieldCount = expectedFieldCount;
        this.delimeter = delimeter;
        this.checkExpectedFieldCount = checkExpectedFieldCount;
        log.info((Object)("sorting [" + inLocation.getDirectory() + inLocation.getFilename() + "]"));
        long start = System.currentTimeMillis();
        ChunkResult chunkResult = this.chunkFileNewConcurrent(inLocation, outLocation, linePartsComparator);
        log.info((Object)("we have " + chunkResult.getChunkCount() + " chunks to merge"));
        this.mergeChunks(outLocation, chunkResult.getChunkCount(), fileEntryComparator);
        log.info((Object)("done sorting [" + inLocation.getDirectory() + inLocation.getFilename() + "], took " + (System.currentTimeMillis() - start) / 1000L + " seconds"));
        return chunkResult.getLastLine();
    }

    private class ChunkResult {
        private int chunkCount = 0;
        private String lastLine;

        private ChunkResult() {
        }

        public int getChunkCount() {
            return this.chunkCount;
        }

        public void addChunkCount(int chunkCount) {
            this.chunkCount += chunkCount;
        }

        public String getLastLine() {
            return this.lastLine;
        }

        public void setLastLine(String lastLine) {
            this.lastLine = lastLine;
        }
    }
}

