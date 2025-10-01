ALTER TABLE voting_users RENAME TO tb_users;
ALTER TABLE agendas RENAME TO tb_agendas;
ALTER TABLE voting_sessions RENAME TO tb_voting_sessions;
ALTER TABLE votes RENAME TO tb_votes;

DROP TABLE IF EXISTS admin_users;
