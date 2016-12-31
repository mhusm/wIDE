# wIDE: A smart IDE for the web - Readme

## About this project
wIDE has been developed by [Fabian Stutz](https://www.linkedin.com/in/fabian-stutz-2a7035107) as his Master Thesis at the [Globis Group at ETH Zürich](https://globis.ethz.ch). The project has been supervised by [Maria Husmann](https://globis.ethz.ch/#!/person/maria-husmann/) and [Alfonso Murolo](https://globis.ethz.ch/#!/person/alfonso-murolo/).

## Setup the project

In order to setup the project, we recommend to first download and install the following applications and tools.

- IntelliJ IDEA Ultimate Edition: `https://www.jetbrains.com/idea/download/`
- Java SDK 1.8: `http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html`
- MySQL Server: `http://dev.mysql.com/downloads/mysql/`
- node.js: `https://nodejs.org/`
- controlsfx: `https://bitbucket.org/controlsfx/controlsfx/` (Should match the JavaFX version)

The IntelliJ IDEA UE contains a useful VCS mechanism that allows downloading and managing of the Git repository directly through the IDE. Nevertheless, you are free to use the Git client of your choice.

## Setup the wIDE-plugin project
This is a step-by-step guide of how to setup the client project on your machine.

1. Install and open the IntelliJ IDEA Ultimate Edition
2. Open the 'wIDE-Plugin' project from wherever you have downloaded the wIDE source code to.
3. Copy the controlsfx library to the `wIDE-Plugin\resources` folder.
4. Check the Project Settings by clicking `File` > `Project Structure...`
    1. Navigate to the entry `SDK` and make sure the Java 8 SDK is imported. If it is not, please import it by clicking the `+` button, selecting `JDK` and navigating to the installation folder of the SDK. Please note, that SDK 1.8 is the minimum version, because the plugin requires JavaFX, which is not available before Java 8.
    2. Also make sure, that the `IntelliJ Platform Plugin SDK` is imported properly too. If it is not, please click on the `+` button, select `IntelliJ Platform Plugin SDK` and navigate to the installation folder of the IntelliJ IDEA UE. After you have clicked `OK`, please select the Java SDK 1.8 from the appearing menu.
    3. Switch to the `Libraries` entry and make sure that `controlfx` is properly imported. If it is not, click on the `+` button, select `Java` and navigate to the `resources` folder of the `wIDE-Plugin` project and select the `controlfx` code folder.
    4. Switch to the `Project` entry and make sure that the project's SDK is set to IntelliJ Platform Plugin SDK with the Java SDK 1.8. If it is not, you should be able to select the correct SDK from the drop-down menu.
    5. Click `OK`. The IDE will most probably have to do some background work and indexing.
5. Select `Run` > `Debug 'wIDE: A smarter web IDE'`. The IDE should start up another IntelliJ IDEA instance that includes the wIDE plugin.

## Setup the wIDE-server project
This step-by-step guide shows how to set-up the server project on your machine.

1. Install and open the `IntelliJ IDEA Ultimate Edition`
2. Open the `wIDE-Server` project from wherever you have downloaded the wIDE source code to.
3. Install nodeJS and its package manager npm.
4. Run `sudo npm install` in the `wIDE-Server`-folder
5. Install the MySQL server.
6. Start the MySQL server.
7. Connect to the MySQL with a MySQL client and the appropriate credentials. After you have connected to the server, run the SQL script of the `db\_setup.sql`-file inside the `wIDE-Server`-project. The script sets up the database for the cache. It also creates a user, which has access to the database.
8. Start the wIDE-Server by clicking `Run` > `Debug bin/www` in the IntelliJ IDEA.
