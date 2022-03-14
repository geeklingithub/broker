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
package doodle.rsocket.broker.server.cluster;

import static doodle.rsocket.broker.server.BrokerServerConstants.PREFIX;

import doodle.rsocket.broker.server.cluster.rsocket.RSocketBrokerClusterManager;
import doodle.rsocket.broker.server.config.BrokerServerProperties;
import doodle.rsocket.broker.server.proxy.BrokerProxyServerProperties;
import doodle.rsocket.broker.server.transport.BrokerRSocketServerBootstrap;
import doodle.rsocket.broker.server.transport.BrokerRSocketServerFactory;
import doodle.rsocket.broker.server.transport.BrokerRSocketServerTransportFactory;
import doodle.rsocket.broker.server.transport.BrokerRSocketTransportConfiguration;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.netty.tcp.TcpServer;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({TcpServerTransport.class, TcpServer.class})
@AutoConfigureAfter({
  BrokerRSocketTransportConfiguration.class,
  BrokerCusterServerMessagingConfiguration.class
})
@ConditionalOnBean(BrokerProxyServerProperties.class) // ensure proxy server is starting
@ConditionalOnProperty(prefix = PREFIX + ".cluster", name = "enabled", matchIfMissing = true)
public class BrokerClusterServerConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RSocketBrokerClusterManager rSocketBrokerClusterManager(
      BrokerServerProperties properties) {
    return new RSocketBrokerClusterManager(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerController brokerClusterServerController(
      BrokerServerProperties properties, RSocketBrokerClusterManager clusterManager) {
    return new BrokerClusterServerController(
        properties,
        clusterNode -> clusterManager.getConnectionEventPublisher().tryEmitNext(clusterNode));
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerProperties brokerClusterServerProperties(
      BrokerServerProperties properties) {
    return properties.getCluster();
  }

  @Bean
  public BrokerRSocketServerBootstrap brokerClusterRSocketServerBootstrap(
      BrokerClusterServerProperties properties,
      ObjectProvider<BrokerRSocketServerTransportFactory> transportFactories,
      BrokerClusterServerAcceptor clusterServerAcceptor,
      ApplicationContext applicationContext) {
    BrokerRSocketServerFactory serverFactory =
        transportFactories
            .orderedStream()
            .filter(transportFactory -> transportFactory.supports(properties.getUri()))
            .findFirst()
            .map(transportFactory -> transportFactory.create(properties.getUri()))
            .orElseThrow(
                () -> new IllegalArgumentException("Unknown transport " + properties.getUri()));
    return new BrokerRSocketServerBootstrap(
        serverFactory,
        clusterServerAcceptor,
        (clusterServer) ->
            applicationContext.publishEvent(
                new BrokerClusterServerInitializedEvent(clusterServer)));
  }
}
