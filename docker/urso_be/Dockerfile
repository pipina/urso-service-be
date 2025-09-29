FROM ubuntu:20.04 

ENV TZ Europe/Bratislava
ENV PATH=$PATH:/opt/java/jdk-17.0.2/bin
ENV JAVA_HOME=/opt/java/jdk-17.0.2

COPY openjdk-17.0.2.tar.gz /opt/java/
COPY urso-0.0.1-SNAPSHOT.jar /usr/app/
COPY application.properties /usr/app/
COPY self_signed_cert.pem /usr/app/csru_client_cert/
COPY private_key.pem /usr/app/csru_client_cert/
COPY password.txt /usr/app/csru_client_cert/
COPY rfo_transform.xsl /usr/app/doc/csru/


COPY configuration /usr/app/configuration/

WORKDIR /opt/java

RUN tar -xzf openjdk-17.0.2.tar.gz && \
    rm -rf openjdk-17.0.2.tar.gz

RUN apt-get update -y && \
	apt-get install curl -y && \
	apt-get install wget -y && \
	apt-get install openssh-client -y;
#   apt-get install software-properties-common -y && \
#	apt install -y --no-install-recommends ca-certificates;

#RUN update-ca-certificates && \
#    ls -1 /usr/local/share/ca-certificates | while read cert; do \
#        openssl x509 -outform der -in /usr/local/share/ca-certificates/$cert -out $cert.der; \
#        $JAVA_HOME/bin/keytool -import -alias $cert -keystore $JAVA_HOME/lib/security/cacerts -trustcacerts -file $cert.der -storepass changeit -noprompt; \
#        rm $cert.der; \
#    done

ADD csru-prod/csru.gov.sk.cer /usr/local/share/ca-certificates/csru.cer
ADD csru-prod/spca2-2018.crt /usr/local/share/ca-certificates/csru-spca.crt

RUN $JAVA_HOME/bin/keytool -import -trustcacerts -cacerts -storepass changeit -noprompt -alias "CSRU-PROD-1" -file "/usr/local/share/ca-certificates/csru.cer" -storepass changeit -noprompt;
RUN	$JAVA_HOME/bin/keytool -import -trustcacerts -cacerts -storepass changeit -noprompt -alias "CSRU-SPCA-PROD-2" -file "/usr/local/share/ca-certificates/csru-spca.crt" -storepass changeit -noprompt;

ADD csru-test/test_csru.gov.sk.cer /usr/local/share/ca-certificates/csru-test.cer
ADD csru-test/spca2-2018.crt /usr/local/share/ca-certificates/csru-spca-test.crt

RUN $JAVA_HOME/bin/keytool -import -trustcacerts -cacerts -storepass changeit -noprompt -alias "CSRU-TEST-1" -file "/usr/local/share/ca-certificates/csru-test.cer" -storepass changeit -noprompt;
RUN $JAVA_HOME/bin/keytool -import -trustcacerts -cacerts -storepass changeit -noprompt -alias "CSRU-SPCA-TEST-2" -file "/usr/local/share/ca-certificates/csru-spca-test.crt" -storepass changeit -noprompt;
RUN update-ca-certificates 


WORKDIR "/usr"
RUN mkdir -p app

WORKDIR /usr/app

RUN mkdir -p tmp_files/csru/csru_rpo_files
RUN mkdir -p tmp_files/csru/csru_rpo_files
RUN mkdir -p tmp_files/csru/csru_ra_files
RUN mkdir -p tmp_files/csru/csru_rfo_files
RUN mkdir -p tmp_files/csru/csru_rfo_codelist_files
RUN mkdir -p urso_fs/tmp


EXPOSE 8080
LABEL version="0.1-dev"

ENTRYPOINT ["java", "-jar", "urso-0.0.1-SNAPSHOT.jar","--spring.config.location=file:./application.properties"]
