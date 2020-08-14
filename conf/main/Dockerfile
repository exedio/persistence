FROM debian:stretch-20200607

ARG JDK
RUN \
	apt-get update && \
	apt-get install --no-install-recommends --assume-yes \
		${JDK}-jdk-headless && \
	rm -rf /var/lib/apt/lists/*

# needed for path to tools.jar when compiling instrumentor
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

LABEL purpose=jenkins
LABEL jdk=${JDK}
