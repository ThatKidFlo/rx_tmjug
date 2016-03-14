package ro.tpg.tmjug;

import ro.tpg.tmjug.omdb.ApiService;
import ro.tpg.tmjug.omdb.OmdbApi;
import ro.tpg.tmjug.omdb.OmdbSearchMovies;
import ro.tpg.tmjug.util.RxLog;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class MovieService {

    private static MovieService service;

    private OmdbApi omdbApi;

    private MovieService() {
        omdbApi = ApiService.getApi();
    }

    public static MovieService getMovieService() {
        if (service == null) {
            service = new MovieService();
        }

        return service;
    }

    public Observable<OmdbSearchMovies> searchMovie(Observable<String> inputObservable) {
        return inputObservable
                .filter(title -> title.length() > 1)
                .debounce(250, TimeUnit.MILLISECONDS)//avoid too many requests
                .switchMap(this::searchMovie)
                .compose(RxLog::log);
    }

    public Observable<OmdbSearchMovies> searchMovie(String title) {
        return omdbApi.searchByTitle(title)
                .retry(2)
                .onErrorReturn(OmdbSearchMovies::new) //never fail
                .subscribeOn(Schedulers.io());
    }

    public Observable<OmdbSearchMovies> inconsistentSearchMovie(Observable<CharSequence> inputObservable) {
        return inputObservable.map(CharSequence::toString)
                .flatMap(this::searchMovie);
    }
}
