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
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy);

        loadBalancer.register(server);
        when(loadBalancingStrategy.selectServer(anyList())).thenReturn(server);

        Server selectedServer = loadBalancer.selectServer();

        assertEquals(server, selectedServer);
    }

    @Test
    void shouldReturnErrorWhenMaxLimitIsReached() {
        LoadBalancingStrategy loadBalancingStrategy = mock(LoadBalancingStrategy.class);
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy);

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
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy);
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
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy);
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
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancingStrategy);

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
}
