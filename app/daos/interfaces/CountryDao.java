package daos.interfaces;

import models.Country;

import java.util.List;

public interface CountryDao {

    //Create methods

    public Boolean createCountry(Country newCountry);


    //Read methods

    //Read methods
    List<Country> getAll();

    public Country getCountryByName(String name);

    public Boolean checkIfExists(String name);

    public Country checkIfExistsThenReturn(Country country);
    //Update methods

    //Delete methods
}
