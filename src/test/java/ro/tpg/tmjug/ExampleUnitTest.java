package ro.tpg.tmjug;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ExampleUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(ExampleUnitTest.class);

    @Test
    public void testObservable() {
        final StringBuilder result = new StringBuilder();

        Observable.just(1, 2, 3).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                logger.debug("onCompleted");
                result.append('|');
            }

            @Override
            public void onError(Throwable e) {
                logger.debug("onError " + e);
                result.append('X');
            }

            @Override
            public void onNext(Integer integer) {
                logger.debug("onNext " + integer);
                result.append(integer);
            }
        });

        assertEquals("subscriber callbacks", "123|", result.toString());
    }

    @Test
    public void testIntervalObservable() {
        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();

        Observable.interval(500, TimeUnit.MILLISECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long number) {
                        return number % 2 == 0;
                    }
                })
                .take(5)
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(5);
        testSubscriber.assertValues(0L, 2L, 4L, 6L, 8L);
    }


    private class Person {
        int index;

        public Person(int index) {
            this.index = index;
        }
    }

    @Test
    public void testMultiThreading() {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        Observable.range(1, 100)
                .map((index) -> {
                    final Person p = new Person(index);
                    logger.debug("[Map-to-person] :: {} :: {}", index, p);
                    return p;
                })
                .observeOn(Schedulers.newThread())
                .filter(p -> {

                    logger.debug("[Filter] :: {} :: {}", p.index, p);
                    return p.index % 2 == 0;
                })
                .observeOn(Schedulers.newThread())
                .map(p -> {
                    logger.debug("[Map-to-string] :: {} :: {}", p.index, p);
                    return "Person " + p.index;
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(50);
    }
}