# p2peer

## Pushing to main
Please make sure you do 2 things before pushing to the main branch:
* Sync to the current version. This can involve pulling from main or merging it to your current branch.
* Test your version. The version in main should __always__ compile in the test environment.
	* $ javac *.java

## Setup instructions
Instructions for Eclipse IDE 2020-12, adapt to your own version if necessary.
* Create a new Java project
* Page 1: Create a Java Project
	* Uncheck "Use default location" and set Location to the github repository.
	* If necessary, select "Use project folder as root for sources and class files" under Project layout. If this option is grayed out, continue to the next step.
	* Press the Next button.
* Page 2: Java Settings
	* If selected, unselect Create module-info.java file. This will allow us to compile the default package.
	* Press the Finish button.
* You should see a new project named p2peer.
## Troubleshooting
Must declare a named package
	* In your project explorer, delete src/module-info.java.