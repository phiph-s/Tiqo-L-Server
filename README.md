# Tiqo-L (Server)

Tiqo-L-Server is a the backend server for tiqo-l. It is based on Java and used with the JavaScript-based Tiqo-L-Client.
But before you start it is essential for you to know what the exact purpose of Tiqo-L is:

Tiqo-L doesn't replace a webserver like apache or nginx!

Tiqo-L wasn't made to host the HTML files even though it might be supported in the future for small standalone projects. Tiqo-L needs a webserver to host the .html files and the JavaScript-client. The real content of the page will be delivered to the client by the server.

Can I write my complete website using Tiqo-L?

Yes you can, but I won't recommence it. Tiqo-L is made to create super responsive web-panels that need real time data (like a performance monitor or a liveticker) or for multi-user applications (like a online document editor or a card game). Using Tiqo-L for very static websites with less to no dynamic content is not recommenced since its wasted server performance (That is why tiqo-l.com is not made with Tiqo-L). The ideal usage of Tiqo-L is a classic website with Tiqo-L elements (for example in iframes).

## Installation

A detailed guide on how to install a Tiqo-L server instance and the client, read this [page](https://tiqo-l.com/setup).

## Usage

The Tiqo-L server will run a core provided in an "core/" subfolder. It will search for a class called "Pointer" inside the default package (no package).

```java
//The Pointer class
import me.m_3.slf.Main; //This is the example Main class. The Main Class must be extending "Core"
import me.m_3.tiqoL.WSServer;
import me.m_3.tiqoL.coreloader.Core;
import me.m_3.tiqoL.coreloader.interfaces.ClassPointer;

public class Pointer implements ClassPointer{

	public Core getCore(WSServer server) {
		return new Main(server , "Your Core name");
	}
	
}
```
The Instance of your Core will be created as soon as the server is ready to serve.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)
