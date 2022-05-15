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
package doodle.samples.rsocket.broker.app2;

import static doodle.rsocket.broker.core.routing.RSocketRoutingMimeTypes.ROUTING_FRAME_MIME_TYPE;

import doodle.rsocket.broker.client.config.BrokerClientProperties;
import doodle.rsocket.broker.client.rsocket.BrokerRSocketRequester;
import doodle.rsocket.broker.core.routing.RSocketRoutingAddress;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@MessageMapping("sample")
@Controller
public class SampleController2 {

  private final BrokerRSocketRequester rSocketRequester;

  private final RSocketRoutingAddress unicastAddress;

  @Autowired
  public SampleController2(
      BrokerClientProperties properties, BrokerRSocketRequester rSocketRequester) {
    this.rSocketRequester = Objects.requireNonNull(rSocketRequester);

    this.unicastAddress =
        RSocketRoutingAddress.from(properties.getRouteId(), properties.getBroker().getBrokerId())
            .with("instance-name", "sample3")
            .build();
  }

  @MessageMapping("receive")
  public void onReceive(String msg) {
    System.out.println(msg);
  }

  @Scheduled(initialDelay = 5000, fixedDelay = 2000)
  public void unicastTask() {
    rSocketRequester
        .route("sample.receive")
        .metadata(unicastAddress, ROUTING_FRAME_MIME_TYPE)
        .data("hi-from-sample2")
        .send()
        .subscribe();
  }
}
