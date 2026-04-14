CREATE TABLE friendships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requester_id UUID REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) CHECK (status IN (
        'PENDING','ACCEPTED','BLOCKED'
    )),
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(requester_id, receiver_id)
);