import java.util.List;
import java.util.Random;

public class RandomStrategy implements LoadBalancingStrategy{
    private final Random random = new Random();
    @Override
    public Server selectServer(List<Server> servers) {
        if (servers.isEmpty()){
            throw new IllegalStateException("No servers are available");
        }
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }
}
