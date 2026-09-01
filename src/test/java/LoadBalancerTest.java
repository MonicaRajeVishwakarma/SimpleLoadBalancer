import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadBalancerTest {

    //shouldReturnAvailableServer return the available server
    @Test
    void shouldReturnAvailableServer(){
        Server server = new Server(1);
        LoadBalancer loadBalancer = new LoadBalancer();

        loadBalancer.register(server);

        Server selectedServer = loadBalancer.selectServer();

        assertEquals(server,selectedServer);
    }
}
