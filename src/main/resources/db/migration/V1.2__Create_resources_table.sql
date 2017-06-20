CREATE TABLE "resources" (
	"md5"		VARCHAR NOT NULL,
	"user"		VARCHAR NOT NULL,
	"course"	VARCHAR NOT NULL,
	"year"		VARCHAR,
	"sem"		INTEGER,
	"id"		BIGSERIAL,
	"res_Type"	VARCHAR,
	"path"		VARCHAR
	);
