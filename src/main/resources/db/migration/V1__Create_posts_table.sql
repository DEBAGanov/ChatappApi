-- Создание таблицы posts
CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);

-- Вставка тестовых данных
INSERT INTO posts (title, content) VALUES
('Первый пост', 'Содержимое первого поста.'),
('Второй пост', 'Содержимое второго поста.'),
('Третий пост', 'Содержимое третьего поста.'); 