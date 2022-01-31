package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.Objects;
import java.time.Duration;
import java.time.Instant;
import java.lang.reflect.InvocationTargetException;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private Object t;
  private final ProfilingState state;


  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object t,ProfilingState state) {
    this.t = Objects.requireNonNull(t);
    this.state = Objects.requireNonNull(state);
    this.clock = Objects.requireNonNull(clock);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    Object result = null;
    Instant start = null;
    boolean record = false;
    if (method.getAnnotation(Profiled.class) != null){
      start = clock.instant();
      record = true;
    }
    try {
      result = method.invoke(t,args);
    }  catch (InvocationTargetException e) {
      throw e.getTargetException();
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } finally {
      if (record){
        Duration duration = Duration.between(start, clock.instant());
        state.record(t.getClass(), method, duration);
      }
    }
    return result;
  }
}
