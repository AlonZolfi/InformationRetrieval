package Model;

public class CityInfoNode {

    private String city_name;
    private String country_name;
    private String population_amount;
    private String currency_sign;

    public CityInfoNode(String city_name, String country_name, String population_amount, String currency_sign) {
        this.city_name = city_name;
        this.country_name = country_name;
        this.population_amount = population_amount;
        this.currency_sign = currency_sign;
    }


    public String getCity_name() {
        return city_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getPopulation_amount() {
        return population_amount;
    }

    public String getCurrency_sign() {
        return  currency_sign;
    }

    @Override
    public String toString() {
        return city_name+"\t"+country_name+"\t"+population_amount+"\t"+currency_sign+"\n";
    }
}
