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
package doodle.samples.rsocket.broker.server;

import doodle.rsocket.broker.server.EnableBrokerServer;
import doodle.rsocket.broker.server.proxy.BrokerProxyServerInitializedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@EnableBrokerServer
@SpringBootApplication
public class SampleBrokerServerApplication {
  private static final Logger logger = LoggerFactory.getLogger(SampleBrokerServerApplication.class);

  @Bean
  public ApplicationListener<BrokerProxyServerInitializedEvent> proxyListener() {
    return (event) -> logger.info("Proxy bind!");
  }

  @Bean
  public ApplicationListener<BrokerProxyServerInitializedEvent> clusterListener() {
    return (event) -> logger.info("Cluster bind!");
  }

  public static void main(String[] args) {
    // a simple server will throw exception after client disconnected
    // https://github.com/rsocket/rsocket-java/issues/1018
    Hooks.onErrorDropped(__ -> {});
    SpringApplication.run(SampleBrokerServerApplication.class, args);
  }
}
