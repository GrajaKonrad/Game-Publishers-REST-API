# Description
The project is an example of a spring-boot, CRUD, RESTful api usage, created as a part of my technical studies at Politecnika Poznańska.
The main idea is to Score Publishers of the boardgames, and collect information on the boardgames published by them

# Communication
The one of possible ways to communicate with the app's api is by Postman. Here I present a table of possible requests:

|                                       | Num.  | GET                           | POST                              | PUT                                   | PATCH                     | DELETE            |
| ------------------------------------- | ----- | ----------------------------- | --------------------------------- | ------------------------------------  | ------------------------- | ----------------- |
| /boardgames                           | 1     | list of every boardgame       | add boardgame                     |                                       |                           |                   |
| /boardgames/{id}                      | 2     | single board game             |                                   | update whole boardgame                | update boardgame field    | delete boardgame  |
| /publishers                           | 3     | list of every publisher       | add publisher                     |                                       |                           |                   |
| /publishers/{id}                      | 4     | single publisher              |                                   | edit whole publisher                  | update publisher field    | delete publisher  |
| /publishers/{id}/boardgames           | 5     | list of publisher boardgames  | add game with defined publisher   |                                       |                           |                   |
| /publishers/{id}/grades               | 6     | list of publisher grades      | add grade to the publisher        |                                       |                           |                   |
| /publishers/{id}/grades/{id}/comment  | 7     | comment to publisher grade    |                                   | update whole comment                  | update comment field      | delete comment    |
| /merges                               | 8     |                               | merge both posted publishers      |                                       |                           |                   |

NOTE: Changing Publisher name updates name in boardgames and grades\
NOTE: Deleting Publisher will set null in boardgames of that publisher and remove its grades from data\
NOTE: Adding grade to publisher will add empty comment to the grade, returning beside grade new comment object\
NOTE: Deleting comment will delte grade given to publisher\
NOTE: Meging publisher will change name in both of them, then leave 1 publisher object, deleting the other (and only publisher object)



Here are request structures for posting, puting and patching:

| Request num | Request json format                                                                         |
| ----------- | ------------------------------------------------------------------------------------------- |
| 1           | { "name": string, "publisher": string }                                                     |
| 3           | { "name": string, "countryCode": string }                                                   |
| 5           | { "name": string }                                                                          |
| 6           | { "grade": int, "concernsName": string }                                                    |
| 8           | { "firstPublisherName": string, "secondPublisherName": string, "newPublisherName": string } |
