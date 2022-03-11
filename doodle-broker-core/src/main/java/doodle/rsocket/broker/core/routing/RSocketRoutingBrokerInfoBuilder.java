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

public class RSocketRoutingBrokerInfoBuilder
    extends RSocketRoutingTagsBuilder<RSocketRoutingBrokerInfoBuilder> {
  private RSocketRoutingRouteId brokerId;
  private long timestamp;
  private RSocketRoutingTags tags;

  public RSocketRoutingBrokerInfoBuilder brokerId(RSocketRoutingRouteId brokerId) {
    this.brokerId = brokerId;
    return this;
  }

  public RSocketRoutingBrokerInfoBuilder timestamp(long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public RSocketRoutingBrokerInfoBuilder tags(RSocketRoutingTags tags) {
    this.tags = tags;
    return this;
  }

  public RSocketRoutingBrokerInfo build() {
    return new RSocketRoutingBrokerInfo(brokerId, timestamp, tags);
  }
}
