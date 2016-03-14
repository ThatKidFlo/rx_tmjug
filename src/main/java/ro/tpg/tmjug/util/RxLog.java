package ro.tpg.tmjug.util;

import org.slf4j.LoggerFactory;
import rx.Observable;

import java.text.DateFormat;
import java.util.Date;

/**
 * Log Rx events, including time and thread name.
 */
public class RxLog {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RxLog.class);

    public static void log(Object source, String item) {
        logger.debug(DateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
                + "\t" + source
                + "\t" + Thread.currentThread().getName()
                + "\t" + item);
    }

    public static void logNext(Object source, Object item) {
        logger.debug(DateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
                + "\t" + source
                + "\t" + Thread.currentThread().getName()
                + "\t" + "onNext: " + item);
    }

    public static void logError(Object source, Throwable throwable) {
        logger.debug(DateFormat.getTimeInstance().format(new Date(System.currentTimeMillis()))
                + "\t" + source
                + "\t" + Thread.currentThread().getName()
                + "\t" + "onError: " + throwable);
    }

    public static <T> Observable<T> log(final Observable<T> observable) {
        return log(observable, observable.toString());
    }

    public static <T> Observable<T> log(final Observable<T> observable, final String name) {
        return observable
                .doOnSubscribe(() -> log(name, "subscribe"))
                //.doOnRequest(n -> log(name, "request " + n))
                .doOnNext(item -> logNext(name, item))
                .doOnError(throwable -> logError(name, throwable))
                .doOnCompleted(() -> log(name, "completed"))
                .doOnUnsubscribe(() -> log(name, "unsubscribe"));

    }
}