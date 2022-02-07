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

import doodle.rsocket.broker.core.routing.codec.RSocketRoutingRouteSetupCodec;
import io.netty.buffer.ByteBuf;

public final class RSocketRoutingRouteSetup extends RSocketRoutingFrame {

  private final RSocketRoutingRouteId routeId;
  private final RSocketRoutingTags tags;

  public static RSocketRoutingRouteSetupBuilder from(RSocketRoutingRouteId routeId) {
    return new RSocketRoutingRouteSetupBuilder(routeId);
  }

  public static RSocketRoutingRouteSetup from(ByteBuf byteBuf) {
    return from(RSocketRoutingRouteSetupCodec.routeId(byteBuf))
        .with(RSocketRoutingRouteSetupCodec.tags(byteBuf))
        .build();
  }

  RSocketRoutingRouteSetup(RSocketRoutingRouteId routeId, RSocketRoutingTags tags) {
    super(RSocketRoutingFrameType.ROUTE_SETUP, 0);
    this.routeId = routeId;
    this.tags = tags;
  }

  public RSocketRoutingRouteId getRouteId() {
    return routeId;
  }

  public RSocketRoutingTags getTags() {
    return tags;
  }

  @Override
  public String toString() {
    return "RSocketRoutingRouteSetup{" + "routeId=" + routeId + ", tags=" + tags + '}';
  }
}