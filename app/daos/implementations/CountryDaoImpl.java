package daos.implementations;

import daos.interfaces.CountryDao;
import io.ebean.DuplicateKeyException;
import models.Country;

public class CountryDaoImpl implements CountryDao {

    //Create methods

    @Override
    public Boolean createCountry(Country newCountry) {
        try {
            newCountry.save();
        } catch (DuplicateKeyException e) {
            return false;
        }

        return true;
    }

    //Read methods
    @Override
    public Country getCountryByName(String name) {
        return Country.getFinder().query()
                .where()
                .eq("name", name)
                .findOne();
    }

    @Override
    public Boolean checkIfExists(String name) {
        return Country.getFinder().query()
                .where()
                .eq("name", name)
                .findCount() != 0;
    }

    @Override
    public Country checkIfExistsThenReturn(Country country) {
        Country temp = getCountryByName(country.getName());
        if (temp != null)
            return temp;
        else return country;
    }


    //Update methods

    //Delete methods
}
