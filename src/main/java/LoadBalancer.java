import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    private final List<Server> servers = new ArrayList<>();
    void register(Server server){
        servers.add(server);
    }

    Server selectServer(){
        return servers.get(0);
    }
}
