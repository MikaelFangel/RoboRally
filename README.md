# RoboRally
This README notes the Features implemented in this project.
There is instructions further below about  
- How to setup the project
- How to play (Locally)
- How to setup Online Play

![Image of game](https://github.com/MikaelFangel/RoboRally/blob/main/images/scrot.png)

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
- Saving Board State to files (Everything Included)  
- Loading Boards from files (Everything Included)  

### Online Multiplayer
- Using RESTful Architecture  
- GET and PUT GameState from and to Server  
- External Server Hosting (Configure IP in Code)  
- Fully Functioning Game  
- Hosting Multiple Games  
- List of active Games  
- Joining Games  
- Seperate Thread HTTP Requests to Server  

### Boards
- Dizzy Highway  
- Dodge This  
- High Octane  
- Risky Crossing  



## Instructions for setting up the project
- Extract the zip file and open folder as an IntelliJ Project.



## Instructions for how to play (Locally)
### Running the Game Application
To start the application follow these simple steps:  
- Navigate to the Game Application at: Game/src/main/java/dk/dtu/compute/se/pisd/roborally/StartRoboRally.java
- Run the main method.  

### Starting a Game
Once the application opens then press File.  
From here you can select whether you want to launch a new game or load a saved game by respectively pressing "New Game" or "Load Game".  
New Game allows you to start a new game with one of the build in default boards. You can choose how many players you whish to play this game.  
Load Game let's you resume a saved game. We have build in a single saved board to display the saving mechanism. However should you want to save your own games, then please follow the "Saving a Game" instruction.  

### Saving a Game
To save a game, you must have the application running an ongoing game. When these conditions are met, then you can select File in the top menu-bar and select "Save Game".  
The application will promt you to input the name for the save file. Please follow the onscreen instructions.  
Once the prompts closes, the game is saved. It can now be launch when Loading a Game, described in "Starting a Game"  

### Exiting a Game
When a game is running, a new Option under File is shown instead of New Game, Stop Game. Press Stop Game to stop the current Game. 

### Exiting the Application
If you wish to exit the application, then either press the X in the corner or navigate to File -> Exit.



## Instructions for Online Play
The Instructions are to guide the player to setup Online Playing.

### How to launch a Server
To launch a server you will have to open a seperate application.  
The application, HttpApplication, can be found in project at: HTTP/src/main/java/dtu/compute.http/HttpApplication.  
- Please run the HttpApplication. 

This Application will be running in the background on the machine, that will function as the server.    
The standard is that all application running on the same machine can connect, because it is the same localhost.  
It can however be changed in the source code to specify the IP address of the server. This will allow all computer on the same subnet to be able to connect.  

When the game is launched. Please proceed to the next instruction.  

### How to Host an Online Game
To host an online game you need to launch the RoboRally application.  
- On the top menu bar, open the "Multiplayer" section.  
- Select Host Game.  
- Configure number of players and boards as you wish.  

When the board is shown the game is now open for players to join.  
Please proceed to the next instruction.  

### How to Join an Online Game
To join an online game you need to launch the a seperate RoboRally application, than the one hosting the game.  
If your IntelliJ does not already support multiple Instances of application running. Follow the next instruction.  

When you have another application of the game running, follow these steps:  
- On the top menu bar, open the "Multiplayer" section.  
- Click "Connect to Server".
- Select the game on the list you want to join and press "Join".
- When all players have joined, then the game can start.

### Setup IntelliJ to run multiple Applications simultaneously.
(This is done on IntelliJ IDEA 2022.1.2 (Ultimate Edition)).  
- Open the application configurations window (Edit configurations).  
- Under "Build and run" menu, select "Modify options".  
- Enable "Allow Multiple Instances".  
- Apply and Exit.  