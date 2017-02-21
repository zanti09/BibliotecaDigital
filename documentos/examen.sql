create table usuario(
	nombreUsuario varchar(100) primary key,
	contrasena varchar(1024) not null
);

create table disponibilidad(
	idDisponibilidad serial primary key,
	conectado boolean not null,
	fecha datetime not null,
	nombreCuenta varchar(100) references usuario
);