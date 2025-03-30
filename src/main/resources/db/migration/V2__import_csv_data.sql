-- Отключаем ограничения и триггеры временно
SET session_replication_role = 'replica';

-- Увеличиваем размер рабочей памяти для оптимизации импорта
SET maintenance_work_mem = '1GB';

-- Начинаем транзакцию
BEGIN;

DO $$
BEGIN
    RAISE NOTICE 'Начало импорта данных: %', NOW();
END $$;

-- Копируем данные из CSV
COPY posts (title, content)
FROM '/docker-entrypoint-initdb.d/data/questions.csv'
WITH (
    FORMAT csv,
    HEADER true,
    DELIMITER ',',
    ENCODING 'UTF8'
);

-- Восстанавливаем ограничения
SET session_replication_role = 'origin';


DO $$
BEGIN
    RAISE NOTICE 'Импорт завершен: %', NOW();
END $$;

COMMIT;