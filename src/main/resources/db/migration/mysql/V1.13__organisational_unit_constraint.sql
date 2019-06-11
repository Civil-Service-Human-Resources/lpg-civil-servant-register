ALTER TABLE organisational_unit DROP FOREIGN KEY FK_organisational_unit_parent_id;
ALTER TABLE organisational_unit
ADD CONSTRAINT FK_organisational_unit_parent_id
FOREIGN KEY (parent_id) REFERENCES organisational_unit (id) ON DELETE SET NULL ON UPDATE CASCADE;