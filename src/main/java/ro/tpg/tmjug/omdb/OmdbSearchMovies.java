package ro.tpg.tmjug.omdb;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * {
 *      Search: [
 *          {...},
 *          {...}
 *      ]
 * }
 */
public class OmdbSearchMovies extends OmdbResponse {

    public OmdbSearchMovies(Throwable throwable) {
        errorMessage = throwable.toString();
        success = false;
    }

    @SerializedName("Search")
    public List<OmdbMovie> movies = Collections.emptyList();

    @Override
    public String toString() {
        return "OmdbSearchMovies{" +
                "movies=" + movies +
                "} " + super.toString();
    }
}
