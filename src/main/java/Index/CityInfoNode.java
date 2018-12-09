package Index;

public class CityInfoNode {

    private String city_name;
    private String country_name;
    private String population_amount;
    private String currency_sign;
    private String m_posting;
    private boolean isCapital;

    public CityInfoNode(String city_name, String country_name, String population_amount, String currency_sign,boolean is_Capital) {
        this.city_name = city_name;
        this.country_name = country_name;
        this.population_amount = population_amount;
        this.currency_sign = currency_sign;
        this.m_posting="not Found";
        this.isCapital = is_Capital;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getCountryName() {
        return country_name;
    }

    public String getPopulationAmount() {
        return population_amount;
    }

    public String getCurrencySign() {
        return  currency_sign;
    }

    public void setPosting(String posting){m_posting=posting;}

    @Override
    public String toString() {
        return city_name+"\t"+country_name+"\t"+population_amount+"\t"+currency_sign+"\t"+m_posting+"\t"+isCapital+"\n";
    }
}
