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

Setup and build instructions: (At the moment, the plugin is built with IDEA CE itself rather than a build script.)
    1) Ensure that the intellij platform SDK is configured in IDEA:
        https://www.jetbrains.com/idea/help/configuring-intellij-platform-plugin-sdk.html
    2) Import project into IDEA as normal (except see #3 below!), select SDK as the one configured above.
    3) Do not import the collabplugin module at first (it is incorrectly set up as an ordinary Java module)
    4) After project is imported, go to project settings w/ Ctrl-Alt-Shift-S
    5) Modules > + icon > Import > navigate to collabplugin module dir > select collaborator.iml
    6) collabplugin should be registered as a plugin-type module, on the dependencies page select the SDK configured
        in step one above
    7) Build as normal (e.g. Ctrl-F9)

Release build:
    1) Build plugin (see above)
    2) Build > Prepare plugin modules for deployment
    3) This makes a zip file in the project root that may be deployed

More information: https://confluence.jetbrains.com/display/IDEADEV/Getting+Started+with+Plugin+Development

Tested platforms:
    * Windows
    * Linux
    * OSX