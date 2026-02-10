
/* Represents an application, program, or process the Key is associated with -------------------------------------*/
DROP TABLE IF EXISTS [Application];

CREATE TABLE [Application] (
    [Id]          TEXT PRIMARY KEY, -- Sqlite3 doesn't have a native GUID/UUID data-type
    [Name]        TEXT,
    [Description] TEXT
);
