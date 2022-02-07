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
package doodle.rsocket.broker.client.config;

import static doodle.rsocket.broker.core.routing.RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE;

import doodle.rsocket.broker.client.context.BrokerClientBootstrap;
import doodle.rsocket.broker.client.rsocket.BrokerClientRSocketRequesterBuilderCustomizer;
import doodle.rsocket.broker.client.rsocket.BrokerClientRSocketRequesterWrapBuilder;
import doodle.rsocket.broker.client.rsocket.BrokerClientRSocketRequesterWrapBuilderCustomizer;
import doodle.rsocket.broker.client.rsocket.loadbalance.BrokerClientLoadbalanceStrategyProvider;
import doodle.rsocket.broker.client.rsocket.loadbalance.BrokerClientLoadbalancedRSocket;
import doodle.rsocket.broker.client.rsocket.loadbalance.BrokerClientRSocketPool;
import doodle.rsocket.broker.core.routing.RSocketRoutingRouteSetup;
import doodle.rsocket.broker.core.routing.RSocketRoutingRouteSetupBuilder;
import doodle.rsocket.broker.core.routing.config.BrokerRSocketStrategiesAutoConfiguration;
import io.rsocket.RSocket;
import io.rsocket.loadbalance.LoadbalanceStrategy;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import io.rsocket.metadata.WellKnownMimeType;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnBean(BrokerClientMarkerConfiguration.Marker.class)
@ConditionalOnClass({RSocketRequester.class, RSocket.class, RSocketStrategies.class})
@EnableConfigurationProperties(BrokerClientProperties.class)
@AutoConfigureAfter({
  RSocketRequesterAutoConfiguration.class,
  BrokerRSocketStrategiesAutoConfiguration.class
})
public class BrokerClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientLoadbalanceStrategyProvider
      brokerClientRoundRobinLoadbalanceStrategyProvider() {
    return RoundRobinLoadbalanceStrategy::new;
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketPool brokerClientRSocketPool(
      ObjectProvider<BrokerClientLoadbalanceStrategyProvider> providers) {
    BrokerClientLoadbalanceStrategyProvider
        provider = // using Round-Robin as default loadbalance strategy
        providers.getIfAvailable(this::brokerClientRoundRobinLoadbalanceStrategyProvider);
    LoadbalanceStrategy loadbalanceStrategy = provider.get();
    return BrokerClientRSocketPool.create(loadbalanceStrategy);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientLoadbalancedRSocket brokerClientLoadbalancedRSocket(
      BrokerClientRSocketPool rSocketPool) {
    return BrokerClientLoadbalancedRSocket.create(rSocketPool);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientBootstrap brokerClientBootstrap(
      RSocketRequester.Builder builder, // inject from RSocketRequesterAutoConfiguration
      BrokerClientRSocketPool rSocketPool,
      BrokerClientProperties properties,
      RSocketStrategies rSocketStrategies,
      ObjectProvider<BrokerClientRSocketRequesterBuilderCustomizer> customizers) {
    RSocketRoutingRouteSetupBuilder routeSetupBuilder =
        RSocketRoutingRouteSetup.from(properties.getRouteId());
    properties
        .getTags()
        .forEach(
            (key, value) -> {
              if (Objects.nonNull(key.getWellKnownKey())) {
                routeSetupBuilder.with(key.getWellKnownKey(), value);
              } else if (Objects.nonNull(key.getKey())) {
                routeSetupBuilder.with(key.getKey(), value);
              }
            });
    builder
        .setupMetadata(routeSetupBuilder.build(), ROUTING_FRAME_MIME_TYPE)
        .rsocketStrategies(rSocketStrategies);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return new BrokerClientBootstrap(builder, rSocketPool, properties.getServerTargets());
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterWrapBuilder brokerClientRSocketRequesterBuilder(
      BrokerClientLoadbalancedRSocket loadbalancedRSocket,
      RSocketStrategies strategies, //  inject from RSocketStrategiesAutoConfiguration
      ObjectProvider<BrokerClientRSocketRequesterWrapBuilderCustomizer> customizers) {
    BrokerClientRSocketRequesterWrapBuilder builder =
        BrokerClientRSocketRequesterWrapBuilder.newBuilder(loadbalancedRSocket)
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON) // "application/json"
            .metadataMimeType( // "message/x.rsocket.composite-metadata.v0"
                MimeType.valueOf(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString()))
            .rsocketStrategies(strategies);
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder;
  }
}
