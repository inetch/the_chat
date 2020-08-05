create table usr_available_action (
    access_level_id     integer
  , action_id           integer
  , foreign key (access_level_id)   references usr_access_level(id)
  , foreign key (action_id)         references com_action(id)
  , primary key (access_level_id, action_id)
);