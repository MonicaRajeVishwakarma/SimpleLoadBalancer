public class Server {
    // making id as final variable so that other part of code can't modify it.
    private final int id;

    private boolean active = true;

    public Server(int id) {
        this.id = id;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        return id == server.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
