package com.microsoft.gctoolkit.integration.core;

import com.microsoft.gctoolkit.GCToolKit;
import com.microsoft.gctoolkit.aggregator.Aggregates;
import com.microsoft.gctoolkit.aggregator.Aggregation;
import com.microsoft.gctoolkit.aggregator.Aggregator;
import com.microsoft.gctoolkit.aggregator.Collates;
import com.microsoft.gctoolkit.aggregator.EventSource;
import com.microsoft.gctoolkit.integration.io.TestLogFile;
import com.microsoft.gctoolkit.io.GCLogFile;
import com.microsoft.gctoolkit.io.SingleGCLogFile;
import com.microsoft.gctoolkit.jvm.JavaVirtualMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class PreunifiedJavaVirtualMachineConfigurationTest {

    private String logFile = "preunified/g1gc/details/tenuring/180/g1gc.log";
    private int[] times = { 0, 1028, 945481, 945481};

    @Test
    public void testSingle() {
        TestLogFile log = new TestLogFile(logFile);
        test(new SingleGCLogFile(log.getFile().toPath()), times);
    }

    private void test(GCLogFile log, int[] endStartTimes ) {
        GCToolKit gcToolKit = new GCToolKit();
        gcToolKit.loadAggregationsFromServiceLoader();
        TestTimeAggregation aggregation = new TestTimeAggregation();
        gcToolKit.registerAggregation(aggregation);
        JavaVirtualMachine machine = null;
        try {
            machine = gcToolKit.analyze(log);
            aggregation = machine.getAggregation(aggregation.getClass()).get(); // todo: this isn't being registered
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Assertions.assertEquals( endStartTimes[0], (int)(machine.getEstimatedJVMStartTime().getTimeStamp() * 1000.0d));
        //Assertions.assertEquals( endStartTimes[1], (int)(aggregation..getTimeStamp() * 1000.0d));
        Assertions.assertEquals( endStartTimes[2], (int)(aggregation.estimatedTerminationTime().getTimeStamp() * 1000.0d));
        Assertions.assertEquals( endStartTimes[3], (int)(aggregation.estimatedRuntime() * 1000.0d));
    }

    @Aggregates({EventSource.G1GC,EventSource.GENERATIONAL,EventSource.ZGC,EventSource.SHENANDOAH})
    public class TestTimeAggregator extends Aggregator<TestTimeAggregation> {

        /**
         * Subclass only.
         *
         * @param aggregation The Aggregation that {@literal @}Collates this Aggregator
         * @see Collates
         * @see Aggregation
         */
        public TestTimeAggregator(TestTimeAggregation aggregation) {
            super(aggregation);
        }
    }

    @Collates(TestTimeAggregator.class)
    public class TestTimeAggregation extends Aggregation {

        public TestTimeAggregation() {}

        @Override
        public boolean hasWarning() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

    }
}
