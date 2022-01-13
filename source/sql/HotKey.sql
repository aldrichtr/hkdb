
/* Represents the key combination that is pressed ------------------------------------------------------------------
   ApplicationId :: Application this key is associated with, lookup in Application table
   ModeId :: Application's Mode this key is associated with, lookup in Mode table
   KeyName :: Character is the key that should be pressed (Ctrl-b, Alt-w, etc) Lookup in the Key Table
   Modifiers :: Any KeyModifiers to include (as flags, so Ctrl+Shift is 6, Ctrl == 4, Shift == 2)
   Function :: the action or effect that the hotkey has (split-pane, open new file, etc)
   Note :: any notes about the keys
   ---------------------------------------------------------------------------------------------------------------*/
DROP TABLE IF EXISTS [Hotkey];

CREATE TABLE [Hotkey] (
    [Id]            INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [ApplicationId] INTEGER REFERENCES [Application]([Id]),
    [ModeId]        INTEGER REFERENCES [Mode]([Id]),
    [KeyName]       STRING    REFERENCES [Key]([Name]),
    [Modifiers]    INTEGER,
    [Function]      STRING,
    [Note]          STRING
);
