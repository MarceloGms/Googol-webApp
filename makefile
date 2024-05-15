# compile java files
compile:
	@./mvnw clean compile

# run client
cli:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.Client

# run gateway
gw:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.Gateway

# run barrels
brl:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.Barrel

# run downloaders
dl:
	@./mvnw exec:java -Dexec.mainClass=com.googol.googolfe.Downloader

# run web app
web:
	@./mvnw spring-boot:run

# gen javadoc
# cd src
# javadoc -private -d javadoc -sourcepath src -classpath "../lib/*.jar" *.java
