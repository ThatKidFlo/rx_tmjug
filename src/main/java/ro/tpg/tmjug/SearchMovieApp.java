package ro.tpg.tmjug;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.tpg.tmjug.file.RxFileUtil;
import ro.tpg.tmjug.omdb.ApiService;
import ro.tpg.tmjug.omdb.OmdbMovie;
import ro.tpg.tmjug.omdb.OmdbMovieDetails;
import rx.Observable;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.List;

public class SearchMovieApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(SearchMovieApp.class);

    private TextField movieTitleInputField;
    private ListView<OmdbMovie> moviesListView;
    private MovieDetailsView movieDetailsView;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final MovieService movieService = MovieService.getMovieService();

    public static void main(String[] args) {

        rxFileWriterDemo();
        launch(args);
    }

    private static final int MAX_FILE_WRITE_CONCURRENT = 10;
    private static void rxFileWriterDemo() {
        Observable.range(1, 1000)
                .map(index -> "f" + index + ".txt")
                .flatMap(fileName ->
                        RxFileUtil.writeToFile(fileName, fileName + " :: content").subscribeOn(Schedulers.io()),
                        MAX_FILE_WRITE_CONCURRENT
                )
                .subscribe(
                        result -> logger.debug("Write result: {}", result),
                        error -> logger.error("Write error: {}", error.getMessage()),
                        () -> logger.debug("All files successfully written!")
                );
    }

    @Override
    public void start(Stage primaryStage) {

        setUp(primaryStage);

        final Subscription hotSearchSubscription = JavaFxObservable.fromObservableValue(movieTitleInputField.textProperty())
                .compose(movieService::searchMovie)
                .observeOn(JavaFxScheduler.getInstance())
                .subscribe(searchResult -> {
                    updateMoviesList(searchResult.movies);
                });
        subscriptions.add(hotSearchSubscription);

        // Subscribe to item selection
        final Subscription movieDetailsSubscription = JavaFxObservable.fromObservableValue(moviesListView.getSelectionModel().selectedItemProperty())
                .filter(selection -> selection != null)
                .map(selection -> selection.title)
                .switchMap(title -> ApiService.getApi().getByTitle(title).subscribeOn(Schedulers.io()))
                .observeOn(JavaFxScheduler.getInstance())
                .subscribe(
                        movieDetails -> movieDetailsView.updateDetails(movieDetails),
                        throwable -> logger.error(throwable.getMessage())
                );
        subscriptions.add(movieDetailsSubscription);
    }

    private void updateMoviesList(final List<OmdbMovie> movies) {
        moviesListView.getItems().clear();
        if (movies != null) {
            moviesListView.getItems().addAll(movies);
        }
    }

    @Override
    public void stop() throws Exception {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    private void setUp(Stage primaryStage) {
        primaryStage.setTitle("Hot Search");

        movieTitleInputField = new TextField();

        moviesListView = new ListView<>();
        moviesListView.setMaxWidth(300);
        moviesListView.setMinWidth(200);
        ObservableList<OmdbMovie> items = FXCollections.observableArrayList();
        moviesListView.setItems(items);

        movieDetailsView = new MovieDetailsView();
        movieDetailsView.setMinWidth(350);

        VBox searchLayout = new VBox();
        searchLayout.getChildren().addAll(movieTitleInputField, moviesListView);
        searchLayout.setSpacing(10);

        HBox rootLayout = new HBox();
        rootLayout.getChildren().addAll(searchLayout, movieDetailsView);
        rootLayout.setSpacing(10);

        StackPane root = new StackPane();
        root.getChildren().add(rootLayout);
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.show();
    }
}

