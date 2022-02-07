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
package doodle.rsocket.broker.server.config;

import doodle.rsocket.broker.server.BrokerServerConstants;
import doodle.rsocket.broker.server.proxy.BrokerProxyServerProperties;
import doodle.rsocket.broker.server.routing.BrokerServerRoutingProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static doodle.rsocket.broker.server.BrokerServerConstants.PREFIX;

@ConfigurationProperties(PREFIX)
public class BrokerServerProperties {

  private final BrokerProxyServerProperties proxy = new BrokerProxyServerProperties();

  private final BrokerServerRoutingProperties routing = new BrokerServerRoutingProperties();

  public BrokerProxyServerProperties getProxy() {
    return proxy;
  }

  public BrokerServerRoutingProperties getRouting() {
    return routing;
  }
}