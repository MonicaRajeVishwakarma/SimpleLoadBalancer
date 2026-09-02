import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    // making servers as final variable so that it cannot be made to point to a different object.
    private final List<Server> servers = new ArrayList<>();
    private final LoadBalancingStrategy loadBalancingStrategy;

    public LoadBalancer(LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    public void register(Server server) {
        if (servers.size() >= 10) {
            throw new IllegalStateException("Maximum of 10 Servers are allowed");
        }
        servers.add(server);
    }

    public Server selectServer() {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No servers are available");
        }
       return loadBalancingStrategy.selectServer(servers);
    }
}
