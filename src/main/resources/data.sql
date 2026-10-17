
INSERT INTO Usuario (id, email, password, rol, activo, username)
VALUES (null, 'admin1@trivia.com', '1234', 'ADMIN', TRUE, 'admin')
    ON DUPLICATE KEY UPDATE
                         password = VALUES(password),
                         rol = VALUES(rol),
                         activo = VALUES(activo),
                         username = VALUES(username);

INSERT INTO Usuario (id, email, password, rol, activo, username)
VALUES (null, 'admin2@trivia.com', '1234', 'COMUN', TRUE, 'user')
    ON DUPLICATE KEY UPDATE
                         password = VALUES(password),
                         rol = VALUES(rol),
                         activo = VALUES(activo),
                         username = VALUES(username);
INSERT INTO Categoria (id, nombre) VALUES (1, 'Historia') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
INSERT INTO Categoria (id, nombre) VALUES (2, 'Ciencia') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
INSERT INTO Categoria (id, nombre) VALUES (3, 'Geografía') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
INSERT INTO Categoria (id, nombre) VALUES (4, 'Deportes')ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
INSERT INTO Categoria (id, nombre) VALUES (5, 'Arte') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
INSERT INTO Categoria (id, nombre) VALUES (6, 'Entretenimiento') ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (101, '¿En qué año se descubrió América?', 1) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (102, '¿Quién fue el primer emperador romano?', 1) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (103, '¿Cuál es el planeta más grande del sistema solar?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (104, '¿Qué elemento químico tiene el símbolo O?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (105, '¿Cuál es el río más caudaloso del mundo?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (106, '¿Cuál es la capital de Australia?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (107, '¿Cada cuántos años se celebran los Juegos Olímpicos?', 4) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (108, '¿Qué país ganó la Copa Mundial de Fútbol en 2022?', 4)ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (109, '¿Quién pintó la Mona Lisa?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (110, '¿A qué movimiento artístico pertenece Salvador Dalí?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (111, '¿Quién dirigió la película "El Padrino"?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (112, '¿Cómo se llama el personaje principal del juego "The Legend of Zelda"?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (1, '1492', true, 101) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (2, '1810', false, 101) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (3, '1776', false, 101) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (4, '1914', false, 101) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (5, 'Augusto', true, 102) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (6, 'Julio César', false, 102) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (7, 'Nerón', false, 102) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (8, 'Calígula', false, 102) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (9, 'Júpiter', true, 103) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (10, 'Saturno', false, 103) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (11, 'Tierra', false, 103) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (12, 'Marte', false, 103) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (13, 'Oxígeno', true, 104) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (14, 'Oro', false, 104) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (15, 'Osmio', false, 104) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (16, 'Oganesón', false, 104) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (17, 'Amazonas', true, 105) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (18, 'Nilo', false, 105) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (19, 'Yangtsé', false, 105) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (20, 'Misisipi', false, 105) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (21, 'Canberra', true, 106) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (22, 'Sídney', false, 106) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (23, 'Melbourne', false, 106) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (24, 'Brisbane', false, 106) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (25, '4 años', true, 107) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (26, '2 años', false, 107) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (27, '3 años', false, 107) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (28, '5 años', false, 107) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (29, 'Argentina', true, 108) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (30, 'Francia', false, 108) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (31, 'Brasil', false, 108) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (32, 'Croacia', false, 108) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (33, 'Leonardo da Vinci', true, 109) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (34, 'Vincent van Gogh', false, 109) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (35, 'Pablo Picasso', false, 109) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (36, 'Miguel Ángel', false, 109) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (37, 'Surrealismo', true, 110) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (38, 'Cubismo', false, 110) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (39, 'Impresionismo', false, 110) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (40, 'Expresionismo', false, 110) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (41, 'Francis Ford Coppola', true, 111) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (42, 'Steven Spielberg', false, 111) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (43, 'Martin Scorsese', false, 111) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (44, 'Quentin Tarantino', false, 111) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (45, 'Link', true, 112) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (46, 'Zelda', false, 112) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (47, 'Ganon', false, 112) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (48, 'Mario', false, 112) ON DUPLICATE KEY UPDATE
    texto = VALUES(texto),
    esCorrecta = VALUES(esCorrecta),
    pregunta_id = VALUES(pregunta_id);
INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (1, 'CINCUENTA_CINCUENTA', '50/50', 'Elimina 2 opciones incorrectas con penalización de tiempo')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (2, 'TIEMPO_EXTRA', 'Más Tiempo', 'Agrega 10 segundos al temporizador')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (4, 'DOBLE_CHANCE', 'Doble Chance', 'Te da un segundo intento si fallas tu respuesta')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);
