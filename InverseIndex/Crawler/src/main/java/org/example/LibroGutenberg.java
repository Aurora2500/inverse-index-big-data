package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LibroGutenberg {

    public static Map<String, String> transform(int id, String text) {
        //this is a list of tuples where each tuple contains the attribute name and an associated regular expression for searching that attribute in the text
        List<AbstractMap.SimpleEntry<String, Pattern>> attributes = Arrays.asList(
                new AbstractMap.SimpleEntry<>("title", Pattern.compile("Title: (.*)", Pattern.CASE_INSENSITIVE)),
                new AbstractMap.SimpleEntry<>("author", Pattern.compile("(?:Author|Creator|Contributor): (.*)", Pattern.CASE_INSENSITIVE)),
                new AbstractMap.SimpleEntry<>("date", Pattern.compile("Release date: (.*)", Pattern.CASE_INSENSITIVE)),
                new AbstractMap.SimpleEntry<>("lang", Pattern.compile("Language: (.*)", Pattern.CASE_INSENSITIVE))
        );

        //these are regular expressions that search for the start and end marks of the book within the text.
        //these marks have the format "*** START OF ... " and "END OF ... ***," are used to delimit the main content of the book.
        Pattern startPattern = Pattern.compile("(\\*\\*\\* START OF (THE|THIS) PROJECT GUTENBERG EBOOK.*?\\*\\*\\*)");
        Pattern endPattern = Pattern.compile("(\\*\\*\\* END OF (THE|THIS) PROJECT GUTENBERG EBOOK.*?\\*\\*\\*)");

        Map<String, String> data = new HashMap<>();

        try {
            for (AbstractMap.SimpleEntry<String, Pattern> attribute : attributes) {
                Matcher matcher = attribute.getValue().matcher(text);
                if (matcher.find()) {
                    data.put(attribute.getKey(), matcher.group(1));
                } else {
                    System.out.println("Couldn't parse " + attribute.getKey() + " for " + id);
                }
            }

            Matcher startMatcher = startPattern.matcher(text);
            Matcher endMatcher = endPattern.matcher(text);

            if (startMatcher.find() && endMatcher.find()) {
                String bookText = text.substring(startMatcher.end(), endMatcher.start());
                data.put("text", bookText);
            } else {
                System.out.println("Error parsing text for " + id);
                data = null;
            }
        } catch (Exception e) {
            System.out.println("Error parsing text for " + id);
            e.printStackTrace();
            System.exit(1);
        }

        return data;
    }

}
