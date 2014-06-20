Reactive Mongo Example
======================

A simple ReactiveMongo plugin bootstrap
---------------------------------------

This example shows a fully working application using [ReactiveMongoCrud plugin](https://github.com/TPTeam/reactive_mongo_crud) for Play Framework.
With this template, you'll get a full working asyncronous CRUD implementation for ReactiveMongo, using just few lines of code; the plugin will take care of parents' relations and will automatically update children.
Moreover, the plugin let you use multiple databases for different collections effortlessly.

###To test the Application:
- Open a terminal in your project's directory
- Type 'play run'
- Head your favourite browser to 'http://localhost:9000'
- Play with the family
 
 
##Family's Routes

###Granpas
* 'GET	    /granpa' Open the Granparents\' table
* 'GET	    /granpa/table' Get all granpas in JSON ready to be parsed by a DataTable
* 'GET	    /granpa/edit/ + id' Get form for granpa with id
* 'GET	    /granpa/new' Get a form for creating a new granpa
* 'GET	    /granpa/submit' Submit the new entry!
    
###Fathers
* 'GET	    /father' Open the Fathers\' page
* 'GET	    /father/table' Get all fathers in JSON ready to be parsed by a DataTable
* 'GET	    /father/edit/ + id' Get form for father with id, useful for editing and deleting
* 'GET	    /father/new' Get a form for creating a new father
* 'GET	    /father/submit' Submit the new entry!

###Sons
* 'GET	    /son' Open the Sons\' page
* 'GET	    /son/table' Get all sons in JSON ready to be parsed by a DataTable
* 'GET	    /son/edit/ + id' Get form for son with id
* 'GET	    /son/new' Get a form for creating a new son
* 'GET	    /son/submit' Submit the new entry!


##Credits
* [ReactiveMongo's developers](http://reactivemongo.org/)
* [Zengularity](http://zengularity.fr)
* [DataTable jQury plugin](https://datatables.net/)
* [SimpsonCrazy for the wonderful](images http://www.simpsoncrazy.com/)

##Notes
Also available as [Typesafe Activator Template](https://typesafe.com/activator/template/play-reactive-mongo).