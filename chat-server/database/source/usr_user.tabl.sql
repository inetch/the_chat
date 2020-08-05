create table usr_user (
    id              integer     primary key
  , nickname        text        not null
  , access_level_id integer
  , last_successful_action_id int, foreign key (access_level_id)   references usr_access_level(id)
  , foreign key (id)                references usr_credential(id)
);