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
package doodle.rsocket.broker.server.transport.netty;

import doodle.rsocket.broker.core.transport.BrokerRSocketTransport;
import doodle.rsocket.broker.server.transport.BrokerRSocketServer;
import doodle.rsocket.broker.server.transport.BrokerRSocketServerFactory;
import doodle.rsocket.broker.server.transport.ConfigurableBrokerRSocketServerFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpServer;

public class NettyBrokerRSocketServerFactory
    implements BrokerRSocketServerFactory, ConfigurableBrokerRSocketServerFactory {

  @SuppressWarnings("unused")
  private BrokerRSocketTransport transport = BrokerRSocketTransport.TCP;

  private URI uri;
  private Duration lifecycleTimeout;

  @Override
  public BrokerRSocketServer createServer(SocketAcceptor socketAcceptor) {
    // TODO: 2022/3/14 websocket support
    TcpServer tcpServer =
        TcpServer.create().bindAddress(() -> new InetSocketAddress(uri.getHost(), uri.getPort()));
    TcpServerTransport serverTransport = TcpServerTransport.create(tcpServer);
    Mono<CloseableChannel> serverStarter =
        RSocketServer.create().acceptor(socketAcceptor).bind(serverTransport);
    return new NettyBrokerRSocketServer(serverStarter, lifecycleTimeout);
  }

  @Override
  public void setTransport(BrokerRSocketTransport transport) {
    this.transport = transport;
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
