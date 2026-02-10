
/* Represents the sequence of keys that is pressed -----------------------------------------------------------------
   ! For the purposes of hkdb, the following are definitions of key combinations:
   - Key :: A single key on the keyboard
   - Modifier :: A special type of key on the keyboard that does not produce a value on its own, but modifies the
                meaning of other keys in the chord (Control, Command, Alt, Extended("win key"))
   - KeyChord :: One or more keys pressed at the same time (Ctrl-Alt-d)
   - KeySequence :: One or more KeyChords
   - KeyBinding : A KeySequence and an Action


   ApplicationId :: Application this key is associated with, lookup in Application table
   ModeId :: Application's Mode this key is associated with, lookup in Mode table
   KeySequence :: A semicolon separated string of KeyChords
   Action :: The action or affect that the hotkey has (split-pane, open new file, etc)
   Commands :: The commands that produce the action/affect
   Note :: any notes about the keys
   ---------------------------------------------------------------------------------------------------------------*/
DROP TABLE IF EXISTS [KeyBinding];

CREATE TABLE [KeyBinding] (
    [Id]            TEXT PRIMARY KEY,
    [ApplicationId] TEXT REFERENCES [Application](Id),
    [ModeId]        TEXT REFERENCES [Mode](Id),
    [Parent]        TEXT REFERENCES [KeyBinding](Id),
    [KeySequence]   TEXT,
    [Action]        TEXT,
    [Commands]      TEXT,
    [Note]          TEXT
);
