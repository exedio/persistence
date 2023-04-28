FROM debian:buster-20220418

# corresponds to .github/workflows/ant.yml
ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF

LABEL purpose=jenkins
LABEL jdk=${JDK}
