package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLToJSON extends DataObject {
    private String JSONString;
    private String XMLString;

    public XMLToJSON(String XMLString) {
        this.XMLString = XMLString;
    }

    @Override
    public void parse() {

        Pattern pattern = Pattern.compile("(?:<)(\\w*\\b)"); //matches the key (in group 1)
        Matcher matcher = pattern.matcher(this.XMLString);

        matcher.find();
        String key = matcher.group(1);

        //matches the value (in group 1). if it doesn't match, the value is null so that's the default value.
        pattern = Pattern.compile("(?:>)(.*?)(?:<\\/)");
        matcher = pattern.matcher(this.XMLString);

        String value = null; //if it doesn't match, it stays null
        if (matcher.find()) {
            value = matcher.group(1);
        }

        Map<String, String> mapOfAttributes = null;

        if (hasAttributes()) {
            Pattern attributePattern = Pattern.compile("\\w*\\s*?=\\s*?\"\\w*?\""); //matches attribute = "value"
            Matcher attributeMatcher = attributePattern.matcher(this.XMLString);

            mapOfAttributes = new HashMap<>();

            while (attributeMatcher.find()) {
                Pattern attributeKeyPattern = Pattern.compile("\\w*(?=\\s*?\\=\\s*?)"); //matches a key followed by =
                Matcher attributeKeyMatcher = attributeKeyPattern.matcher(attributeMatcher.group());

                attributeKeyMatcher.find();
                String attributeKey = attributeKeyMatcher.group();

                Pattern attributeValuePattern = Pattern.compile("(?<=\\\")\\w*(?=\\\")"); //matches a word enclosed in ""
                Matcher attributeValueMatcher = attributeValuePattern.matcher(attributeMatcher.group());

                attributeValueMatcher.find();
                String attributeValue = attributeValueMatcher.group();

                mapOfAttributes.put(attributeKey, attributeValue);
            }
        }

        //map of attributes is null if there are no attributes
        this.JSONString = createJsonObject(key, value, mapOfAttributes);
    }

    @Override
    public String getConvertedResult() {
        return this.JSONString;
    }

    private String createJsonObject(String key, String value, Map<String, String> attributeMap) {
        StringBuilder str = new StringBuilder();
        str.append("{ \"" + key + "\" : ");

        if (attributeMap == null) { //if it doesn't have attributes
            if (value == null) {
                str.append("null");
            } else {
                str.append(isNumeric(value) ? value : "\"" + value + "\"");
            }
        } else {
            str.append("\n{ ");
            for (String attributeKey : attributeMap.keySet()) {
                String attributeValue = attributeMap.get(attributeKey);

                str.append("\"" + ATTRIBUTE_CHAR + attributeKey + "\" : \"" + attributeValue + "\"");
                //str.append(isNumeric(attributeValue) ? attributeValue : "\"" + attributeValue + "\"");
                str.append(",\n");
            }
            str.append("\"" + ELEMENT_CHAR + key + "\" : ");
            str.append(value == null ? value : "\"" + value + "\"");
            str.append(" }\n");
        }

        str.append(" }");

        return str.toString();
    }

    private boolean isNumeric(String value) {
        // if the conversion to number throws an exception, the string it's not a number
        try {
            int valueInt = Integer.valueOf(value);
            double valueDbl = Double.parseDouble(value);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean hasAttributes() {
        //matches the pattern <key attribute1 = "value1" .. attributeN = "valueN"> or />
        Pattern pattern = Pattern.compile("<.*?=\\s*?\".*?\"\\s*?\\/?>");
        Matcher matcher = pattern.matcher(this.XMLString);

        return matcher.find();
    }

    @Override
    public String getXMLString() {
        return this.XMLString;
    }

    @Override
    public String getJSONString() {
        return this.JSONString;
    }
}