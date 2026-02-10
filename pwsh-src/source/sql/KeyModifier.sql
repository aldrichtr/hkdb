
/* Represents modifier keys (Ctrl, Alt, etc.) --------------------------------------------------------------------*/
DROP TABLE IF EXISTS [KeyModifier];

CREATE TABLE [KeyModifier] (
    [Id] INTEGER PRIMARY KEY,
    [Name]      STRING
);

/* Load data -----------------------------------------------------------------------------------------------------*/
INSERT INTO [KeyModifier] VALUES(0, 'None');         -- 0x00
INSERT INTO [KeyModifier] VALUES(256, 'Alt');        -- 0x01
INSERT INTO [KeyModifier] VALUES(512, 'Shift');      -- 0x02
INSERT INTO [KeyModifier] VALUES(1024, 'Ctrl');      -- 0x04
INSERT INTO [KeyModifier] VALUES(2048, 'Ext');       -- 0x08
