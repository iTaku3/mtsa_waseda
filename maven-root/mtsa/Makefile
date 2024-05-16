REMOTE_JVM_FLAG	= -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
MEMORY_FLAG		= -Xmx20G
JAR_FLAG		= -jar
TARGET			= ./target/mtsa-1.0-SNAPSHOT.jar

clean:
	mvn clean
.PHONY: build
build:
	mvn clean \
	&& mvn install -DskipTests=true
debug:
	java $(REMOTE_JVM_FLAG) $(MEMORY_FLAG) $(JAR_FLAG) $(TARGET)
run:
	java $(MEMORY_FLAG) $(JAR_FLAG) $(TARGET)
