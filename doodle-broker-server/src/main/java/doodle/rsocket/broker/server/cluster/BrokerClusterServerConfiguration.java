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

import doodle.rsocket.broker.server.cluster.context.BrokerClusterServerBootstrap;
import doodle.rsocket.broker.server.cluster.rsocket.RSocketBrokerClusterServerFactory;
import doodle.rsocket.broker.server.cluster.rsocket.RSocketBrokerClusterServerFactoryCustomizer;
import doodle.rsocket.broker.server.config.BrokerServerProperties;
import doodle.rsocket.broker.server.routing.BrokerServerRoutingAcceptor;
import doodle.rsocket.broker.server.routing.BrokerServerRoutingConfiguration;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import reactor.netty.tcp.TcpServer;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({TcpServerTransport.class, TcpServer.class})
@AutoConfigureAfter(BrokerServerRoutingConfiguration.class)
@ConditionalOnProperty(prefix = PREFIX + ".cluster", name = "port")
public class BrokerClusterServerConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerController brokerClusterServerController() {
    return new BrokerClusterServerController();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerProperties brokerClusterServerProperties(
      BrokerServerProperties properties) {
    return properties.getCluster();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerFactory brokerClusterServerFactory(
      BrokerClusterServerProperties properties,
      ObjectProvider<RSocketBrokerClusterServerFactoryCustomizer> customizers) {
    RSocketBrokerClusterServerFactory serverFactory = new RSocketBrokerClusterServerFactory();
    PropertyMapper mapper = PropertyMapper.get();
    mapper.from(properties::getHost).to(serverFactory::setHost);
    mapper.from(properties::getPort).to(serverFactory::setPort);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(serverFactory));
    return serverFactory;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClusterServerBootstrap brokerClusterServerBootstrap(
      BrokerClusterServerFactory serverFactory, BrokerServerRoutingAcceptor routingAcceptor) {
    return new BrokerClusterServerBootstrap(serverFactory, routingAcceptor);
  }
}
