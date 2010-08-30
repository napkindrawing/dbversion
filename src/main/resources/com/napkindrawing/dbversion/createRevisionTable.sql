CREATE TABLE `__database_revision` (
 `id` INTEGER NOT NULL AUTO_INCREMENT ,
 `profile` VARCHAR(200) NOT NULL ,
 `version` VARCHAR(10) NOT NULL ,
 `upgrade_script_name` VARCHAR(250) NOT NULL ,
 `upgrade_script_template` MEDIUMTEXT NOT NULL ,
 `upgrade_script_template_checksum` VARCHAR(32) NOT NULL ,
 `upgrade_script_data` MEDIUMTEXT NOT NULL ,
 `upgrade_script_compiled` MEDIUMTEXT NOT NULL ,
 `upgrade_script_compiled_checksum` VARCHAR(32) NOT NULL ,
 `post_upgrade_schema_dump` MEDIUMTEXT NOT NULL ,
 `post_upgrade_schema_dump_checksum` VARCHAR(32) NOT NULL ,
 `upgrade_date` TIMESTAMP NOT NULL ,
 PRIMARY KEY (`id`) ,
 CONSTRAINT `uq_db_rev__profile_version`
 UNIQUE (`profile`,`version`)
)
CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
ENGINE = InnoDB