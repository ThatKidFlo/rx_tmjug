package ro.tpg.tmjug;

import org.junit.Before;
import org.junit.Test;
import ro.tpg.tmjug.omdb.ApiService;
import ro.tpg.tmjug.omdb.OmdbApi;
import ro.tpg.tmjug.omdb.OmdbMovieDetails;
import ro.tpg.tmjug.omdb.OmdbSearchMovies;
import ro.tpg.tmjug.util.RxLog;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class OmdbApiTest {

    OmdbApi api;

    @Before
    public void setUp() {
        api = ApiService.getApi();
    }


    @Test
    public void searchByTitle() {
        TestSubscriber<OmdbSearchMovies> sub = new TestSubscriber<>();

        api.searchByTitle("star").compose(RxLog::log).subscribe(sub);

        sub.awaitTerminalEvent();
        sub.assertNoErrors();
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertValueCount(1);
    }

    @Test
    public void getByTitle() {
        TestSubscriber<OmdbMovieDetails> sub = new TestSubscriber<>();

        api.getByTitle("Lego Star Wars: The Force Awakens").compose(RxLog::log).subscribe(sub);

        sub.awaitTerminalEvent();
        sub.assertNoErrors();
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertValueCount(1);

        OmdbMovieDetails movie = sub.getOnNextEvents().get(0);
        assertEquals("Lego Star Wars: The Force Awakens", movie.title);
        assertEquals("2016", movie.year);
    }


    @Test
    public void getByTitleNotFound() {
        TestSubscriber<OmdbMovieDetails> sub = new TestSubscriber<>();

        api.getByTitle("Star Wars: Episode XX").compose(RxLog::log).subscribe(sub);

        sub.awaitTerminalEvent();
        sub.assertNoErrors();
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertValueCount(1);
    }

    @Test
    public void searchByTitleNotFound() {
        TestSubscriber<OmdbSearchMovies> sub = new TestSubscriber<>();

        api.searchByTitle("Star Wars: Episode XX").compose(RxLog::log).subscribe(sub);

        sub.awaitTerminalEvent();
        sub.assertNoErrors();
        sub.assertCompleted();
        sub.assertUnsubscribed();
        sub.assertValueCount(1);
        assertFalse(sub.getOnNextEvents().get(0).success);
    }
}
