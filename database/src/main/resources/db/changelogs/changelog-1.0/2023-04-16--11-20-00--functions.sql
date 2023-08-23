--liquibase formatted sql

--changeset Andrey Ryabtsev:1 comment:Automatically set on update timestamp
CREATE FUNCTION on_update()
  RETURNS TRIGGER
  LANGUAGE PLPGSQL
AS '
BEGIN
  IF NEW.deleted THEN
    NEW."deletedAt" = NOW();
  ELSE
    NEW."updatedAt" = NOW();
  END IF;

  RETURN NEW;
END;
'
--rollback DROP FUNCTION on_update;