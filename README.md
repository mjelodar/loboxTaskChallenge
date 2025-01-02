**Git structure**
There are 2 branches in this project 
1.	main 
2.	simple 
Against usual, executive and main branch is **simple** branch. First I try to use spring batch partition for improving pace of importing datasets. But there is a problem that I prefer not to spend time on it now. Then I created new branch and continue on it. 

**Improvement**
Some of improvement that comes to my mind are included:
1.	Using spring batch **partition** to use multiple threads and improve pace of importing. I tried to do this in main branch but I encounter an exception that needs more time to resolve.
2.	Using **cache** and load data in that and respond to services via this cache without slow I/O operation.
3.	Create **index** on some database fields.
4.	Also we can put all of** one-to-one relationship in one table**.  It’s against normalization rules but have a good effect on our query speed (if don’t  want to use cache mechanism) 
5.	Using **NoSql** dbs like Mongodb that have more throughput in reading data especially tree-like data structure
6.	Using “where clause” before “join” for performance improvement

**Run instruction**

There are 5 properties in application.properties file. They are file system based address of dataset file. Based on requested staff, I decided to import only 5 of them. (name.basics.tsv, title.crew.tsv, title.principals.tsv, title.rating.tsv, title.basics.tsv).
Properties includes:
•	title.basics.file.path

•	title.crew.file.path

•	title.rating.file.path

•	title.principals.file.path

•	name.basics.file.path


Unfortunately, I couldn’t run with original file, because my laptop’s resource is limit and after running I get outOfMemory error. For that I use small part of each file. Therefore, maybe there are some bug in my implementation that I couldn’t find them.

 
**Services** 

**First request:**   Import the dataset into the application (it is NOT necessary to call. importing data after loading)

Service address: /importdata

Method: post



**Second request:** Return all the titles in which both director and writer are the same person and he/she is still alive.

Service address: get/movie/sameAliveDirectorWriter

Method: get




**Third request:** Get two actors and return all the titles in which both of them played at

Service address: get/movie/twoCommonActors?actor1=Brett%20Adams=&actor2=Brian%20A.%20Adams

Method: get


**Forth request:** Get a genre from the user and return best titles on each year for that genre based on number of votes and rating

Service address: get/bestMovieOnEachYear?genre=Drama

Method: get


**Fifth request:** Count how many HTTP requests you received in this application since the last startup
Service address: get/requestCounter
Method: get


