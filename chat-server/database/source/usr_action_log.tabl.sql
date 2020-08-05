create table usr_action_log (
    user_id         integer
  , access_level_id integer
  , action_id       integer
  , is_successful   integer     not null
  , timestamp       datetime    default current_timestamp
  , foreign key (user_id)           references usr_user(id)
  , foreign key (access_level_id )  references usr_access_level(id)
  , foreign key (action_id)         references com_action(id)
);