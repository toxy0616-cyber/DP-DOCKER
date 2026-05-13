CREATE TABLE IF NOT EXISTS chat_record (
    id VARCHAR(64) NOT NULL,
    content LONGTEXT,
    chat_type VARCHAR(32),
    chat_time DATETIME,
    family_member VARCHAR(128),
    PRIMARY KEY (id)
);
