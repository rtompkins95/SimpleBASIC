# BASIC Lexer

A lexer for a subset of the BASIC language that outputs a list of Tokens given a file

### Prerequisites

Java 1.8
Maven 3.8.x (at the time of publishing, use the latest version otherwise)
Ensure you have at least Maven 3.8.x installed on your system or environment where the project is being built

### Installation

Clone the repo and run:

> mvn clean install -U
> mvn package

An easy way to do this in IntelliJ:
- Click "Open",  navigate to the project root directory, select the "pom.xml" file, then click "Open as Project"
- Click "View" -> "Tool Windows" -> "Maven"
- Open Maven Settings for your project and ensure you are using "Bundled (Maven 3)" under "Maven home path"
    - This will run the correct Maven version if you are running a different version of Maven at the system level

## Usage

java -jar /target/basic-app-1.0.jar "basic_file_name.bas"