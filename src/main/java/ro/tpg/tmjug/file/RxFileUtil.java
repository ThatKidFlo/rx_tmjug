package ro.tpg.tmjug.file;

import com.sun.istack.internal.NotNull;
import rx.Observable;

public class RxFileUtil {

    @NotNull
    public static Observable<Boolean> writeToFile(@NotNull final String fileName, @NotNull final String data) {

        return Observable.create(new WriteToFileOnSubscribe(fileName, data));
    }
}
