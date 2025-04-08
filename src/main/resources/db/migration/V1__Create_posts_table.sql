-- Создание таблицы posts
CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL
);



-- Вставка тестовых данных
