db.createUser(
        {
            user: "crackhash_user",
            pwd: "crackhash",
            roles: [
                {
                    role: "readWrite",
                    db: "crackhash"
                }
            ]
        }
);