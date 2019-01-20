FROM openjdk:11
COPY ./out/production/ignitingcode/ /tmp


# Ignite version
ENV IGNITE_VERSION 2.7.0

# Ignite home
ENV IGNITE_HOME /opt/ignite/apache-ignite-${IGNITE_VERSION}-bin

# Do not rely on anything provided by base image(s), but be explicit, if they are installed already it is noop then
RUN apt-get update && apt-get install -y --no-install-recommends \
unzip \
curl \
&& rm -rf /var/lib/apt/lists/*

WORKDIR /opt/ignite

RUN curl https://dist.apache.org/repos/dist/release/ignite/${IGNITE_VERSION}/apache-ignite-${IGNITE_VERSION}-bin.zip -o ignite.zip \
&& unzip ignite.zip \
&& rm ignite.zip

RUN ls apache-ignite-2.7.0-bin/bin
# Copy sh files and set permission
RUN cp apache-ignite-2.7.0-bin/bin/ignite.sh $IGNITE_HOME/

RUN chmod +x $IGNITE_HOME/ignite.sh

CMD $IGNITE_HOME/ignite.sh

EXPOSE 11211 47100 47500 49112

WORKDIR /tmp
ENTRYPOINT ["java","dk.incoding.igniting.Main"]