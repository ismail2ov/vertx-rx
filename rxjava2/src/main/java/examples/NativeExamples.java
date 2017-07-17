package examples;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.reactivex.FlowableHelper;
import io.vertx.reactivex.ObservableHelper;
import io.vertx.reactivex.RxHelper;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class NativeExamples {

  public void toObservable(Vertx vertx) {
    FileSystem fileSystem = vertx.fileSystem();
    fileSystem.open("/data.txt", new OpenOptions(), result -> {
      AsyncFile file = result.result();
      Flowable<Buffer> observable = FlowableHelper.toFlowable(file);
      observable.forEach(data -> System.out.println("Read data: " + data.toString("UTF-8")));
    });
  }

  private Flowable<Buffer> getFlowable() {
    throw new UnsupportedOperationException();
  }

  public void toReadStream(HttpServerResponse response) {
    Flowable<Buffer> observable = getFlowable();
    ReadStream<Buffer> readStream = FlowableHelper.toReadStream(observable);
    Pump pump = Pump.pump(readStream, response);
    pump.start();
  }

/*
  public void observableHandler(Vertx vertx) {
    ObservableHandler<Long> observable = RxHelper.observableHandler();
    observable.subscribe(id -> {
      // Fired
    });
    vertx.setTimer(1000, observable.toHandler());
  }
*/

/*
  public void handlerToSubscriber(Observable<String> observable,
                                  Single<String> single,
                                  Handler<AsyncResult<String>> handler1,
                                  Handler<AsyncResult<String>> handler2) {
    //
    // Subscribe to an Observable
    observable.subscribe(RxHelper.toSubscriber(handler1));

    // Subscribe to a Single
    single.subscribe(RxHelper.toSubscriber(handler2));
  }
*/

/*
  public void observableFuture(Vertx vertx) {
    ObservableFuture<HttpServer> observable = RxHelper.observableFuture();
    observable.subscribe(
        server -> {
          // Server is listening
        },
        failure -> {
          // Server could not start
        }
    );
    vertx.createHttpServer(new HttpServerOptions().
        setPort(1234).
        setHost("localhost")
    ).listen(observable.toHandler());
  }
*/

/*
  public void observableToHandler() {
    Observer<HttpServer> observer = new Observer<HttpServer>() {
      @Override
      public void onNext(HttpServer o) {
      }
      @Override
      public void onError(Throwable e) {
      }
      @Override
      public void onComplete() {
      }
    };
    Handler<AsyncResult<HttpServer>> handler = RxHelper.toFuture(observer);
  }
*/

/*
  public void actionsToHandler() {
    Action1<HttpServer> onNext = httpServer -> {};
    Action1<Throwable> onError = httpServer -> {};
    Action0 onComplete = () -> {};

    Handler<AsyncResult<HttpServer>> handler1 = RxHelper.toFuture(onNext);
    Handler<AsyncResult<HttpServer>> handler2 = RxHelper.toFuture(onNext, onError);
    Handler<AsyncResult<HttpServer>> handler3 = RxHelper.toFuture(onNext, onError, onComplete);
  }
*/

  public void scheduler(Vertx vertx) {
    Scheduler scheduler = RxHelper.scheduler(vertx);
    Observable<Long> timer = Observable.interval(100, 100, TimeUnit.MILLISECONDS, scheduler);
  }

  public void scheduler(WorkerExecutor workerExecutor) {
    Scheduler scheduler = RxHelper.blockingScheduler(workerExecutor);
    Observable<Long> timer = Observable.interval(100, 100, TimeUnit.MILLISECONDS, scheduler);
  }

  public void schedulerHook(Vertx vertx) {
    RxJavaPlugins.setComputationSchedulerHandler(s -> RxHelper.scheduler(vertx));
    RxJavaPlugins.setIoSchedulerHandler(s -> RxHelper.blockingScheduler(vertx));
    RxJavaPlugins.setNewThreadSchedulerHandler(s -> RxHelper.scheduler(vertx));
  }

  private class MyPojo {
  }

  public void unmarshaller(FileSystem fileSystem) {
    fileSystem.open("/data.txt", new OpenOptions(), result -> {
      AsyncFile file = result.result();
      Observable<Buffer> observable = ObservableHelper.toObservable(file);
      observable.lift(ObservableHelper.unmarshaller(MyPojo.class)).subscribe(
          mypojo -> {
            // Process the object
          }
      );
    });
  }
}
