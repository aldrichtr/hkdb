
/* Represents modifier keys (Ctrl, Alt, etc.) --------------------------------------------------------------------*/
DROP TABLE IF EXISTS [KeyModifier];

CREATE TABLE [KeyModifier] (
    [Id] INTEGER PRIMARY KEY,
    [Name]      STRING
);

/* Load data -----------------------------------------------------------------------------------------------------*/
INSERT INTO [KeyModifier] VALUES(0, 'None');
INSERT INTO [KeyModifier] VALUES(1, 'Alt');
INSERT INTO [KeyModifier] VALUES(2, 'Shift');
INSERT INTO [KeyModifier] VALUES(4, 'Ctrl');
