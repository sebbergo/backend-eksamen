/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fetcher;

import com.google.gson.Gson;
import dto.CountriesDTO;
import dto.PlanetsDTO;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import utils.HttpUtils;

/**
 *
 * @author sebas
 */
public class CountriesFetcher {
    
    private static String COUNTRIES_URL = "https://api.covid19api.com/countries";

    public static String responseFromExternalServersSequential(ExecutorService threadPool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        Callable<CountriesDTO[]> countriesTask = new Callable<CountriesDTO[]>() {
            @Override
            public CountriesDTO[] call() throws Exception {
                String countries = HttpUtils.fetchData(COUNTRIES_URL);
                CountriesDTO[] countriesDTO = gson.fromJson(countries, CountriesDTO[].class);

                return countriesDTO;
            }
        };
        
        Future<CountriesDTO[]> futureCountries = threadPool.submit(countriesTask);

        CountriesDTO[] countries = futureCountries.get(2, TimeUnit.SECONDS);

        String countriesJson = gson.toJson(countries);
        
        return countriesJson;
    }
}
