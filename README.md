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
GET | /q/:name | Lists the most recent events for QUEUE :name
POST | /q/:name | Create a new message on QUEUE :name
GET | /q/:name/:page | Lists archived events for QUEUE :name for PAGE :page
GET | /m/:uuid | Shows details for an EVENT with :uuid

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

`curl -v -d '{"title":"my message","author":"xian","content":{"some":"data"}}' -H "Content-Type:application/json" http://localhost:3000/q/myqueue`

