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
        List<Server> activeServers  = new ArrayList<>();
        for(Server server : servers){
            if (server.isActive()){
                activeServers.add(server);
            }
        }
        if (activeServers.isEmpty()) {
            throw new IllegalStateException("No active servers are available");
        }
       return loadBalancingStrategy.selectServer(activeServers);
    }

    public synchronized int getServerCount(){
        return servers.size();
    }
    public synchronized void excludeServer(Server serverToExclude){
       for (Server server : servers){
           if (server.equals(serverToExclude)){
               server.setActive(false);
               break;
           }
       }
    }

    public synchronized void includeServer(Server serverToInclude){
        for(Server server : servers){
            if (server.equals(serverToInclude)){
                server.setActive(true);
                break;
            }
        }
    }
}
