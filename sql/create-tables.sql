CREATE TABLE IF NOT EXISTS message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  uuid VARCHAR(36),
  queue VARCHAR(255) NOT NULL,
  producer VARCHAR(255),
  body BLOB,
  created BIGINT NOT NULL
);
CREATE INDEX queue_idx ON message (queue);
CREATE INDEX uuid_idx ON message (uuid);

CREATE TABLE IF NOT EXISTS acknowledgement (
  queue varchar(255) NOT NULL,
  client VARCHAR(255) NOT NULL,
  message BIGINT NOT NULL,
  created BIGINT NOT NULL,
  PRIMARY KEY (queue, client, message)
);
