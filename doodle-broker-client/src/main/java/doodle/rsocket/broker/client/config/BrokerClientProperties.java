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

import static doodle.rsocket.broker.client.BrokerClientConstants.*;

import doodle.rsocket.broker.client.BrokerServerTarget;
import doodle.rsocket.broker.core.routing.RSocketRoutingMutableKey;
import doodle.rsocket.broker.core.routing.RSocketRoutingRouteId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PREFIX)
public class BrokerClientProperties {

  private RSocketRoutingRouteId routeId = RSocketRoutingRouteId.random();

  private final Map<RSocketRoutingMutableKey, String> tags = new LinkedHashMap<>();

  private final List<BrokerServerTarget> serverTargets = new ArrayList<>();

  public RSocketRoutingRouteId getRouteId() {
    return routeId;
  }

  public void setRouteId(RSocketRoutingRouteId routeId) {
    this.routeId = routeId;
  }

  public Map<RSocketRoutingMutableKey, String> getTags() {
    return tags;
  }

  public List<BrokerServerTarget> getServerTargets() {
    return serverTargets;
  }
}
