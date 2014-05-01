CREATE TABLE IF NOT EXISTS message (
  id varchar(36) PRIMARY KEY,
  queue VARCHAR(255) NOT NULL,
  producer VARCHAR(255),
  body BLOB,
  created BIGINT NOT NULL
);
CREATE INDEX queue_idx ON message (queue);

CREATE TABLE IF NOT EXISTS acknowledgement (
  queue varchar(255) NOT NULL,
  client VARCHAR(255) NOT NULL,
  message VARCHAR(36) NOT NULL,
  created BIGINT NOT NULL,
  PRIMARY KEY (queue, client, message)
);
