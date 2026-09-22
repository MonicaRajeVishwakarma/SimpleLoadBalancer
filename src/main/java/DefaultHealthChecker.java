public class DefaultHealthChecker implements HealthChecker{
    private final ServerHealthProbe probe;

    public DefaultHealthChecker(ServerHealthProbe probe){
        this.probe = probe;
    }

    @Override
    public boolean isHealthy(Server server) {
        return probe.check(server);
    }
}
