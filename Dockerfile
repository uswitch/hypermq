FROM docker-registry-v2.uswitchinternal.com/uswitch/clojure:2.3

RUN mkdir -p /opt/uswitch/hypermq
WORKDIR /opt/uswitch/hypermq

COPY project.clj /opt/uswitch/hypermq/project.clj
RUN lein deps

COPY . /opt/uswitch/hypermq

EXPOSE 8080

CMD ["lein", "ring", "server-headless"]
