import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoadBalancerTest {

    //shouldReturnAvailableServer return the available server
    @Test
    void shouldReturnAvailableServer() {
        Server server = new Server(1);
        LoadBalancer loadBalancer = new LoadBalancer();

        loadBalancer.register(server);

        Server selectedServer = loadBalancer.selectServer();

        assertEquals(server, selectedServer);
    }

    @Test
    void shouldReturnErrorWhenMaxLimitIsReached() {
        LoadBalancer loadBalancer = new LoadBalancer();
        for (int i = 1; i <= 10; i++) {
            loadBalancer.register(new Server(i));
        }
        assertThrows(
                IllegalStateException.class,
                () -> loadBalancer.register(new Server(11))
        );
    }

    @Test
    void shouldReturnErrorWhenNoServersAreRegistered() {
        LoadBalancer loadBalancer = new LoadBalancer();
        assertThrows(
                IllegalStateException.class,
                () -> loadBalancer.selectServer()
        );
    }

    @Test
    void shouldReturnServerInRoundRobinOrder() {
        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);
        LoadBalancer loadBalancer = new LoadBalancer();

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        assertEquals(server1, loadBalancer.selectServer());
        assertEquals(server2, loadBalancer.selectServer());
        assertEquals(server3, loadBalancer.selectServer());
        assertEquals(server1, loadBalancer.selectServer());
    }
}
