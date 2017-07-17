package io.vertx.reactivex;

import com.fasterxml.jackson.core.type.TypeReference;
import io.reactivex.Flowable;
import io.reactivex.FlowableOperator;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import io.vertx.reactivex.core.impl.FlowableReadStream;
import io.vertx.reactivex.core.impl.ReadStreamSubscriber;
import io.vertx.reactivex.core.json.FlowableUnmarshaller;

import java.util.function.Function;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FlowableHelper {

  /**
   * Adapts an RxJava {@link Flowable<T>} to a Vert.x {@link io.vertx.core.streams.ReadStream<T>}. The returned
   * readstream will be subscribed to the {@link Flowable<T>}.<p>
   *
   * @param observable the observable to adapt
   * @return the adapted stream
   */
  public static <T> ReadStream<T> toReadStream(Flowable<T> observable) {
    return ReadStreamSubscriber.asReadStream(observable, Function.identity());
  }

  /**
   * Adapts a Vert.x {@link ReadStream<T>} to an RxJava {@link Flowable <U>}. After
   * the stream is adapted to a flowable, the original stream handlers should not be used anymore
   * as they will be used by the flowable adapter.<p>
   *
   * @param stream the stream to adapt
   * @return the adapted observable
   */
  public static <T, U> Flowable<U> toFlowable(ReadStream<T> stream, Function<T, U> f) {
    return new FlowableReadStream<>(stream, FlowableReadStream.DEFAULT_MAX_BUFFER_SIZE, f);
  }

  /**
   * Adapts a Vert.x {@link ReadStream<T>} to an RxJava {@link Flowable<T>}. After
   * the stream is adapted to a flowable, the original stream handlers should not be used anymore
   * as they will be used by the flowable adapter.<p>
   *
   * @param stream the stream to adapt
   * @return the adapted observable
   */
  public static <T> Flowable<T> toFlowable(ReadStream<T> stream) {
    return new FlowableReadStream<>(stream, FlowableReadStream.DEFAULT_MAX_BUFFER_SIZE, Function.identity());
  }

  /**
   * Adapts a Vert.x {@link ReadStream<T>} to an RxJava {@link Flowable<T>}. After
   * the stream is adapted to a flowable, the original stream handlers should not be used anymore
   * as they will be used by the flowable adapter.<p>
   *
   * @param stream the stream to adapt
   * @return the adapted observable
   */
  public static <T> Flowable<T> toFlowable(ReadStream<T> stream, long maxBufferSize) {
    return new FlowableReadStream<>(stream, maxBufferSize, Function.identity());
  }

  public static <T> FlowableOperator<T, Buffer> unmarshaller(Class<T> mappedType) {
    return new FlowableUnmarshaller<>(java.util.function.Function.identity(), mappedType);
  }

  public static <T> FlowableOperator<T, Buffer> unmarshaller(TypeReference<T> mappedTypeRef) {
    return new FlowableUnmarshaller<>(java.util.function.Function.identity(), mappedTypeRef);
  }
}
