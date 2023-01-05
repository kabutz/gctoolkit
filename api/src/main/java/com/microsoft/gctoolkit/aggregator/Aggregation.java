// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.gctoolkit.aggregator;

import com.microsoft.gctoolkit.jvm.JavaVirtualMachine;
import com.microsoft.gctoolkit.time.DateTimeStamp;

import java.util.Date;

/**
 * An {@code Aggregation} collates data from an {@link Aggregator} and may be thought of as a view
 * of the data. An {@code Aggregation} might collate data into a time series for plotting, or it might
 * summarize the data. Separating the capture of the data, which is the job of the {@code Aggregator}
 * from how the data is aggregated, which is the job of the {@code Aggregation}, allows for multiple
 * views of the data to be built for the same {@code Aggregator}.
 * <p>
 * The {@link Collates} annotation is used to indicate which {@code Aggregator} an {@code Aggregation}
 * works with. If an {@code Aggregation} does not have the {@code Collates} annotation, the {@code Aggregation}
 * will not be used.
 * <p>
 * An implementation of Aggregation must have a public, no-arg constructor.
 * <p>
 * Best practice for creating an {@code Aggregation} is to create an interface for the methods the
 * {@code Aggregator} will call.  When a GC log is analyzed, {@code JVMEvents} are captured by the
 * {@code Aggregators}. An {@code Aggregator} extracts data from the JVMEvent and calls the
 * {@code Aggregation} API to collate the data.
 * <p>
 * As an example, say one wants to record the pause times of full GCs. An {@code Aggregation}
 * could present an API that takes the date/time of the event, the cause of the full GC, and
 * the duration of the full GC. The {@code Aggregator} could capture G1FullGC events and
 * FullGC events. Which event is actually sent to the {@code Aggregator} depends on the
 * what kind of GC log file is being parsed.
 * <p>
 * The example FullGCAggregator is annotated with the {@code @Aggregates} annotation, giving
 * the G1GC and Generational as the event source. This lets GCToolKit know that this Aggregator
 * is capturing events from those sources. Notice also that the constructor of FullGCAggregator
 * registers the JVMEvent types that it is interested in, and gives the method to call for that
 * event type. Lastly, the process method extracts the data from the event and calls the
 * FullGCAggregation API.
 * <p>
 * The implementation of FullGCAggregation can collate the data however desired. MaxFullGCPauseTime is
 * just one example. Notice that the method to get the maximum pause time is defined in MaxFullGCPauseTime,
 * not in FullGCAggregation. This keeps the FullGCAggregation interface from imposing API that some
 * other view (some other Aggregation) of the data might not want or need.
 *
 * <pre><code>
 * {@literal @}Collates(FullGCAggregator.class)
 * public interface FullGCAggregation extends Aggregation {
 *      void recordFullGC(DateTimeStamp timeStamp, GCCause cause, double pauseTime);
 * }
 *
 * {@literal @}Aggregates({EventSource.G1GC, EventSource.Generational})
 * public class FullGCAggregator implements Aggregator{@literal <}FullGCAggregation{@literal >} {
 *
 *     public FullGCAggregator(FullGCAggregation aggregation) {
 *         super(aggregation);
 *         register(G1FullGC.class, this::process);
 *         register(FullGC.class, this::process);
 *     }
 *
 *      private void process(GCEvent event) {
 *          aggregation().recordFullGC(event.getDateTimeStamp(), event.getGCCause(), event.getDuration());
 *      }
 * }
 *
 * public class MaxFullGCPauseTime implements FullGCAggregation {
 *     Map{@literal <}GCCause, Double{@literal >} maxPauseTime = new HashMap();
 *
 *     public MaxFullGCPauseTime() {}
 *
 *     {@literal @}Override
 *     public void recordFullGC(DateTimeStamp timeStamp, GCCause cause, double pauseTime) {
 *         maxPauseTime.compute(cause, (k, v) -{@literal >} (v == null) ? pauseTime : Math.max(v, pauseTime));
 *     }
 *
 *     public double getMaxPauseTime(GCCause cause) {
 *         return maxPauseTime.get(cause);
 *     }
 *
 *     {@literal @}Override
 *     public boolean hasWarning() { return false; }
 *
 *     {@literal @}Override
 *     public boolean isEmpty() { return maxPauseTime.isEmpty(); }
 * }
 * </code></pre>
 *
 * @see JavaVirtualMachine#getAggregation(Class)
 * @see Collates
 */
public abstract class Aggregation {

    private DateTimeStamp estimatedStartTime = null;
    private DateTimeStamp estimatedTerminationTime = new DateTimeStamp(0.0d);
    private double estimatedRuntime = -1.0d;

    protected Aggregation() {}

    /**
     * Interface to record the time span of the log
     * Estimate based on information carried in the JVMTermination event.
     * @param startTime - estimate start time of the log.
     */

    public void estimatedStartTime(DateTimeStamp startTime) {
        this.estimatedStartTime = startTime;
    }
    public DateTimeStamp estimatedStartTime() {
        return this.estimatedStartTime;
    }
    public void estimatedRuntime(double estimatedUpTime) {
        this.estimatedRuntime = estimatedUpTime;
    }
    public double estimatedRuntime() {
        return estimatedRuntime;
    }

    public void estimatedTerminationTime(DateTimeStamp terminationTime) {
        this.estimatedTerminationTime = terminationTime;

    }
    public DateTimeStamp estimatedTerminationTime() {
        return estimatedTerminationTime;
    }

    /**
     * Return true if the Aggregation contains a warning. For example, an Aggregation that
     * looks at GC Cause might return {@code true} if it finds a System.gc() call.
     * @return {@code true} if the Aggregation contains a warning.
     */
    abstract public boolean hasWarning();

    /**
     * Return {@code true} if there is no data in the Aggregation.
     * @return {@ncode true} if there is no data in the Aggregation.
     */
    abstract public boolean isEmpty();

    /**
     * Sort if a given Aggregator collates for this aggregation.
      * @return aggregator
     */
    public Class<? extends Aggregator<?>> collates() {
        return collates(getClass());
    }

    private Class<? extends Aggregator<?>> collates(Class<?> clazz) {
        Class<? extends Aggregator<?>> target;
        if (clazz != null && clazz != Aggregation.class) {

            if (clazz.isAnnotationPresent(Collates.class)) {
                Collates collates = clazz.getAnnotation(Collates.class);
                return collates.value();
            }

            Class<?> superClass = clazz.getSuperclass();
            target = collates(superClass);
            if ( target != null)
                return target;

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> iface : interfaces) {
                target = collates(iface);
                if (target != null)
                    return target;
            }
        }
        return null;
    }
}
