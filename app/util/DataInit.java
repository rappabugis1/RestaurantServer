package util;

import daos.implementations.LocationDaoImpl;
import daos.interfaces.LocationDao;
import io.ebean.Ebean;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataInit {
    public DataInit() {

        LocationDao locDao = new LocationDaoImpl();

        if (!(Country.getFinder().all().size() > 0)) {

            //Create extension for randoms
            String sql = "CREATE EXTENSION if not exists tsm_system_rows;";
            Ebean.createSqlUpdate(sql).execute();

            //One country
            Country country = new Country("Bosnia and Herzegovina");
            country.save();

            //Locations

            ArrayList<String> cityNames = new ArrayList<>();
            cityNames.add("Sarajevo");
            cityNames.add("Mostar");
            cityNames.add("Zenica");
            cityNames.add("Travnik");
            cityNames.add("Banja Luka");
            cityNames.add("Maglaj");
            cityNames.add("Kljuc");
            cityNames.add("Vitez");
            cityNames.add("Gorazde");
            cityNames.add("Gracanica");
            cityNames.add("Cazin");
            cityNames.add("Visoko");

            for (String name : cityNames) {
                Location location = new Location(name, country);
                location.save();
            }

            //Categories
            ArrayList<String> categoryNames = new ArrayList<>();
            categoryNames.add("Bosnian");
            categoryNames.add("American");
            categoryNames.add("Fast Food");
            categoryNames.add("Vegetarian");
            categoryNames.add("Arabic");
            categoryNames.add("Chinese");
            categoryNames.add("Meat");
            categoryNames.add("Indian");
            categoryNames.add("Italian");
            categoryNames.add("Pizzas");
            categoryNames.add("Spanish");
            categoryNames.add("Take-out");

            for (String name : categoryNames) {
                Category category = new Category(name);
                category.save();
            }

            //Dish types
            ArrayList<String> dishTypeNames = new ArrayList<>();
            dishTypeNames.add("Beverages");
            dishTypeNames.add("Meals");
            dishTypeNames.add("Soups");
            dishTypeNames.add("BBQ Foods");

            for (String name : dishTypeNames) {
                DishType dishType = new DishType(name);
                dishType.save();
            }

            //Restaurants
            ArrayList<String> restaurantNames = new ArrayList<>();
            restaurantNames.add("Panera");
            restaurantNames.add("Picolo Mondo");
            restaurantNames.add("Nanina Kuhinja");
            restaurantNames.add("U2");
            restaurantNames.add("Four Seasons");
            restaurantNames.add("Dva Goluba");
            restaurantNames.add("Avlija");
            restaurantNames.add("Bon Appetit");
            restaurantNames.add("Zmaj");
            restaurantNames.add("Mala kuhinja");
            restaurantNames.add("Galija");
            restaurantNames.add("Taj Mahal");
            restaurantNames.add("Saraj Bosna");
            restaurantNames.add("Cakum Pakum");
            restaurantNames.add("Trattoria Boccone");
            restaurantNames.add("Chipas");
            restaurantNames.add("iChicken");
            restaurantNames.add("Preperito");
            restaurantNames.add("Pod Lipom");
            restaurantNames.add("Cevabdzinica Zeljo");
            restaurantNames.add("Pizzeria Mozzart");
            restaurantNames.add("Sushi San");
            restaurantNames.add("Steak House");
            restaurantNames.add("Mr Gurman");
            restaurantNames.add("Dulagin Dvor");
            restaurantNames.add("Aeroplan");
            restaurantNames.add("Maestro");
            restaurantNames.add("Tutto Bene");
            restaurantNames.add("Restoran Zelena Dolina");
            restaurantNames.add("Kod Bibana Restoran");
            restaurantNames.add("Zacin");
            restaurantNames.add("Soho Caffe Restoran");
            restaurantNames.add("Restoran Sendi");
            restaurantNames.add("Pivnica");
            restaurantNames.add("Mala Basta");
            restaurantNames.add("Milki ");
            restaurantNames.add("Esmeralda");
            restaurantNames.add("Cevabdzinica Kastel");
            restaurantNames.add("Hacienda");
            restaurantNames.add("Delikatesna Radnja");
            restaurantNames.add("Merak Food");
            restaurantNames.add("Moscanica");
            restaurantNames.add("Zeljo");
            restaurantNames.add("Yam Yam");
            restaurantNames.add("Mrkva");
            restaurantNames.add("Metropolis");
            restaurantNames.add("Restoran San ");
            restaurantNames.add("Trattoria Anatra");
            restaurantNames.add("Tima-Irma");
            restaurantNames.add("Sadrvan");
            restaurantNames.add("Hindin Han");
            restaurantNames.add("Lagero");
            restaurantNames.add("Konoba Taurus");
            restaurantNames.add("Food House");
            restaurantNames.add("Megi");
            restaurantNames.add("Behar");
            restaurantNames.add("Restoran Babilon");
            restaurantNames.add("Europa Restoran");
            restaurantNames.add("Porto Pizzeria");
            restaurantNames.add("Restoran Teatar");
            restaurantNames.add("Moon Star Caffe ");
            restaurantNames.add("Gusar");

            ArrayList<String> restaurantImageFiles = new ArrayList<>();
            restaurantImageFiles.add("https://images.pexels.com/photos/460537/pexels-photo-460537.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/262047/pexels-photo-262047.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/720299/pexels-photo-720299.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/279813/pexels-photo-279813.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/1024359/pexels-photo-1024359.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/6267/menu-restaurant-vintage-table.jpg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=480");
            restaurantImageFiles.add("https://images.pexels.com/photos/67468/pexels-photo-67468.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=640&w=4800");


            String coverFile = "https://images.pexels.com/photos/791810/pexels-photo-791810.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";

            String description = "Restaurants are classified or distinguished in many different ways. The primary factors are usually the food itself (e.g. vegetarian, seafood, steak); the cuisine (e.g. Italian, Chinese, Japanese, Indian, French, Mexican, Thai) or the style of offering (e.g. tapas bar, a sushi train, a tastet restaurant, a buffet restaurant or a yum cha restaurant). Beyond this, restaurants may differentiate themselves on factors including speed (see fast food), formality, location, cost, service, or novelty themes (such as automated restaurants).\n" +
                    "\n" +
                    "Restaurants range from inexpensive and informal lunching or dining places catering to people working nearby, with modest food served in simple settings at low prices, to expensive establishments serving refined food and fine wines in a formal setting. In the former case, customers usually wear casual clothing. In the latter case, depending on culture and local traditions, customers might wear semi-casual, semi-formal or formal wear. Typically, at mid- to high-priced restaurants, customers sit at tables, their orders are taken by a waiter, who brings the food when it is ready. After eating, the customers then pay the bill. In some restaurants, such as workplace cafeterias, there are no waiters; the customers use trays, on which they place cold items that they select from a refrigerated container and hot items which they request from cooks, and then they pay a cashier before they sit down. Another restaurant approach which uses few waiters is the buffet restaurant. Customers serve food onto their own plates and then pay at the end of the meal. Buffet restaurants typically still have waiters to serve drinks and alcoholic beverages. Fast food restaurants are also considered a restaurant.";

            float lat = 18.4130763f;
            float longit = 43.8562586f;

            Random rand = new Random();

            List<Location> locations = Location.getFinder().all();
            List<Category> categories = Category.finder.all();


            //MenuTypes
            ArrayList<String> menuTypes = new ArrayList<>();
            menuTypes.add("Breakfast");
            menuTypes.add("Lunch");
            menuTypes.add("Dinner");

            for (String name : restaurantNames) {
                Restaurant restaurant = new Restaurant(name, description, lat, longit, restaurantImageFiles.get(rand.nextInt(restaurantImageFiles.size())), coverFile, rand.nextInt(5) + 1);

                List<Category> tempCats = new ArrayList<>(categories);

                restaurant.getCategoryList().clear();

                for (int i = 0; i < 3; i++) {
                    int randomIndex = rand.nextInt(tempCats.size());
                    restaurant.getCategoryList().add(tempCats.get(randomIndex));
                    tempCats.remove(randomIndex);
                }

                restaurant.setLocation(locations.get(rand.nextInt(locations.size())));

                restaurant.save();

                //Menus for each restaurant
                for (String menuType : menuTypes) {
                    Menu menu = new Menu(menuType, restaurant);
                    menu.save();
                }

                for (int i = 1; i < 8; i++) {
                    Table table = new Table(i, restaurant);
                    table.save();
                }
            }

            //Dishes for menus
            ArrayList<String> bevaregeNames = new ArrayList<>();
            bevaregeNames.add("Coca Cola");
            bevaregeNames.add("Pepsi");
            bevaregeNames.add("Kiseljak");
            bevaregeNames.add("Dvojni C");
            bevaregeNames.add("Multivitamin");
            bevaregeNames.add("Sinalco Cola");
            bevaregeNames.add("Voda");
            bevaregeNames.add("Ledeni Caj");

            ArrayList<String> mealNames = new ArrayList<>();
            mealNames.add("Sarma");
            mealNames.add("Riza sa gulasom");
            mealNames.add("Riza sa piletinom");
            mealNames.add("Batak sa hljebom");
            mealNames.add("Burek");
            mealNames.add("Burek sa sirom");
            mealNames.add("Sirnica");
            mealNames.add("Pire krompir");

            ArrayList<String> soupNames = new ArrayList<>();
            soupNames.add("Grah");
            soupNames.add("Corba");
            soupNames.add("Begova Corba");
            soupNames.add("Supa sa piletinom");
            soupNames.add("Supa sa grahom");
            soupNames.add("Grah sa supom");
            soupNames.add("Sutlija");

            ArrayList<String> bbqNames = new ArrayList<>();
            bbqNames.add("Cevapi");
            bbqNames.add("Cevapcici");
            bbqNames.add("Tele");
            bbqNames.add("Pile");
            bbqNames.add("Hamburger");
            bbqNames.add("Pileci Hamburger");
            bbqNames.add("Hot Dog");

            List<Menu> menus = Menu.getFinder().all();

            for (Menu menuTemp : menus) {
                List<String> tempBev = new ArrayList<>(bevaregeNames);
                List<String> tempMeal = new ArrayList<>(mealNames);
                List<String> tempSoup = new ArrayList<>(soupNames);
                List<String> tempBBQ = new ArrayList<>(bbqNames);

                for (int i = 0; i < 4; i++) {

                    int randomIndex = rand.nextInt(tempBev.size());
                    Dish dish = new Dish(tempBev.get(randomIndex), "", rand.nextInt(20), menuTemp, DishType.getFinder().query().where().eq("type", "Beverages").findOne());
                    tempBev.remove(randomIndex);

                    dish.save();
                }
                for (int i = 0; i < 4; i++) {

                    int randomIndex = rand.nextInt(tempMeal.size());
                    Dish dish = new Dish(tempMeal.get(randomIndex), "", rand.nextInt(20), menuTemp, DishType.getFinder().query().where().eq("type", "Meals").findOne());
                    tempMeal.remove(randomIndex);

                    dish.save();
                }
                for (int i = 0; i < 4; i++) {

                    int randomIndex = rand.nextInt(tempSoup.size());
                    Dish dish = new Dish(tempSoup.get(randomIndex), "", rand.nextInt(20), menuTemp, DishType.getFinder().query().where().eq("type", "Soups").findOne());
                    tempSoup.remove(randomIndex);

                    dish.save();
                }
                for (int i = 0; i < 4; i++) {

                    int randomIndex = rand.nextInt(tempBBQ.size());
                    Dish dish = new Dish(tempBBQ.get(randomIndex), "", rand.nextInt(20), menuTemp, DishType.getFinder().query().where().eq("type", "BBQ Foods").findOne());
                    tempBBQ.remove(randomIndex);

                    dish.save();
                }
            }


            //User seeding

            //admin

            UserData adminData = new UserData("Ridvan", "Appa Bugis", "061641709");
            adminData.setLocation(locDao.getLocationByName("Sarajevo"));

            User admin = new User("ridvan_appa@hotmail.com", "admin", "admin");

            admin.setUser_data(adminData);

            adminData.setUser(admin);

            PasswordSetting(admin);

            admin.save();


        }
    }

    private static void PasswordSetting(User user) {
        String salt = PasswordUtil.getSalt(30);
        String securedPassword = PasswordUtil.generateSecurePassword(user.getPassword(), salt);

        user.setPassword(securedPassword);
        user.setSalt(salt);
    }

}
