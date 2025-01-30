INSERT INTO Users (email, username, password, role) VALUES
('igor@stefaniak.com', 'admin', '$2a$10$BLjKt0ilAkz2sER8FJv53uAE.lC32.EIekIvokyuZrq2C8DWaxAsu', 'ADMIN'),
('karolina@wrzalik.com', 'user', '$2a$10$BLjKt0ilAkz2sER8FJv53uAE.lC32.EIekIvokyuZrq2C8DWaxAsu', 'USER');

INSERT INTO Products (name, description, image, price, stock) VALUES
('Nuka-Cola', 'Klasyczny napój gazowany, który podbił serca mieszkańców przedwojennej Ameryki. Wyjątkowy smak łączy orzeźwiającą słodycz z nutą tajemnicy.','https://static.wikia.nocookie.net/fallout/images/1/10/Fallout4_Nuka_Cola.png', 1200.00, 35),
('Nuka-Cola Quantum', 'Legendarny napój z lśniącą, niebieską poświatą. Quantum to wyższa liga Nuka-Coli, wzbogacona o radioaktywną energię, która błyskawicznie regeneruje zdrowie i dodaje mocy.', 'https://static.wikia.nocookie.net/fallout/images/9/9d/FO3_Nuka-Cola_Quantum.png', 2500.00, 3),
('Rosół z makaronem', 'Domowy smak w apokaliptycznym świecie. Gorący, aromatyczny rosół z mięsem i miękkim makaronem to doskonały posiłek dla wędrowca. Nie tylko rozgrzewa i regeneruje zdrowie, ale także przywodzi na myśl lepsze czasy sprzed Wielkiej Wojny.', 'https://static.wikia.nocookie.net/fallout/images/d/db/Vegetable_soup.png', 150.00, 5);                                                                                                                                                             -- kainafetsrogi

INSERT INTO Orders (user_id, total_price, status) VALUES
(1, 1350.00, 'COMPLETED'),
(2, 800.00, 'PENDING'),
(2, 150.00, 'SHIPPED');
