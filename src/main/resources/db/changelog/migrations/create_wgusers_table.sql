create table wgusers
(
    id            int primary key AUTO_INCREMENT,
    username      varchar(255) not null,
    email         varchar(255),
    allowedIPs    varchar(255) not null,
    privatekey    varchar(255) not null,
    publickey     varchar(255) not null,
    lastHandShake varchar(255)
);

