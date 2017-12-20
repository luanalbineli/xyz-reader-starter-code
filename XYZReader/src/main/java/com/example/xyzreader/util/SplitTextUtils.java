package com.example.xyzreader.util;

public abstract class SplitTextUtils {
    public static String[] splitTextIntoParts(String text, int lengthPerPart) {
        int numberOfParts = (int) Math.ceil((float) text.length() / (float) lengthPerPart);
        String[] splittedBody = new String[numberOfParts];

        for (int index = 0; index < numberOfParts; index++) {
            int beginIndex = index == 0 ? 0 : index * lengthPerPart;
            int endIndex = ((index + 1) * lengthPerPart);
            if (endIndex > text.length()) {
                endIndex = text.length();
            }

            splittedBody[index] = text.substring(beginIndex, endIndex);
        }

        return splittedBody;
    }
}
