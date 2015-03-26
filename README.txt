SmartBear Collaborator IntelliJ Plugin

This plugin is based mostly in the open api provided by default in the IntelliJ Idea libraries,
this libs include all the functionality required to access the SCM's included in the IDE such as Git, Subversion, etc.
To interact with the Smartbear Collaborator Server the plugin uses Jersey 2.15 and Jackson 2.5 for RESTFull webservices and serialization.
Since the json-rpc implementation on the server is not exactly the json-rpc as specified in http://www.jsonrpc.org/specification
this plugin implements a RESTFull way of communication.

The plugin consists of 3 modules.
client: the json-rpc interface.
common: commont objects for other modules
collaborator: the IntelliJ Idea plugin module.

In the plugin module there are 2 features:
Add to review: this is an IntelliJ Idea action to create new reviews and also add new change lists to an existing review.
Login: in the IntelliJ Idea Settings -> Tools -> Smartbear Collaborator are the settings to connect to the server.
