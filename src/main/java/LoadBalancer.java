import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    // Registered servers are managed by the load balancer.
    private final List<Server> servers = new ArrayList<>();
    private final LoadBalancingStrategy loadBalancingStrategy;

    public LoadBalancer(LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    public synchronized void register(Server server) {
        if (servers.size() >= 10) {
            throw new IllegalStateException("Maximum of 10 Servers are allowed");
        }
        servers.add(server);
    }

    public synchronized Server selectServer() {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No servers are available");
        }
       return loadBalancingStrategy.selectServer(servers);
    }

    public synchronized int getServerCount(){
        return servers.size();
    }
}
