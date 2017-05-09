# comScore Code Challenge 

Notes:  
Hashing the STB + Title + Date to create unique key  
Tests are just an example. Normally there would be far more unit tests as well as integration tests.  
Did not implement logging but instead just used system out  

## Part 1: File Import and Datastore

Using JSON to store the data.  
Records will be exported to JSON every 1000 records by default. 

Assumptions:  
Every record will have all attributes.  
The view time will be between 0 and 24 hours.  
Currency can be fractions of cents.  
Strings will be 64 characters or less. (can easily add a check to exure this)


##Part 2:  
Assumptions:  
