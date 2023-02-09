package edu.jsu.mcis.cs310;

import java.util.*;
import com.opencsv.*;
import java.io.StringWriter;
import java.io.StringReader;
import com.github.cliftonlabs.json_simple.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",086w2q
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
      
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            List<String[]> full = csvReader.readAll();
            Iterator<String[]> iterator = full.iterator();
       
            JsonObject jsonRecord = new JsonObject();
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();
       
            if (iterator.hasNext()){
           
                String[] headings = iterator.next();
           
                while(iterator.hasNext()){
               
                    String[] csvRecord = iterator.next();
                    prodNums.add(csvRecord[0]);
                    JsonArray internalDataArray = new JsonArray();
               
                    for(int i = 1; i < headings.length; ++i){
                   
                        if(headings[i].endsWith("Season") || headings[i].endsWith("Episode")) {
                            internalDataArray.add(Integer.valueOf(csvRecord[i]));
                        }
                        else {
                            internalDataArray.add(csvRecord[i]);
                        }
                    }
                    data.add(internalDataArray);
                }
           
                jsonRecord.put("ProdNums", prodNums);
                jsonRecord.put("ColHeadings", headings);
                jsonRecord.put("Data", data);
            }
       
            result = jsonRecord.toJson();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            Map<String, Object> data = new HashMap<>();
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
        
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray dataArray = (JsonArray) jsonObject.get("Data");
        
            data.put("ProdNums", prodNums);
            data.put("ColHeadings", colHeadings);
            data.put("Data", dataArray);
        
            String[] colHeadingsString = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                colHeadingsString[i] = colHeadings.get(i).toString();
            }
        
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            csvWriter.writeNext(colHeadingsString);
            String csvString = writer.toString();
        
            for (int i = 0; i < dataArray.size(); i++) {
                JsonArray csvRecord = (JsonArray) dataArray.get(i);
                String prodNumForRecord = prodNums.get(i).toString();
            
                String[] csvRecordString = new String[csvRecord.size()];
                for (int j = 0; j < csvRecord.size(); j++) {
                    String value = csvRecord.get(j).toString();
                    if (j == 2) {
                        value = String.format("%02d", Integer.parseInt(value));
                    }
                    csvRecordString[j] = value;
                }
            
                String[] combinedArray = new String[csvRecordString.length + 1];
                combinedArray[0] = prodNumForRecord;
                System.arraycopy(csvRecordString, 0, combinedArray, 1, csvRecordString.length);
            
                csvWriter.writeNext(combinedArray);
                csvString = writer.toString();
            }
        
            result = csvString;
        
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
        
    }

}