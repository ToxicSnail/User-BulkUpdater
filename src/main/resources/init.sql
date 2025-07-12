CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    first_name  VARCHAR(50)  NOT NULL
        CONSTRAINT chk_first_name_format CHECK (first_name ~ '^[А-ЯЁ][а-яёА-ЯЁ''\- ]{2,}$')
        CONSTRAINT chk_first_name_length CHECK (length(first_name) BETWEEN 3 AND 50),
    last_name   VARCHAR(50)  NOT NULL
        CONSTRAINT chk_last_name_format CHECK (last_name ~ '^[А-ЯЁ][а-яёА-ЯЁ''\- ]{2,}$')
        CONSTRAINT chk_last_name_length CHECK (length(last_name) BETWEEN 3 AND 50),
    middle_name VARCHAR(50)
        CONSTRAINT chk_middle_name CHECK (
                middle_name IS NULL OR (
                        middle_name ~ '^[А-ЯЁ][а-яёА-ЯЁ''\- ]{2,}$' AND
                        length(middle_name) BETWEEN 3 AND 50
                )
            ),
    email       VARCHAR(100) NOT NULL
        CONSTRAINT chk_email_domain CHECK (email ~* '^[A-Za-z0-9._%-]+@(shift\.com|shift\.ru)$'),
    phone       VARCHAR(11)  NOT NULL UNIQUE
        CONSTRAINT chk_phone_format CHECK (phone ~ '^7[0-9]{10}$'),
    birth_date  DATE         NOT NULL
        CONSTRAINT chk_birth_date_age CHECK (birth_date <= (CURRENT_DATE - INTERVAL '18 years')),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Таблица для хранения информации о пользователях системы';
COMMENT ON COLUMN users.first_name IS 'Имя пользователя';
COMMENT ON COLUMN users.last_name IS 'Фамилия пользователя';
COMMENT ON COLUMN users.middle_name IS 'Отчество пользователя';
COMMENT ON COLUMN users.email IS 'Электронная почта';
COMMENT ON COLUMN users.phone IS 'Номер телефона';
COMMENT ON COLUMN users.birth_date IS 'Дата рождения';

CREATE TABLE uploaded_files
(
    id                SERIAL PRIMARY KEY,
    original_filename VARCHAR(50)  NOT NULL
        CONSTRAINT chk_file_name_length CHECK (length(original_filename) BETWEEN 3 AND 100),
    storage_path      VARCHAR(512) NOT NULL UNIQUE,
    status            VARCHAR(50)  NOT NULL DEFAULT 'NEW'
        CONSTRAINT chk_status_value CHECK (status IN ('NEW', 'IN_PROGRESS', 'DONE', 'FAILED')),
    hash              VARCHAR(40)  NOT NULL,
    inserted_rows     INTEGER,
    updated_rows      INTEGER
);

COMMENT ON TABLE uploaded_files IS 'Таблица для хранения информации о загруженных файлах';
COMMENT ON COLUMN uploaded_files.original_filename IS 'Оригинальное имя файла';
COMMENT ON COLUMN uploaded_files.storage_path IS 'Путь хранения файла';
COMMENT ON COLUMN uploaded_files.status IS 'Статус обработки файла';
COMMENT ON COLUMN uploaded_files.hash IS 'Хэш файла';
COMMENT ON COLUMN uploaded_files.inserted_rows IS 'Количество записей, успешно добавленных в базу клиентов';
COMMENT ON COLUMN uploaded_files.updated_rows IS 'Количество записей, успешно обновлённых в базе клиентов';

CREATE TABLE file_processing_errors
(
    id            SERIAL PRIMARY KEY,
    file_id       INTEGER     NOT NULL REFERENCES uploaded_files (id),
    row_number    INTEGER     NOT NULL,
    error_message TEXT        NOT NULL,
    error_code    VARCHAR(30) NOT NULL,
    raw_data      TEXT
);

COMMENT ON TABLE file_processing_errors IS 'Таблица для хранения детализированной информации об ошибках обработки';
COMMENT ON COLUMN file_processing_errors.file_id IS 'Идентификатор файла';
COMMENT ON COLUMN file_processing_errors.row_number IS 'Номер строки с ошибкой';
COMMENT ON COLUMN file_processing_errors.error_message IS 'Текст ошибки';
COMMENT ON COLUMN file_processing_errors.raw_data IS 'Исходное содержимое строки';
COMMENT ON COLUMN file_processing_errors.error_code IS 'Код ошибки';