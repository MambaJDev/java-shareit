DROP TABLE IF EXISTS users, items, bookings, requests, comments CASCADE;

CREATE TABLE IF NOT EXISTS users(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
description VARCHAR(255) NOT NULL,
requester_id BIGINT NOT NULL,
create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
owner_id BIGINT NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(255) NOT NULL,
available Boolean NOT NULL,
request_id BIGINT,
CONSTRAINT items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
CONSTRAINT items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
item_id BIGINT NOT NULL,
booker_id BIGINT NOT NULL,
status VARCHAR(32) NOT NULL,
CONSTRAINT bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
CONSTRAINT bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
text VARCHAR(255) NOT NULL,
item_id BIGINT NOT NULL,
author_id BIGINT NOT NULL,
CONSTRAINT comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT comments_to_users FOREIGN KEY(author_id) REFERENCES users(id)
);