# Snilsing 

## Build notes

### Build with Maven
To build a project with maven use the following command:<br>
``mvn clean package`` <br>
Here we get jar with all dependencies.<br>
The disadvantage of this approach is that when you restart the application, the changed settings are not saved. (app-settings.properties)     

### Build with Intellij IDEA
To build a project using Intellij IDEA first create an artifact:<br>
*Project structure* -> *Artifacts*<br>
Choose artifact name.<br>
Then, add all available elements to output.<br>
Go to Java-FX tab, select main class (SpringJavafx) and change value of **native bundle**
to **all**<br>
Now you can build artifact by choosing *Build Artifacts...* -> *Artifact name* -> *build* 
Here we get application with included JRE


