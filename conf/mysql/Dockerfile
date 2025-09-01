# check=error=true;skip=InvalidDefaultArgInFrom,SecretsUsedInArgOrEnv
ARG VERSION
FROM mysql:${VERSION}

ENV MYSQL_ROOT_PASSWORD=password-JUST-FOR-SETUP
# saves 2 seconds during container startup, but hampers CONVERT_TZ()
ENV MYSQL_INITDB_SKIP_TZINFO=disable

ARG CONF
COPY ${CONF} /etc/mysql/conf.d/z_my.cnf
