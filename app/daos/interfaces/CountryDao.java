package daos.interfaces;

import models.Country;

public interface CountryDao {

    //Create methods

    public Boolean createCountry (Country newCountry);


    //Read methods

    public Country getCountryByName (String name);
    public Boolean checkIfExists (String name);
    public  Country checkIfExistsThenReturn (Country country);
    //Update methods

    //Delete methods
}
