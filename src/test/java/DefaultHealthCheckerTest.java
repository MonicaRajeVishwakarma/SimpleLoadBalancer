import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultHealthCheckerTest {

    @Test
    void shouldReturnTheServerHealth(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);

        ServerHealthProbe probe = mock(ServerHealthProbe.class);
        when(probe.check(server1)).thenReturn(true);
        when(probe.check(server2)).thenReturn(false);

        DefaultHealthChecker defaultHealthChecker = new DefaultHealthChecker(probe);

        assertTrue(defaultHealthChecker.isHealthy(server1));
        assertFalse(defaultHealthChecker.isHealthy(server2));

        verify(probe).check(server1);
        verify(probe).check(server2);

    }
}