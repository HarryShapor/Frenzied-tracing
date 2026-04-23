CREATE TABLE boards (
	id BIGSERIAL PRIMARY KEY,
	height REAL NOT NULL,
	width REAL NOT NULL,
	grid_pitch REAL NOT NULL,
	layers INT NOT NULL DEFAULT 1,
	diagonals Boolean NOT NULL DEFAULT false,
	count_vertical_points INT,
	count_horizontal_points INT,
	n INT
);


CREATE TABLE paths_bitmask (
    id BIGSERIAL PRIMARY KEY,

    vertex_mask BYTEA NOT NULL,
    
    start_vertex INt NOT NULL,     
    end_vertex INT NOT NULL,      
    path_length INT NOT NULL,         
    
    CHECK (path_length >= 2)
);

CREATE INDEX idx_start_end ON paths_bitmask (start_vertex, end_vertex);
CREATE INDEX idx_mask_brin ON paths_bitmask USING brin (vertex_mask);
CREATE INDEX idx_length ON paths_bitmask (path_length);


CREATE TABLE paths_string (
    id BIGSERIAL PRIMARY KEY,
    path_string TEXT NOT NULL,
    start_vertex INT NOT NULL,
    end_vertex INT NOT NULL,
    path_length INT NOT NULL,
	turns INT,

    CHECK (path_length >= 2),
    CHECK (path_string ~ '^(\d+,)*\d+$')  -- Проверка формата "1,2,3,4"
);

CREATE INDEX idx_start_end ON paths_string(start_vertex, end_vertex);
CREATE INDEX idx_path_length ON paths_string(path_length);