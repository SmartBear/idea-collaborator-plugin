SmartBear Collaborator IntelliJ Plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This plugin is designed to work with IDEA v14.

This plugin is based mostly in the open api provided by default in the IntelliJ Idea libraries,
These libs include all the functionality required to access the SCM's included in the IDE such as Git, Subversion, etc.
To interact with the Smartbear Collaborator Server the plugin uses Jersey 2.15 and Jackson 2.5.

The plugin consists of 3 modules.
client: the json-rpc interface.
common: commont objects for other modules
collaborator: the IntelliJ Idea plugin module.

In the plugin module there are 2 features:
Add to review: this is an IntelliJ Idea action to create new reviews and also add new change lists to an existing review.
Login: in the IntelliJ Idea Settings -> Tools -> Smartbear Collaborator are the settings to connect to the server.

Build instructions: (At the moment, the plugin is built with IDEA CE itself rather than a build script.)
    1) Ensure that the intellij platform SDK is configured in IDEA:
        https://www.jetbrains.com/idea/help/configuring-intellij-platform-plugin-sdk.html
    2) Edit the module settings for collabplugin (select "collabplugin", press F4), set module SDK to
        the intellij platform SDK defined in step one above
    3) Build as normal

Tested platforms:
    * Windows
    * Linux
    * OSX