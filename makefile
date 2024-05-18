# compile java files
compile:
	@./mvnw clean compile

# run client
cli:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.server.Client

# run gateway
gw:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.server.Gateway

# run barrels
brl:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.server.Barrel

# run downloaders
dl:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.server.Downloader

# run web app
web:
	@./mvnw spring-boot:run

# gen javadoc
# cd src
# javadoc -private -d javadoc -sourcepath src -classpath "../lib/*.jar" *.java
