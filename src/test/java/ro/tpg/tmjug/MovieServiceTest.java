package ro.tpg.tmjug;

import org.junit.Before;
import org.junit.Test;
import ro.tpg.tmjug.omdb.OmdbSearchMovies;
import ro.tpg.tmjug.util.RxLog;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class MovieServiceTest {

    MovieService movieService;

    @Before
    public void setUp() {
        movieService = MovieService.getMovieService();
    }

    @Test
    public void last_title_should_match_search() throws InterruptedException {

        CharSequence input = "empire at";

        Observable<CharSequence> obs = Observable.interval(0, TimeUnit.MILLISECONDS)
                .map(Long::intValue)
                .take(input.length())
                .map(end -> input.subSequence(0, end + 1));

        TestSubscriber<String> subscriber = new TestSubscriber<>();

        obs.compose(movieService::inconsistentSearchMovie)
                .map(this::extractFirstTitle)
                .compose(RxLog::log)
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        assertTrue(subscriber.getOnNextEvents().get(0).toLowerCase().contains("empire at"));
    }

    String extractFirstTitle(OmdbSearchMovies searchMovies) {
        if (searchMovies.movies == null || searchMovies.movies.isEmpty()) {
            return null;
        } else {
            return searchMovies.movies.get(0).title;
        }
    }
}
