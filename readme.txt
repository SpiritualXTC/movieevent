Chris Snell - s3435406!

API Level = 22
Option B!

Design Notes:
-------------
 - SQL DB, uses Lazy "Instantiation" when connecting.
 - Chain of Responisibility is used for:
	- Search Movies (Online -> Local)
	- Movie Details (Cached -> Local -> Online)
	- Images (Cached -> Online)
		
Bonus Features:
---------------
	- LRU Image Cache (In Memory, Limit to 10 Images -- number of movies returned by a OMDB query)
	- LRU Movie Cache (In Memory, Limit to 2 Movies -- for testing purposes)
	-* One-to-Many: Movies <-> Parties (Done as it made more sense for Firebase)

* Wasn't actually a bonus feature for A2 :P
	
Requirements:
-------------
Unlimited Movie Searching
	- Search for movies												[x]
	- Search field													[x]
	- Movie Details View											[x]
	- OMDB Search / Details											[x]
	- SQL Search / Details											[x]
	- Caching														[x]
		- Cache Limit [Bonus]										[x]
	- JSON Parser													[x]
	
Option B Requirements:
	- Firebase Connection / API										[x]
	- "Shared" access, so users can see other Firebase parties
		- Default Firebase User Account								[x]
	- Firebase Delete												[x]
	- Firebase Create/Update										[x]
	- Party Modifications made offline must Sync to Firebase 		[x]
		(Probably)
  
Functional Requirements:
	- Target Android Version API 19 or higher						[x]
	- Broadcast Receiver for Network Connectivity					[x]
	- Search text left in the text field, should auto-search when 	[x]
		interwebs reconnects (seems to)
				
	
Non-Functional Requirements
	- Non-Trivial I/O operations (SQL / Online)	need to be threaded	[x]
	- Intuitive & Efficient means of interation with the user		[.]
		(Probably)
	- Efficient use of UI resources through styles/themes/xml		[.]
		(Probably)
	
Model Persistance:
	- Model Persists to Local SQLite DB								[.]
		- Movies													[x]
		- Parties													[x]
		(I think! LOL)
	- Model Persists to Memory										[.]
		- Movies (Cache, Load on Demand)							[x]
		- Parties (Stored. Load on Initialisation)					[x]

Network Availablity:
	- Broadcast Receiver											[x]
	- OMDB Connectivity Check										[x]
	- Offline Search: search again when online						[x]
	- Firebase Sync when online										[x]
		(for the most part)
	
General:
	- Chain of Responisibility for getting the details of a movies	[x]
		- Memory													[x]
		- SQL														[x]
		- OMDB														[x]
	- Chain of Responsibility for Searching Movies					[x]
		- OMDB														[x]
		- SQL														[x]

Database / Local:
	- Full Details of every party in the Database					[x]
	- Full Details of all movies that are result of every search	[x]
	
Memory / Cache:
	- Preload Party / Invitee Information							[x]
	- Store movies in memory ONLY if the detail screen is selected	[x]
	
Clarifications:
	- Local DB acts as a cache for OMDB Web Service
	- Memory Cache as a cache for the Local DB	
	- Movies stored in memory ONLY when the details page is opened
	- Movies stored in Database when they are results of a search