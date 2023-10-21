JStone is a Java application inspired by Hearthstone.
Additional cards can be added to the game via AdminPanel and are stored in a database.
Cards can trigger special actions when played, for example. Standard actions can be configured.
In addition to a standard card set, further sets can be defined which can be unlocked via pack-opening mechanism.

The data exchange between server and client is event-based.
The messages are exchanged in JSON format.
Currently, the entire event handling is done in the server and client class without being able to dynamically add events.

Warning: The classes used follow the documentation of the central abitur.
For this reason, for example, there is only a rudimentary check for SQL injections.


The project was developed within the context of a final project for the german 'Abitur' and is mostly available in German only.
Currently, the [MySQL Connector](https://www.mysql.com/products/connector/) must be added to the server manually. 