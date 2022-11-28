FROM alpine:3.16.2 AS idea-fetcher
RUN apk --no-cache add curl
ARG IDEA
ARG IDEA_SHA256
RUN \
	curl \
		--output /tmp/ideaIC.tar.gz \
		https://ivyartifacts.exedio.com/idea/ideaIC-${IDEA}.tar.gz && \
	(echo "${IDEA_SHA256}  /tmp/ideaIC.tar.gz" | sha256sum -c -)


FROM debian:buster-20220418

ARG JDK
RUN \
	apt-get update && \
	apt-get install --no-install-recommends --assume-yes \
		${JDK}-jdk-headless && \
	rm -rf /var/lib/apt/lists/*

COPY --from=idea-fetcher /tmp/ideaIC.tar.gz /tmp
RUN \
	mkdir /opt/idea && \
	tar \
		-xf /tmp/ideaIC.tar.gz \
		--directory /opt/idea \
		--strip-components=1 && \
	rm /tmp/ideaIC.tar.gz

# keeping idea files  out of workspace, where they are scanned by idea inspections
COPY idea64.vmoptions /tmp
RUN \
	cat /tmp/idea64.vmoptions >> /opt/idea/bin/idea64.vmoptions

ARG IDEA
LABEL purpose=jenkins-idea
LABEL jdk=${JDK}
LABEL idea=${IDEA}
LABEL idea.properties=yes
