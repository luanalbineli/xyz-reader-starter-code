package com.example.xyzreader;

import com.example.xyzreader.util.SplitTextUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SplitTextTest {

    @Test
    public void justOnePosition() throws Exception {
        final String text = "1234567";
        String[] parts = SplitTextUtils.splitTextIntoParts(text, 10);

        assertEquals("Expected one part", parts.length, 1);

        assertEquals("The unique part", "1234567", parts[0]);
    }

    @Test
    public void simpleSplit() throws Exception {
        final String text = "1234567ENDBEGIN";
        String[] parts = SplitTextUtils.splitTextIntoParts(text, 10);

        assertEquals("Expected two parts", parts.length, 2);

        assertEquals("The first part", "1234567END", parts[0]);

        assertEquals("The second part", "BEGIN", parts[1]);
    }

    @Test
    public void anotherSplit() throws Exception {
        final String text = "1234567ENDBEGIN00000FINAL00000";
        String[] parts = SplitTextUtils.splitTextIntoParts(text, 10);

        assertEquals("Expected two parts", parts.length, 3);

        assertEquals("The first part", "1234567END", parts[0]);

        assertEquals("The second part", "BEGIN00000", parts[1]);

        assertEquals("The third part", "FINAL00000", parts[2]);
    }

    @Test
    public void anotherAnotherSplit() throws Exception {
        final String text = "1234567ENDBEGIN00000FINAL00000L";
        String[] parts = SplitTextUtils.splitTextIntoParts(text, 10);

        assertEquals("Expected two parts", parts.length, 4);

        assertEquals("The first part", "1234567END", parts[0]);

        assertEquals("The second part", "BEGIN00000", parts[1]);

        assertEquals("The third part", "FINAL00000", parts[2]);

        assertEquals("The final part", "L", parts[3]);
    }
}
