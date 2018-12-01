package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;

public class CitysMemoryDataBase {

    private HashMap<String,CityInfoNode> CountryDB;

    public CitysMemoryDataBase(String WebServiceURL) throws IOException {

        this.CountryDB = new HashMap<>();
        APIRequest request = new APIRequest();
        JSONObject jsonDetails = request.post(WebServiceURL);
        JSONArray result = jsonDetails.getJSONArray("result");

        for (Object obj: result){
            JSONObject data = (JSONObject)obj;
            String currency = data.getJSONArray("currencies").getJSONObject(0).get("name").toString();
            String countryName = data.get("name").toString();
            String capitalName = data.get("capital").toString();
            String population = data.get("population").toString();
            CityInfoNode cur = new CityInfoNode(capitalName,countryName,population,currency);
            this.CountryDB.put(cur.getCity_name(),cur);
        }
    }


    public CityInfoNode getCountryByCapital(String capitalName) {
        return this.CountryDB.get(capitalName);
    }
}
