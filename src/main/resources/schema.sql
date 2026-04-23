CREATE TABLE boards (
	id BIGSERIAL PRIMARY KEY,
	height REAL NOT NULL,
	width REAL NOT NULL,
	grid_pitch REAL NOT NULL,
	la

)


-- Таблица с битовыми масками
CREATE TABLE paths_bitmask (
    id BIGSERIAL PRIMARY KEY,
    
    -- Битовые маски (25 бит для платы 5x5x1)
    vertex_mask BIT(25) NOT NULL,           -- Какие вершины в пути
    
    -- Дополнительная информация
    start_vertex INt NOT NULL,          -- 0-24
    end_vertex INT NOT NULL,            -- 0-24
    path_length INT NOT NULL,           -- Количество вершин
    
    CHECK (start_vertex BETWEEN 0 AND 24),
    CHECK (end_vertex BETWEEN 0 AND 24),
    CHECK (path_length >= 2)
);

-- Индексы для быстрого поиска
CREATE INDEX idx_start_end ON paths_bitmask (start_vertex, end_vertex);
CREATE INDEX idx_mask_brin ON paths_bitmask USING brin (vertex_mask);
CREATE INDEX idx_length ON paths_bitmask (path_length);