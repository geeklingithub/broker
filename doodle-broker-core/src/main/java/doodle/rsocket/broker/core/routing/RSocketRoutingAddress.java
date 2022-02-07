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

import doodle.rsocket.broker.core.routing.codec.RSocketRoutingAddressCodec;
import io.netty.buffer.ByteBuf;

public class RSocketRoutingAddress extends RSocketRoutingFrame {

  private final RSocketRoutingRouteId originRouteId;
  private final RSocketRoutingTags tags;

  public static RSocketRoutingAddressBuilder from(RSocketRoutingRouteId originRouteId) {
    return new RSocketRoutingAddressBuilder(originRouteId);
  }

  public static RSocketRoutingAddress from(ByteBuf byteBuf, int flags) {
    return from(RSocketRoutingAddressCodec.originRouteId(byteBuf))
        .with(RSocketRoutingAddressCodec.tags(byteBuf))
        .flags(flags)
        .build();
  }

  RSocketRoutingAddress(RSocketRoutingRouteId originRouteId, RSocketRoutingTags tags, int flags) {
    super(RSocketRoutingFrameType.ADDRESS, flags);
    this.originRouteId = originRouteId;
    this.tags = tags;
  }

  public RSocketRoutingType getRoutingType() {
    int routingType = getFlags() & (~RSocketRoutingAddressCodec.ROUTING_TYPE_MASK);
    return RSocketRoutingType.from(routingType);
  }

  public RSocketRoutingRouteId getOriginRouteId() {
    return originRouteId;
  }

  public RSocketRoutingTags getTags() {
    return tags;
  }
}
