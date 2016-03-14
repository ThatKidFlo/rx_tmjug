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
import ro.tpg.tmjug.omdb.OmdbMovie;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;
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
        launch(args);
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
                // 1. Get a movie by title using the ApiService -> you should get an OmdbMovieDetails object
                .subscribe(movieDetails -> {

                    logger.debug("Selected: {}", movieDetails);

                    // 2. Display the movie details using the MovieDetailsView stage
                    // movieDetailsView.updateDetails(movieDetails);
                });
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

