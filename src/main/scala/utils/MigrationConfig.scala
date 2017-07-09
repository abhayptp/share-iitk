package utils

import org.flywaydb.core.Flyway

trait MigrationConfig  {

  private val flyway = new Flyway()
  flyway.setDataSource("jdbc:postgresql://localhost/", "postgres", "")
  //flyway.setDataSource("jdbc:postgres://ec2-184-73-236-170.compute-1.amazonaws.com:5432/dfjg9h0ssn3q0u","fzxgthobehxhcc","8a597691fd0be0fde896b9268f0e8a6301fede6168ecbea46cfe9abafcd0d72e")
  def migrate() = {
    flyway.migrate()
  }

  def reloadSchema() = {
    flyway.clean()
    flyway.migrate()
  }
}
