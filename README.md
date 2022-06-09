# RoboRally
This README notes the Features implemented in this project.
There is instructions on how to setup Online Multiplayer.

## Features

### Field Actions / Spaces
- Checkpoints (Fully Functioning)  
- Conveyor Belts (Fully Functioning)  
- Laser (Fully Functioning)  
- Pit (Visual Only)  
- Priority Antenna (Fully functioning)  
- Push Panel (Fully functioning)  
- Reboot Token (Visual Only)  
- Start Gears (Static Player Placement)  

### Deck and Cards
- Interative Programming Cards (Fully functioning)  
- Non-interactive Programming Cards (Fully functioning)  
- Damage Cards (SPAM only)  
- Special Programming (Not in use)  

### Saving and Loading with Serialization
- Reading JSON from files  
- Write JSON to files  
- Serialization of Board's to JSON  
- Deserialization of JSON to Board's  
- Saving Board State to files (Fully Functioning)  
- Loading Boards from files (Fully Functioning)  

### Online Multiplayer
- Using RESTful Architecture  
- GET and PUT GameState from and to Server  
- External Server Hosting (Configure IP in Code)  
- Fully Functioning Game  
- Hosting Multiple Games  
- List of active Games  
- Joining Games  
- Seperate Thread HTTP Requests to Server  


## Instructions
The Instructions are to guide the player to setup Online player.

### How to launch a Server
To launch a server you will have to open a seperate application
The application, HttpApplication, can be found in project at: HTTP/src/main/java/dtu/compute.http/HttpApplication.
- Please run the HttpApplication.
This Application will be running in the background on the machine.
All computers on the same subnet can connect to the server if the IP address is changed to the servers IP.
The standard is that all application running on the same machine can connect, because it is the same localhost.

When the server is launched. Please proceed to the next instruction.

### How to Host an Online Game
To host an online game you need to launch the RoboRally application.
- On the top menu bar, open the "Multiplayer" section.
- Select Host Game
- Configure number of players and boards as you wish.

When the board is shown the game is now open for players to join.
Please proceed to the next instruction.

### How to Join an Online Game
To join an online game you need to launch the a seperate RoboRally application, than the one hosting the game.
If your IntelliJ does not already support multiple Instances of application running. Follow the next instruction.

When you have another application of the game running, follow these steps:
- On the top menu bar, open the "Multiplayer" section.
- Click "Connect to Server"
- Select the server on the list you want to join and press "Join"
- When all players have joined, then the game can start.

### Setup IntelliJ to run multiple Applications simultaneously.
(This is done on IntelliJ IDEA 2022.1.2 (Ultimate Edition))
- Open the application configrations window (Edit configurations).
- Under "Build and run" section, select "Modify options".
- Enable "Allow Multiple Instances"
- Apply and Exit