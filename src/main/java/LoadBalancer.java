import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    // making servers as final variable so that it cannot be made to point to a different object.
    private final List<Server> servers = new ArrayList<>();
    void register(Server server){
        servers.add(server);
    }
    Server selectServer(){
        return servers.get(0);
    }
}
