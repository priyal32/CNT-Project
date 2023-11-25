package Messages;

public class Request extends Message{
    int index;
    public Request(int index) {
        super(Type.Request);
        this.index = index;
    }
}
