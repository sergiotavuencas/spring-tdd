CREATE TABLE IF NOT EXISTS post (
    id INT NOT NULL,
    user_id INT NOT NULL,
    title VARCHAR(250) NOT NULL,
    body TEXT NOT NULL,
    version INT,
    PRIMARY KEY(id)
);