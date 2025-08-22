CREATE TABLE voting_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    cpf VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY (cpf)
) ENGINE=InnoDB;

CREATE TABLE admin_users (
    id BIGINT NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES voting_users(id)
) ENGINE=InnoDB;

CREATE TABLE agendas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    creation_date DATE,
    created_by VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE voting_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    duration_in_minutes INT,
    start_time DATETIME,
    end_time DATETIME,
    agenda_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (agenda_id) REFERENCES agendas(id)
) ENGINE=InnoDB;

CREATE TABLE votes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    agenda_id BIGINT NOT NULL,
    vote_option VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES voting_users(id),
    FOREIGN KEY (agenda_id) REFERENCES agendas(id),
    UNIQUE KEY (user_id, agenda_id)
) ENGINE=InnoDB;