package Web;

import Index.CityInfoNode;
import Parse.Parse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

public class CitysMemoryDataBase {

    private HashMap<String, CityInfoNode> CountryDB;

    /**
     * this function creates the city data base containing all data about it
     * @param webURL the url to address to
     * @throws IOException .
     */
    public CitysMemoryDataBase(String webURL) throws IOException {

        this.CountryDB = new HashMap<>();
        APIRequest request = new APIRequest();
        JSONObject details = request.post(webURL);
        JSONArray result = details.getJSONArray("result");

        for (Object obj: result){
            JSONObject data = (JSONObject)obj;
            String currency = data.getJSONArray("currencies").getJSONObject(0).get("name").toString();
            String countryName = data.get("name").toString();
            String capitalName = data.get("capital").toString();
            String population = data.get("population").toString();
            Parse parse = new Parse();
            population = parse.handleNumber(Integer.parseInt(population));
            CityInfoNode cur = new CityInfoNode(capitalName.toUpperCase(),countryName,population,currency,true);
            this.CountryDB.put(cur.getCity_name(),cur);
        }
    }

    /**
     * returns the capital name of the country given
     * @param capitalName the country given
     * @return the capital name of the country given
     */
    public CityInfoNode getCountryByCapital(String capitalName) {
        if(CountryDB.containsKey(capitalName))
            return this.CountryDB.get(capitalName);
        return null;
    }
}
