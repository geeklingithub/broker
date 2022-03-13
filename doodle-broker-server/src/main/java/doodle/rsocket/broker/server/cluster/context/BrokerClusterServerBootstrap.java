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
package doodle.rsocket.broker.server.cluster.context;

import doodle.rsocket.broker.server.cluster.BrokerClusterServer;
import doodle.rsocket.broker.server.cluster.BrokerClusterServerFactory;
import io.rsocket.SocketAcceptor;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

public class BrokerClusterServerBootstrap implements SmartLifecycle, ApplicationContextAware {

  private final BrokerClusterServer clusterServer;
  private ApplicationContext applicationContext;

  public BrokerClusterServerBootstrap(
      BrokerClusterServerFactory serverFactory, SocketAcceptor socketAcceptor) {
    this.clusterServer = Objects.requireNonNull(serverFactory.createServer(socketAcceptor));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    this.clusterServer.start();
    this.applicationContext.publishEvent(
        new BrokerClusterServerInitializedEvent(this.clusterServer));
  }

  @Override
  public void stop() {
    this.clusterServer.stop();
  }

  @Override
  public boolean isRunning() {
    return this.clusterServer.isRunning();
  }
}
