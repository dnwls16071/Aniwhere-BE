select * from users;

insert into users (created_at, updated_at, birthday,
                   birthyear, email, nickname,
                   password, role, sex, provider, provider_id)
            values(NOW(), NOW(), '1024',
                   '2024', 'admin@admin.com', 'admin',
                   'admin', 'ROLE_ADMIN', 'male', 'self', 'self');