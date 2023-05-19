--liquibase formatted sql

--changeset Andrey Ryabtsev:7 comment:Add users on update triggers
CREATE TRIGGER on_users_update
BEFORE UPDATE OR DELETE
ON users
FOR EACH ROW EXECUTE FUNCTION on_update();
--rollback DROP TRIGGER on_users_update ON users;

--changeset Andrey Rybatsev:8 comment: Add gardens on update trigger
CREATE TRIGGER on_gardens_update
BEFORE UPDATE OR DELETE
ON gardens
FOR EACH ROW EXECUTE FUNCTION on_update();
--rollback DROP TRIGGER on_gardens_update ON gardens;