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

import static doodle.rsocket.broker.core.routing.RSocketRoutingFrameType.ROUTE_SETUP;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingCodecUtils.*;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingFrameHeaderCodec.BYTES;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingTagsCodec.decodeTag;
import static doodle.rsocket.broker.core.routing.codec.RSocketRoutingTagsCodec.encodeTag;

import doodle.rsocket.broker.core.routing.RSocketRoutingRouteId;
import doodle.rsocket.broker.core.routing.RSocketRoutingTags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.Objects;

public final class RSocketRoutingRouteSetupCodec {

  public static ByteBuf encode(
      ByteBufAllocator allocator,
      RSocketRoutingRouteId routeId,
      String serviceName,
      RSocketRoutingTags tags,
      int flags) {
    Objects.requireNonNull(routeId);
    ByteBuf byteBuf = RSocketRoutingFrameHeaderCodec.encode(allocator, ROUTE_SETUP, flags);
    encodeRouteId(byteBuf, routeId);
    encodeByteString(byteBuf, serviceName);
    encodeTag(byteBuf, tags);
    return byteBuf;
  }

  public static RSocketRoutingRouteId routeId(ByteBuf byteBuf) {
    return decodeRouteId(byteBuf, BYTES);
  }

  public static String serviceName(ByteBuf byteBuf) {
    int offset = BYTES + ROUTE_ID_BYTES;
    return decodeByteString(byteBuf, offset);
  }

  public static RSocketRoutingTags tags(ByteBuf byteBuf) {
    int offset = BYTES + ROUTE_ID_BYTES;
    offset += decodeByteStringLength(byteBuf, offset);
    return decodeTag(offset, byteBuf);
  }

  private RSocketRoutingRouteSetupCodec() {}
}
