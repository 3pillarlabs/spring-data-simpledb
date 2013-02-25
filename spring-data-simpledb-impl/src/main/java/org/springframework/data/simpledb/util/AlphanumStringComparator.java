package org.springframework.data.simpledb.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Used to compare Strings containing numbers
 * <p/>
 * "attr10" should be <b>bigger</b> than "attr2" with this comparator
 */
public class AlphanumStringComparator implements Comparator<String>, Serializable {

    public static final int START_NUMBER_CHAR = 48;
    public static final int END_NUMBER_CHAR = 57;

    public int compare(String s1, String s2) {
        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result = 0;
            if (isNumericalChunk(thisChunk) && isNumericalChunk(thatChunk)) {
                result = compareNumericalChunks(thisChunk, thatChunk);
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0) {
                return result;
            }
        }

        return s1Length - s2Length;
    }




    private int compareNumericalChunks(String thisChunk, String thatChunk){
        int result = 0;
        // Simple chunk comparison by length.
        int thisChunkLength = thisChunk.length();
        result = thisChunkLength - thatChunk.length();
        // If equal, the first different number counts
        if (result == 0) {
            for (int i = 0; i < thisChunkLength; i++) {
                result = thisChunk.charAt(i) - thatChunk.charAt(i);
                if (result != 0) {
                    return result;
                }
            }
        }

        return result;
    }


    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once) *
     */
    private String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    private boolean isNumericalChunk(String chunk){
        return isDigit(chunk.charAt(0));
    }

    private boolean isDigit(char ch) {
        return ch >= START_NUMBER_CHAR && ch <= END_NUMBER_CHAR;
    }

}
