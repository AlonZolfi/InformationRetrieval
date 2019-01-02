package IO;

import Queries.Query;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ReadQuery {

    /**
     * reads queries from a file
     * @param queries queries file
     * @return list of Query object
     */
    public static LinkedList<Query> readQueries(File queries){
        FileInputStream fis = null;
        LinkedList<Query> queryList=null;
        try {
            fis = new FileInputStream(queries);
            Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            Elements elements = doc.select("top");
            queryList = new LinkedList<>();
            for(Element element: elements){
                String numberWithOtherData = element.getElementsByTag("num").text();
                //String queryNum = getNumberOfQuery(numberWithOtherData);
                String num = "Number: ";
                int startNumIndex = numberWithOtherData.indexOf(num)+num.length();
                int endNumIndex = numberWithOtherData.indexOf(' ',startNumIndex+1);
                String queryNum = numberWithOtherData.substring(startNumIndex,endNumIndex);

                String titleWithOtherData = element.getElementsByTag("title").text();
                String title = titleWithOtherData.substring(0,titleWithOtherData.indexOf("\n")-1);

                String descWithOtherData = element.getElementsByTag("desc").text();
                String description = "Description: \n";
                int startDescIndex = descWithOtherData.indexOf(description)+description.length();
                int endDescIndex = descWithOtherData.indexOf("Narrative");
                String desc = descWithOtherData.substring(startDescIndex,endDescIndex-3);

                String narrWithOtherData = element.getElementsByTag("narr").text();
                String narrative ="Narrative: \n";
                String narr = narrWithOtherData.substring(narrWithOtherData.indexOf(narrative)+narrative.length());
                queryList.add(new Query(queryNum,title,desc,narr));
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryList;
    }
}
