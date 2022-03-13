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

import doodle.rsocket.broker.server.cluster.BrokerClusterNodeProperties;
import doodle.rsocket.broker.server.config.BrokerServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

public class RSocketBrokerClusterManager {
  private static final Logger logger = LoggerFactory.getLogger(RSocketBrokerClusterManager.class);

  private final BrokerServerProperties properties;

  private final Sinks.Many<BrokerClusterNodeProperties> connectionEvents =
      Sinks.many().multicast().directBestEffort(); // emits element with best effort

  public RSocketBrokerClusterManager(BrokerServerProperties properties) {
    this.properties = properties;

    connectionEvents.asFlux().map(this::onConnect).subscribe();
  }

  private Disposable onConnect(BrokerClusterNodeProperties clusterNode) {
    logger.info("Starting connect to broker cluster node: {}", clusterNode);
    // TODO: 3/13/22 create RSocketRequester connection to broker cluster

    // steps:
    //      1: create rsocket requester
    //      2: request remote BROKER-INFO through rsocket request
    //      3: create routing rsocket to broker cluster

    return null;
  }

  public Sinks.Many<BrokerClusterNodeProperties> getConnectionEventPublisher() {
    return this.connectionEvents;
  }
}
