package protocol;

public class EchoProtocolFactory implements ServerProtocolFactory {
	
    public EchoProtocol create(){
        return new EchoProtocol();
    }
}