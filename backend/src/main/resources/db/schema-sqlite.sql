CREATE TABLE IF NOT EXISTS todo (
  id            INTEGER       NOT NULL PRIMARY KEY AUTOINCREMENT,
  title         VARCHAR(255)  NOT NULL,
  content       TEXT,
  priority      VARCHAR(20)   NOT NULL DEFAULT 'medium',
  status        INTEGER       NOT NULL DEFAULT 0,
  deadline      DATE          NULL,
  category      VARCHAR(32)   NOT NULL DEFAULT 'other',
  create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_todo_status_deadline ON todo(status, deadline);

CREATE TABLE IF NOT EXISTS ai_chat (
  id          INTEGER   NOT NULL PRIMARY KEY AUTOINCREMENT,
  question    TEXT      NOT NULL,
  answer      TEXT,
  create_time DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP
);