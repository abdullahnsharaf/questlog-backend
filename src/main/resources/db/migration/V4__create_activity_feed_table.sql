CREATE TABLE activity_feed (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL,
    rawg_game_id INTEGER,
    game_title VARCHAR(255),
    details JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);