// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.gctoolkit.vertx;

import com.microsoft.gctoolkit.message.Channels;
import com.microsoft.gctoolkit.message.DataSourceChannel;
import com.microsoft.gctoolkit.message.DataSourceParser;
import io.vertx.core.Future;

public class VertxDataSourceChannel extends VertxChannel implements DataSourceChannel {

    public VertxDataSourceChannel() {
        super();
    }

    @Override
    public void registerListener(DataSourceParser listener) {
        final DataSourceVerticle processor = new DataSourceVerticle(vertx(), listener.channel().getName(), listener);
        vertx().deployVerticle(processor, state -> processor.setID((state.succeeded()) ? state.result() : ""));
    }

    @Override
    public void publish(Channels channel, String message) {
        vertx().eventBus().publish(channel.getName(),message);
    }

    @Override
    public void close() {
//        try {
//            vertx().close();
//        } catch(IllegalStateException ise) {
//            //Yes, I am intentionally ignoring this exception because I can.
//        }
    }
}
