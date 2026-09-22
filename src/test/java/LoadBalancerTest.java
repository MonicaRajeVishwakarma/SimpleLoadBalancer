import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class LoadBalancerTest {

    //shouldReturnAvailableServer return the available server
    @Test
    void shouldReturnAvailableServer() {
        Server server = new Server(1);

        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);

        loadBalancer.register(server);
        when(loadBalancingStrategy.selectServer(anyList())).thenReturn(server);

        Server selectedServer = loadBalancer.selectServer();

        assertEquals(server, selectedServer);
    }

    @Test
    void shouldReturnErrorWhenMaxLimitIsReached() {
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);

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
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);

        assertThrows(
                IllegalStateException.class,
                () -> loadBalancer.selectServer()
        );
    }

    @Test
    void shouldDelegateServerSelectionToStrategy() {
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);

        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        when(loadBalancingStrategy.selectServer(anyList())).thenReturn(server1, server2, server3, server1);

        assertEquals(server1, loadBalancer.selectServer());
        assertEquals(server2, loadBalancer.selectServer());
        assertEquals(server3, loadBalancer.selectServer());
        assertEquals(server1, loadBalancer.selectServer());

        verify(loadBalancingStrategy, times(4))
                .selectServer(anyList());
    }

    @Test
    void shouldNeverRegisterMoreThan10ServersConcurrently(){
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);


        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<Future<?>> futures = new ArrayList<>();


        for (int i=0 ; i <20 ; i++){
            int id = i;
            Future<?> future = executor.submit( () -> {
                loadBalancer.register(new Server(id));
            });

            futures.add(future);
        }

        executor.shutdown();

        for (Future<?> future : futures){
            try{
                System.out.println("try");
                future.get();
            }catch (InterruptedException e){
                System.out.println("InterruptedException");
                Thread.currentThread().interrupt();
            }catch (ExecutionException e){
                System.out.println("ExecutionException");
                e.printStackTrace();
                assertTrue(e.getCause() instanceof IllegalStateException);
            }
        }

        assertEquals(10, loadBalancer.getServerCount());
    }

    @Test
    void shouldReturnAvailableActiveServer(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);

        List<Server> servers = List.of(server1,server2,server3);

        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);

        for(Server server : servers){
            loadBalancer.register(server);
        }

        loadBalancer.excludeServer(server2);

        when(loadBalancingStrategy.selectServer(anyList())).thenReturn(server1);

        assertEquals(server1,loadBalancer.selectServer());

        verify(loadBalancingStrategy).selectServer(List.of(server1,server3));
    }

    @Test
    void shouldReturnErrorWhenAllServersAreExcluded(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);

        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);


        loadBalancer.register(server1);
        loadBalancer.register(server2);

        loadBalancer.excludeServer(server1);
        loadBalancer.excludeServer(server2);

        assertThrows(
                IllegalStateException.class,
                () -> loadBalancer.selectServer()
        );
    }

    @Test
    void shouldReturnIncludeServerAlso(){
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);


        Server server1 = new Server(1);
        Server server2 = new Server(2);
        Server server3 = new Server(3);

        List<Server> servers = List.of(server1,server2,server3);

        for(Server server : servers){
            loadBalancer.register(server);
        }

        loadBalancer.selectServer();
        verify(loadBalancingStrategy)
                .selectServer(List.of(server1,server2,server3));

        loadBalancer.excludeServer(server2);

        loadBalancer.selectServer();
        verify(loadBalancingStrategy)
                .selectServer(List.of(server1,server3));

        loadBalancer.includeServer(server2);

        loadBalancer.selectServer();
        verify(loadBalancingStrategy);
    }

    @Test
    void shouldSetStatusBasedOnTheServerHealth(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);
        loadBalancer.register(server1);
        loadBalancer.register(server2);

        when(healthChecker.isHealthy(server1)).thenReturn(true);
        when(healthChecker.isHealthy(server2)).thenReturn(false);

        loadBalancer.checkServerHealth(server1);
        loadBalancer.checkServerHealth(server2);

        assertTrue(server1.isActive());
        assertFalse(server2.isActive());
    }
    @Test
    void shouldReturnTheHealthyServer(){
        Server server1 = new Server(1);
        Server server2 = new Server(2);

        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        HealthChecker healthChecker = mock(HealthChecker.class);

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy,healthChecker);
        loadBalancer.register(server1);
        loadBalancer.register(server2);

        when(loadBalancingStrategy.selectServer(anyList())).thenReturn(server1);
        when(healthChecker.isHealthy(server1)).thenReturn(true);
        when(healthChecker.isHealthy(server2)).thenReturn(false);

        loadBalancer.checkServerHealth(server1);
        loadBalancer.checkServerHealth(server2);

        assertEquals(server1,loadBalancer.selectServer());

        verify(loadBalancingStrategy).selectServer(List.of(server1));
    }
}
