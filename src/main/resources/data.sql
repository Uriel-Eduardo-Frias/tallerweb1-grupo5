INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'ADMIN', true);
INSERT INTO Usuario (id, email, password, rol, activo, username) VALUES (NULL, 'admin1@trivia.com', '1234', 'ADMIN', TRUE, 'admin');
INSERT INTO Usuario (id, email, password, rol, activo, username) VALUES (NULL, 'admin2@trivia.com', '1234', 'COMUN', TRUE, 'user');
INSERT INTO Categoria (id, nombre) VALUES (1, 'Historia');
INSERT INTO Categoria (id, nombre) VALUES (2, 'Ciencia');
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (101, '¿En qué año se descubrió América?', 1);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (1, '1492', true, 101);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (2, '1810', false, 101);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (3, '1776', false, 101);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (4, '1914', false, 101);

