package prototype.interfaces;

public interface ICar {
    int flowRate = 0;
	void connect();
	void send();
	void close();
	default int getFlowRate(){
	    return flowRate;
    }
}
