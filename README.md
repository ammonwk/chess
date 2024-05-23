# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQmAOwphIhJ4YwDKKUAblbvocaWDABLBoAmCdeBRJDi0o0iKevyZCYAEWBhgAQRAgUAZzUTGgsnIUAhYGvGYA5lAgBXHDADEUFJ3g98dgO4ALJGHGJUZAFoAPgoqWigALgBtGQBRABlYgBVYgF0YAHpOACNMEXCYYLYObioIgG8AIk4UHh9KiMqYSoBfTHYuHlgi-LEIkBcoPWBsoxQACgBKPLCxQpDh5VUNCJq6lEUEBABVYyg1KcxFlXU1edl5YENjCLjElJgAMQAlAHkAWRhLPZnRVXPjss1KtaigfJsEABxYAAW3Uh0BpwBl2uKFuCWSsSeb0+plh6l+1DmRURKzWYI2W0UljAHgRlyWSJJKLG6PuWJeHxgwBpHiODJOGkKPVmqgi1AgSE4hOJRQ6pUiiBQ+GGo2Mz3UlgQWHlXWFIXyZSqcI0wFMKAalVVYxgA2VDicaksgrUADMtQgAJ6tQlUfXOfxgCIAJgADKGADpoY2nM0WxrW4y2waOGBOl3urbetroaVmCzWOzmFDoGAa0xINQ+WC2TzeXzIdAsEU0MpRAAKr3ISXSGW+VCjVX7UDQ+MtzQANM08Bo3NBOOPKlPKigYcAkAhF21Dd0QrqjZVh6O4YvlzO1HOoAvGpPmqv15ubz797vQn80Q4K1WqONy5XqxqACOljqGA0y9P8zIKIyKzmmAuw-ke+LTKSZxQVcrLkBicBJF8ezHigMCuhYMJ4XQqH+hBaJoB6MqQQsApAv0DjyCgCFQOMSFwlO56XpwU73huKGMUyDEGKyACSAByWHPLhXEoDxRgXvOAlrhuMDSUkrxkVAdGEehMHAiALHgrynH4ch-LQS6yLiTc0myfJlncdyvJJBAADWpZaTpPK0tZSi2QELZEmK-keB53loPp-ovhEf7flAGpOtq7QlHqIUGmEB4KQ0zS3pUEVRegW6+t0RR+E2IbhoOh4ufGTRLs0xVeaVT45lw2DmFYNi2NkCAgTA8QQBWaDuF4PiYFVgTZa2kQdl2PaZMYGhIBAaB1XlT5nspfFlTu5zxUODWntOe3zmVL7nFREQIKNSBoOMI1jUBIFVuBooGWJQVMXB7EWVQBHCTZQJ2RhNxYYkOG6QRSmzvO2JcsOMAAOqsLEzxYgpMAALyw-iMCKFJMgwLxiP4+TV6BUZlFfRU9VA2OO3nQjV4HV94NGcxypmbSgMjlZFHoaiESOZjzlM65rXRZpUnaW5AXC6FfQy+gsVZcUnRlC9j0pVqOoZX6ms7gz20Fc1RXuW1aAc-N-ozUGYaRtGjOCyeLNW7SJW2x1mC5t1BZ9cWnrDaNVgsLWk0NoGN05Qtnbdr292mBHdVq77TXbvHR1G5EVQZ1dedx++d3hzSz3l2Ab2gZ975cy6ILrNS-MZyDv2iRc9loncmJI58EVoxjWOK5FNt46PPs08FKtihKUoa3KecRC9Ef62l10m-HDMmmocaWi95pOBHabOkCmZej6h2a47NUuzGprmgfo1HzAJ-pufHrZv7XX5r1djQA4OaYa-4YDQhNBNes01GyzTfOEaIkJki9jxHCdO1top2wKEvbW+cvZjwwX7Tec0wpomQFWcB8J4j-goWoGuH0NY-W5tQYAyBOCsRbnSNu08wYi0wthSW7tCKcgHryIemMsQZwnhnbhTJZ5ogUgwruHdYJghofSUGndhiiyhrEGGAAqfuMAUEEmVsQvoxjNBUTisvKh5D8S0M1BvYuW95oMwsflKIFjUjZ3tjfGBTtaqu3cREGAnj7HeJ-nmHqhZbA4EsFAHAPB4CmUIhQyBU1HYl3gYtJOmRjFoO9jbM6lRjFSWZlncquccEM0Lp7Up5TmhtCIXAvoJleYoAoeMOAKSKF0LAoo1C4oWFSnYeZLhwsfraP4QTOEhjB7o3EZPce+NpGmJaWKBRVjDKNzaaxTp9TXIKXbrTXhDkZISyMfiMprljESScDjXylyTQyKFC4khERbnSi2dghUERuntN6Y4w2OC6auKqJ8-KnyfFYMqv4u+dUIUhKhZEwO-9+oWDcONAAUpKcaaSo5QMyaFNs7ZtjLQyPk12tSmrLkRTS6cCBgCeioHACA90oCYONnuZeBd0HtXpSU-EdzimJKZSytl0Ai4grkREAAVrizpOLHqAuArXAZIkVjMNYaM1ufK0DHOCpMvh0MBEETmaIhZI9JErL1S8tCMrNmc22X9VR+JxifINTwo1kNpkGOEU89QYiR6fInp8u1oL3nGPVRolY8rlVus+VOBSU4QASqgJ6zRLIbjbHbDIRQDwLEUGSGTRlzKoCsvZRPHGlqsQhvxiG4mpNRVlordATS5AYBSW2PEeI4a3l9Hnl8p1PyugRCVWgQFqVgUKgjQeXe+9GjjtTMY0+GYv5Xxzn4wM8LXbzqfou3Fy7CYf1OBfb+uYgA