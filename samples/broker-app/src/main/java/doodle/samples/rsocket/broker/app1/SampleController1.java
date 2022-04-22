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
package doodle.samples.rsocket.broker.app1;

import static doodle.rsocket.broker.core.routing.RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE;

import doodle.rsocket.broker.client.config.BrokerClientProperties;
import doodle.rsocket.broker.client.rsocket.BrokerRSocketRequester;
import doodle.rsocket.broker.core.routing.RSocketRoutingAddress;
import doodle.rsocket.broker.core.routing.RSocketRoutingAddressBuilder;
import doodle.samples.rsocket.broker.common.SampleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class SampleController1 {

  private static final Logger logger = LoggerFactory.getLogger(SampleController1.class);

  private final BrokerRSocketRequester rSocketRequester;
  private final RSocketRoutingAddressBuilder addressBuilder;

  @Autowired
  public SampleController1(
      BrokerClientProperties properties, BrokerRSocketRequester rSocketRequester) {
    this.rSocketRequester = rSocketRequester;
    this.addressBuilder = RSocketRoutingAddress.from(properties.getRouteId());
  }

  @MessageMapping("app1.sample")
  public Mono<String> sample() {
    return Mono.just("sample1");
  }

  @Scheduled(fixedDelay = 1000, initialDelay = 1000)
  public void loopForever() {
    rSocketRequester
        .route("app2.sample")
        .metadata(addressBuilder.with("instance-name", "sample2").build(), ROUTING_FRAME_MIME_TYPE)
        .retrieveMono(SampleEvent.class)
        .subscribe(this::logoutMsg);
    //    rSocketRequester
    //        .route("app3.sample")
    //        .metadata(addressBuilder.with("instance-name", "sample3").build(),
    // ROUTING_FRAME_MIME_TYPE)
    //        .retrieveMono(String.class)
    //        .subscribe(this::logoutMsg);
  }

  private void logoutMsg(SampleEvent sampleEvent) {
    logger.info("Received event {}", sampleEvent);
  }

  private void logoutMsg(String s) {
    logger.info("Received message {}", s);
  }
}
