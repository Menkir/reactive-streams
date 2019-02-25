# Reactive-Streams
This project is a result of my bachelor thesis about reactive programming. It has two implementations. First one in an reactive manner and second one in an blocking manner.


The Javadocs of this Project are [here](https://menkir.github.io/reactive-streams/java/index.html)

The Scaladocs of this Project are [here](https://menkir.github.io/reactive-streams/scala/index.html)

| Date of Submission |
|--------------------|
| 28.02.2019         |


## Graphic User Interface
If you want to checkout the GUI you need to switch to the [gui branch](https://github.com/Menkir/reactive-streams/tree/gui). The reason why the gui is on a separate branch is, because it's not part of the core-software. 
It's more like a extension, where you can see, whats happening with incoming measurements from Car. The Main-Method of the reactive Server (prototype.async) instantiate a Monitor JFrame and get incoming Measurements in a reactive manner. 