
/* Represents a Mode the Application is in when the key is pressed -------------------------------------------------
   The Mode is a specific setting, environment or configuration within the application
   for example, in tmux, you can scroll around the screen by first enabling 'Scroll Mode' ( Ctrl-b [)
   and then using the arrow keys
   ---------------------------------------------------------------------------------------------------------------*/
DROP TABLE IF EXISTS [Mode];

CREATE TABLE [Mode] (
    [Id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    [Name]        STRING,
    [Description] STRING
);
