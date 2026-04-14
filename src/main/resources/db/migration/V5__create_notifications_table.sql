CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient_id UUID REFERENCES users(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_user_games_user_id ON user_games(user_id);
CREATE INDEX idx_user_games_status ON user_games(status);
CREATE INDEX idx_friendships_requester ON friendships(requester_id);
CREATE INDEX idx_friendships_receiver ON friendships(receiver_id);
CREATE INDEX idx_activity_user_id ON activity_feed(user_id);
CREATE INDEX idx_notifications_recipient ON notifications(recipient_id);