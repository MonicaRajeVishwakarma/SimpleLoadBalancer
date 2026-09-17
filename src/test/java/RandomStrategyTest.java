import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrategyTest {

    @Test
    void shouldReturnOneOfTheAvailableServers(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);

        List<Server> servers = List.of(server1,server2,server3);

        RandomStrategy randomStrategy = new RandomStrategy();

        Server pickedServer = randomStrategy.selectServer(servers);

        assertTrue(servers.contains(pickedServer));
    }

    @Test
    void shouldReturnErrorWhenNoServersAreAvailable(){
        RandomStrategy randomStrategy = new RandomStrategy();

        assertThrows(IllegalStateException.class,
                () -> randomStrategy.selectServer(new ArrayList<>()));
    }

}