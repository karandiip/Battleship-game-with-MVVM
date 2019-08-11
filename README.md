# SOEN 6441 Battleship

### Installing Maven (Mac Only)
```shell
brew install maven
```

## Import Project in IntelliJ
`Open > pom.xml > Open as Project`

 Run:
 ```shell
 mvn install
 ```


## Run the project (direct)
```shell
mvn exec:java -Dexec.mainClass="com.soen6441.battleship.app.AppWithGUI"
```