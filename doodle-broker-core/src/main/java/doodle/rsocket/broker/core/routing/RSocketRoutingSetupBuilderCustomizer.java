package doodle.rsocket.broker.core.routing;

@FunctionalInterface
public interface RSocketRoutingSetupBuilderCustomizer {
  void customize(RSocketRoutingRouteSetupBuilder builder);
}
