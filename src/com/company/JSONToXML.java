package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONToXML extends DataObject {
    private String JSONString;
    private String XMLString;

    public JSONToXML(String JSONString) {
        this.JSONString = JSONString;
    }

    @Override
    public void parse() {
        if (hasAttributes()) {
            //matches "attr" : value for each attribute and value
            Pattern pattern = Pattern.compile("\"[" + ATTRIBUTE_CHAR + ELEMENT_CHAR + "]\\w*\".*?(?=(,|$|}))");
            Matcher matcher = pattern.matcher(this.JSONString);

            Map<String, String> mapOfAttributes = new HashMap<>();

            String mainKey = "", mainValue = "";
            while (matcher.find()) {

                String match = matcher.group();

                //if it's an attribute, the second char will be an @, else it will be an #
                if (match.charAt(1) == ATTRIBUTE_CHAR || match.charAt(1) == ELEMENT_CHAR) {

                    match = match.replaceAll("\"", "");
                    //now the previously character at pos 1 is now in pos 0

                    //matches a string which begins with @ or # and ends with :
                    Pattern keyPattern = Pattern.compile("(?<=" + match.charAt(0) + ").*?(?=:)"); //has a space at the end
                    Matcher keyMatcher = keyPattern.matcher(match);

                    keyMatcher.find();
                    String key = keyMatcher.group().trim();

                    //matches a string that begins with : and ends with , or } or end of string.
                    Pattern valuePattern = Pattern.compile("(?<=:).*?(?=(,|}|$))");
                    Matcher valueMatcher = valuePattern.matcher(match);

                    valueMatcher.find();
                    String value = valueMatcher.group().trim();


                    if (match.charAt(0) == ATTRIBUTE_CHAR) {
                        mapOfAttributes.put(key, value);
                    } else {
                        mainKey = key;
                        mainValue = value;
                    }
                } else {
                    System.out.println("Unknown character at the start of JSON attribute.");
                    System.exit(0);
                }
            }

            this.XMLString = createXMLTag(mainKey, mainValue, mapOfAttributes);

        } else {
            Pattern pattern = Pattern.compile("(?:\")(\\w*?)(?:\")"); //matches the key (in group 1)
            Matcher matcher = pattern.matcher(this.JSONString);

            matcher.find();
            String key = matcher.group(1);

            //matches the value (in group 1) even if it has no quotes
            pattern = Pattern.compile("(?::\\s*[\"]?)(.*?)(?:[\"]?\\s*})");
            matcher = pattern.matcher(this.JSONString);

            matcher.find();

            String value = matcher.group(1);

            this.XMLString = createXMLTag(key, value);

        }
    }

    @Override
    public String getConvertedResult() {
        return this.XMLString;
    }

    private boolean hasAttributes() {
        //matches the pattern { "key" : {
        Pattern pattern = Pattern.compile("\\{\\s*?\".*?\"\\s*?:\\s*?\\{");
        Matcher matcher = pattern.matcher(JSONString);

        return matcher.find();
    }

    private String createXMLTag(String key, String value, Map<String, String> attributeMap) {
        StringBuilder XMLTag = new StringBuilder();
        XMLTag.append("<" + key);

        if (attributeMap != null) {
            for (String k : attributeMap.keySet()) {
                XMLTag.append(" " + k + " = \"" + attributeMap.get(k) + "\"");
            }
        }

        if (value == null || "null".equals(value)) {
            XMLTag.append(" />");
        } else {
            XMLTag.append(">" + value + "</" + key + ">");
        }

        return XMLTag.toString();
    }

    private String createXMLTag(String key, String value) {
        return createXMLTag(key, value, null);
    }

    @Override
    public String getJSONString() {
        return JSONString;
    }

    @Override
    public String getXMLString() {
        return XMLString;
    }
}