/*
 * Copyright (c) 2022-present Doodle. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package doodle.rsocket.broker.server.cluster.rsocket;

import static doodle.rsocket.broker.server.BrokerServerConstants.RSOCKET_CLUSTER_SERVER_DEFAULT_URI;

import doodle.rsocket.broker.server.cluster.BrokerClusterServer;
import doodle.rsocket.broker.server.cluster.BrokerClusterServerFactory;
import doodle.rsocket.broker.server.cluster.ConfigurableBrokerClusterServerFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

public class RSocketBrokerClusterServerFactory
    implements BrokerClusterServerFactory, ConfigurableBrokerClusterServerFactory {

  private URI uri = URI.create(RSOCKET_CLUSTER_SERVER_DEFAULT_URI);
  private Duration lifecycleTimeout;

  @Override
  public BrokerClusterServer createServer(SocketAcceptor socketAcceptor) {
    TcpServer tcpServer =
        TcpServer.create().bindAddress(() -> new InetSocketAddress(uri.getHost(), uri.getPort()));
    TcpServerTransport serverTransport = TcpServerTransport.create(tcpServer);
    Mono<CloseableChannel> serverStarter =
        RSocketServer.create().acceptor(socketAcceptor).bind(serverTransport);
    return new RSocketBrokerClusterServer(serverStarter, this.lifecycleTimeout);
  }

  @Override
  public void setUri(URI uri) {
    this.uri = uri;
  }

  @Override
  public void setLifecycleTimeout(Duration lifecycleTimeout) {
    this.lifecycleTimeout = lifecycleTimeout;
  }
}
