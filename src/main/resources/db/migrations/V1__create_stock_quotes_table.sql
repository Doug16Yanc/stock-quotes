CREATE TABLE stock_quotes (
      id UUID PRIMARY KEY,
      symbol VARCHAR(10) NOT NULL,
      current_price NUMERIC(10, 2) NOT NULL,
      change NUMERIC(10, 2) NOT NULL,
      change_percent NUMERIC(10, 2) NOT NULL,
      high_price NUMERIC(10, 2) NOT NULL,
      low_price NUMERIC(10, 2) NOT NULL,
      open_price NUMERIC(10, 2) NOT NULL,
      previous_close NUMERIC(10, 2) NOT NULL,
      quoted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);