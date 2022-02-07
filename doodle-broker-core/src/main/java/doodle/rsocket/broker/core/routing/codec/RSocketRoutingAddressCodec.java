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
package doodle.rsocket.broker.core.routing.codec;

import doodle.rsocket.broker.core.routing.RSocketRoutingFrameType;
import doodle.rsocket.broker.core.routing.RSocketRoutingRouteId;
import doodle.rsocket.broker.core.routing.RSocketRoutingTags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.Objects;

public final class RSocketRoutingAddressCodec {

  public static final int FLAGS_U = 0b00_1000_0000;
  public static final int FLAGS_M = 0b00_0100_0000;
  public static final int ROUTING_TYPE_MASK = 0b11_0001_1111;

  private RSocketRoutingAddressCodec() {}

  public static ByteBuf encode(
      ByteBufAllocator allocator,
      RSocketRoutingTags tags,
      RSocketRoutingRouteId originRouteId,
      int flags) {
    Objects.requireNonNull(originRouteId);
    ByteBuf byteBuf =
        RSocketRoutingFrameHeaderCodec.encode(allocator, RSocketRoutingFrameType.ADDRESS, flags);
    RSocketRoutingCodecUtils.encodeRouteId(byteBuf, originRouteId);
    RSocketRoutingTagsCodec.encodeTag(byteBuf, tags);
    return byteBuf;
  }

  public static RSocketRoutingRouteId originRouteId(ByteBuf byteBuf) {
    return RSocketRoutingCodecUtils.decodeRouteId(byteBuf, RSocketRoutingFrameHeaderCodec.BYTES);
  }

  public static RSocketRoutingTags tags(ByteBuf byteBuf) {
    int offset = RSocketRoutingFrameHeaderCodec.BYTES + RSocketRoutingCodecUtils.ROUTE_ID_BITS;
    return RSocketRoutingTagsCodec.decodeTag(offset, byteBuf);
  }
}
