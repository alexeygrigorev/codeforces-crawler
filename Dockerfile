FROM ubuntu:16.04

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" >> /etc/apt/sources.list

RUN apt-get -qqy update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN apt-get -qqy install --no-install-recommends oracle-java8-installer --allow-unauthenticated

RUN apt-get -qqy install --no-install-recommends firefox

WORKDIR /codeforces

COPY bin/ bin/
COPY libs/ libs/
COPY target/codeforces-crawler-0.0.1-SNAPSHOT.jar codeforces-crawler.jar

ENTRYPOINT [ \
    "/usr/bin/java", \
    "-cp", "codeforces-crawler.jar:libs/*", \
    "-Xms256m", "-Xmx512m", \
    "codeforcescrawl.App" \
]