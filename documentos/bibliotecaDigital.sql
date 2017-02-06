create table usuario(
	nombreCuenta varchar(100) primary key,
	nombres varchar(100) not null,
	apellidos varchar(100)  not null,
	clave varchar(150) not null,
	activo boolean not null
);

create table libro(
	codigoLibro serial primary key,
	nombre varchar(100) not null
);

create table usuarioLibros(
	idUsuarioLibros serial primary key,
	fecha date not null,
	accion char not null,
	nombreCuenta varchar(100) references usuario,
	codigoLibro integer references libro
);

create table disponibilidad(
	idDisponibilidad serial primary key,
	realizado boolean not null,
	nombreCuenta varchar(100) references usuario
);