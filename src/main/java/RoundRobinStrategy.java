import java.util.List;

public class RoundRobinStrategy implements LoadBalancingStrategy {
    private int index = 0;

    @Override
    public Server selectServer(List<Server> servers) {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No servers are available");
        }

        Server server = servers.get(index);
        index = (index + 1) % servers.size();

        return server;
    }
}
