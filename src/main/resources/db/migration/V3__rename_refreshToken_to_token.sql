ALTER TABLE refresh_tokens
    ADD token VARCHAR(255);

ALTER TABLE refresh_tokens
    ALTER COLUMN token SET NOT NULL;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_token UNIQUE (token);

ALTER TABLE refresh_tokens
    DROP COLUMN refresh_token;
