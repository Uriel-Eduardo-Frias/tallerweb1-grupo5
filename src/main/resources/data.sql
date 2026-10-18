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
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (113, '¿En qué año comenzó la Segunda Guerra Mundial?', 1) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (114, '¿Quién fue conocido como el Libertador de América tras cruzar los Andes?', 1) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (115, '¿Qué civilización antigua construyó la ciudadela de Machu Picchu?', 1) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (103, '¿Cuál es el planeta más grande del sistema solar?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (104, '¿Qué elemento químico tiene el símbolo O?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (116, '¿Cuál es la fórmula química del agua?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (117, '¿Qué científico formuló la teoría de la relatividad general?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (118, '¿Cuál es el órgano más grande del cuerpo humano?', 2) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (105, '¿Cuál es el río más caudaloso del mundo?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (106, '¿Cuál es la capital de Australia?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (119, '¿Cuál es la montaña más alta del mundo sobre el nivel del mar?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (120, '¿Cuál es el océano más grande y profundo de la Tierra?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (121, '¿Cuál es el país más grande del mundo por superficie territorial?', 3) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (107, '¿Cada cuántos años se celebran los Juegos Olímpicos?', 4) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (108, '¿Qué país ganó la Copa Mundial de Fútbol en 2022?', 4)ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (122, '¿Cuántos jugadores por equipo juegan simultáneamente en la cancha en un partido de fútbol?', 4) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (123, '¿En qué deporte de raqueta se disputa el prestigioso torneo de Wimbledon?', 4) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (124, '¿Quién es el deportista con más medallas de oro en la historia de los Juegos Olímpicos?', 4) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (109, '¿Quién pintó la Mona Lisa?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (110, '¿A qué movimiento artístico pertenece Salvador Dalí?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (125, '¿Quién esculpió la célebre estatua de David en mármol?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (126, '¿Quién pintó la famosa obra La noche estrellada?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (127, '¿En qué famoso museo de París se encuentra la Mona Lisa?', 5) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);

INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (111, '¿Quién dirigió la película "El Padrino"?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (112, '¿Cómo se llama el personaje principal del juego "The Legend of Zelda"?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (128, '¿Cómo se llama el fontanero con bigote y traje rojo icónico de Nintendo?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (129, '¿En qué serie animada aparecen Homer, Marge, Bart, Lisa y Maggie?', 6) ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    categoria_id = VALUES(categoria_id);
INSERT INTO Pregunta (identificador, descripcion, categoria_id) VALUES (130, '¿En qué franquicia de ciencia ficción aparecen los Caballeros Jedi y la Fuerza?', 6) ON DUPLICATE KEY UPDATE
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

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (49, '1939', true, 113) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (50, '1914', false, 113) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (51, '1945', false, 113) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (52, '1929', false, 113) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (53, 'José de San Martín', true, 114) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (54, 'Manuel Belgrano', false, 114) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (55, 'Cornelio Saavedra', false, 114) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, persona_id) VALUES (56, 'Juan Manuel de Rosas', false, 114) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (57, 'Inca', true, 115) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (58, 'Maya', false, 115) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (59, 'Azteca', false, 115) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (60, 'Olmeca', false, 115) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (61, 'H2O', true, 116) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (62, 'CO2', false, 116) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (63, 'NaCl', false, 116) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (64, 'O2', false, 116) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (65, 'Albert Einstein', true, 117) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (66, 'Isaac Newton', false, 117) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (67, 'Galileo Galilei', false, 117) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (68, 'Nikola Tesla', false, 117) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (69, 'La piel', true, 118) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (70, 'El hígado', false, 118) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (71, 'El cerebro', false, 118) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (72, 'El corazón', false, 118) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (73, 'Monte Everest', true, 119) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (74, 'Aconcagua', false, 119) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (75, 'K2', false, 119) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (76, 'Kilimanjaro', false, 119) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (77, 'Océano Pacífico', true, 120) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (78, 'Océano Atlántico', false, 120) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (79, 'Océano Índico', false, 120) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (80, 'Océano Ártico', false, 120) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (81, 'Rusia', true, 121) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (82, 'Canadá', false, 121) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (83, 'China', false, 121) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (84, 'Estados Unidos', false, 121) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (85, '11', true, 122) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (86, '10', false, 122) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (87, '12', false, 122) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (88, '9', false, 122) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (89, 'Tenis', true, 123) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (90, 'Bádminton', false, 123) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (91, 'Squash', false, 123) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (92, 'Pádel', false, 123) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (93, 'Michael Phelps', true, 124) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (94, 'Usain Bolt', false, 124) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (95, 'Carl Lewis', false, 124) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (96, 'Larisa Latýnina', false, 124) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (97, 'Miguel Ángel', true, 125) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (98, 'Donatello', false, 125) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (99, 'Bernini', false, 125) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (100, 'Auguste Rodin', false, 125) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (101, 'Vincent van Gogh', true, 126) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (102, 'Claude Monet', false, 126) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, persona_id) VALUES (103, 'Paul Cézanne', false, 126) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (104, 'Edvard Munch', false, 126) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (105, 'Museo del Louvre', true, 127) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (106, 'Museo de Orsay', false, 127) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (107, 'Centro Pompidou', false, 127) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (108, 'Museo del Prado', false, 127) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (109, 'Mario', true, 128) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (110, 'Luigi', false, 128) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (111, 'Wario', false, 128) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (112, 'Bowser', false, 128) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (113, 'Los Simpson', true, 129) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (114, 'Padre de Familia', false, 129) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (115, 'Futurama', false, 129) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (116, 'South Park', false, 129) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (117, 'Star Wars', true, 130) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (118, 'Star Trek', false, 130) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (119, 'El Señor de los Anillos', false, 130) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);
INSERT INTO Opcion (id, texto, esCorrecta, pregunta_id) VALUES (120, 'Dune', false, 130) ON DUPLICATE KEY UPDATE texto = VALUES(texto), esCorrecta = VALUES(esCorrecta), pregunta_id = VALUES(pregunta_id);

INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (1, 'CINCUENTA_CINCUENTA', '50/50', 'Elimina 2 opciones incorrectas con penalización de tiempo')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (2, 'TIEMPO_EXTRA', 'Más Tiempo', 'Agrega 10 segundos al temporizador')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO comodines (id, codigo, nombre, descripcion) VALUES
    (4, 'DOBLE_CHANCE', 'Doble Chance', 'Te da un segundo intento si fallas tu respuesta')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);