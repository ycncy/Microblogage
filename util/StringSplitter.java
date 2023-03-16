package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StringSplitter {

    public static Map<String, String> split (String header) {
        String[] header_parts = header.split("\\s+");

        Map<String, String> header_map = new HashMap<>();

        for (String element : header_parts) {
            String[] element_splited = element.split(":@|:");
            header_map.put(element_splited[0], element_splited[1]);
        }

        return header_map;
    }
}
