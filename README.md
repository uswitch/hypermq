# hypermq

A JSON hypermedia based message queue. Inspired by the AtomPub messaging as described in Rest in Practice

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

You will need [sqlite][2] installed. `brew install sqlite`

[2]: https://sqlite.org

## Setup

Create sqlite database `development.db` and create tables as shown in `sql/create-tables.sql` 

## Running

To start a web server for the application, run:

    lein ring server

### API usage

Method | URI | Action
--- | --- | ---
GET | /q/:name | Lists the most recent events for QUEUE :name
POST | /q/:name | Create a new message on QUEUE :name
GET | /q/:name/:page | Lists archived events for QUEUE :name for PAGE :page
GET | /e/:uuid | Shows details for an EVENT with :uuid

### Message body details

Each json message body looks as follows. All are optional (title, author, content)

```json
{
  "title":"the title of the event",
  "author","the message creator",
  "content", { "some":"custom data", "can":"contain any json" }
}
```

### Example message creation

`curl -v -d '{"title":"my event","author":"xian","content":{"some":"data"}}' -H "Content-Type:application/json" http://localhost:3000/q/myqueue`

