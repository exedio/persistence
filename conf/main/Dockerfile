ARG RELEASE=stretch
FROM debian:${RELEASE}-20200607

ARG JDK
RUN \
	apt-get update && \
	apt-get install --no-install-recommends --assume-yes \
		${JDK}-jdk-headless && \
	rm -rf /var/lib/apt/lists/*

# needed for path to tools.jar when compiling instrumentor
ARG JAVAHOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_HOME ${JAVAHOME}

RUN \
	mkdir /var/dependencies-cache && \
	chown 1001:1001 /var/dependencies-cache

LABEL purpose=jenkins
LABEL jdk=${JDK}
