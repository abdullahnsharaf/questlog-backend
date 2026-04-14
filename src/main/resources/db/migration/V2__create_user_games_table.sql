CREATE TABLE user_games (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    rawg_game_id INTEGER NOT NULL,
    game_title VARCHAR(255) NOT NULL,
    game_cover_url VARCHAR(500),
    status VARCHAR(20) CHECK (status IN (
        'PLAYING','COMPLETED','WISHLIST','DROPPED','ON_HOLD'
    )),
    personal_rating INTEGER CHECK (
        personal_rating BETWEEN 1 AND 10
    ),
    review_text TEXT,
    hours_played FLOAT,
    added_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, rawg_game_id)
);