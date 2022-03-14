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
package doodle.rsocket.broker.server.proxy;

import java.net.InetAddress;
import java.net.URI;

public class BrokerProxyServerProperties {

  private URI uri = URI.create("tcp://localhost:8001");

  private InetAddress host;
  private int port;

  public URI getUri() {
    return uri;
  }

  public BrokerProxyServerProperties setUri(URI uri) {
    this.uri = uri;
    return this;
  }

  public InetAddress getHost() {
    return host;
  }

  public void setHost(InetAddress host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
