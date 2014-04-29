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
GET | /q/myqueue | Lists the most recent events for QUEUE `myqueue`
POST | /q/myqueue | Create a new message on QUEUE `myqueue`
GET | /q/myqueue/1 | Lists archived events for QUEUE `myqueue` for PAGE 1
GET | /m/ca9539ef-4763-4940-8565-e7699c1404da | Shows details for an EVENT with uuid `ca9539ef-4763-4940-8565-e7699c1404da`
GET | /ack/myqueue/myclient | Shows the latest acknowledgement for QUEUE `myqueue` and CLIENT `myclient`
POST | /ack/myqueue/myclient | Create a new acknowledgement on QUEUE `myqueue` for CLIENT `myclient`

#### Note on queue creation

When POSTing to a queue, as well as creating the message it will also create the queue if one does not already exist.

### Message body details

Each json message body looks as follows. All are optional (title, author, content)

```json
{
  "title" : "the title of the event",
  "author" : "the message creator",
  "content" : { "some":"custom data", "can":"contain any json" }
}
```

### Example message creation

```bash
curl -v -d '{"title":"my message","author":"xian","content":{"some":"data"}}' -H "Content-Type:application/json" http://localhost/q/myqueue
```

## Etags

When you view a queue, an ETAG header will be returned indicating the state of the queue.  This can later be used to make use of cached responses. If the ETAG has unchanged since the last view then a 304 Not Modified will be returned.

ETAGs are used by providing the value in the If-None-Match header.

```bash
curl -i -H 'Content-Type:application/json' -H 'If-None-Match:"ca9539ef-4763-4940-8565-e7699c1404da"' http://localhost/q/data
```

## Acknowledgements

After you have consumed a message, your client can acknowledge you have processed it by POSTing the message uuid to the Acknowledgement resource.

### Example Acknowledgement creation

```bash
curl -v -d '{"uuid":"ca9539ef-4763-4940-8565-e7699c1404da"}' -H "Content-Type:application/json" http://localhost/ack/myqueue/myclient
```

### Example retrieving an acknowledgement

```bash
curl -v -H "Content-Type:application/json" http://localhost/ack/myqueue/myclient
```
