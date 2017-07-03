# SmartBear Collaborator IntelliJ Plugin

This plugin is designed to work with IDEA v14, and supports creating (or adding
to) reviews from git and svn commits. 

PLEASE NOTE: This is presently beta! You will in all likelyhood encounter bugs!

This plugin is based mostly on the open api provided by default in the IntelliJ
Idea libraries, with third party libraries to handle a few of the smaller SCM 
interactions that IntelliJ does not handle natively. To interact with the 
Collaborator server the plugin uses Jersey 2.15 and Jackson 2.5.

The plugin consists of 3 modules.
- client: the json-rpc interface.
- common: commont objects for other modules
- collaborator: the IntelliJ Idea plugin module.

In the plugin module there are 2 features:
- Add to review: this is an IntelliJ Idea action to create new reviews and also add new change lists to an existing review.
- Login: in the IntelliJ Idea Settings -> Tools -> Smartbear Collaborator are the settings to connect to the server.

## Installation
1. Download the zip file from the project root
2. Go to File > Settings > Plugins in IDEA
3. Choose "Install plugin from disk..."
4. Pick the zip file you downloaded in step one
5. Click Apply and then Restart
6. Go to File > Settings > Tools > SmartBear Collaborator to configure parameters like your Collaborator URL, username, etc. (Apply to save; should you get login ticket errors later, the "Refresh Auth Ticket" button here should resolve that)
7. At this point the plugin is fully installed and configured.

## Use:
From the git or svn history of a file, selections of commits will now have an "add to review" context menu entry.  This allows for creation of reviews from the selected commit(s), or addition of them to an existing review, in a fashion similar to the standalone GUI client.  Note that large commits may take some time to transfer.

## Build instructions
(At the moment, the plugin is built with IDEA CE itself rather than a build script.)
1. Ensure that the intellij platform SDK is configured in IDEA: https://www.jetbrains.com/idea/help/configuring-intellij-platform-plugin-sdk.html
2. Import project into IDEA as normal (except see #3 below!), select SDK as the one configured above.
3. Do not import the collabplugin module at first (it is incorrectly set up as an ordinary Java module)
4. After project is imported, go to project settings w/ Ctrl-Alt-Shift-S
5. Modules > + icon > Import > navigate to collabplugin module dir > select collaborator.iml
6. collabplugin should be registered as a plugin-type module, on the dependencies page select the SDK configured in step one above
7. Build as normal (e.g. Ctrl-F9)

## Release build
1. Build plugin (see above)
2. Build > Prepare plugin modules for deployment
3. This makes a zip file in the project root that may be deployed

## More information
https://confluence.jetbrains.com/display/IDEADEV/Getting+Started+with+Plugin+Development

## Tested platforms:
- Windows
- Linux
- OSX

## Authors
- Miguel Zumbado
- Michael Jackson
