package fetcher;

import com.google.gson.Gson;
import dto.CombinedDTO;
import dto.JokeDTO;
import dto.PeopleDTO;
import dto.PlanetDTO;
import dto.SpeciesDTO;
import dto.StarshipDTO;
import dto.VehicleDTO;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import utils.HttpUtils;

public class JokeFetcher {
    private static final String JOKE_URL = "https://icanhazdadjoke.com/";

    public static String responseFromExternalServersParrallel(ExecutorService threadPool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException {

        Callable<JokeDTO> jokeTask = new Callable<JokeDTO>() {
            @Override
            public JokeDTO call() throws Exception {
                String joke = HttpUtils.fetchData(JOKE_URL);
                JokeDTO jokeDTO = gson.fromJson(joke, JokeDTO.class);

                return jokeDTO;
            }
        };

        Future<JokeDTO> futureJoke = threadPool.submit(jokeTask);

        JokeDTO joke = futureJoke.get(2, TimeUnit.SECONDS);
        
        String jokeJSON = gson.toJson(joke);
        
        return jokeJSON;
    }

}
