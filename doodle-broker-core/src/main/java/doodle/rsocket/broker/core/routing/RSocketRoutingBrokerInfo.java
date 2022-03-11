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
package doodle.rsocket.broker.core.routing;

import static doodle.rsocket.broker.core.routing.RSocketRoutingFrameType.BROKER_INFO;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingBrokerInfoCodec.brokerId;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingBrokerInfoCodec.tags;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingBrokerInfoCodec.timestamp;

import io.netty.buffer.ByteBuf;
import java.util.Objects;

public class RSocketRoutingBrokerInfo extends RSocketRoutingFrame {
  private final RSocketRoutingRouteId brokerId;
  private final long timestamp;
  private RSocketRoutingTags tags;

  public static RSocketRoutingBrokerInfoBuilder builder() {
    return new RSocketRoutingBrokerInfoBuilder();
  }

  public static RSocketRoutingBrokerInfo from(ByteBuf byteBuf) {
    return builder()
        .brokerId(brokerId(byteBuf))
        .timestamp(timestamp(byteBuf))
        .with(tags(byteBuf))
        .build();
  }

  public RSocketRoutingBrokerInfo(
      RSocketRoutingRouteId brokerId, long timestamp, RSocketRoutingTags tags) {
    super(BROKER_INFO, 0);
    this.brokerId = brokerId;
    this.timestamp = timestamp;
    this.tags = tags;
  }

  public RSocketRoutingRouteId getBrokerId() {
    return brokerId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public RSocketRoutingTags getTags() {
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RSocketRoutingBrokerInfo that = (RSocketRoutingBrokerInfo) o;
    return timestamp == that.timestamp
        && Objects.equals(brokerId, that.brokerId)
        && Objects.equals(tags, that.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brokerId, timestamp, tags);
  }
}