import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundRobinStrategyTest {

    @Test
    void shouldReturnServerInRoundRobinOrder() {
        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);

        List<Server> servers = List.of(server1, server2, server3);

        RoundRobinStrategy strategy = new RoundRobinStrategy();

        assertEquals(server1, strategy.selectServer(servers));
        assertEquals(server2, strategy.selectServer(servers));
        assertEquals(server3, strategy.selectServer(servers));
        assertEquals(server1, strategy.selectServer(servers));
    }
}

