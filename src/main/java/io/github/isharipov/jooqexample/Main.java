package io.github.isharipov.jooqexample;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.*;

import java.sql.DriverManager;

import static io.github.isharipov.jooqexample.Tables.REGISTRATION;

public class Main {
    public static void main(String[] args) throws Exception {
        var connection = DriverManager.getConnection("jdbc:h2:~/test;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS \"public\"", "sa","");
        var stmt = connection.createStatement();
        String sql =  "CREATE TABLE IF NOT EXISTS REGISTRATION " +
                "(id INTEGER not NULL, " +
                " first VARCHAR(255), " +
                " last VARCHAR(255), " +
                " age INTEGER, " +
                " PRIMARY KEY ( id ))";

        stmt.executeUpdate(sql);

        GenerationTool.generate(new Configuration().withLogging(Logging.TRACE)
                .withJdbc(new Jdbc()
                        .withDriver("org.h2.Driver")
                        .withUrl("jdbc:h2:~/test;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS \"public\"")
                        .withUser("sa")
                        .withPassword(""))
                .withGenerator(new Generator()
                        .withDatabase(new Database().withInputSchema("PUBLIC"))
                        .withGenerate(new Generate()
                                .withPojos(true)
                                .withDaos(true))
                        .withTarget(new Target()
                                .withDirectory("build/generated-sources/jooq").withPackageName("io.github.isharipov.jooqexample"))));

        var dslContext = DSL.using(connection, SQLDialect.H2);
        dslContext.insertInto(REGISTRATION)
                .values(4, "first_name", "last_name", 35)
                .execute();
        Result<Record> result = dslContext.select().from(REGISTRATION).fetch();
        result.forEach(System.out::println);
        stmt.close();
        connection.close();
    }
}
