create table usr_access_level_log (
    initiator_user_id       integer
  , target_user_id          integer
  , prev_access_level_id    integer
  , access_level_id         integer
  , timestamp               datetime    default current_timestamp
  , foreign key (initiator_user_id)     references usr_user(id)
  , foreign key (target_user_id)        references usr_user(id)
  , foreign key (prev_access_level_id)  references usr_access_level(id)
  , foreign key (access_level_id)       references usr_access_level(id)
);