
/* Represents an application, program, or process the Key is associated with -------------------------------------*/
DROP TABLE IF EXISTS [Application];

CREATE TABLE [Application] (
    [Id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name]        STRING,
    [Description] STRING
);
