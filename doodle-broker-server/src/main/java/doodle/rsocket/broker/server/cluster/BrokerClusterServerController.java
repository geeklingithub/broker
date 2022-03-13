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

import doodle.rsocket.broker.core.routing.RSocketRoutingBrokerInfo;
import doodle.rsocket.broker.core.routing.RSocketRoutingFrame;
import doodle.rsocket.broker.server.config.BrokerServerProperties;
import java.time.Duration;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Controller
public class BrokerClusterServerController {
  private static final Logger logger = LoggerFactory.getLogger(BrokerClusterServerController.class);
  private final BrokerServerProperties properties;

  private final Consumer<BrokerClusterNodeProperties>
      connectionEventPublisher; // for updating broker info

  private final Sinks.Many<RSocketRoutingBrokerInfo> connectEvents =
      Sinks.many().multicast().directBestEffort(); // emits element with best effort

  public BrokerClusterServerController(
      BrokerServerProperties properties,
      Consumer<BrokerClusterNodeProperties> connectionEventPublisher) {
    this.properties = properties;
    this.connectionEventPublisher = connectionEventPublisher;

    connectEvents
        .asFlux()
        .delayElements(Duration.ofSeconds(1))
        .flatMap(this::onConnectEvent)
        .subscribe();
  }

  private Mono<RSocketRoutingBrokerInfo> onConnectEvent(RSocketRoutingBrokerInfo brokerInfo) {
    // TODO: 3/13/22 handle connect event
    return Mono.empty();
  }

  @ConnectMapping
  public Mono<Void> onConnect(RSocketRoutingFrame routingFrame, RSocketRequester rSocketRequester) {
    if (!(routingFrame instanceof RSocketRoutingBrokerInfo)) {
      return Mono.empty();
    }
    RSocketRoutingBrokerInfo brokerInfo = (RSocketRoutingBrokerInfo) routingFrame;

    // TODO: 3/13/22 handle connection

    connectEvents.tryEmitNext(brokerInfo); // emits event when connection established
    return Mono.empty();
  }
}
