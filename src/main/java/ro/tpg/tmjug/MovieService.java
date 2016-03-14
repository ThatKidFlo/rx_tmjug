package ro.tpg.tmjug;

import ro.tpg.tmjug.omdb.ApiService;
import ro.tpg.tmjug.omdb.OmdbApi;
import ro.tpg.tmjug.omdb.OmdbSearchMovies;
import rx.Observable;
import rx.schedulers.Schedulers;

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

    public Observable<OmdbSearchMovies> searchMovie(String title) {
        return omdbApi.searchByTitle(title)
                .subscribeOn(Schedulers.io());
    }

    public Observable<OmdbSearchMovies> inconsistentSearchMovie(Observable<CharSequence> inputObservable) {
        return inputObservable.map(CharSequence::toString)
                .flatMap(this::searchMovie);
    }
}
