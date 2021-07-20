// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.censum.event.g1gc;

import com.microsoft.censum.event.GCCause;
import com.microsoft.censum.event.GarbageCollectionTypes;
import com.microsoft.censum.time.DateTimeStamp;

/**
 *
 */

public class ConcurrentCompleteCleanup extends G1GCConcurrentEvent {

    public ConcurrentCompleteCleanup(DateTimeStamp timeStamp, double duration) {
        super(timeStamp, GarbageCollectionTypes.ConcurrentCompleteCleanup, GCCause.UNKNOWN_GCCAUSE, duration);
    }

    public ConcurrentCompleteCleanup(DateTimeStamp timeStamp, GCCause cause, double duration) {
        super(timeStamp, GarbageCollectionTypes.ConcurrentCompleteCleanup, cause, duration);
    }

}
