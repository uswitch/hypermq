# hypermq

A JSON hypermedia based message queue. Inspired by the AtomPub messaging as described in [Rest in Practice][1]. Have opted for the leaner [json+hal][2] media type over the more heavy weight Atom.

[1]: http://restinpractice.com/book/
[2]: http://stateless.co/hal_specification.html 

### Hypermq is...

* persistent messages 
* durable queue
* restful
* *not* distributed
* *not* low latency
* *not* high throughput

## Prerequisites

You will need [Leiningen][3] 1.7.0 or above installed.

[3]: https://github.com/technomancy/leiningen

You will need [sqlite][4] installed.

[4]: https://sqlite.org

## Setup

Create sqlite database `development.db` and create tables as shown in `sql/create-tables.sql`

```bash
brew install sqlite
sqlite3 development.db < sql/create-tables.sql
```

## Running

To start a web server for the application, run:

    lein ring server

### API usage

Method | URI | Action
--- | --- | ---
GET | /q/myqueue | Lists page of events for QUEUE `myqueue` from beginning
POST | /q/myqueue | Create a new message on QUEUE `myqueue`
GET | /q/myqueue/ca9539ef-4763-4940-8565-e7699c1404da | List page of events for QUEUE `myqueue` from message `ca9539ef-4763-4940-8565-e7699c1404da`
GET | /m/ca9539ef-4763-4940-8565-e7699c1404da | Shows details for an EVENT with uuid `ca9539ef-4763-4940-8565-e7699c1404da`
GET | /ack/myqueue/myclient | Shows the latest acknowledgement for QUEUE `myqueue` and CLIENT `myclient`
POST | /ack/myqueue/myclient | Create a new acknowledgement on QUEUE `myqueue` for CLIENT `myclient`

#### Note on queue creation

When POSTing to a queue, as well as creating the message it will also create the queue if one does not already exist.

### Message body details

Each json message body looks as follows. All are optional (title, author, content)

```json
{
  "producer" : "name/id of the message creator",
  "body" : { "some" : "custom data", "can" : "contain any json" }
}
```

### Example message creation

```bash
curl -v -d '{"producer":"myproducer","body":{"some":"data"}}' -H "Content-Type:application/json" http://localhost/q/myqueue
```

## Etags

When you view a queue, an ETAG header will be returned indicating the state of the queue.  This can later be used to make use of cached responses. If the ETAG has unchanged since the last view then a 304 Not Modified will be returned.

ETAGs are used by providing the value in the If-None-Match header.

```bash
curl -i -H 'Content-Type:application/json' -H 'If-None-Match:"ca9539ef-4763-4940-8565-e7699c1404da"' http://localhost/q/data
```

## Acknowledgements (optional)

After you have consumed a message, your client can acknowledge you have processed it by POSTing the message uuid to the Acknowledgement resource.  
This is completely optional and the consumer can choose to remember where you are up to in the queue however you like, such as zookeeper for instance. 

Using the built in acknowledgement system does have the advantage of making the hypermq monitoring page (see below) visualualy show which clients have consumed which queues and are upto date.

### Example Acknowledgement creation

```bash
curl -v -d '{"id":"ca9539ef-4763-4940-8565-e7699c1404da"}' -H "Content-Type:application/json" http://localhost/ack/myqueue/myclient
```

### Example retrieving an acknowledgement

```bash
curl -v -H "Content-Type:application/json" http://localhost/ack/myqueue/myclient
```

## Monitoring page

Hypermq provides a simple monitoring endpoint `/monitoring` which will show all the queues and the UUID if the most recent message on those queues. It will also show a label for each consumer that has acknowledged messages on that queue.  

The labels will be colour coded either:

* green - meaning the consumer is upto date on the queue
* red - meaning the consumer is not upto date on the queue

