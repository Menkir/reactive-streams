package prototype.interfaces;

import java.io.IOException;

public interface IServer {
	void receive() throws IOException;
	void close();
}
