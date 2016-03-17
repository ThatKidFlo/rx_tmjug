package ro.tpg.tmjug.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteToFileOnSubscribe implements Observable.OnSubscribe<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(WriteToFileOnSubscribe.class);

    private String fileName;
    private String data;

    public WriteToFileOnSubscribe(final String fileName, final String data) {
        this.fileName = fileName;
        this.data = data;
    }

    @Override
    public void call(Subscriber<? super Boolean> subscriber) {

        String userHome = System.getProperty("user.home");
        Path pathInUserHome = Paths.get(userHome, "rx_files", fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(pathInUserHome)) {
            createDir(userHome);

            logger.debug("Writing to {} ...", fileName);
            writer.write(data);

            /* simulate long running operation */
            Thread.sleep(10000);

            subscriber.onNext(true);
        } catch (Throwable e) {
            subscriber.onError(e);
        } finally {
            subscriber.onCompleted();
        }
    }

    private void createDir(final String userHome) throws IOException {
        Path rxFilesDir = Paths.get(userHome, "rx_files");
        if (!Files.exists(rxFilesDir)) {
            Files.createDirectory(rxFilesDir);
        }
    }
}
