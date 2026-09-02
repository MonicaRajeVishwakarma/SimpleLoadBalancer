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
}
