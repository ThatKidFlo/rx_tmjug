package ro.tpg.tmjug;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ro.tpg.tmjug.omdb.OmdbMovieDetails;

public class MovieDetailsView extends ScrollPane {

    private final ImageView posterImageView;
    private final Label imdbRating;
    private final Label director;
    private final Label actors;
    private final Label plot;

    public MovieDetailsView() {

        posterImageView = new ImageView();

        imdbRating = new Label("Rating:\nN/A");
        director = new Label("Director:\nN/A");

        actors = new Label("Actors:\nN/A");
        actors.setWrapText(true);
        actors.setMaxWidth(400);

        plot = new Label("Plot:\nN/A");
        plot.setWrapText(true);
        plot.setMaxWidth(400);

        VBox detailedDescriptionsLayout = new VBox();
        detailedDescriptionsLayout.getChildren().addAll(imdbRating, director, actors, plot);
        detailedDescriptionsLayout.setSpacing(10);

        HBox rootLayout = new HBox();
        rootLayout.getChildren().addAll(posterImageView, detailedDescriptionsLayout);
        rootLayout.setSpacing(10);

        setContent(rootLayout);
    }

    public void updateDetails(final OmdbMovieDetails movie) {
        try {
            final Image posterImage = new Image(movie.posterUri.toString());
            posterImageView.setImage(posterImage);
        } catch (Throwable t) { }

        imdbRating.setText("Rating:\n" + movie.imdbRating);
        director.setText("Director:\n" + movie.director);
        actors.setText("Actors:\n" + movie.actors);

        plot.setText("Plot:\n" + movie.plot);
    }
}
